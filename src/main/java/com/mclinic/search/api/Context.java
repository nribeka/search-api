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
package com.mclinic.search.api;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;
import com.mclinic.search.api.module.FactoryModule;
import com.mclinic.search.api.module.SearchModule;
import com.mclinic.search.api.resolver.Resolver;
import com.mclinic.search.api.resource.Resource;
import com.mclinic.search.api.serialization.Algorithm;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public class Context {

    private static Injector injector;

    private static ServiceContext serviceContext;

    private static ServiceContext getServiceContext() {
        return serviceContext;
    }

    public static void initialize(final Module... modules) {
        injector = Guice.createInjector(new SearchModule(), new FactoryModule(), Modules.combine(modules));
        serviceContext = injector.getInstance(ServiceContext.class);
    }

    /**
     * Get an instance of a registered component inside this guice framework. This call will be delegated to the
     * internal injector implementation.
     *
     * @param clazz the type of the registered component
     * @param <T>   the generic type of the object
     * @return the object implementation
     */
    public static <T> T getInstance(final Class<T> clazz) {
        return injector.getInstance(clazz);
    }

    /**
     * Get the service layer for the search api. The service layer is the layer where consumer can perform load and
     * searches.
     *
     * @return the service layer.
     */
    public static RestAssuredService getService() {
        return getServiceContext().getRestAssuredService();
    }

    /**
     * Register a new resource object for future use.
     *
     * @param resource the resource to be registered.
     * @should register programmatically created resource object.
     * @should not register resource without resource name.
     */
    public static void registerResource(final Resource resource) {
        getServiceContext().registerResource(resource);
    }

    /**
     * Read the input file and then convert each file into resource object and register them.
     *
     * @param file the file (could be a directory too).
     * @throws IOException when the parser fail to read the configuration file.
     * @shoud recursively register all resources inside directory.
     * @should only register resource files with j2l extension.
     * @should create valid resource object based on the resource file.
     */
    public static void registerResources(final File file) throws IOException {
        getServiceContext().registerResources(file);
    }

    /**
     * Get all registered resources from the resource registry.
     *
     * @return all registered resources.
     * @should return all registered resource object.
     */
    public static Collection<Resource> getResources() {
        return getServiceContext().getResources();
    }

    /**
     * Get resource with the name denoted by the parameter.
     *
     * @param name the name of the resource
     * @return the matching resource object or null if no resource match have the matching name.
     * @should return resource object based on the name of the resource.
     */
    public static Resource getResource(final String name) {
        return getServiceContext().getResource(name);
    }

    /**
     * Remove a resource from the resource registry.
     *
     * @param resource the resource to be removed
     * @return the removed resource or null if no resource was removed
     * @should return removed resource object
     */
    public static Resource removeResource(final Resource resource) {
        return getServiceContext().removeResource(resource);
    }

    /**
     * Register all domain object classes.
     *
     * @param classes the domain object classes.
     * @should register all domain object classes in the domain object registry.
     */
    public static void registerObject(final Class<?>... classes) {
        getServiceContext().registerObject(classes);
    }

    public static Class<?> removeObject(final Class<?> clazz) {
        return getServiceContext().removeObject(clazz);
    }

    /**
     * Register all algorithm classes.
     *
     * @param algorithms the algorithm classes.
     * @should register all algorithm classes in the algorithm registry.
     */
    public static void registerAlgorithm(final Class<? extends Algorithm>... algorithms) {
        getServiceContext().registerAlgorithm(algorithms);
    }

    public static Class<? extends Algorithm> removeAlgorithm(final Class<? extends Algorithm> algorithm) {
        return getServiceContext().removeAlgorithm(algorithm);
    }

    /**
     * Register all resolver classes
     *
     * @param resolvers the resolver classes
     * @should register all resolver classes in the resolve registry.
     */
    public static void registerResolver(final Class<? extends Resolver>... resolvers) {
        getServiceContext().registerResolver(resolvers);
    }

    public static Class<? extends Resolver> removeResolver(final Class<? extends Resolver> resolver) {
        return getServiceContext().removeResolver(resolver);
    }
}
