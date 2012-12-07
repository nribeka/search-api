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

package com.mclinic.search.api;

import com.mclinic.search.api.resource.Resource;
import org.apache.lucene.queryParser.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface RestAssuredService {

    /**
     * Load object described using the <code>resource</code> into local lucene repository. This method will use the URI
     * resolver to resolve the URI of the REST resources and then apply the <code>searchString</code> to limit the data
     * which will be loaded into the local lucene repository.
     * <p/>
     * Internally, this method will also add the following field:
     * <pre>
     * _class : the expected representation of the json when serialized
     * _resource : the resource configuration used to convert the json to lucene
     * _date_indexed : date and time when the json was indexed
     * </pre>
     *
     * @param searchString the string to filter object that from the REST resource.
     * @param resource     the resource object which will describe how to index the json resource to lucene.
     * @should load objects based on the resource description
     */
    void loadObjects(final String searchString, final Resource resource) throws ParseException, IOException;

    /**
     * Load object described using the <code>resource</code> into local lucene repository. This method will load locally
     * saved json payload and then apply the <code>searchString</code> to limit the data which will be loaded into the
     * local lucene repository.
     *
     * @param searchString the search string to filter object returned from the file.
     * @param resource     the resource object which will describe how to index the json resource to lucene.
     * @param file         the file in the filesystem where the json resource is saved.
     * @should load object from filesystem based on the resource description
     * @see RestAssuredService#loadObjects(String, com.mclinic.search.api.resource.Resource)
     */
    void loadObjects(final String searchString, final Resource resource, final File file)
            throws ParseException, IOException;

    /**
     * Search for an object with matching <code>key</code> and <code>clazz</code> type from the local repository. This
     * method will only return single object or null if no object match the key.
     * <p/>
     * Internally, this method will go through every registered resources to find which resources can be used to convert
     * the json payload to the an instance of <code>clazz</code> object. The method then extract the unique field from
     * each resource and then perform the lucene query for that resource. If the resource doesn't specify unique
     * searchable field, all searchable fields for that resource will be used for searching.
     *
     * @param key   the key to distinguish the object
     * @param clazz the expected return type of the object
     * @return object with matching key and clazz or null
     * @should return object with matching key and type
     * @should return null when no object match the key and type
     * @should throw IOException if the key and class unable to return unique object
     */
    <T> T getObject(final String key, final Class<T> clazz) throws ParseException, IOException;

    /**
     * Search for an object with matching <code>key</code> and <code>clazz</code> type from the local repository. This
     * method will only return single object or null if no object match the key.
     * <p/>
     * Internally, this method will pull unique searchable fields from the resource and then create the query for that
     * fields and passing the key as the value. If the resource doesn't specify unique searchable field, all
     * searchable fields for that resource will be used for searching.
     *
     * @param key      the key to distinguish the object
     * @param resource the resource object which will describe how to index the json resource to lucene.
     * @return object with matching key and clazz or null
     * @should return object with matching key
     * @should return null when no object match the key
     * @should throw IOException if the key and resource unable to return unique object
     */
    Object getObject(final String key, final Resource resource) throws ParseException, IOException;

    /**
     * Search for objects with matching <code>searchString</code> and <code>clazz</code> type from the local repository.
     * This method will return list of all matching object or empty list if no object match the search string.
     *
     * @param searchString the search string to limit the number of returned object
     * @param clazz        the expected return type of the object
     * @return list of all object with matching <code>searchString</code> and <code>clazz</code> or empty list
     * @should return all object matching the search search string and class
     * @should return empty list when no object match the search string and class
     */
    <T> List<T> getObjects(final String searchString, final Class<T> clazz) throws ParseException, IOException;

    /**
     * Search for objects with matching <code>searchString</code> and <code>resource</code> type from the local
     * repository. This method will return list of all matching object or empty list if no object match the search
     * string.
     *
     * @param searchString the search string to limit the number of returned object
     * @param resource     the resource descriptor used to register the object
     * @return list of all object with matching <code>searchString</code> and <code>resource</code> or empty list
     * @should return all object matching the search search string and resource
     * @should return empty list when no object match the search string and resource
     */
    List<Object> getObjects(final String searchString, final Resource resource) throws ParseException, IOException;

    /**
     * Remove an object based on the resource from the local repository. The method will determine if there's unique
     * <code>object</code> in the local repository and then remove it. This method will return null if there's no
     * object in the local repository match the object passed to this method.
     * <p/>
     * Internally, this method will serialize the object to json and then using the resource object, the method will
     * recreate unique key query to find the entry in the local lucene repository. If no unique searchable field is
     * specified in the resource configuration, this method will use all searchable index to find the entry.
     *
     * @param object   the object to be removed if the object exists.
     * @param resource the resource object which will describe how to index the json resource to lucene.
     * @return removed object or null if no object was removed.
     * @should remove an object from the internal index system
     */
    Object invalidate(final Object object, final Resource resource) throws ParseException, IOException;

    /**
     * Create an instance of object in the local repository.
     * <p/>
     * Internally, this method will serialize the object and using the resource configuration to create an entry in
     * the lucene local repository.
     *
     * @param object   the object to be created
     * @param resource the resource object which will describe how to index the json resource to lucene.
     * @return the object that was created
     * @should create a new object in the internal index system
     */
    Object createObject(final Object object, final Resource resource) throws ParseException, IOException;

    /**
     * Update an instance of object in the local repository.
     * <p/>
     * Internally, this method will perform invalidation of the object and then recreate the object in the local lucene
     * repository. If the changes are performed on the unique searchable field, this method will end up creating a new
     * entry in the lucene local repository.
     *
     * @param object   the object to be updated
     * @param resource the resource object which will describe how to index the json resource to lucene.
     * @return the object that was updated
     */
    Object updateObject(final Object object, final Resource resource) throws ParseException, IOException;

}
