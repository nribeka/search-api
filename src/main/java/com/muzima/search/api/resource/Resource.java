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

package com.muzima.search.api.resource;

import com.muzima.search.api.model.object.Searchable;
import com.muzima.search.api.model.resolver.Resolver;
import com.muzima.search.api.model.serialization.Algorithm;

import java.io.IOException;
import java.util.List;

/**
 * Interface for to represent a single resource object for the REST Assured service. This interface describe how a
 * a REST resource will be converted into lucene index and how to convert the representation into a concrete object.
 * <p/>
 * It is strongly advised for the user of the API to extends the <code>ObjectResource</code> instead of directly
 * implementing this interface.
 * <p/>
 * TODO: think about how we can use the etags to limit number of download need to be performed to get data from server.
 */
public interface Resource {

    String getName();

    /**
     * Get the root node's <a href="http://goessner.net/articles/JsonPath/">JsonPath</a> expression. The expression can
     * then be evaluated on the resource's json representation to find the json representation of the object.
     * <p/>
     * <pre>
     * Assuming we will work with the Patient object.
     * Given a single patient json:
     * {
     *     "uuid":"dd55e586-1691-11df-97a5-7038c432aabf",
     *     "display":"363MO-5 - Testarius Ambote Indakasi",
     *     "identifiers":[
     *         {
     *             "display":"Old AMPATH Medical Record Number = 363MO-5",
     *             "uuid":"dcea0c45-1691-11df-97a5-7038c432aabf",
     *             "identifier":"363MO-5",
     *             "identifierType":{
     *                 "uuid":"58a46a32-1359-11df-a1f1-0026b9348838",
     *                 "display":"Old AMPATH Medical Record Number"
     *             },
     *             "location":{
     *                 "uuid":"c0937b97-1691-11df-97a5-7038c432aabf",
     *                 "display":"Wishard Hospital - "
     *             },
     *             "preferred":false,
     *             "voided":false,
     *             "resourceVersion":"1.8"
     *         }
     *     ]
     * }
     *
     * The JsonPath example:
     * "$" is the root node expression because the entire json is representing a single patient object.
     * "$.uuid" will return the uuid value of the above patient.
     * "$.identifiers[0]" will return the first identifier object of the identifiers array.
     * "$.identifiers[0].identifier" will return the identifier value of the first identifier in the array
     *
     * Given a cohort of patients json:
     * {
     *     "results":[
     *         {
     *             "display":"134TS-4 - Misula Chepseng'Egny Waula",
     *             "patient":{
     *                 "uuid":"dd5588e5-1691-11df-97a5-7038c432aabf",
     *                 "display":"134TS-4 - Misula Chepseng'Egny Waula",
     *                 "identifiers":[
     *                     {
     *                         "uuid":"dce9abf2-1691-11df-97a5-7038c432aabf",
     *                         "display":"Old AMPATH Medical Record Number = 134TS-4"
     *                     }
     *                 ]
     *             }
     *         }
     *     ],[
     *         {
     *             "display":"1545BF-7 - Nyoman Winardi Ribeka",
     *             "patient":{
     *                 "uuid":"dd6fc9f4-1691-11df-97a5-7038c432aabf",
     *                 "display":"1545BF-7 - Nyoman Winardi Ribeka",
     *                 "identifiers":[
     *                     {
     *                         "uuid":"dd6fc9f4-1691-11df-97a5-7038c432aabf",
     *                         "display":"Old AMPATH Medical Record Number = 1545BF-7"
     *                     }
     *                 ]
     *             }
     *         }
     *     ]
     * }
     *
     * The JsonPath example:
     * "$.results" is the root node expression because the full json is a cohort, and "results" hold array of patients
     *
     * All subsequent JsonPath will be evaluated relative against the root node.
     * "$.patient.uuid" will return the uuid value of the above patient.
     * "$.patient.identifiers[0]" will return the first identifier object of the identifiers array.
     * "$.patient.identifiers[0].uuid" will return the uuid value of the first identifier in the array.
     * </pre>
     *
     * @return the root node's JsonPath expression
     * @see <a href="http://goessner.net/articles/JsonPath/">JsonPath Operator</a>
     */
    String getRootNode();

    /**
     * Get the resource class for which this resource applicable to. This class will denote what kind of class this
     * Resource will return when client call the de-serialize method.
     *
     * @return the class for which this resource applicable to.
     */
    Class getSearchable();

    /**
     * Get class which will perform the serialization and de-serialization process from String representation to the
     * correct object representation.
     *
     * @return the serialization algorithm class for this resource implementation
     */
    Algorithm getAlgorithm();

    /**
     * Get class which will resolve the REST resource URI for this particular resource.
     *
     * @return the resource's Resolver
     */
    Resolver getResolver();

    /**
     * Add a new searchable field for the current resource object. Searchable field is a field on which a client can
     * do filter and search. The search / query string will in the form of <a href="https://lucene.apache
     * .org/">Lucene</a> query.
     *
     * @param name       the name of the field
     * @param expression the JsonPath expression to retrieve the value for the field
     * @param unique     flag whether this field can uniquely identify an object for this resource
     * @see <a href="https://lucene.apache.org/core/old_versioned_docs/versions/3_0_0/queryparsersyntax.html">Query
     *      Syntax</a>
     * @see <a href="http://goessner.net/articles/JsonPath/">JsonPath Operators</a>
     */
    void addFieldDefinition(final String name, final String expression, final Boolean unique);

    /**
     * Get all searchable fields configuration for this resource. Searchable field are a field on which a client can
     * do filter and search. The search / query string will in the form of <a href="https://lucene.apache
     * .org/">Lucene</a> query.
     *
     * @return the list of all searchable fields for this resource
     * @see <a href="https://lucene.apache.org/core/old_versioned_docs/versions/3_0_0/queryparsersyntax.html">Query
     *      Syntax</a>
     */
    List<SearchableField> getSearchableFields();

    /**
     * Perform serialization for the object and returning the String representation of the object. Default
     * implementation of this should delegate the serialization to the <code>Algorithm</code> object.
     *
     * @param object the object
     * @return String representation of the object
     */
    String serialize(final Searchable object) throws IOException;

    /**
     * Perform de-serialization of the object's String representation into the concrete object representation. Default
     * implementation should delegate the serialization to the <code>Algorithm</code> object.
     *
     * @param string the String representation of the object
     * @return the concrete object based on the String input
     */
    Searchable deserialize(final String string) throws IOException;

    /**
     * Get the URI for the resource where the api can retrieve data. Default implementation should delegate this call to
     * the <code>Resolver</code> class.
     *
     * @param searchString the search term for the REST URI
     * @return the full REST URI with the search string
     */
    String getUri(final String searchString) throws IOException;
}
