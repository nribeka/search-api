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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;

/**
 * Created after reading the IOUtils from Apache's commons-io
 */
public class StreamUtil {

    /**
     * The default buffer size to use.
     */
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    public static String readAsString(final Reader reader) throws IOException {

        char[] buffer = new char[DEFAULT_BUFFER_SIZE];

        StringWriter writer = new StringWriter();
        BufferedReader bufferedReader = new BufferedReader(reader);

        int count;
        while ((count = bufferedReader.read(buffer)) != -1)
            writer.write(buffer, 0, count);
        return writer.toString();
    }
}
