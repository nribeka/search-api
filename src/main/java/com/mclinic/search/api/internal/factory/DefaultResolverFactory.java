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

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.mclinic.search.api.resolver.Resolver;

import java.lang.reflect.Constructor;

@Singleton
public class DefaultResolverFactory extends BaseFactory<Resolver> {

    /**
     * The implementation of the base factory.
     */
    @Inject
    protected DefaultResolverFactory(final @Named("ResolverFactory.name") String implementationName) {
        super(implementationName);
    }

    /**
     * Create a constructor.
     *
     * @param resolverClass implementation class for which to create the constructor
     * @param key           the key to this implementation class
     * @return the constructor to use for creating an instance
     * @throws NoSuchMethodException in case of error
     */
    @Override
    protected Constructor<? extends Resolver> getConstructor(final Class<? extends Resolver> resolverClass,
                                                             final String key)
            throws NoSuchMethodException {
        return resolverClass.getConstructor();
    }

    /**
     * Create an implementation class instance.
     *
     * @param constructor the constructor to use for creating the instance
     * @param key         the key to differentiate this implementation class
     * @return the created instance
     * @throws Exception in case of error
     */
    @Override
    protected Resolver createInstance(final Constructor<? extends Resolver> constructor,
                                      final String key)
            throws Exception {
        return constructor.newInstance();
    }
}
