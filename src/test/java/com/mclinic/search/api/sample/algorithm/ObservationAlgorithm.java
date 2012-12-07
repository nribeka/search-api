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
import com.mclinic.search.api.sample.domain.Observation;
import com.mclinic.search.api.serialization.Algorithm;
import com.mclinic.search.api.util.ISO8601Util;
import net.minidev.json.JSONObject;

import java.text.ParseException;

public class ObservationAlgorithm implements Algorithm {

    /**
     * Implementation of this method will define how the object will be serialized from the String representation.
     *
     * @param serialized the string representation
     * @return the concrete object
     */
    @Override
    public Object deserialize(final String serialized) {
        Observation observation = new Observation();

        // get the full json object representation and then pass this around to the next JsonPath.read()
        // this should minimize the time for the subsequent read() call
        Object jsonObject = JsonPath.read(serialized, "$");

        String uuid = JsonPath.read(jsonObject, "$.uuid");
        observation.setUuid(uuid);

        String patient = JsonPath.read(jsonObject, "$.person.uuid");
        observation.setPatient(patient);

        String conceptName = JsonPath.read(jsonObject, "$.concept.display");
        observation.setFieldName(conceptName);

        String conceptUuid = JsonPath.read(jsonObject, "$.concept.uuid");
        observation.setFieldUuid(conceptUuid);

        Object jsonValue = JsonPath.read(jsonObject, "$.value");
        String value = jsonValue.toString();
        if (jsonValue instanceof JSONObject)
            value = JsonPath.read(jsonValue, "$.name.display");
        observation.setValueText(value);

        String obsDatetime = JsonPath.read(jsonObject, "$.obsDatetime");
        try {
            observation.setObservationDate(ISO8601Util.toCalendar(obsDatetime).getTime());
        } catch (ParseException e) {
            // suppress the exception
        }

        observation.setJson(serialized);

        return observation;
    }

    /**
     * Implementation of this method will define how the object will be de-serialized into the String representation.
     *
     * @param object the object
     * @return the string representation
     */
    @Override
    public String serialize(final Object object) {
        Observation observation = (Observation) object;
        return observation.getJson();
    }
}
