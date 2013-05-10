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
package com.mclinic.search.api.filter;

/**
 * TODO: Write brief description about the class here.
 */
public class Filter {

    private String fieldName;

    private String fieldValue;

    /**
     * Get the filtered field's name.
     *
     * @return the filtered field's name.
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Set  the filtered field's name.
     *
     * @param fieldName the filtered field's name.
     */
    public void setFieldName(final String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * Get the filtered field's value.
     *
     * @return the filtered field's value.
     */
    public String getFieldValue() {
        return fieldValue;
    }

    /**
     * Set the filtered field's value.
     *
     * @param fieldValue the filtered field's value.
     */
    public void setFieldValue(final String fieldValue) {
        this.fieldValue = fieldValue;
    }
}
