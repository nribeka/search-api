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
import com.mclinic.search.api.context.ServiceContext;
import com.mclinic.search.api.exception.ServiceException;
import com.mclinic.search.api.internal.file.ResourceFileFilter;
import com.mclinic.search.api.model.object.Searchable;
import com.mclinic.search.api.model.resolver.Resolver;
import com.mclinic.search.api.model.serialization.Algorithm;
import com.mclinic.search.api.module.JUnitModule;
import com.mclinic.search.api.module.SearchModule;
import com.mclinic.search.api.registry.Registry;
import com.mclinic.search.api.resource.Resource;
import com.mclinic.search.api.resource.ResourceConstants;
import com.mclinic.search.api.resource.SearchableField;
import com.mclinic.search.api.sample.algorithm.PatientAlgorithm;
import com.mclinic.search.api.sample.domain.Patient;
import com.mclinic.search.api.sample.resolver.PatientResolver;
import com.mclinic.search.api.util.ResourceUtil;
import com.mclinic.search.api.util.StringUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class ServiceContextTest {

    private ServiceContext serviceContext;

    @Before
    public void prepare() throws Exception {
        Injector injector = Guice.createInjector(new SearchModule(), new JUnitModule());
        serviceContext = injector.getInstance(ServiceContext.class);
        // register algorithms classes for the testing
        serviceContext.registerAlgorithm(new PatientAlgorithm());
        // register resolver classes for the testing
        serviceContext.registerResolver(new PatientResolver());
        // register domain object classes for the testing
        serviceContext.registerSearchable(new Patient());
    }

    /**
     * @verifies register resource object.
     * @see ServiceContext#registerResource(com.mclinic.search.api.resource.Resource)
     */
    @Test
    public void registerResource_shouldRegisterResourceObject() throws Exception {
        String resourceName = "Example Resource";

        Resource resource = Mockito.mock(Resource.class);
        Mockito.when(resource.getName()).thenReturn(resourceName);

        serviceContext.registerResource(resource);
        // check the registration process
        Assert.assertTrue(serviceContext.getResources().size() > 0);

        // check the registered resource internal property
        Resource registeredResource = serviceContext.getResource(resourceName);
        Assert.assertNotNull(registeredResource);
    }

    /**
     * @verifies not register resource without resource name.
     * @see ServiceContext#registerResource(com.mclinic.search.api.resource.Resource)
     */
    @Test(expected = ServiceException.class)
    public void registerResource_shouldNotRegisterResourceWithoutResourceName() throws Exception {
        String resourceName = null;

        Resource resource = Mockito.mock(Resource.class);
        Mockito.when(resource.getName()).thenReturn(resourceName);

        // should throw exception here
        serviceContext.registerResource(resource);
    }

    /**
     * @verifies only register resource files with j2l extension.
     * @see ServiceContext#registerResources(java.io.File)
     */
    @Test
    public void registerResources_shouldOnlyRegisterResourceFilesWithJ2lExtension() throws Exception {

        URL url = ServiceContextTest.class.getResource("sample/j2l");
        File resourceFile = new File(url.getPath());
        serviceContext.registerResources(resourceFile);

        File[] files = resourceFile.listFiles(new ResourceFileFilter());
        Assert.assertNotNull(files);
        Assert.assertEquals(files.length, serviceContext.getResources().size());
    }

    /**
     * @verifies recursively register all resources inside directory.
     * @see ServiceContext#registerResources(java.io.File)
     */
    @Test
    public void registerResources_shouldRecursivelyRegisterAllResourcesInsideDirectory() throws Exception {

        URL url = ServiceContextTest.class.getResource("sample/j2l");
        File resourceFile = new File(url.getPath());
        serviceContext.registerResources(resourceFile);

        File[] files = resourceFile.listFiles(new ResourceFileFilter());
        for (File file : files) {
            Registry<String, String> stringRegistry = ResourceUtil.readConfiguration(file);
            String resourceName = stringRegistry.getEntryValue(ResourceConstants.RESOURCE_NAME);
            Resource registeredResource = serviceContext.getResource(resourceName);
            Assert.assertNotNull(registeredResource);
            Assert.assertEquals(stringRegistry.getEntryValue(ResourceConstants.RESOURCE_ROOT_NODE),
                    registeredResource.getRootNode());
            Assert.assertTrue(Algorithm.class.isAssignableFrom(registeredResource.getAlgorithm().getClass()));
            Assert.assertTrue(Resolver.class.isAssignableFrom(registeredResource.getResolver().getClass()));

            Assert.assertEquals(stringRegistry.getEntries().size() - ResourceConstants.NON_SEARCHABLE_FIELDS.size(),
                    registeredResource.getSearchableFields().size());

            String uniqueKey = stringRegistry.getEntryValue(ResourceConstants.RESOURCE_UNIQUE_FIELD);
            List<String> uniqueKeyFields = Arrays.asList(StringUtil.split(uniqueKey, ","));
            for (SearchableField searchableField : registeredResource.getSearchableFields()) {
                if (uniqueKeyFields.contains(searchableField.getName()))
                    Assert.assertEquals(Boolean.TRUE, searchableField.isUnique());
            }
        }
    }

    /**
     * @verifies create valid resource object based on the resource file.
     * @see ServiceContext#registerResources(java.io.File)
     */
    @Test
    public void registerResources_shouldCreateValidResourceObjectBasedOnTheResourceFile() throws Exception {

        URL url = ServiceContextTest.class.getResource("sample/j2l");
        File resourceFile = new File(url.getPath());
        serviceContext.registerResources(resourceFile);

        File[] files = resourceFile.listFiles(new ResourceFileFilter());
        for (File file : files) {
            Registry<String, String> stringRegistry = ResourceUtil.readConfiguration(file);
            String resourceName = stringRegistry.getEntryValue(ResourceConstants.RESOURCE_NAME);
            Resource registeredResource = serviceContext.getResource(resourceName);
            Assert.assertNotNull(registeredResource);
            Assert.assertEquals(stringRegistry.getEntryValue(ResourceConstants.RESOURCE_ROOT_NODE),
                    registeredResource.getRootNode());
            Assert.assertTrue(Algorithm.class.isAssignableFrom(registeredResource.getAlgorithm().getClass()));
            Assert.assertTrue(Resolver.class.isAssignableFrom(registeredResource.getResolver().getClass()));

            Assert.assertEquals(stringRegistry.getEntries().size() - ResourceConstants.NON_SEARCHABLE_FIELDS.size(),
                    registeredResource.getSearchableFields().size());

            String uniqueKey = stringRegistry.getEntryValue(ResourceConstants.RESOURCE_UNIQUE_FIELD);
            List<String> uniqueKeyFields = Arrays.asList(StringUtil.split(uniqueKey, ","));
            for (SearchableField searchableField : registeredResource.getSearchableFields()) {
                if (uniqueKeyFields.contains(searchableField.getName()))
                    Assert.assertEquals(Boolean.TRUE, searchableField.isUnique());
            }
        }
    }

    /**
     * @verifies return all registered resource object.
     * @see ServiceContext#getResources()
     */
    @Test
    public void getResources_shouldReturnAllRegisteredResourceObject() throws Exception {

        URL url = ServiceContextTest.class.getResource("sample/j2l");
        File resourceFile = new File(url.getPath());
        serviceContext.registerResources(resourceFile);

        File[] files = resourceFile.listFiles(new ResourceFileFilter());
        Assert.assertNotNull(files);
        for (File file : files) {
            Registry<String, String> stringRegistry = ResourceUtil.readConfiguration(file);
            String resourceName = stringRegistry.getEntryValue(ResourceConstants.RESOURCE_NAME);
            Resource registeredResource = serviceContext.getResource(resourceName);
            Assert.assertNotNull(registeredResource);
        }
    }

    /**
     * @verifies return resource object based on the name of the resource.
     * @see ServiceContext#getResource(String)
     */
    @Test
    public void getResource_shouldReturnResourceObjectBasedOnTheNameOfTheResource() throws Exception {

        URL url = ServiceContextTest.class.getResource("sample/j2l");
        File resourceFile = new File(url.getPath());
        serviceContext.registerResources(resourceFile);

        File[] files = resourceFile.listFiles(new ResourceFileFilter());
        Assert.assertNotNull(files);
        for (File file : files) {
            Registry<String, String> stringRegistry = ResourceUtil.readConfiguration(file);
            String resourceName = stringRegistry.getEntryValue(ResourceConstants.RESOURCE_NAME);
            Resource registeredResource = serviceContext.getResource(resourceName);
            Assert.assertNotNull(registeredResource);
        }
    }

    /**
     * @verifies return removed resource object
     * @see ServiceContext#removeResource(com.mclinic.search.api.resource.Resource)
     */
    @Test
    public void removeResource_shouldReturnRemovedResourceObject() throws Exception {

        URL url = ServiceContextTest.class.getResource("sample/j2l");
        File resourceFile = new File(url.getPath());
        serviceContext.registerResources(resourceFile);

        int resourceCounter = serviceContext.getResources().size();
        Resource registeredResource = serviceContext.getResource("Patient Resource");
        Resource removedResource = serviceContext.removeResource(registeredResource);
        Assert.assertEquals(registeredResource, removedResource);

        Assert.assertEquals(resourceCounter - 1, serviceContext.getResources().size());
    }

    /**
     * @verifies register domain object using the class name.
     * @see ServiceContext#registerSearchable(com.mclinic.search.api.model.object.Searchable)
     */
    @Test
    public void registerObject_shouldRegisterDomainObjectUsingTheClassName() throws Exception {
        Searchable clazz = serviceContext.getSearchable(Patient.class.getName());
        Assert.assertNotNull(clazz);
        Assert.assertEquals(Patient.class.getName(), clazz.getClass().getName());
        serviceContext.removeSearchable(clazz);
        clazz = serviceContext.getSearchable(Patient.class.getName());
        Assert.assertNull(clazz);
    }

    /**
     * @verifies register algorithm using the class name.
     * @see ServiceContext#registerAlgorithm(com.mclinic.search.api.model.serialization.Algorithm)
     */
    @Test
    public void registerAlgorithm_shouldRegisterAlgorithmUsingTheClassName() throws Exception {
        Algorithm algorithm = serviceContext.getAlgorithm(PatientAlgorithm.class.getName());
        Assert.assertNotNull(algorithm);
        Assert.assertEquals(PatientAlgorithm.class.getName(), algorithm.getClass().getName());
    }

    /**
     * @verifies register resolver using the class name.
     * @see ServiceContext#registerResolver(com.mclinic.search.api.model.resolver.Resolver)
     */
    @Test
    public void registerResolver_shouldRegisterResolverUsingTheClassName() throws Exception {
        Resolver resolver = serviceContext.getResolver(PatientResolver.class.getName());
        Assert.assertNotNull(resolver);
        Assert.assertEquals(PatientResolver.class.getName(), resolver.getClass().getName());
    }
}
