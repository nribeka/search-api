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

import com.mclinic.search.api.util.StringUtil;

import java.util.Date;

public class Observation {

    private String uuid;

    private String patient;

    private String valueText = StringUtil.EMPTY;

    private String fieldName;

    private String fieldUuid;

    private Date observationDate;

    private String json;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public String getPatient() {
        return patient;
    }

    public void setPatient(final String patient) {
        this.patient = patient;
    }

    public String getValueText() {
        return valueText;
    }

    public void setValueText(final String valueText) {
        this.valueText = valueText;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(final String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldUuid() {
        return fieldUuid;
    }

    public void setFieldUuid(final String fieldUuid) {
        this.fieldUuid = fieldUuid;
    }

    public Date getObservationDate() {
        return observationDate;
    }

    public void setObservationDate(final Date observationDate) {
        this.observationDate = observationDate;
    }

    public String getJson() {
        return json;
    }

    public void setJson(final String json) {
        this.json = json;
    }
}
