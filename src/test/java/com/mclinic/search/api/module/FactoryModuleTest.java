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
package com.mclinic.search.api.module;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.mclinic.search.api.internal.factory.Factory;
import com.mclinic.search.api.registry.Registry;
import com.mclinic.search.api.resolver.Resolver;
import com.mclinic.search.api.resource.Resource;
import com.mclinic.search.api.sample.resolver.CohortResolver;
import com.mclinic.search.api.serialization.Algorithm;
import junit.framework.Assert;
import org.junit.Test;

public class FactoryModuleTest {

    /**
     * @verifies bind instances of factory and string
     * @see com.mclinic.search.api.module.FactoryModule#configure()
     */
    @Test
    public void configure_shouldBindInstancesOfFactoryAndString() throws Exception {
        Injector injector = Guice.createInjector(new FactoryModule());

        Registry<String, String> stringRegistry = injector.getInstance(
                Key.get(new TypeLiteral<Registry<String, String>>() {}));
        Assert.assertNotNull(stringRegistry);

        Registry<String, Resource> resourceRegistry = injector.getInstance(
                Key.get(new TypeLiteral<Registry<String, Resource>>() {}));
        Assert.assertNotNull(resourceRegistry);

        Registry<String, Class> classRegistry = injector.getInstance(
                Key.get(new TypeLiteral<Registry<String, Class>>() {}));
        Assert.assertNotNull(classRegistry);

        Factory<Algorithm> algorithmFactory = injector.getInstance(
                Key.get(new TypeLiteral<Factory<Algorithm>>() {}));
        Assert.assertNotNull(algorithmFactory);

        Factory<Resolver> resolverFactory = injector.getInstance(
                Key.get(new TypeLiteral<Factory<Resolver>>() {}));
        Assert.assertNotNull(resolverFactory);
    }

    /**
     * @verifies bind instances as singleton when specified
     * @see FactoryModule#configure()
     */
    @Test
    public void configure_shouldBindInstancesAsSingletonWhenSpecified() throws Exception {
        // Singleton factory
        Resolver resolver;
        Factory<Resolver> resolverFactory;

        Injector injector = Guice.createInjector(new FactoryModule());

        resolverFactory = injector.getInstance(Key.get(new TypeLiteral<Factory<Resolver>>() {}));
        Assert.assertNotNull(resolverFactory);
        resolverFactory.registerImplementation(CohortResolver.class.getName(), CohortResolver.class);
        Assert.assertTrue(resolverFactory.hasMapping(CohortResolver.class.getName()));

        resolver = resolverFactory.createImplementation(CohortResolver.class.getName());
        Assert.assertNotNull(resolver);

        resolverFactory = injector.getInstance(Key.get(new TypeLiteral<Factory<Resolver>>() {}));
        Assert.assertNotNull(resolverFactory);
        resolverFactory.registerImplementation(CohortResolver.class.getName(), CohortResolver.class);
        Assert.assertTrue(resolverFactory.hasMapping(CohortResolver.class.getName()));

        resolver = resolverFactory.createImplementation(CohortResolver.class.getName());
        Assert.assertNotNull(resolver);

        // Singleton registry
        Registry<String, String> stringRegistry;
        stringRegistry = injector.getInstance(Key.get(new TypeLiteral<Registry<String, String>>() {}));
        Assert.assertNotNull(stringRegistry);

        stringRegistry.putEntry("Example Entry", "Example Value");
        Assert.assertTrue(stringRegistry.hasEntry("Example Entry"));
        Assert.assertEquals("Example Value", stringRegistry.getEntryValue("Example Entry"));

        stringRegistry = injector.getInstance(Key.get(new TypeLiteral<Registry<String, String>>() {}));
        Assert.assertTrue(stringRegistry.hasEntry("Example Entry"));
        Assert.assertEquals("Example Value", stringRegistry.getEntryValue("Example Entry"));
    }
}
