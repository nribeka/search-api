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
package com.mclinic.search.api.logger;

public interface Logger {

    /**
     * Check if debugging is enabled for this logger.
     *
     * @return true if debug is enabled
     */
    boolean isDebugEnabled();

    /**
     * Set the level of logging will be performed
     *
     * @param logLevel the log level
     */
    void setLogLevel(final LogLevel logLevel);

    /**
     * Get the log level for this logger
     *
     * @return the log level
     */
    LogLevel getLevel();

    /**
     * Log an info message.
     *
     * @param source  the source of the message
     * @param message the message to log
     */
    void info(final String source, final String message);

    /**
     * Log an info message.
     *
     * @param source    the source of the message
     * @param message   the message to log
     * @param throwable throwable object to log
     */
    void info(final String source, final String message, final Throwable throwable);

    /**
     * Log a warning message.
     *
     * @param source  the source of the message
     * @param message the message to log
     */
    void warn(final String source, final String message);

    /**
     * Log a warning message.
     *
     * @param source    the source of the message
     * @param message   the message to log
     * @param throwable throwable object to log
     */
    void warn(final String source, final String message, final Throwable throwable);

    /**
     * Log an error message.
     *
     * @param source  the source of the message
     * @param message the message to log
     */
    void error(final String source, final String message);

    /**
     * Log an error message.
     *
     * @param source    the source of the message
     * @param message   the message to log
     * @param throwable throwable object to log
     */
    void error(final String source, final String message, final Throwable throwable);

    /**
     * Log a debug message.
     *
     * @param source  the source of the message
     * @param message the message to log
     */
    void debug(final String source, final String message);

    /**
     * Log a debug message.
     *
     * @param source    the source of the message
     * @param message   the message to log
     * @param throwable throwable object to log
     */
    void debug(final String source, final String message, final Throwable throwable);
}
