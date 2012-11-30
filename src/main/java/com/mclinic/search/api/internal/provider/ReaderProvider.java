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

package com.mclinic.search.api.internal.provider;

import com.google.inject.Inject;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;

import java.io.IOException;

public class ReaderProvider implements SearchProvider<IndexReader> {

    private final SearchProvider<Directory> directoryProvider;

    @Inject
    protected ReaderProvider(final SearchProvider<Directory> directoryProvider) {
        this.directoryProvider = directoryProvider;
    }

    @Override
    public IndexReader get() throws IOException {
        Directory directory = directoryProvider.get();
        return IndexReader.open(directory);
    }
}
