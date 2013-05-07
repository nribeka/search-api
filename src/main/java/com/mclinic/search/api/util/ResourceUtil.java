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

package com.mclinic.search.api.util;

import com.mclinic.search.api.exception.ParseException;
import com.mclinic.search.api.registry.DefaultRegistry;
import com.mclinic.search.api.registry.Registry;
import com.mclinic.search.api.resource.ResourceConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ResourceUtil {

    public static Registry<String, String> readConfiguration(final InputStream inputStream)
            throws ParseException, IOException {

        Registry<String, String> registry = new DefaultRegistry<String, String>();

        Properties properties = new Properties();
        properties.load(inputStream);

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

    public static Registry<String, String> readConfiguration(final File file)
            throws ParseException, IOException {
        InputStream inputStream = new FileInputStream(file);
        return readConfiguration(inputStream);
    }

}
