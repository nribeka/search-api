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

package com.mclinic.search.api.module;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.google.inject.throwingproviders.ThrowingProviderBinder;
import com.mclinic.search.api.internal.lucene.DefaultIndexer;
import com.mclinic.search.api.internal.lucene.Indexer;
import com.mclinic.search.api.internal.provider.AnalyzerProvider;
import com.mclinic.search.api.internal.provider.DirectoryProvider;
import com.mclinic.search.api.internal.provider.ReaderProvider;
import com.mclinic.search.api.internal.provider.SearchProvider;
import com.mclinic.search.api.internal.provider.SearcherProvider;
import com.mclinic.search.api.internal.provider.WriterProvider;
import com.mclinic.search.api.registry.DefaultRegistry;
import com.mclinic.search.api.registry.Registry;
import com.mclinic.search.api.resource.Resource;
import com.mclinic.search.api.service.RestAssuredService;
import com.mclinic.search.api.service.impl.RestAssuredServiceImpl;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

public class SearchModule extends AbstractModule {

    /**
     * Configures a {@link com.google.inject.Binder} via the exposed methods.
     */
    @Override
    protected void configure() {
        bind(Integer.class)
                .annotatedWith(Names.named("connection.timeout"))
                .toInstance(1000);

        bind(new TypeLiteral<Registry<String, Resource>>() {})
                .toInstance(new DefaultRegistry<String, Resource>());

        bind(Indexer.class).to(DefaultIndexer.class).in(Singleton.class);
        bind(RestAssuredService.class).to(RestAssuredServiceImpl.class).in(Singleton.class);

        bind(Version.class).toInstance(Version.LUCENE_36);
        bind(Analyzer.class).toProvider(AnalyzerProvider.class);

        ThrowingProviderBinder.create(binder())
                .bind(SearchProvider.class, Directory.class)
                .to(DirectoryProvider.class)
                .in(Singleton.class);

        ThrowingProviderBinder.create(binder())
                .bind(SearchProvider.class, IndexReader.class)
                .to(ReaderProvider.class);

        ThrowingProviderBinder.create(binder())
                .bind(SearchProvider.class, IndexSearcher.class)
                .to(SearcherProvider.class);

        ThrowingProviderBinder.create(binder())
                .bind(SearchProvider.class, IndexWriter.class)
                .to(WriterProvider.class);
    }
}
