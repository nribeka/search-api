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

package com.mclinic.search.api.util;

import com.mclinic.search.api.exception.ParseException;
import com.mclinic.search.api.registry.DefaultRegistry;
import com.mclinic.search.api.registry.Registry;
import com.mclinic.search.api.resource.ResourceConstants;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ResourceUtil {

    public static Registry<String, String> readConfiguration(final File file)
            throws ParseException, IOException {

        Registry<String, String> registry = new DefaultRegistry<String, String>();

        Properties properties = new Properties();
        properties.load(new FileReader(file));

        for (String mandatoryField : ResourceConstants.MANDATORY_FIELDS) {
            if (!properties.containsKey(mandatoryField))
                throw new ParseException("Unable to read '" + mandatoryField + "' property from j2l file.");
        }

        for (Object objectKey : properties.keySet()) {
            String propertyName = (String) objectKey;
            registry.putEntry(propertyName, properties.getProperty(propertyName));
        }
        return registry;
    }

}
