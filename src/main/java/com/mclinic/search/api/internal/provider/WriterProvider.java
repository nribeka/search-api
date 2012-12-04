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
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

import java.io.IOException;

public class WriterProvider implements SearchProvider<IndexWriter> {

    private final Version version;

    private final Analyzer analyzer;

    private final SearchProvider<Directory> directoryProvider;

    @Inject
    protected WriterProvider(final Version version, final Analyzer analyzer,
                             final SearchProvider<Directory> directoryProvider) {
        this.version = version;
        this.analyzer = analyzer;
        this.directoryProvider = directoryProvider;
    }

    @Override
    public IndexWriter get() throws IOException {
        Directory directory = directoryProvider.get();
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36, analyzer);
        return new IndexWriter(directory, config);
    }
}
