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
import com.mclinic.search.api.sample.domain.Cohort;
import com.mclinic.search.api.serialization.Algorithm;

public class CohortAlgorithm implements Algorithm {

    /**
     * Implementation of this method will define how the object will be serialized from the String representation.
     *
     * @param serialized the string representation
     * @return the concrete object
     */
    @Override
    public Object deserialize(final String serialized) {
        Cohort cohort = new Cohort();
        // TODO: remember that performing JsonPath.read() followed by toString() might get us into NPE
        Object jsonObject = JsonPath.read(serialized, "$");
        String uuid = JsonPath.read(jsonObject, "$.uuid");
        cohort.setUuid(uuid);
        String name = JsonPath.read(jsonObject, "$.display");
        cohort.setName(name);

        cohort.setJson(serialized);

        return cohort;
    }

    /**
     * Implementation of this method will define how the object will be de-serialized into the String representation.
     *
     * @param object the object
     * @return the string representation
     */
    @Override
    public String serialize(final Object object) {
        Cohort cohort = (Cohort) object;
        return cohort.getJson();
    }
}
