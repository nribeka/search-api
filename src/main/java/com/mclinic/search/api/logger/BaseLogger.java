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

import com.google.inject.Inject;

public abstract class BaseLogger implements Logger {

    private LogLevel logLevel;

    public BaseLogger() {
        this.logLevel = LogLevel.INFO;
    }

    @Inject
    protected BaseLogger(final LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    /**
     * Check if debugging is enabled for this logger.
     *
     * @return true if debug is enabled
     */
    @Override
    public boolean isDebugEnabled() {
        return getLevel() == LogLevel.DEBUG;
    }

    /**
     * Set the level of logging will be performed
     *
     * @param logLevel the log level
     */
    @Override
    @Inject(optional = true)
    public void setLogLevel(final LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    /**
     * Get the log level for this logger
     *
     * @return the log level
     */
    @Override
    public LogLevel getLevel() {
        return logLevel;
    }

    /**
     * Log an info message.
     *
     * @param source  the source of the message
     * @param message the message to log
     */
    @Override
    public void info(final String source, final String message) {
        log(LogLevel.INFO, source, message, null);
    }

    /**
     * Log an info message.
     *
     * @param source    the source of the message
     * @param message   the message to log
     * @param throwable throwable object to log
     */
    @Override
    public void info(final String source, final String message, final Throwable throwable) {
        log(LogLevel.INFO, source, message, throwable);
    }

    /**
     * Log a warning message.
     *
     * @param source  the source of the message
     * @param message the message to log
     */
    @Override
    public void warn(final String source, final String message) {
        log(LogLevel.WARN, source, message, null);
    }

    /**
     * Log a warning message.
     *
     * @param source    the source of the message
     * @param message   the message to log
     * @param throwable throwable object to log
     */
    @Override
    public void warn(final String source, final String message, final Throwable throwable) {
        log(LogLevel.WARN, source, message, throwable);
    }

    /**
     * Log an error message.
     *
     * @param source  the source of the message
     * @param message the message to log
     */
    @Override
    public void error(final String source, final String message) {
        log(LogLevel.ERROR, source, message, null);
    }

    /**
     * Log an error message.
     *
     * @param source    the source of the message
     * @param message   the message to log
     * @param throwable throwable object to log
     */
    @Override
    public void error(final String source, final String message, final Throwable throwable) {
        log(LogLevel.ERROR, source, message, throwable);
    }

    /**
     * Log a debug message.
     *
     * @param source  the source of the message
     * @param message the message to log
     */
    @Override
    public void debug(final String source, final String message) {
        log(LogLevel.DEBUG, source, message, null);
    }

    /**
     * Log a debug message.
     *
     * @param source    the source of the message
     * @param message   the message to log
     * @param throwable throwable object to log
     */
    @Override
    public void debug(final String source, final String message, final Throwable throwable) {
        log(LogLevel.DEBUG, source, message, throwable);
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
    protected abstract void doLog(final LogLevel logLevel, final String source,
                                  final String message, final Throwable throwable);

    /**
     * Delegate the call to the actual implementation of the logger.
     *
     * @param level     the level of the log
     * @param source    the source of the log
     * @param message   the message of the log
     * @param throwable the throwable object
     */
    private void log(final LogLevel level, final String source,
                     final String message, final Throwable throwable) {
        if (getLevel().getLevel() >= level.getLevel())
            doLog(level, source, message, throwable);
    }
}
