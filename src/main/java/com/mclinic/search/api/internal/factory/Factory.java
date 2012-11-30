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
