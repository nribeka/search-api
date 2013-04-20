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
import com.mclinic.search.api.util.StringUtil;

import java.util.Date;

public class Observation extends BaseSearchable {

    private String uuid;

    private String patient;

    private String value = StringUtil.EMPTY;

    private String question;

    private String questionUuid;

    private Date observationDatetime;

    private String checksum;

    /**
     * Get the uuid for the observation.
     *
     * @return the uuid for the observation.
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Set the uuid for the observation.
     *
     * @param uuid the uuid for the observation.
     */
    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    /**
     * Get the patient for the observation.
     *
     * @return the patient.
     */
    public String getPatient() {
        return patient;
    }

    /**
     * Set the patient for the observation.
     *
     * @param patient the patient.
     */
    public void setPatient(final String patient) {
        this.patient = patient;
    }

    /**
     * Get the value of the observation.
     *
     * @return the value of the observation.
     */
    public String getValue() {
        return value;
    }

    /**
     * Set the value of the observation.
     *
     * @param value the value of the observation.
     */
    public void setValue(final String value) {
        this.value = value;
    }

    /**
     * Get the question for the observation.
     *
     * @return the question for the observation.
     */
    public String getQuestion() {
        return question;
    }

    /**
     * Set the question for the observation.
     *
     * @param question the question for the observation.
     */
    public void setQuestion(final String question) {
        this.question = question;
    }

    /**
     * Get the question uuid for the observation.
     *
     * @return the question uuid for the observation.
     */
    public String getQuestionUuid() {
        return questionUuid;
    }

    /**
     * Set the question uuid for the observation.
     *
     * @param questionUuid the question uuid for the observation.
     */
    public void setQuestionUuid(final String questionUuid) {
        this.questionUuid = questionUuid;
    }

    /**
     * Get the datetime for the observation.
     *
     * @return the datetime for the observation.
     */
    public Date getObservationDatetime() {
        return observationDatetime;
    }

    /**
     * Set the datetime for the observation.
     *
     * @param observationDatetime the datetime for the observation.
     */
    public void setObservationDatetime(final Date observationDatetime) {
        this.observationDatetime = observationDatetime;
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
