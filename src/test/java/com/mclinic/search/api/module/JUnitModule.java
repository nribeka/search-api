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
import com.google.inject.Inject;
import com.google.inject.name.Names;
import com.mclinic.search.api.logger.LogLevel;
import com.mclinic.search.api.logger.Logger;

import java.io.File;

public class JUnitModule extends AbstractModule {

    public static final String LUCENE_DIRECTORY = File.separator + "lucene";

    /**
     * Configures a {@link com.google.inject.Binder} via the exposed methods.
     */
    @Override
    protected void configure() {
        String tmpDirectory = System.getProperty("java.io.tmpdir");
        bind(String.class)
                .annotatedWith(Names.named("configuration.lucene.directory"))
                .toInstance(tmpDirectory  + LUCENE_DIRECTORY);
        bind(String.class)
                .annotatedWith(Names.named("configuration.lucene.document.key"))
                .toInstance("uuid");

        bind(LogLevel.class).toInstance(LogLevel.DEBUG);
    }
}
