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

package com.mclinic.search.api.sample.algorithm;

import com.jayway.jsonpath.JsonPath;
import com.mclinic.search.api.model.object.Searchable;
import com.mclinic.search.api.model.serialization.BaseAlgorithm;
import com.mclinic.search.api.sample.domain.Patient;
import com.mclinic.search.api.util.DigestUtil;
import com.mclinic.search.api.util.StringUtil;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class PatientAlgorithm  extends BaseAlgorithm {

    /**
     * Implementation of this method will define how the patient will be serialized from the JSON representation.
     *
     * @param serialized the json representation
     * @return the concrete patient object
     */
    @Override
    public Searchable deserialize(final String serialized) throws IOException {
        Patient patient = new Patient();

        String checksum = DigestUtil.getSHA1Checksum(serialized);
        patient.setChecksum(checksum);

        // get the full json object representation and then pass this around to the next JsonPath.read()
        // this should minimize the time for the subsequent read() call
        Object jsonObject = JsonPath.read(serialized, "$");

        String uuid = JsonPath.read(jsonObject, "$.uuid");
        patient.setUuid(uuid);

        String name = JsonPath.read(jsonObject, "$.person.display");
        patient.setName(name);

        String identifier = JsonPath.read(jsonObject, "$.identifiers[0].display");
        patient.setIdentifier(identifier);

        String gender = JsonPath.read(jsonObject, "$.person.gender");
        patient.setGender(gender);

        return patient;
    }

    /**
     * Implementation of this method will define how the patient will be deserialized into the JSON representation.
     *
     * @param object the patient
     * @return the json representation
     */
    @Override
    public String serialize(final Searchable object) throws IOException {
        Patient patient = (Patient) object;
        return patient.toString();
    }
}
