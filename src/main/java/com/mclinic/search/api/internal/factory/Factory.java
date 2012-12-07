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

package com.mclinic.search.api.internal.factory;

import java.util.List;
import java.util.Map;

public interface Factory<T> {

    /**
     * @param key the key associated with the implementation class
     * @return true if the mapping is already registered or false otherwise
     */
    boolean hasMapping(final String key);

    /**
     * @param key the key associated with the implementation class to return
     * @return the implementation class
     */
    Class<? extends T> getMapping(final String key);

    /**
     * @return the mappings indexed using the key
     */
    Map<String, Class<? extends T>> getMappings();

    /**
     * Register an implementation class for a given key.
     *
     * @param key                 the key under which to register the implementation class
     * @param implementationClass the implementation class to register
     */
    void registerImplementation(final String key, final Class<? extends T> implementationClass);

    /**
     * Generic method to create an implementation based on the registered implementation classes.
     *
     * @param key the key under which the implementation class is registered
     * @return the created instance
     */
    T createImplementation(final String key);

    /**
     * @return the keys that have been registered for this factory
     */
    List<String> getKeys();
}
