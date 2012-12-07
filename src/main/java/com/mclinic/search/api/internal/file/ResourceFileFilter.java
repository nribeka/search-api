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

package com.mclinic.search.api.internal.file;

import com.mclinic.search.api.util.FilenameUtil;
import com.mclinic.search.api.util.StringUtil;

import java.io.File;
import java.io.FileFilter;

public class ResourceFileFilter implements FileFilter {

    public static final String RESOURCE_FILE_EXTENSION = "j2l";

    @Override
    public boolean accept(final File file) {
        if (file.isDirectory())
            return true;

        String extension = FilenameUtil.getExtension(file);
        return StringUtil.equals(extension, RESOURCE_FILE_EXTENSION);
    }
}
