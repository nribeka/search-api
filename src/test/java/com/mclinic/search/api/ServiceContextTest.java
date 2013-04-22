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

import com.mclinic.search.api.exception.ServiceException;
import com.mclinic.search.api.internal.file.ResourceFileFilter;
import com.mclinic.search.api.module.JUnitModule;
import com.mclinic.search.api.model.object.Searchable;
import com.mclinic.search.api.registry.Registry;
import com.mclinic.search.api.model.resolver.Resolver;
import com.mclinic.search.api.resource.Resource;
import com.mclinic.search.api.resource.ResourceConstants;
import com.mclinic.search.api.resource.SearchableField;
import com.mclinic.search.api.sample.algorithm.CohortAlgorithm;
import com.mclinic.search.api.sample.algorithm.CohortMemberAlgorithm;
import com.mclinic.search.api.sample.algorithm.ObservationAlgorithm;
import com.mclinic.search.api.sample.algorithm.PatientAlgorithm;
import com.mclinic.search.api.sample.domain.Cohort;
import com.mclinic.search.api.sample.domain.CohortMember;
import com.mclinic.search.api.sample.domain.Observation;
import com.mclinic.search.api.sample.domain.Patient;
import com.mclinic.search.api.sample.resolver.CohortMemberResolver;
import com.mclinic.search.api.sample.resolver.CohortResolver;
import com.mclinic.search.api.sample.resolver.ObservationResolver;
import com.mclinic.search.api.sample.resolver.PatientResolver;
import com.mclinic.search.api.model.serialization.Algorithm;
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

    @Before
    public void prepare() throws Exception {
        // initialize context object with additional parameters from the junit module
        Context.initialize(new JUnitModule());

        // register algorithms classes for the testing
        Context.registerAlgorithm(new PatientAlgorithm());
        Context.registerAlgorithm(new CohortAlgorithm());
        Context.registerAlgorithm(new CohortMemberAlgorithm());
        Context.registerAlgorithm(new ObservationAlgorithm());

        // register resolver classes for the testing
        Context.registerResolver(new PatientResolver());
        Context.registerResolver(new CohortResolver());
        Context.registerResolver(new CohortMemberResolver());
        Context.registerResolver(new ObservationResolver());

        // register domain object classes for the testing
        Context.registerObject(new Patient());
        Context.registerObject(new Cohort());
        Context.registerObject(new CohortMember());
        Context.registerObject(new Observation());
    }

    /**
     * @verifies register resource object.
     * @see com.mclinic.search.api.context.ServiceContext#registerResource(com.mclinic.search.api.resource.Resource)
     */
    @Test
    public void registerResource_shouldRegisterResourceObject() throws Exception {
        String resourceName = "Example Resource";

        Resource resource = Mockito.mock(Resource.class);
        Mockito.when(resource.getName()).thenReturn(resourceName);

        Context.initialize(new JUnitModule());
        Context.registerResource(resource);
        // check the registration process
        Assert.assertTrue(Context.getResources().size() > 0);

        // check the registered resource internal property
        Resource registeredResource = Context.getResource(resourceName);
        Assert.assertNotNull(registeredResource);
    }

    /**
     * @verifies not register resource without resource name.
     * @see Context#registerResource(com.mclinic.search.api.resource.Resource)
     */
    @Test(expected = ServiceException.class)
    public void registerResource_shouldNotRegisterResourceWithoutResourceName() throws Exception {
        String resourceName = null;

        Resource resource = Mockito.mock(Resource.class);
        Mockito.when(resource.getName()).thenReturn(resourceName);

        Context.initialize(new JUnitModule());
        // should throw exception here
        Context.registerResource(resource);
    }

    /**
     * @verifies only register resource files with j2l extension.
     * @see Context#registerResources(java.io.File)
     */
    @Test
    public void registerResources_shouldOnlyRegisterResourceFilesWithJ2lExtension() throws Exception {

        URL url = Context.class.getResource("sample/j2l");
        File resourceFile = new File(url.getPath());
        Context.registerResources(resourceFile);

        File[] files = resourceFile.listFiles(new ResourceFileFilter());
        Assert.assertNotNull(files);
        Assert.assertEquals(files.length, Context.getResources().size());
    }

    /**
     * @verifies recursively register all resources inside directory.
     * @see com.mclinic.search.api.context.ServiceContext#registerResources(java.io.File)
     */
    @Test
    public void registerResources_shouldRecursivelyRegisterAllResourcesInsideDirectory() throws Exception {

        URL url = Context.class.getResource("sample/j2l");
        File resourceFile = new File(url.getPath());
        Context.registerResources(resourceFile);

        File[] files = resourceFile.listFiles(new ResourceFileFilter());
        for (File file : files) {
            Registry<String, String> stringRegistry = ResourceUtil.readConfiguration(file);
            String resourceName = stringRegistry.getEntryValue(ResourceConstants.RESOURCE_NAME);
            Resource registeredResource = Context.getResource(resourceName);
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
     * @see Context#registerResources(java.io.File)
     */
    @Test
    public void registerResources_shouldCreateValidResourceObjectBasedOnTheResourceFile() throws Exception {

        URL url = Context.class.getResource("sample/j2l");
        File resourceFile = new File(url.getPath());
        Context.registerResources(resourceFile);

        File[] files = resourceFile.listFiles(new ResourceFileFilter());
        for (File file : files) {
            Registry<String, String> stringRegistry = ResourceUtil.readConfiguration(file);
            String resourceName = stringRegistry.getEntryValue(ResourceConstants.RESOURCE_NAME);
            Resource registeredResource = Context.getResource(resourceName);
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
     * @see Context#getResources()
     */
    @Test
    public void getResources_shouldReturnAllRegisteredResourceObject() throws Exception {

        URL url = Context.class.getResource("sample/j2l");
        File resourceFile = new File(url.getPath());
        Context.registerResources(resourceFile);

        File[] files = resourceFile.listFiles(new ResourceFileFilter());
        Assert.assertNotNull(files);
        for (File file : files) {
            Registry<String, String> stringRegistry = ResourceUtil.readConfiguration(file);
            String resourceName = stringRegistry.getEntryValue(ResourceConstants.RESOURCE_NAME);
            Resource registeredResource = Context.getResource(resourceName);
            Assert.assertNotNull(registeredResource);
        }
    }

    /**
     * @verifies return resource object based on the name of the resource.
     * @see Context#getResource(String)
     */
    @Test
    public void getResource_shouldReturnResourceObjectBasedOnTheNameOfTheResource() throws Exception {

        URL url = Context.class.getResource("sample/j2l");
        File resourceFile = new File(url.getPath());
        Context.registerResources(resourceFile);

        File[] files = resourceFile.listFiles(new ResourceFileFilter());
        Assert.assertNotNull(files);
        for (File file : files) {
            Registry<String, String> stringRegistry = ResourceUtil.readConfiguration(file);
            String resourceName = stringRegistry.getEntryValue(ResourceConstants.RESOURCE_NAME);
            Resource registeredResource = Context.getResource(resourceName);
            Assert.assertNotNull(registeredResource);
        }
    }

    /**
     * @verifies return removed resource object
     * @see Context#removeResource(com.mclinic.search.api.resource.Resource)
     */
    @Test
    public void removeResource_shouldReturnRemovedResourceObject() throws Exception {

        URL url = Context.class.getResource("sample/j2l");
        File resourceFile = new File(url.getPath());
        Context.registerResources(resourceFile);

        int resourceCounter = Context.getResources().size();
        Resource registeredResource = Context.getResource("Cohort Member Resource");
        Resource removedResource = Context.removeResource(registeredResource);
        Assert.assertEquals(registeredResource, removedResource);

        Assert.assertEquals(resourceCounter - 1, Context.getResources().size());
    }

    /**
     * @verifies register domain object using the class name.
     * @see com.mclinic.search.api.context.ServiceContext#registerObject(com.mclinic.search.api.model.object.Searchable)
     */
    @Test
    public void registerObject_shouldRegisterDomainObjectUsingTheClassName() throws Exception {
        Searchable clazz = Context.getObject(Patient.class.getName());
        Assert.assertNotNull(clazz);
        Assert.assertEquals(Patient.class.getName(), clazz.getClass().getName());
        Context.removeObject(clazz);
        clazz = Context.getObject(Patient.class.getName());
        Assert.assertNull(clazz);

        clazz = Context.getObject(Observation.class.getName());
        Assert.assertNotNull(clazz);
        Assert.assertEquals(Observation.class.getName(), clazz.getClass().getName());
        Context.removeObject(clazz);
        clazz = Context.getObject(Observation.class.getName());
        Assert.assertNull(clazz);

        clazz = Context.getObject(Cohort.class.getName());
        Assert.assertNotNull(clazz);
        Assert.assertEquals(Cohort.class.getName(), clazz.getClass().getName());
        Context.removeObject(clazz);
        clazz = Context.getObject(Cohort.class.getName());
        Assert.assertNull(clazz);
    }

    /**
     * @verifies register algorithm using the class name.
     * @see com.mclinic.search.api.context.ServiceContext#registerAlgorithm(com.mclinic.search.api.model.serialization.Algorithm)
     */
    @Test
    public void registerAlgorithm_shouldRegisterAlgorithmUsingTheClassName() throws Exception {
        Algorithm algorithm = Context.getAlgorithm(PatientAlgorithm.class.getName());
        Assert.assertNotNull(algorithm);
        Assert.assertEquals(PatientAlgorithm.class.getName(), algorithm.getClass().getName());

        algorithm = Context.getAlgorithm(CohortMemberAlgorithm.class.getName());
        Assert.assertNotNull(algorithm);
        Assert.assertEquals(CohortMemberAlgorithm.class.getName(), algorithm.getClass().getName());
    }

    /**
     * @verifies register resolver using the class name.
     * @see com.mclinic.search.api.context.ServiceContext#registerResolver(com.mclinic.search.api.model.resolver.Resolver)
     */
    @Test
    public void registerResolver_shouldRegisterResolverUsingTheClassName() throws Exception {
        Resolver resolver = Context.getResolver(PatientResolver.class.getName());
        Assert.assertNotNull(resolver);
        Assert.assertEquals(PatientResolver.class.getName(), resolver.getClass().getName());

        resolver = Context.getResolver(CohortMemberResolver.class.getName());
        Assert.assertNotNull(resolver);
        Assert.assertEquals(CohortMemberResolver.class.getName(), resolver.getClass().getName());
    }
}
