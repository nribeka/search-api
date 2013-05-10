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

import com.mclinic.search.api.Loggable;
import com.mclinic.search.api.model.object.Searchable;
import com.mclinic.search.api.resource.Resource;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Query;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface Indexer extends Loggable {

    void loadObjects(final Resource resource, final InputStream inputStream) throws ParseException, IOException;

    <T> T getObject(final String key, final Class<T> clazz) throws ParseException, IOException;

    Searchable getObject(final String key, final Resource resource) throws ParseException, IOException;

    <T> List<T> getObjects(final Query query, final Class<T> clazz) throws IOException;

    List<Searchable> getObjects(final Query query, final Resource resource) throws IOException;

    <T> List<T> getObjects(final String searchString, final Class<T> clazz) throws ParseException, IOException;

    List<Searchable> getObjects(final String searchString, final Resource resource) throws ParseException, IOException;

    Searchable deleteObject(final Searchable object, final Resource resource) throws ParseException, IOException;

    Searchable createObject(Searchable object, Resource resource) throws ParseException, IOException;

    Searchable updateObject(Searchable object, Resource resource) throws ParseException, IOException;

    void commit() throws IOException;
}
