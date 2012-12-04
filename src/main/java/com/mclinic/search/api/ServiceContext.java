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

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mclinic.search.api.internal.factory.Factory;
import com.mclinic.search.api.internal.file.ResourceFileFilter;
import com.mclinic.search.api.registry.Registry;
import com.mclinic.search.api.resolver.Resolver;
import com.mclinic.search.api.resource.ObjectResource;
import com.mclinic.search.api.resource.Resource;
import com.mclinic.search.api.resource.ResourceConstants;
import com.mclinic.search.api.serialization.Algorithm;
import com.mclinic.search.api.util.ResourceUtil;
import com.mclinic.search.api.util.StringUtil;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Singleton
public class ServiceContext {

    @Inject
    private Registry<String, Class> classRegistry;

    @Inject
    private Registry<String, Resource> resourceRegistry;

    @Inject
    private Registry<String, String> digestRegistry;

    @Inject
    private Factory<Resolver> resolverFactory;

    @Inject
    private Factory<Algorithm> algorithmFactory;

    @Inject
    private RestAssuredService restAssuredService;

    public RestAssuredService getRestAssuredService() {
        return restAssuredService;
    }

    /**
     * Register a new resource object for future use.
     *
     * @param resource the resource to be registered.
     * @should register programmatically created resource object.
     * @should not register resource without resource name.
     */
    public void registerResource(final Resource resource) {
        if (resource != null && resource.getName() != null)
            resourceRegistry.putEntry(resource.getName(), resource);
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
    public void registerResources(final File file) throws IOException {
        FileFilter fileFilter = new ResourceFileFilter();
        if (!file.isDirectory() && fileFilter.accept(file)) {
            registerResource(createResource(file));
        } else {
            File[] files = file.listFiles(fileFilter);
            if (files != null) {
                for (File resourceFile : files)
                    registerResources(resourceFile);
            }
        }
    }

    /**
     * Internal method to convert the actual resource file into the resource object.
     *
     * @param file the file
     * @return the resource object
     * @throws IOException when the parser fail to read the configuration file
     */
    private Resource createResource(final File file) throws IOException {

        // TODO: see this gist to prevent re-reading the same resource file if it's already registered
        // https://gist.github.com/3998818

        Registry<String, String> properties = ResourceUtil.readConfiguration(file);
        String resourceName = properties.getEntryValue(ResourceConstants.RESOURCE_NAME);

        String rootNode = properties.getEntryValue(ResourceConstants.RESOURCE_ROOT_NODE);

        String objectClassKey = properties.getEntryValue(ResourceConstants.RESOURCE_OBJECT);
        Class objectClass = classRegistry.getEntryValue(objectClassKey);

        String algorithmKey = properties.getEntryValue(ResourceConstants.RESOURCE_ALGORITHM_CLASS);
        Algorithm algorithm = algorithmFactory.createImplementation(algorithmKey);

        String resolverKey = properties.getEntryValue(ResourceConstants.RESOURCE_URI_RESOLVER_CLASS);
        Resolver resolver = resolverFactory.createImplementation(resolverKey);

        Resource resource = new ObjectResource(resourceName, rootNode, objectClass, algorithm, resolver);

        Object uniqueField = properties.getEntryValue(ResourceConstants.RESOURCE_UNIQUE_FIELD);
        List<String> uniqueFields = new ArrayList<String>();
        if (uniqueField != null)
            uniqueFields = Arrays.asList(StringUtil.split(uniqueField.toString(), ","));

        List<String> ignoredField = ResourceConstants.NON_SEARCHABLE_FIELDS;
        Map<String, String> entries = properties.getEntries();
        for (String fieldName : entries.keySet()) {
            if (!ignoredField.contains(fieldName)) {
                Boolean unique = Boolean.FALSE;
                if (uniqueFields.contains(fieldName))
                    unique = Boolean.TRUE;
                resource.addFieldDefinition(fieldName, entries.get(fieldName), unique);
            }
        }

        return resource;
    }

    /**
     * Get all registered resources from the resource registry.
     *
     * @return all registered resources.
     * @should return all registered resource object.
     */
    public Collection<Resource> getResources() {
        return resourceRegistry.getEntries().values();
    }

    /**
     * Get resource with the name denoted by the parameter.
     *
     * @param name the name of the resource
     * @return the matching resource object or null if no resource match have the matching name.
     * @should return resource object based on the name of the resource.
     */
    public Resource getResource(final String name) {
        return resourceRegistry.getEntryValue(name);
    }

    /**
     * Remove a resource from the resource registry.
     *
     * @param resource the resource to be removed
     * @return the removed resource or null if no resource was removed
     * @should return removed resource object
     */
    public Resource removeResource(final Resource resource) {
        return resourceRegistry.removeEntry(resource.getName());
    }

    /**
     * Register all domain object classes.
     *
     * @param classes the domain object classes.
     * @should register all domain object classes in the domain object registry.
     */
    public void registerObject(final Class<?>... classes) {
        for (Class<?> clazz : classes)
            classRegistry.putEntry(clazz.getName(), clazz);
    }

    public Class<?> removeObject(final Class<?> clazz) {
        return classRegistry.removeEntry(clazz.getName());
    }

    /**
     * Register all algorithm classes.
     *
     * @param algorithms the algorithm classes.
     * @should register all algorithm classes in the algorithm registry.
     */
    public void registerAlgorithm(final Class<? extends Algorithm>... algorithms) {
        for (Class<? extends Algorithm> algorithm : algorithms)
            algorithmFactory.registerImplementation(algorithm.getName(), algorithm);
    }

    public Class<? extends Algorithm> removeAlgorithm(final Class<? extends Algorithm> algorithm) {
        return algorithmFactory.getMapping(algorithm.getName());
    }

    /**
     * Register all resolver classes
     *
     * @param resolvers the resolver classes
     * @should register all resolver classes in the resolve registry.
     */
    public void registerResolver(final Class<? extends Resolver>... resolvers) {
        for (Class<? extends Resolver> resolver : resolvers)
            resolverFactory.registerImplementation(resolver.getName(), resolver);
    }

    public Class<? extends Resolver> removeResolver(final Class<? extends Resolver> resolver) {
        return resolverFactory.getMapping(resolver.getName());
    }
}
