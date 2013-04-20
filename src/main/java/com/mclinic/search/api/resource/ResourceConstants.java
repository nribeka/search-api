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

package com.mclinic.search.api.resource;

import java.util.Arrays;
import java.util.List;

public class ResourceConstants {

    public static final String RESOURCE_NAME = "resource.name";

    public static final String RESOURCE_SEARCHABLE = "resource.object";

    public static final String RESOURCE_ROOT_NODE = "node.root";

    public static final String RESOURCE_UNIQUE_FIELD = "field.unique";

    public static final String RESOURCE_ALGORITHM_CLASS = "algorithm.class";

    public static final String RESOURCE_URI_RESOLVER_CLASS = "resolver.class";

    public static final List<String> NON_SEARCHABLE_FIELDS = Arrays.asList(RESOURCE_NAME, RESOURCE_SEARCHABLE,
            RESOURCE_ROOT_NODE, RESOURCE_UNIQUE_FIELD, RESOURCE_ALGORITHM_CLASS, RESOURCE_URI_RESOLVER_CLASS);

    public static final List<String> MANDATORY_FIELDS = Arrays.asList(RESOURCE_NAME, RESOURCE_SEARCHABLE,
            RESOURCE_ROOT_NODE, RESOURCE_ALGORITHM_CLASS, RESOURCE_URI_RESOLVER_CLASS);
}
