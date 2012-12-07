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
