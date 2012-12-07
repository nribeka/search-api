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

public class LogLevel {

    public static final LogLevel INFO = new LogLevel("INFO", 1);

    public static final LogLevel WARN = new LogLevel("WARN", 2);

    public static final LogLevel ERROR = new LogLevel("ERROR", 3);

    public static final LogLevel DEBUG = new LogLevel("DEBUG", 4);

    private String name;

    private Integer level;

    private LogLevel(final String name, final Integer level) {
        this.name = name;
        this.level = level;
    }

    public Integer getLevel() {
        return this.level;
    }

    public String getName() {
        return this.name;
    }


    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof LogLevel)) return false;

        final LogLevel logLevel = (LogLevel) o;

        return (name.equals(logLevel.name) && level.equals(logLevel.level));
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + level.hashCode();
        return result;
    }
}
