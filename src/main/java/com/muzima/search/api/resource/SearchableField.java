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

package com.muzima.search.api.resource;

public class SearchableField {

    private final String name;

    private final String expression;

    private final Boolean unique;

    public SearchableField(final String name, final String expression, final Boolean unique) {
        this.name = name;
        this.expression = expression;
        this.unique = unique;
    }

    /**
     * Get the name of the searchable field.
     *
     * @return the searchable field's name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the expression to get the actual value of the searchable field.
     *
     * @return expression to get the value of the searchable value
     */
    public String getExpression() {
        return expression;
    }

    /**
     * Flag to determine whether the value of this field would be unique for a resource.
     *
     * @return true if the value of the this field should be unique for this resource
     */
    public Boolean isUnique() {
        return unique;
    }
}
