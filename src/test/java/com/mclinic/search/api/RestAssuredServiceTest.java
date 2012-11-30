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

import com.mclinic.search.api.module.UnitTestModule;
import com.mclinic.search.api.resource.Resource;
import com.mclinic.search.api.sample.algorithm.CohortAlgorithm;
import com.mclinic.search.api.sample.algorithm.CohortMemberAlgorithm;
import com.mclinic.search.api.sample.algorithm.ObservationAlgorithm;
import com.mclinic.search.api.sample.algorithm.PatientAlgorithm;
import com.mclinic.search.api.sample.domain.Cohort;
import com.mclinic.search.api.sample.domain.Observation;
import com.mclinic.search.api.sample.domain.Patient;
import com.mclinic.search.api.sample.resolver.CohortMemberResolver;
import com.mclinic.search.api.sample.resolver.CohortResolver;
import com.mclinic.search.api.sample.resolver.ObservationResolver;
import com.mclinic.search.api.sample.resolver.PatientResolver;
import com.mclinic.search.api.util.StringUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class RestAssuredServiceTest {

    private static final String REST_PATIENT_UUID = "dd706581-1691-11df-97a5-7038c432aabf";

    public static final String REST_PATIENT_NAME = "Abey Kiriao Kibwambok";

    public static final String FILE_PATIENT_NAME = "Testarius Ambote Indakasi";

    private ServiceContext serviceContext;

    private RestAssuredService service;

    @Before
    public void prepare() throws Exception {

        Context.initialize(new UnitTestModule());
        // register classes for the testing (algorithms, resolver, object)
        Context.registerAlgorithm(PatientAlgorithm.class, CohortAlgorithm.class, CohortMemberAlgorithm.class,
                ObservationAlgorithm.class);
        Context.registerResolver(PatientResolver.class, CohortResolver.class, CohortMemberResolver.class,
                ObservationResolver.class);
        Context.registerObject(Patient.class, Cohort.class, Observation.class);

        URL j2l = RestAssuredService.class.getResource("sample/j2l");
        Context.registerResources(new File(j2l.getPath()));

        service = Context.getService();
    }

    /**
     * @verifies load objects based on the resource description
     * @see RestAssuredService#loadObjects(String, com.mclinic.search.api.resource.Resource)
     */
    @Test
    public void loadObjects_shouldLoadObjectsBasedOnTheResourceDescription() throws Exception {

        Resource resource = null;

        resource = Context.getResource("Cohort Resource");
        Assert.assertNotNull(resource);
        service.loadObjects(StringUtil.EMPTY, resource);
        List<Cohort> cohorts = service.getObjects(StringUtil.EMPTY, Cohort.class);
        for (Cohort cohort : cohorts) {
            resource = Context.getResource("Cohort Member Resource");
            Assert.assertNotNull(resource);
            service.loadObjects(cohort.getUuid(), resource);
            List<Patient> patients = service.getObjects(StringUtil.EMPTY, Patient.class);
            for (Patient patient : patients) {
                resource = Context.getResource("Observation Resource");
                Assert.assertNotNull(resource);
                service.loadObjects(patient.getUuid(), resource);
            }
        }

        List<Patient> patients = service.getObjects("name:A*", Patient.class);
        Assert.assertNotNull(patients);
        Assert.assertTrue(patients.size() > 0);
        for (Patient patient : patients) {
            Assert.assertNotNull(patient);
            Assert.assertEquals(Patient.class, patient.getClass());
        }

        Patient patient = service.getObject("name: " + StringUtil.quote(REST_PATIENT_NAME), Patient.class);
        Assert.assertNotNull(patient);
        Assert.assertEquals(Patient.class, patient.getClass());
    }

    /**
     * @verifies load object from filesystem based on the resource description
     * @see RestAssuredService#loadObjects(String, com.mclinic.search.api.resource.Resource, java.io.File)
     */
    @Test
    public void loadObjects_shouldLoadObjectFromFilesystemBasedOnTheResourceDescription() throws Exception {

        URL corpus = RestAssuredService.class.getResource("sample/corpus");
        Resource resource = Context.getResource("Patient Resource");
        Assert.assertNotNull(resource);
        service.loadObjects(StringUtil.EMPTY, resource, new File(corpus.getPath()));

        List<Patient> patients = service.getObjects("name:Test*", Patient.class);
        Assert.assertNotNull(patients);
        Assert.assertTrue(patients.size() > 0);
        for (Patient patient : patients) {
            Assert.assertNotNull(patient);
            Assert.assertEquals(Patient.class, patient.getClass());
        }

        Patient patient = service.getObject("name: " + StringUtil.quote(FILE_PATIENT_NAME), Patient.class);
        Assert.assertNotNull(patient);
        Assert.assertEquals(Patient.class, patient.getClass());
    }

    /**
     * @verifies return object with matching key and type
     * @see RestAssuredService#getObject(String, Class)
     */
    @Test
    public void getObject_shouldReturnObjectWithMatchingKeyAndType() throws Exception {
        Patient patient = service.getObject(StringUtil.quote(REST_PATIENT_UUID), Patient.class);
        Assert.assertNotNull(patient);
        Assert.assertEquals(Patient.class, patient.getClass());
    }

    /**
     * @verifies return null when no object match the key and type
     * @see RestAssuredService#getObject(String, Class)
     */
    @Test
    public void getObject_shouldReturnNullWhenNoObjectMatchTheKeyAndType() throws Exception {
        // passing random uuid into the getObject method
        Patient patient = service.getObject(StringUtil.quote("1234"), Patient.class);
        Assert.assertNull(patient);
    }

    /**
     * @verifies throw IOException if the key and class unable to return unique object
     * @see RestAssuredService#getObject(String, Class)
     */
    @Test(expected = IOException.class)
    public void getObject_shouldThrowIOExceptionIfTheKeyAndClassUnableToReturnUniqueObject() throws Exception {
        Patient patient = service.getObject("name:A*", Patient.class);
        Assert.assertNull(patient);
    }

    /**
     * @verifies return object with matching key
     * @see RestAssuredService#getObject(String, com.mclinic.search.api.resource.Resource)
     */
    @Test
    public void getObject_shouldReturnObjectWithMatchingKey() throws Exception {

        Resource resource = Context.getResource("Cohort Member Resource");
        Patient patient = (Patient) service.getObject(StringUtil.quote(REST_PATIENT_UUID), resource);
        Assert.assertNotNull(patient);
        Assert.assertEquals(Patient.class, patient.getClass());
    }

    /**
     * @verifies return null when no object match the key
     * @see RestAssuredService#getObject(String, com.mclinic.search.api.resource.Resource)
     */
    @Test
    public void getObject_shouldReturnNullWhenNoObjectMatchTheKey() throws Exception {
        Patient patient;
        Resource resource;

        resource = Context.getResource("Patient Resource");
        patient = (Patient) service.getObject(StringUtil.quote(REST_PATIENT_UUID), resource);
        Assert.assertNull(patient);

        resource = Context.getResource("Cohort Member Resource");
        patient = (Patient) service.getObject(StringUtil.quote(REST_PATIENT_UUID), resource);
        Assert.assertNotNull(patient);
        Assert.assertEquals(Patient.class, patient.getClass());
    }

    /**
     * @verifies throw IOException if the key and resource unable to return unique object
     * @see RestAssuredService#getObject(String, com.mclinic.search.api.resource.Resource)
     */
    @Test(expected = IOException.class)
    public void getObject_shouldThrowIOExceptionIfTheKeyAndResourceUnableToReturnUniqueObject() throws Exception {
        Resource resource = Context.getResource("Cohort Member Resource");
        Patient patient = (Patient) service.getObject("name:A*", resource);
        Assert.assertNull(patient);
    }

    /**
     * @verifies return all object matching the search search string and class
     * @see RestAssuredService#getObjects(String, Class)
     */
    @Test
    public void getObjects_shouldReturnAllObjectMatchingTheSearchSearchStringAndClass() throws Exception {
        List<Patient> patients = service.getObjects("name:Ab*", Patient.class);
        Assert.assertNotNull(patients);
        Assert.assertTrue(patients.size() > 0);
        for (Patient patient : patients) {
            Assert.assertNotNull(patient);
            Assert.assertEquals(Patient.class, patient.getClass());
        }
    }

    /**
     * @verifies return empty list when no object match the search string and class
     * @see RestAssuredService#getObjects(String, Class)
     */
    @Test
    public void getObjects_shouldReturnEmptyListWhenNoObjectMatchTheSearchStringAndClass() throws Exception {
        List<Patient> patients = service.getObjects("name:Zz*", Patient.class);
        Assert.assertNotNull(patients);
        Assert.assertTrue(patients.size() == 0);
    }

    /**
     * @verifies return all object matching the search search string and resource
     * @see RestAssuredService#getObjects(String, com.mclinic.search.api.resource.Resource)
     */
    @Test
    public void getObjects_shouldReturnAllObjectMatchingTheSearchSearchStringAndResource() throws Exception {
        Resource resource = Context.getResource("Cohort Member Resource");
        List<Object> patients = service.getObjects("name:Ab*", resource);
        Assert.assertNotNull(patients);
        Assert.assertTrue(patients.size() > 0);
        for (Object patient : patients) {
            Assert.assertNotNull(patient);
            Assert.assertEquals(Patient.class, patient.getClass());
        }
    }

    /**
     * @verifies return empty list when no object match the search string and resource
     * @see RestAssuredService#getObjects(String, com.mclinic.search.api.resource.Resource)
     */
    @Test
    public void getObjects_shouldReturnEmptyListWhenNoObjectMatchTheSearchStringAndResource() throws Exception {
        Resource resource = Context.getResource("Cohort Member Resource");
        List<Object> patients = service.getObjects("name:Zz*", resource);
        Assert.assertNotNull(patients);
        Assert.assertTrue(patients.size() == 0);
    }

    /**
     * @verifies remove an object from the internal index system
     * @see RestAssuredService#invalidate(Object, com.mclinic.search.api.resource.Resource)
     */
    @Test
    public void invalidate_shouldRemoveAnObjectFromTheInternalIndexSystem() throws Exception {
        Patient patient = service.getObject(StringUtil.quote(REST_PATIENT_UUID), Patient.class);
        Assert.assertNotNull(patient);
        Assert.assertEquals(Patient.class, patient.getClass());

        Resource resource = Context.getResource("Cohort Member Resource");
        Patient deletedPatient = (Patient) service.invalidate(patient, resource);
        Assert.assertNotNull(deletedPatient);
        Assert.assertEquals(Patient.class, deletedPatient.getClass());

        Patient afterDeletionPatient = service.getObject(StringUtil.quote(REST_PATIENT_UUID), Patient.class);
        Assert.assertNull(afterDeletionPatient);
    }
}
