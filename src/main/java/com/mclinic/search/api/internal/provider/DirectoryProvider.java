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
import com.google.inject.name.Named;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;

import java.io.File;
import java.io.IOException;

public class DirectoryProvider implements SearchProvider<Directory> {

    private final String directory;

    // TODO: create a factory to customize the type of directory returned by this provider
    @Inject
    protected DirectoryProvider(final @Named("configuration.lucene.directory") String directory) {
        this.directory = directory;
    }

    @Override
    public Directory get() throws IOException {
        return NIOFSDirectory.open(new File(directory));
    }
}
