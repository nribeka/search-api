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

package com.mclinic.search.api.logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConsoleLogger extends BaseLogger {

    private final DateFormat dateFormat;

    /**
     * Create the console logger to log messages from the REST Assured framework.
     */
    protected ConsoleLogger() {
        this.dateFormat = new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss.SSS");
    }

    /**
     * Actual implementation of the logger should implement the actual process of writing this message to whatever
     * the logger wants to write. Stone is definitely not an option :)
     *
     * @param logLevel  the log level
     * @param source    the source of the log
     * @param message   the message inside the log
     * @param throwable the throwable object
     */
    @Override
    protected void doLog(final LogLevel logLevel, final String source, final String message,
                         final Throwable throwable) {
        StringBuilder logBuilder = new StringBuilder();

        logBuilder.append("[").append(dateFormat.format(new Date())).append("]");
        logBuilder.append("[").append(logLevel.getName()).append("]");
        logBuilder.append("[").append(source).append("]");
        logBuilder.append(message);
        System.out.println(logBuilder.toString());

        if (throwable != null)
            throwable.printStackTrace();
    }
}
