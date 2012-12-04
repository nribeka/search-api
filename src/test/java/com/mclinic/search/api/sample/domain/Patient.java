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

public class Patient {

    private String uuid;

    private String name;

    private String identifier;

    private String gender;

    private String json;

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
     * Get the underlying json representation for the patient
     *
     * @return the json representation
     */
    public String getJson() {
        return json;
    }

    /**
     * Set the underlying json representation for the patient
     *
     * @param json the json representation
     */
    public void setJson(final String json) {
        this.json = json;
    }
}
