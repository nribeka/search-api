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

package com.muzima.search.api.util;

import java.io.File;

public class FilenameUtil {

    public static String getExtension(final File file) {
        String extension = null;
        String fileName = file.getName();
        int i = fileName.lastIndexOf('.');

        if (i > 0 && i < fileName.length() - 1) {
            extension = fileName.substring(i + 1).toLowerCase();
        }
        return extension;
    }

    public static boolean contains(final String filename, final String subset) {
        return filename.toLowerCase().contains(subset.toLowerCase());
    }
}
