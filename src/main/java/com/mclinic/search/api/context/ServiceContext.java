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

package com.mclinic.search.api.context;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mclinic.search.api.exception.ServiceException;
import com.mclinic.search.api.internal.file.ResourceFileFilter;
import com.mclinic.search.api.model.object.Searchable;
import com.mclinic.search.api.registry.DefaultRegistry;
import com.mclinic.search.api.registry.Registry;
import com.mclinic.search.api.model.resolver.Resolver;
import com.mclinic.search.api.resource.ObjectResource;
import com.mclinic.search.api.resource.Resource;
import com.mclinic.search.api.resource.ResourceConstants;
import com.mclinic.search.api.model.serialization.Algorithm;
import com.mclinic.search.api.service.RestAssuredService;
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
public final class ServiceContext {

    private Registry<String, Searchable> objectRegistry;

    private Registry<String, Algorithm> algorithmRegistry;

    private Registry<String, Resolver> resolverRegistry;

    @Inject
    private Registry<String, Resource> resourceRegistry;

    @Inject
    private RestAssuredService service;

    protected ServiceContext() {
        objectRegistry = new DefaultRegistry<String, Searchable>();
        algorithmRegistry = new DefaultRegistry<String, Algorithm>();
        resolverRegistry = new DefaultRegistry<String, Resolver>();
    }

    private Registry<String, Searchable> getObjectRegistry() {
        return objectRegistry;
    }

    private Registry<String, Algorithm> getAlgorithmRegistry() {
        return algorithmRegistry;
    }

    private Registry<String, Resolver> getResolverRegistry() {
        return resolverRegistry;
    }

    private Registry<String, Resource> getResourceRegistry() {
        return resourceRegistry;
    }

    public RestAssuredService getService() {
        return service;
    }

    /**
     * Register a new resource object.
     *
     * @param name     the name of the resource (will be used to retrieve the resource later on).
     * @param resource the resource object.
     * @throws ServiceException when name or resource is invalid (null).
     */
    protected void registerResource(final String name, final Resource resource) throws ServiceException {
        if (StringUtil.isBlank(name))
            throw new ServiceException("Trying to register resource without handle.");

        if (resource == null)
            throw new ServiceException("Trying to register invalid resource object.");

        getResourceRegistry().putEntry(name, resource);
    }

    /**
     * Register a new resource object for future use.
     *
     * @param resource the resource to be registered.
     * @should register resource object.
     * @should not register resource without resource name.
     */
    public void registerResource(final Resource resource) throws ServiceException {
        registerResource(resource.getName(), resource);
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
    public void registerResources(final File file) throws IOException, ServiceException {
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
    private Resource createResource(final File file) throws IOException, ServiceException {

        // TODO: see this gist to prevent re-reading the same resource file if it's already registered
        // https://gist.github.com/3998818

        Registry<String, String> properties = ResourceUtil.readConfiguration(file);
        String resourceName = properties.getEntryValue(ResourceConstants.RESOURCE_NAME);

        String rootNode = properties.getEntryValue(ResourceConstants.RESOURCE_ROOT_NODE);
        if (StringUtil.isBlank(rootNode))
            throw new ServiceException("Unable to create resource because of missing root node definition.");

        String objectClassKey = properties.getEntryValue(ResourceConstants.RESOURCE_SEARCHABLE);
        Searchable searchable = getObjectRegistry().getEntryValue(objectClassKey);
        if (searchable == null)
            throw new ServiceException("Unable to create resource because of missing rest assured object.");

        String algorithmKey = properties.getEntryValue(ResourceConstants.RESOURCE_ALGORITHM_CLASS);
        Algorithm algorithm = getAlgorithmRegistry().getEntryValue(algorithmKey);
        if (algorithm == null)
            throw new ServiceException("Unable to create resource because of missing algorithm object.");

        String resolverKey = properties.getEntryValue(ResourceConstants.RESOURCE_URI_RESOLVER_CLASS);
        Resolver resolver = getResolverRegistry().getEntryValue(resolverKey);
        if (resolver == null)
            throw new ServiceException("Unable to create resource because of missing resolver object.");

        Resource resource = new ObjectResource(resourceName, rootNode, searchable.getClass(), algorithm, resolver);

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
        return getResourceRegistry().getEntries().values();
    }

    /**
     * Get resource with the name denoted by the parameter.
     *
     * @param name the name of the resource
     * @return the matching resource object or null if no resource match have the matching name.
     * @should return resource object based on the name of the resource.
     */
    public Resource getResource(final String name) {
        return getResourceRegistry().getEntryValue(name);
    }

    /**
     * Remove a resource from the resource registry.
     *
     * @param resource the resource to be removed
     * @return the removed resource or null if no resource was removed
     * @should return removed resource object
     */
    public Resource removeResource(final Resource resource) {
        return getResourceRegistry().removeEntry(resource.getName());
    }

    /**
     * Register domain object which can be used to create a new resource object.
     *
     * @param searchable the domain object.
     * @should register domain object using the class name.
     */
    public void registerObject(final Searchable searchable) throws ServiceException {
        if (searchable == null)
            throw new ServiceException("Trying to register invalid domain object.");

        getObjectRegistry().putEntry(searchable.getClass().getName(), searchable);
    }

    public Searchable getObject(final String name) {
        return getObjectRegistry().getEntryValue(name);
    }

    public Searchable removeObject(final Searchable searchable) {
        return getObjectRegistry().removeEntry(searchable.getClass().getName());
    }

    public boolean containsObject(final Searchable searchable) {
        return getObjectRegistry().hasEntry(searchable.getClass().getName());
    }

    /**
     * Register algorithm which can be used to create a new resource object.
     *
     * @param algorithm the algorithm.
     * @should register algorithm using the class name.
     */
    public void registerAlgorithm(final Algorithm algorithm) throws ServiceException {
        if (algorithm == null)
            throw new ServiceException("Trying to register invalid algorithm object.");

        getAlgorithmRegistry().putEntry(algorithm.getClass().getName(), algorithm);
    }

    public Algorithm getAlgorithm(final String name) {
        return getAlgorithmRegistry().getEntryValue(name);
    }

    public Algorithm removeAlgorithm(final Algorithm algorithm) {
        return getAlgorithmRegistry().removeEntry(algorithm.getClass().getName());
    }

    public boolean containsAlgorithm(final Algorithm algorithm) {
        return getAlgorithmRegistry().hasEntry(algorithm.getClass().getName());
    }

    /**
     * Register resolver which can be used to create a new resource object.
     *
     * @param resolver the resolver
     * @should register resolver using the class name.
     */
    public void registerResolver(final Resolver resolver) {
        getResolverRegistry().putEntry(resolver.getClass().getName(), resolver);
    }

    public Resolver getResolver(final String name) {
        return getResolverRegistry().getEntryValue(name);
    }

    public Resolver removeResolver(final Resolver resolver) {
        return getResolverRegistry().removeEntry(resolver.getClass().getName());
    }

    public boolean containsResolver(final Resolver resolver) {
        return getResolverRegistry().hasEntry(resolver.getClass().getName());
    }
}
