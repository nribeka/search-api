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
