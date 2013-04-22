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

package com.mclinic.search.api;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;
import com.mclinic.search.api.context.ServiceContext;
import com.mclinic.search.api.exception.ServiceException;
import com.mclinic.search.api.module.SearchModule;
import com.mclinic.search.api.model.object.Searchable;
import com.mclinic.search.api.model.resolver.Resolver;
import com.mclinic.search.api.resource.Resource;
import com.mclinic.search.api.model.serialization.Algorithm;
import com.mclinic.search.api.service.RestAssuredService;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public class Context {

    private static Injector injector;

    public static void initialize(final Module... modules) {
        injector = Guice.createInjector(new SearchModule(), Modules.combine(modules));
    }

    private static ServiceContext getServiceContext() {
        return injector.getInstance(ServiceContext.class);
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
     * Get the service layer for the search api. The service layer is the layer where consumer can perform load, get and
     * search.
     *
     * @return the service layer.
     */
    public static RestAssuredService getService() {
        return getServiceContext().getService();
    }

    /**
     * Register a new resource object for future use.
     *
     * @param resource the resource to be registered.
     * @should register the resource object.
     * @should not register resource without resource name.
     * @should not register null object.
     */
    public static void registerResource(final Resource resource) {
        getServiceContext().registerResource(resource);
    }

    /**
     * Read the input file and then convert each file into resource object and register them.
     *
     * @param file the file (could be a directory too).
     * @throws IOException when the parser fail to read the configuration file.
     * @should recursively register all resources inside directory.
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
     * Register domain object which can be used to create a new resource object.
     *
     * @param searchable the domain object.
     * @should register domain object using the class name.
     */
    public static void registerObject(final Searchable searchable) throws ServiceException {
        getServiceContext().registerObject(searchable);
    }

    public static Searchable getObject(final String name) {
        return getServiceContext().getObject(name);
    }

    public static Searchable removeObject(final Searchable searchable) {
        return getServiceContext().removeObject(searchable);
    }

    public boolean containsObject(final Searchable searchable) {
        return getServiceContext().containsObject(searchable);
    }

    /**
     * Register algorithm which can be used to create a new resource object.
     *
     * @param algorithm the algorithm.
     * @should register algorithm using the class name.
     */
    public static void registerAlgorithm(final Algorithm algorithm) throws ServiceException {
        getServiceContext().registerAlgorithm(algorithm);
    }

    public static Algorithm getAlgorithm(final String name) {
        return getServiceContext().getAlgorithm(name);
    }

    public static Algorithm removeAlgorithm(final Algorithm algorithm) {
        return getServiceContext().removeAlgorithm(algorithm);
    }

    public boolean containsAlgorithm(final Algorithm algorithm) {
        return getServiceContext().containsAlgorithm(algorithm);
    }

    /**
     * Register resolver which can be used to create a new resource object.
     *
     * @param resolver the resolver
     * @should register resolver using the class name.
     */
    public static void registerResolver(final Resolver resolver) throws ServiceException {
        getServiceContext().registerResolver(resolver);
    }

    public static Resolver getResolver(final String name) {
        return getServiceContext().getResolver(name);
    }

    public static Resolver removeResolver(final Resolver resolver) {
        return getServiceContext().removeResolver(resolver);
    }

    public boolean containsResolver(final Resolver resolver) {
        return getServiceContext().containsResolver(resolver);
    }
}
