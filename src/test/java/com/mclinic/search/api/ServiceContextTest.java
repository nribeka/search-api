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

import com.mclinic.search.api.internal.file.ResourceFileFilter;
import com.mclinic.search.api.module.UnitTestModule;
import com.mclinic.search.api.registry.Registry;
import com.mclinic.search.api.resolver.Resolver;
import com.mclinic.search.api.resource.ObjectResource;
import com.mclinic.search.api.resource.Resource;
import com.mclinic.search.api.resource.ResourceConstants;
import com.mclinic.search.api.resource.SearchableField;
import com.mclinic.search.api.sample.algorithm.CohortAlgorithm;
import com.mclinic.search.api.sample.algorithm.CohortMemberAlgorithm;
import com.mclinic.search.api.sample.algorithm.ObservationAlgorithm;
import com.mclinic.search.api.sample.algorithm.PatientAlgorithm;
import com.mclinic.search.api.sample.domain.Billing;
import com.mclinic.search.api.sample.domain.Cohort;
import com.mclinic.search.api.sample.domain.Encounter;
import com.mclinic.search.api.sample.domain.Observation;
import com.mclinic.search.api.sample.domain.Patient;
import com.mclinic.search.api.sample.resolver.CohortMemberResolver;
import com.mclinic.search.api.sample.resolver.CohortResolver;
import com.mclinic.search.api.sample.resolver.ObservationResolver;
import com.mclinic.search.api.sample.resolver.PatientResolver;
import com.mclinic.search.api.serialization.Algorithm;
import com.mclinic.search.api.util.ResourceUtil;
import com.mclinic.search.api.util.StringUtil;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class ServiceContextTest {

    /**
     * @verifies register programmatically created resource object.
     * @see com.mclinic.search.api.Context#registerResource(com.mclinic.search.api.resource.Resource)
     */
    @Test
    public void registerResource_shouldRegisterProgrammaticallyCreatedResourceObject() throws Exception {
        String rootNode = "$";
        String resourceName = "Example Resource";
        Algorithm algorithm = new PatientAlgorithm();
        Resolver resolver = new PatientResolver();
        Resource resource = new ObjectResource(resourceName, rootNode, Patient.class, algorithm, resolver);

        Context.initialize(new UnitTestModule());
        Context.registerResource(resource);
        // check the registration process
        Assert.assertTrue(Context.getResources().size() > 0);

        // check the registered resource internal property
        Resource registeredResource = Context.getResource(resourceName);
        Assert.assertNotNull(registeredResource);
        Assert.assertEquals(rootNode, registeredResource.getRootNode());
        Assert.assertEquals(Patient.class, registeredResource.getResourceObject());
        Assert.assertEquals(algorithm, registeredResource.getAlgorithm());
        Assert.assertEquals(resolver, registeredResource.getResolver());
        Assert.assertTrue(Algorithm.class.isAssignableFrom(registeredResource.getAlgorithm().getClass()));
        Assert.assertTrue(Resolver.class.isAssignableFrom(registeredResource.getResolver().getClass()));
    }

    /**
     * @verifies not register resource without resource name.
     * @see Context#registerResource(com.mclinic.search.api.resource.Resource)
     */
    @Test
    public void registerResource_shouldNotRegisterResourceWithoutResourceName() throws Exception {
        String rootNode = "$";
        String resourceName = null;
        Algorithm algorithm = new PatientAlgorithm();
        Resolver resolver = new PatientResolver();
        Resource resource = new ObjectResource(resourceName, rootNode, Patient.class, algorithm, resolver);

        Context.initialize(new UnitTestModule());
        Context.registerResource(resource);
        // check the registration process
        Assert.assertTrue(Context.getResources().size() == 0);
    }

    /**
     * @verifies only register resource files with j2l extension.
     * @see Context#registerResources(java.io.File)
     */
    @Test
    public void registerResources_shouldOnlyRegisterResourceFilesWithJ2lExtension() throws Exception {

        URL url = Context.class.getResource("sample/j2l");
        File resourceFile = new File(url.getPath());

        Context.initialize(new UnitTestModule());

        Context.registerObject(Patient.class, Cohort.class, Observation.class);
        Context.registerAlgorithm(CohortAlgorithm.class, CohortMemberAlgorithm.class, PatientAlgorithm.class,
                ObservationAlgorithm.class);
        Context.registerResolver(CohortResolver.class, CohortMemberResolver.class, PatientResolver.class,
                ObservationResolver.class);

        Context.registerResources(resourceFile);

        File[] files = resourceFile.listFiles(new ResourceFileFilter());
        Assert.assertNotNull(files);
        Assert.assertEquals(files.length, Context.getResources().size());
    }

    /**
     * @verifies create valid resource object based on the resource file.
     * @see Context#registerResources(java.io.File)
     */
    @Test
    public void registerResources_shouldCreateValidResourceObjectBasedOnTheResourceFile() throws Exception {

        URL url = Context.class.getResource("sample/j2l");
        File resourceFile = new File(url.getPath());

        Context.initialize(new UnitTestModule());

        Context.registerObject(Patient.class, Cohort.class, Observation.class);
        Context.registerAlgorithm(CohortAlgorithm.class, CohortMemberAlgorithm.class, PatientAlgorithm.class,
                ObservationAlgorithm.class);
        Context.registerResolver(CohortResolver.class, CohortMemberResolver.class, PatientResolver.class,
                ObservationResolver.class);

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

        Context.initialize(new UnitTestModule());

        Context.registerObject(Patient.class, Cohort.class, Observation.class);
        Context.registerAlgorithm(CohortAlgorithm.class, CohortMemberAlgorithm.class, PatientAlgorithm.class,
                ObservationAlgorithm.class);
        Context.registerResolver(CohortResolver.class, CohortMemberResolver.class, PatientResolver.class,
                ObservationResolver.class);

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

        Context.initialize(new UnitTestModule());

        Context.registerObject(Patient.class, Cohort.class, Observation.class);
        Context.registerAlgorithm(CohortAlgorithm.class, CohortMemberAlgorithm.class, PatientAlgorithm.class,
                ObservationAlgorithm.class);
        Context.registerResolver(CohortResolver.class, CohortMemberResolver.class, PatientResolver.class,
                ObservationResolver.class);

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
     * @verifies register all domain object classes in the domain object registry.
     * @see Context#registerObject(Class...)
     */
    @Test
    public void registerObject_shouldRegisterAllDomainObjectClassesInTheDomainObjectRegistry() throws Exception {

        Context.initialize(new UnitTestModule());

        Context.registerObject(Patient.class, Observation.class, Encounter.class, Billing.class);
        Class clazz = Context.removeObject(Patient.class);
        Assert.assertNotNull(clazz);
        Assert.assertEquals(Patient.class.getName(), clazz.getName());

        clazz = Context.removeObject(Observation.class);
        Assert.assertNotNull(clazz);
        Assert.assertEquals(Observation.class.getName(), clazz.getName());

        clazz = Context.removeObject(Encounter.class);
        Assert.assertNotNull(clazz);
        Assert.assertEquals(Encounter.class.getName(), clazz.getName());

        clazz = Context.removeObject(Patient.class);
        Assert.assertNull(clazz);
        clazz = Context.removeObject(Observation.class);
        Assert.assertNull(clazz);
        clazz = Context.removeObject(Encounter.class);
        Assert.assertNull(clazz);
    }

    /**
     * @verifies register all algorithm classes in the algorithm registry.
     * @see Context#registerAlgorithm(Class...)
     */
    @Test
    public void registerAlgorithm_shouldRegisterAllAlgorithmClassesInTheAlgorithmRegistry() throws Exception {

        Context.initialize(new UnitTestModule());

        Context.registerAlgorithm(PatientAlgorithm.class, CohortMemberAlgorithm.class);
        Class<? extends Algorithm> clazz = Context.removeAlgorithm(PatientAlgorithm.class);
        Assert.assertNotNull(clazz);
        Assert.assertEquals(PatientAlgorithm.class.getName(), clazz.getName());

        clazz = Context.removeAlgorithm(CohortMemberAlgorithm.class);
        Assert.assertNotNull(clazz);
        Assert.assertEquals(CohortMemberAlgorithm.class.getName(), clazz.getName());

        clazz = Context.removeAlgorithm(PatientAlgorithm.class);
        Assert.assertNotNull(clazz);
        clazz = Context.removeAlgorithm(CohortMemberAlgorithm.class);
        Assert.assertNotNull(clazz);
    }

    /**
     * @verifies register all resolver classes in the resolve registry.
     * @see Context#registerResolver(Class...)
     */
    @Test
    public void registerResolver_shouldRegisterAllResolverClassesInTheResolveRegistry() throws Exception {

        Context.initialize(new UnitTestModule());

        Context.registerResolver(PatientResolver.class, CohortMemberResolver.class);
        Class<? extends Resolver> clazz = Context.removeResolver(PatientResolver.class);
        Assert.assertNotNull(clazz);
        Assert.assertEquals(PatientResolver.class.getName(), clazz.getName());

        clazz = Context.removeResolver(CohortMemberResolver.class);
        Assert.assertNotNull(clazz);
        Assert.assertEquals(CohortMemberResolver.class.getName(), clazz.getName());

        clazz = Context.removeResolver(PatientResolver.class);
        Assert.assertNotNull(clazz);
        clazz = Context.removeResolver(CohortMemberResolver.class);
        Assert.assertNotNull(clazz);
    }

    /**
     * @verifies return removed resource object
     * @see Context#removeResource(com.mclinic.search.api.resource.Resource)
     */
    @Test
    public void removeResource_shouldReturnRemovedResourceObject() throws Exception {

        URL url = Context.class.getResource("sample/j2l");
        File resourceFile = new File(url.getPath());

        Context.initialize(new UnitTestModule());

        Context.registerObject(Patient.class, Cohort.class, Observation.class);
        Context.registerAlgorithm(CohortAlgorithm.class, CohortMemberAlgorithm.class, PatientAlgorithm.class,
                ObservationAlgorithm.class);
        Context.registerResolver(CohortResolver.class, CohortMemberResolver.class, PatientResolver.class,
                ObservationResolver.class);

        Context.registerResources(resourceFile);

        int resourceCounter = Context.getResources().size();
        Resource registeredResource = Context.getResource("Cohort Member Resource");
        Resource removedResource = Context.removeResource(registeredResource);
        Assert.assertEquals(registeredResource, removedResource);

        Assert.assertEquals(resourceCounter - 1, Context.getResources().size());
    }
}
