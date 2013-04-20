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

public class Patient extends BaseSearchable {

    private String uuid;

    private String name;

    private String identifier;

    private String gender;

    private String checksum;

    /**
     * Get the patient internal uuid
     *
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Set the patient internal uuid
     *
     * @param uuid the uuid
     */
    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    /**
     * Get the patient name
     *
     * @return the patient name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the patient name
     *
     * @param name the patient name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Get the patient identifier
     *
     * @return the patient identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Set the patient identifier
     *
     * @param identifier the patient identifier
     */
    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    /**
     * Get the patient gender
     *
     * @return the patient gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * Set the patient gender
     *
     * @param gender the patient gender
     */
    public void setGender(final String gender) {
        this.gender = gender;
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
