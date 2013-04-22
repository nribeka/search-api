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
package com.mclinic.search.api.sample.domain;

import com.mclinic.search.api.model.object.BaseSearchable;

import java.util.List;

/**
 */
public class CohortMember extends BaseSearchable {

    private String checksum;

    private List<Patient> patientList;

    /**
     * Get list of all patients in the cohort.
     *
     * @return list of all patients in the cohort.
     */
    public List<Patient> getPatientList() {
        return patientList;
    }

    /**
     * Set the patient list in this cohort.
     *
     * @param patientList list of all patients in the cohort.
     */
    public void setPatientList(final List<Patient> patientList) {
        this.patientList = patientList;
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
