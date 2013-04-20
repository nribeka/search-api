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

import com.jayway.jsonpath.JsonPath;
import com.mclinic.search.api.model.object.Searchable;
import com.mclinic.search.api.module.JUnitModule;
import com.mclinic.search.api.resource.Resource;
import com.mclinic.search.api.sample.algorithm.PatientAlgorithm;
import com.mclinic.search.api.sample.domain.Patient;
import com.mclinic.search.api.sample.resolver.PatientResolver;
import com.mclinic.search.api.service.RestAssuredService;
import com.mclinic.search.api.util.StreamUtil;
import com.mclinic.search.api.util.StringUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.UUID;

public class RestAssuredServiceTest {

    private RestAssuredService service;

    private static String patientName;

    private static String patientUuid;

    private static final String CORPUS_DIRECTORY = "sample/corpus";

    private static final String PATIENT_RESOURCE = "Patient Resource";

    private static final String CORPUS_CONFIGURATION_FILE = "sample/j2l/patient-template.j2l";

    @BeforeClass
    public static void prepareData() throws Exception {
        // we can read this values from the configuration document but it will take more coding haha ...
        String patientUuidPath = "$.uuid";
        String patientNamePath = "$.person.display";

        URL corpusUri = RestAssuredServiceTest.class.getResource(CORPUS_DIRECTORY);
        File corpusDirectory = new File(corpusUri.getPath());
        for (String corpusFile : corpusDirectory.list()) {
            String jsonPayload = StreamUtil.readAsString(new FileReader(new File(corpusDirectory, corpusFile)));
            // read the patient name information :)
            patientUuid = JsonPath.read(jsonPayload, patientUuidPath);
            patientName = JsonPath.read(jsonPayload, patientNamePath);
        }

        Assert.assertNotNull(patientUuid);
        Assert.assertNotNull(patientName);
    }

    @Before
    public void prepare() throws Exception {
        // initialize context object with additional parameters from the junit module
        Context.initialize(new JUnitModule());

        Context.registerObject(new Patient());
        Context.registerResolver(new PatientResolver());
        Context.registerAlgorithm(new PatientAlgorithm());

        URL configurationUri = RestAssuredServiceTest.class.getResource(CORPUS_CONFIGURATION_FILE);
        Context.registerResources(new File(configurationUri.getPath()));

        Resource resource = Context.getResource(PATIENT_RESOURCE);
        Assert.assertNotNull(resource);

        service = Context.getService();
        Assert.assertNotNull(service);

        // read the corpus location
        URL corpus = RestAssuredServiceTest.class.getResource(CORPUS_DIRECTORY);

        // load and save the corpus information into lucene database
        service.loadObjects(StringUtil.EMPTY, resource, new File(corpus.getPath()));
    }

    @After
    public void cleanUp() {
        String tmpDirectory = System.getProperty("java.io.tmpdir");
        String lucenePath = tmpDirectory + JUnitModule.LUCENE_DIRECTORY;

        File luceneDirectory = new File(lucenePath);
        for (String filename : luceneDirectory.list()) {
            File file = new File(luceneDirectory, filename);
            Assert.assertTrue(file.delete());
        }
    }

    /**
     * @verifies load objects based on the resource description
     * @see RestAssuredService#loadObjects(String, com.mclinic.search.api.resource.Resource)
     */
    @Test
    public void loadObjects_shouldLoadObjectsBasedOnTheResourceDescription() throws Exception {
        Assert.assertNotNull("You need to uncomment this unit test to test it with the rest module.");
        /*
         * This part of the unit test use the following assumption:
         * - You have installation of OpenMRS in your local computer
         * - At least a patient have name with letter "a" in the lucene repository
         */
        /*
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

        List<Patient> patients = service.getObjects(StringUtil.EMPTY, Patient.class);
        Assert.assertNotNull(patients);
        Assert.assertTrue(patients.size() > 0);
        */
    }

    /**
     * @verifies load object from filesystem based on the resource description
     * @see RestAssuredService#loadObjects(String, com.mclinic.search.api.resource.Resource, java.io.File)
     */
    @Test
    public void loadObjects_shouldLoadObjectFromFilesystemBasedOnTheResourceDescription() throws Exception {
        // search for multiple patients
        List<Patient> patients = service.getObjects("name:Test*", Patient.class);
        Assert.assertNotNull(patients);
        Assert.assertEquals(3, patients.size());
        // search for specific patient using the name
        Patient patient = service.getObject("name: " + StringUtil.quote(patientName), Patient.class);
        Assert.assertNotNull(patient);
        Assert.assertEquals(patientName, patient.getName());
    }

