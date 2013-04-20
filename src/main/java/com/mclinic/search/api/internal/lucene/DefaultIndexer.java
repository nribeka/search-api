/**
 * Copyright 2012 Muzima Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mclinic.search.api.internal.lucene;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.jayway.jsonpath.JsonPath;
import com.mclinic.search.api.internal.provider.SearcherProvider;
import com.mclinic.search.api.internal.provider.WriterProvider;
import com.mclinic.search.api.logger.Logger;
import com.mclinic.search.api.model.object.Searchable;
import com.mclinic.search.api.registry.Registry;
import com.mclinic.search.api.resource.Resource;
import com.mclinic.search.api.resource.SearchableField;
import com.mclinic.search.api.util.CollectionUtil;
import com.mclinic.search.api.util.DigestUtil;
import com.mclinic.search.api.util.StreamUtil;
import com.mclinic.search.api.util.StringUtil;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DefaultIndexer implements Indexer {

    private Logger logger;

    private IndexWriter indexWriter;

    private IndexSearcher indexSearcher;

    @Inject
    private WriterProvider writerProvider;

    @Inject
    private SearcherProvider searcherProvider;

    @Inject
    private Registry<String, Resource> resourceRegistry;

    private final QueryParser parser;

    private static final String DEFAULT_FIELD_CHECKSUM = "_checksum";

    private static final String DEFAULT_FIELD_JSON = "_json";

    private static final String DEFAULT_FIELD_CLASS = "_class";

    private static final String DEFAULT_FIELD_RESOURCE = "_resource";

    private static final Integer DEFAULT_MAX_DOCUMENTS = 20;

    @Inject
    protected DefaultIndexer(final @Named("configuration.lucene.document.key") String defaultField,
                             final Version version, final Analyzer analyzer) {
        this.parser = new QueryParser(version, defaultField, analyzer);
    }

    /**
     * Set the logger for this class. The logger will be injected using guice.
     *
     * @param logger the logger class.
     */
    @Inject
    @Override
    public void setLogger(final Logger logger) {
        this.logger = logger;
    }

    /**
     * Get the logger for this class. The logger will be injected by guice.
     *
     * @return the logger.
     */
    @Override
    public Logger getLogger() {
        return logger;
    }

    /**
     * Get the singleton object of the resource registry.
     *
     * @return the resource registry object.
     */
    public Registry<String, Resource> getResourceRegistry() {
        return resourceRegistry;
    }

    /*
     * Private Getter and Setter section **
     */

    private IndexWriter getIndexWriter() throws IOException {
        if (indexWriter == null)
            indexWriter = writerProvider.get();
        return indexWriter;
    }

    private void setIndexWriter(final IndexWriter indexWriter) {
        this.indexWriter = indexWriter;
    }

    private IndexSearcher getIndexSearcher() throws IOException {
        try {
            if (indexSearcher == null)
                indexSearcher = searcherProvider.get();
        } catch (IOException e) {
            // silently ignoring this exception.
        }
        return indexSearcher;
    }

    private void setIndexSearcher(final IndexSearcher indexSearcher) {
        this.indexSearcher = indexSearcher;
    }

    /*
     * Low level lucene operation **
     */

    /**
     * Commit the changes in the index. This method will ensure that deletion, update and addition to the lucene index
     * are written to the filesystem (persisted).
     *
     * @throws IOException when the operation encounter errors.
     */
    @Override
    public void commit() throws IOException {
        if (getIndexWriter() != null) {
            getIndexWriter().commit();
            getIndexWriter().close();
        }
        // remove the instance
        // TODO: need a better to gracefully remove the instances
        setIndexWriter(null);
        setIndexSearcher(null);
    }

    /**
     * Create a single term lucene query. The value for the query will be surrounded with single quote.
     *
     * @param field the field on which the query should be performed.
     * @param value the value for the field
     * @return the valid lucene query for single term.
     */
    private String createQuery(final String field, final String value) {
        return "(" + field + ":" + StringUtil.quote(value) + ")";
    }

    /**
     * Create lucene query string based on the searchable field name and value. The value for the searchable field
     * will be retrieved from the <code>object</code>. This method will try to create a unique query in the case
     * where a searchable field is marked as unique. Otherwise the method will create a query string using all
     * available searchable fields.
     *
     * @param object the json object from which the value for each field can be retrieved from.
     * @param fields the searchable fields definition
     * @return query string which could be either a unique or full searchable field based query.
     */
    private String createObjectQuery(final Object object, final List<SearchableField> fields) {
        boolean uniqueExists = false;
        StringBuilder fullQuery = new StringBuilder();
        StringBuilder uniqueQuery = new StringBuilder();
        for (SearchableField searchableField : fields) {
            // we shouldn't include field that have null / empty value in the query
            Object valueObject = JsonPath.read(object, searchableField.getExpression());
            if (valueObject != null) {
                String value = valueObject.toString();
                String query = createQuery(searchableField.getName(), value);

                if (searchableField.isUnique()) {
                    uniqueExists = true;
                    if (!StringUtil.isBlank(uniqueQuery.toString()))
                        uniqueQuery.append(" AND ");
                    uniqueQuery.append(query);
                }

                // only create the full query if we haven't found any unique key in the searchable fields.
                if (!uniqueExists) {
                    if (!StringUtil.isBlank(fullQuery.toString()))
                        fullQuery.append(" AND ");
                    fullQuery.append(query);
                }
            }
        }

        if (uniqueExists)
            return uniqueQuery.toString();
        else
            return fullQuery.toString();
    }

    /**
     * Create query fragment for a certain class. Calling this method will ensure the documents returned will of the
     * <code>clazz</code> type meaning the documents can be converted into object of type <code>clazz</code>. Converting
     * the documents need to be performed by getting the correct resource object from the document and then calling the
     * serialize method or getting the algorithm class and perform the serialization process from the algorithm object.
     * <p/>
     * Example use case: please retrieve all patients data. This should be performed by querying all object of certain
     * class type  because the caller is interested only in the type of object, irregardless of the resources from
     * which the objects are coming from.
     *
     * @param clazz the clazz for which the query is based on
     * @return the base query for a resource
     */
    private String createClassQuery(final Class clazz) {
        StringBuilder builder = new StringBuilder();
        builder.append(createQuery(DEFAULT_FIELD_CLASS, clazz.getName()));
        return builder.toString();
    }

    /**
     * Create query for a certain resource object. Calling this method will ensure the documents returned will be
     * documents that was indexed using the resource object.
     *
     * @param resource the resource for which the query is based on
     * @return the base query for a resource
     */
    private String createResourceQuery(final Resource resource) {
        StringBuilder builder = new StringBuilder();
        builder.append(createQuery(DEFAULT_FIELD_RESOURCE, resource.getName()));
        return builder.toString();
    }

    /**
     * Search the local lucene repository for documents with similar information with information inside the
     * <code>query</code>. Search can return multiple documents with similar information or empty list when no
     * document have similar information with the <code>query</code>.
     *
     * @param query the lucene query.
     * @return objects with similar information with the query.
     * @throws IOException when the search encounter error.
     */
    private List<Document> findDocuments(final Query query) throws IOException {
        List<Document> documents = new ArrayList<Document>();
        IndexSearcher searcher = getIndexSearcher();
        if (searcher != null) {
            TopDocs docs = searcher.search(query, DEFAULT_MAX_DOCUMENTS);
            ScoreDoc[] hits = docs.scoreDocs;
            for (ScoreDoc hit : hits)
                documents.add(searcher.doc(hit.doc));
        }
        return documents;
    }

    /**
     * Write json representation of a single object as a single document entry inside Lucene index.
     *
     * @param jsonObject the json object to be written to the index
     * @param resource   the configuration to transform json to lucene document
     * @param writer     the lucene index writer
     * @throws java.io.IOException when writing document failed
     */
    private void writeObject(final Object jsonObject, final Resource resource, final IndexWriter writer)
            throws IOException {

        String checksum = DigestUtil.getSHA1Checksum(jsonObject.toString());

        Document document = new Document();
        document.add(new Field(DEFAULT_FIELD_JSON, jsonObject.toString(), Field.Store.YES, Field.Index.NO));
        document.add(new Field(DEFAULT_FIELD_CHECKSUM, checksum, Field.Store.NO,
                Field.Index.ANALYZED_NO_NORMS));
        document.add(new Field(DEFAULT_FIELD_CLASS, resource.getResourceObject().getName(), Field.Store.NO,
                Field.Index.ANALYZED_NO_NORMS));
        document.add(new Field(DEFAULT_FIELD_RESOURCE, resource.getName(), Field.Store.YES,
                Field.Index.ANALYZED_NO_NORMS));

        for (SearchableField searchableField : resource.getSearchableFields()) {
            Object valueObject = JsonPath.read(jsonObject, searchableField.getExpression());
            String value = StringUtil.EMPTY;
            // TODO: need a better way to handle this value
            if (valueObject != null)
                value = valueObject.toString();
            document.add(new Field(searchableField.getName(), value, Field.Store.NO, Field.Index.ANALYZED_NO_NORMS));
        }

        if (getLogger().isDebugEnabled())
            getLogger().debug(this.getClass().getSimpleName(), "Writing document: " + document);

        writer.addDocument(document);
    }

    /**
     * Delete an entry from the lucene index. The method will search for a single entry in the index (throwing
     * IOException when more than one index match the object).
     *
     * @param jsonObject  the json object to be deleted.
     * @param resource    the resource definition used to register the json to lucene index.
     * @param indexWriter the index writer used to delete the index.
     * @throws ParseException when the json can't be used to create a query to identify the correct lucene index.
     * @throws IOException    when other error happens during the deletion process.
     */
    private void deleteObject(final Object jsonObject, final Resource resource, final IndexWriter indexWriter)
            throws ParseException, IOException {
        String queryString =
                createResourceQuery(resource)
                        + " AND (" + createObjectQuery(jsonObject, resource.getSearchableFields()) + ")";

        if (getLogger().isDebugEnabled())
            getLogger().debug(this.getClass().getSimpleName(), "Query deleteObject(): " + queryString);

        Query query = parser.parse(queryString);
        List<Document> documents = findDocuments(query);
        // only delete object if we can uniquely identify the object
        if (documents.size() == 1)
            indexWriter.deleteDocuments(query);
        else if (documents.size() > 1)
            throw new IOException("Unable to uniquely identify an object using the json object in the repository.");
    }

    /**
     * Update an object inside the lucene index with a new data. Updating process practically means deleting old object
     * and then adding the new object.
     *
     * @param jsonObject  the json object to be updated.
     * @param resource    the resource definition used to register the json to lucene index.
     * @param indexWriter the index writer used to delete the index.
     * @throws ParseException when the json can't be used to create a query to identify the correct lucene index.
     * @throws IOException    when other error happens during the deletion process.
     */
    private void updateObject(final Object jsonObject, final Resource resource, final IndexWriter indexWriter)
            throws ParseException, IOException {
        // search for the same object, if they exists, delete them :)
        deleteObject(jsonObject, resource, indexWriter);
        // write the new object
        writeObject(jsonObject, resource, indexWriter);
    }

    @Override
    public void loadObjects(final Resource resource, final InputStream inputStream)
            throws ParseException, IOException {
        InputStreamReader reader = new InputStreamReader(inputStream);
        String json = StreamUtil.readAsString(reader);
        Object jsonObject = JsonPath.read(json, resource.getRootNode());
        if (jsonObject instanceof JSONArray) {
            JSONArray array = (JSONArray) jsonObject;
            for (Object element : array)
                updateObject(element, resource, getIndexWriter());
        } else if (jsonObject instanceof JSONObject) {
            updateObject(jsonObject, resource, getIndexWriter());
        }
    }

    @Override
    public <T> T getObject(final String key, final Class<T> clazz) throws ParseException, IOException {
        T object = null;

        String queryString = createClassQuery(clazz);
        if (!StringUtil.isEmpty(key))
            queryString = queryString + " AND " + key;

        if (getLogger().isDebugEnabled())
            getLogger().debug(this.getClass().getSimpleName(), "Query getObject(String, Class): " + queryString);

        Query query = parser.parse(queryString);
        List<Document> documents = findDocuments(query);

        if (!CollectionUtil.isEmpty(documents) && documents.size() > 1)
            throw new IOException("Unable to uniquely identify an object using key: '" + key + "' in the repository.");

        for (Document document : documents) {
            String resourceName = document.get(DEFAULT_FIELD_RESOURCE);
            Resource resource = getResourceRegistry().getEntryValue(resourceName);
            String json = document.get(DEFAULT_FIELD_JSON);
            object = clazz.cast(resource.deserialize(json));
        }

        return object;
    }

    @Override
    public Searchable getObject(final String key, final Resource resource) throws ParseException, IOException {
        Searchable object = null;

        String queryString = createResourceQuery(resource);
        if (!StringUtil.isEmpty(key))
            queryString = queryString + " AND (" + key + ")";

        if (getLogger().isDebugEnabled())
            getLogger().debug(this.getClass().getSimpleName(), "Query getObject(String,  Resource): " + queryString);

        Query query = parser.parse(queryString);
        List<Document> documents = findDocuments(query);

        if (!CollectionUtil.isEmpty(documents) && documents.size() > 1)
            throw new IOException("Unable to uniquely identify an object using key: '" + key + "' in the repository.");

        for (Document document : documents) {
            String json = document.get(DEFAULT_FIELD_JSON);
            object = resource.deserialize(json);
        }

        return object;
    }

    @Override
    public <T> List<T> getObjects(final String searchString, final Class<T> clazz)
            throws ParseException, IOException {
        List<T> objects = new ArrayList<T>();

        String queryString = createClassQuery(clazz);
        if (!StringUtil.isEmpty(searchString))
            queryString = queryString + " AND (" + searchString + ")";

        if (getLogger().isDebugEnabled())
            getLogger().debug(this.getClass().getSimpleName(), "Query getObjects(String, Class): " + queryString);

        Query query = parser.parse(queryString);
        List<Document> documents = findDocuments(query);
        for (Document document : documents) {
            String resourceName = document.get(DEFAULT_FIELD_RESOURCE);
            Resource resource = getResourceRegistry().getEntryValue(resourceName);
            String json = document.get(DEFAULT_FIELD_JSON);
            objects.add(clazz.cast(resource.deserialize(json)));
        }
        return objects;
    }

    @Override
    public List<Searchable> getObjects(final String searchString, final Resource resource)
            throws ParseException, IOException {
        List<Searchable> objects = new ArrayList<Searchable>();

        // TODO: use checksum here.
        // - add checksum field to the lucene instead of a new uuid field
        // - add checksum field to the searchable object
        // - add checksum value on the object from the algorithm
        String queryString = createResourceQuery(resource);
        if (!StringUtil.isEmpty(searchString))
            queryString = queryString + " AND (" + searchString + ")";

        if (getLogger().isDebugEnabled())
            getLogger().debug(this.getClass().getSimpleName(), "Query getObjects(String, Resource): " + queryString);

        Query query = parser.parse(queryString);
        List<Document> documents = findDocuments(query);
        for (Document document : documents) {
            String json = document.get(DEFAULT_FIELD_JSON);
            objects.add(resource.deserialize(json));
        }
        return objects;
    }

    @Override
    public Searchable deleteObject(final Searchable object, final Resource resource)
            throws ParseException, IOException {

        String checksum = object.getChecksum();
        String queryString = createResourceQuery(resource);
        if (!StringUtil.isEmpty(checksum))
            queryString = queryString + " AND " + createQuery(DEFAULT_FIELD_CHECKSUM, checksum);

        if (getLogger().isDebugEnabled())
            getLogger().debug(this.getClass().getSimpleName(), "Query deleteObject(): " + queryString);

        Query query = parser.parse(queryString);
        List<Document> documents = findDocuments(query);
        // only delete object if we can uniquely identify the object
        if (documents.size() == 1)
            getIndexWriter().deleteDocuments(query);
        else if (documents.size() > 1)
            throw new IOException("Unable to uniquely identify an object using the json object in the repository.");

        commit();
        return object;
    }
}
