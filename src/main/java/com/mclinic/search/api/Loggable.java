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
package com.mclinic.search.api;

import com.mclinic.search.api.logger.Logger;

/**
 * Top level object for the search api.
 */
public interface Loggable {

    /**
     * Set the logger for this class. The logger will be injected using guice.
     *
     * @param logger the logger class.
     */
    void setLogger(final Logger logger);

    /**
     * Get the logger for this class. The logger will be injected by guice.
     *
     * @return the logger.
     */
    Logger getLogger();

}
