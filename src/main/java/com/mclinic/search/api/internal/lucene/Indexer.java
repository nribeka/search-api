/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package com.mclinic.search.api.internal.lucene;

import com.mclinic.search.api.resource.Resource;
import org.apache.lucene.queryParser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;

public interface Indexer {

    void loadObjects(final Resource resource, final InputStream inputStream) throws ParseException, IOException;

    void loadObjects(final Resource resource, final Reader reader) throws ParseException, IOException;

    <T> T getObject(final String key, final Class<T> clazz) throws ParseException, IOException;

    Object getObject(final String key, final Resource resource) throws ParseException, IOException;

    <T> List<T> getObjects(final String searchString, final Class<T> clazz) throws ParseException, IOException;

    List<Object> getObjects(final String searchString, final Resource resource) throws ParseException, IOException;

    Object createObject(final Object object, final Resource resource) throws ParseException, IOException;

    Object deleteObject(final Object object, final Resource resource) throws ParseException, IOException;

    Object updateObject(final Object object, final Resource resource) throws ParseException, IOException;

    void commit() throws IOException;
}
