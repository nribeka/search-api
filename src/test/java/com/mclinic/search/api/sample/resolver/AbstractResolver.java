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

package com.mclinic.search.api.sample.resolver;

import com.mclinic.search.api.model.resolver.BaseResolver;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import java.net.URLConnection;

public abstract class AbstractResolver extends BaseResolver {

    protected final String WEB_SERVER = "http://localhost:8081/";

    protected final String WEB_CONTEXT = "openmrs-standalone/";

    @Override
    public URLConnection authenticate(final URLConnection connection) {
        String basicAuth = Base64.encode("admin:est".getBytes());
        connection.setRequestProperty("Authorization", basicAuth);
        return connection;
    }
}