    /**
     * @verifies return object with matching key and type
     * @see RestAssuredService#getObject(String, Class)
     */
    @Test
    public void getObject_shouldReturnObjectWithMatchingKeyAndType() throws Exception {
        // search for specific patient using uuid
        Patient patient = service.getObject(StringUtil.quote(patientUuid), Patient.class);
        Assert.assertNotNull(patient);
        Assert.assertEquals(patientUuid, patient.getUuid());
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
        Patient patient = service.getObject("name: T*", Patient.class);
        Assert.assertNull(patient);
    }

    /**
     * @verifies return object with matching key
     * @see RestAssuredService#getObject(String, com.mclinic.search.api.resource.Resource)
     */
    @Test
    public void getObject_shouldReturnObjectWithMatchingKey() throws Exception {

        Resource resource = Context.getResource(PATIENT_RESOURCE);
        Patient patient = (Patient) service.getObject(StringUtil.quote(patientUuid), resource);
        Assert.assertNotNull(patient);
        Assert.assertEquals(patientUuid, patient.getUuid());
    }

    /**
     * @verifies return null when no object match the key
     * @see RestAssuredService#getObject(String, com.mclinic.search.api.resource.Resource)
     */
    @Test
    public void getObject_shouldReturnNullWhenNoObjectMatchTheKey() throws Exception {
        Resource resource = Context.getResource(PATIENT_RESOURCE);
        Patient patient = (Patient) service.getObject(StringUtil.quote(UUID.randomUUID().toString()), resource);
        Assert.assertNull(patient);
    }

    /**
     * @verifies throw IOException if the key and resource unable to return unique object
     * @see RestAssuredService#getObject(String, com.mclinic.search.api.resource.Resource)
     */
    @Test(expected = IOException.class)
    public void getObject_shouldThrowIOExceptionIfTheKeyAndResourceUnableToReturnUniqueObject() throws Exception {
        Resource resource = Context.getResource(PATIENT_RESOURCE);
        Patient patient = (Patient) service.getObject("name: T*", resource);
        Assert.assertNull(patient);
    }

    /**
     * @verifies return all object matching the search search string and class
     * @see RestAssuredService#getObjects(String, Class)
     */
    @Test
    public void getObjects_shouldReturnAllObjectMatchingTheSearchSearchStringAndClass() throws Exception {
        List<Patient> patients = service.getObjects("name: T*", Patient.class);
        Assert.assertNotNull(patients);
        Assert.assertTrue(patients.size() > 0);
    }

    /**
     * @verifies return empty list when no object match the search string and class
     * @see RestAssuredService#getObjects(String, Class)
     */
    @Test
    public void getObjects_shouldReturnEmptyListWhenNoObjectMatchTheSearchStringAndClass() throws Exception {
        List<Patient> patients = service.getObjects("name: Zz*", Patient.class);
        Assert.assertNotNull(patients);
        Assert.assertTrue(patients.size() == 0);
    }

    /**
     * @verifies return all object matching the search search string and resource
     * @see RestAssuredService#getObjects(String, com.mclinic.search.api.resource.Resource)
     */
    @Test
    public void getObjects_shouldReturnAllObjectMatchingTheSearchSearchStringAndResource() throws Exception {
        Resource resource = Context.getResource(PATIENT_RESOURCE);
        List<Searchable> patients = service.getObjects("name: T*", resource);
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
        Resource resource = Context.getResource(PATIENT_RESOURCE);
        List<Searchable> patients = service.getObjects("name: Zz*", resource);
        Assert.assertNotNull(patients);
        Assert.assertTrue(patients.size() == 0);
    }

    /**
     * @verifies remove an object from the internal index system
     * @see RestAssuredService#invalidate(com.mclinic.search.api.model.object.Searchable, com.mclinic.search.api.resource.Resource)
     */
    @Test
    public void invalidate_shouldRemoveAnObjectFromTheInternalIndexSystem() throws Exception {
        Patient patient = service.getObject(StringUtil.quote(patientUuid), Patient.class);
        Assert.assertNotNull(patient);
        Assert.assertEquals(patientUuid, patient.getUuid());

        Resource resource = Context.getResource(PATIENT_RESOURCE);
        Patient deletedPatient = (Patient) service.invalidate(patient, resource);
        Assert.assertNotNull(deletedPatient);
        Assert.assertEquals(patientUuid, deletedPatient.getUuid());

        Patient afterDeletionPatient = service.getObject(StringUtil.quote(patientUuid), Patient.class);
        Assert.assertNull(afterDeletionPatient);
    }
}
