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

package com.mclinic.search.api.sample.domain;

import com.mclinic.search.api.model.object.BaseSearchable;

public class Cohort extends BaseSearchable {

    private String uuid;

    private String name;

    private String checksum;

    /**
     * Get the cohort uuid.
     *
     * @return the cohort uuid.
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Set the cohort uuid.
     *
     * @param uuid the cohort uuid.
     */
    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    /**
     * Get the cohort name.
     *
     * @return the cohort name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the cohort name.
     *
     * @param name the cohort name.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Get the checksum for the searchable object.
     *
     * @return the searchable object's checksum.
     */
    @Override
    public String getChecksum() {
        return checksum;
    }

    /**
     * Set the checksum for the searchable object.
     *
     * @param checksum the checksum for the searchable object.
     */
    @Override
    public void setChecksum(final String checksum) {
        this.checksum = checksum;
    }
}
