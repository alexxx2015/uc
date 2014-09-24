package de.tum.in.i22.uc.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.ptp.adaptation.engine.AdaptationController;
import de.tum.in.i22.uc.ptp.adaptation.engine.DomainMergeException;
import de.tum.in.i22.uc.ptp.adaptation.engine.InvalidDomainModelFormatException;
import de.tum.in.i22.uc.ptp.adaptation.engine.ModelLoader;
import de.tum.in.i22.uc.ptp.adaptation.model.DomainModel;
import de.tum.in.i22.uc.ptp.utilities.Config;

public class AdaptationEngineTest {

	private ModelLoader modelHandler;
	/**
	 * The tests were used only in development.
	 * The tests could potentially affect configuration files unless
	 * only test files are correctly specified.
	 */
	private static final boolean TESTS_ENABLED = true;
	
	private static final Logger logger = LoggerFactory.getLogger(AdaptationEngineTest.class);
	
	private static String MODELS4TEST_DIR = "models4test";
	
	private static String DataContainer_MODELS4TEST_DIR = MODELS4TEST_DIR + File.separator + "DataContainerModels";
	
	private static String ActionTransformer_MODELS4TEST_DIR = MODELS4TEST_DIR + File.separator + "ActionTransformerModels";
	
	private static String System_MODELS4TEST_DIR = MODELS4TEST_DIR + File.separator +"SystemModels";
	
	private static String Dummy_MODELS4TEST_DIR = MODELS4TEST_DIR + File.separator + "DummyModels";
	
	private static String resourcesDir = "";
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Config config = new Config();
		resourcesDir = config.getResourcesDir();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testLoadBaseModel() {
		if(!TESTS_ENABLED){
			assertTrue("AdaptationEngineTest disabled", true);
			return;
		}
		modelHandler = new ModelLoader();
		DomainModel base = null;
		try {
			base = modelHandler.loadBaseDomainModel();
		} catch (InvalidDomainModelFormatException e1) {
			fail(e1.getMessage());
		}
		int loadedElements = base.getElementsSize();
		logger.info("Load Base Model: " + loadedElements);
		assertTrue(loadedElements > 0);
	}

	@Test
	public void testLoadModelFromFile() {
		if(!TESTS_ENABLED){
			assertTrue("AdaptationEngineTest disabled", true);
			return;
		}
		modelHandler = new ModelLoader();
		String file = resourcesDir + File.separator+ MODELS4TEST_DIR + File.separator+  "BaseDomainModel1.xml";
		DomainModel base = null;
		try {
			base = modelHandler.loadDomainModel(file);
		} catch (InvalidDomainModelFormatException e1) {
			fail(e1.getMessage());
		}
		int loadedElements = base.getElementsSize();
		assertTrue(loadedElements > 0);
		logger.info("Load Model from: " + file +" " + loadedElements);
		
	}
	
	
	@Test		
	public void testMergeDataContainers1() {
		if(!TESTS_ENABLED){
			assertTrue("AdaptationEngineTest disabled", true);
			return;
		}
		modelHandler = new ModelLoader();
		String file = resourcesDir + File.separator+  DataContainer_MODELS4TEST_DIR +File.separator+  "DataContainerBase.xml";
		DomainModel baseDM = null;
		try {
			baseDM = modelHandler.loadDomainModel(file);
		} catch (InvalidDomainModelFormatException e1) {
			fail(e1.getMessage());
		}

		file = resourcesDir + File.separator+  DataContainer_MODELS4TEST_DIR +File.separator+  "DataContainerNew.xml";
		DomainModel newDM = null;
		try {
			newDM = modelHandler.loadDomainModel(file);
		} catch (InvalidDomainModelFormatException e1) {
			fail(e1.getMessage());
		}
		
		AdaptationController ac = new AdaptationController();
		ac.setBaseDomainModel(baseDM);
		ac.setNewDomainModel(newDM);
		int updatedElements = 0;
		try {
			updatedElements = ac.mergeDomainModels();
		} catch (DomainMergeException e) {
			fail(e.getMessage());
		}
		
		logger.info("UPDATED ELEMENTS: " + updatedElements);
		System.out.println("=============TEST RESULT===========");
		System.out.println(baseDM);
		
		int pimData = baseDM.getPimLayer().getDataContainers().size();
		int psmContainers = baseDM.getPsmLayer().getDataContainers().size();
		int ismContainers = baseDM.getIsmLayer().getDataContainers().size();
		assertEquals("data", 4, pimData);
		assertEquals("ism containers", 3, psmContainers);
		assertEquals("psm containers", 7, ismContainers);
	}
	
	/**
	 * The domain models used are in ./src/test/resources/models4test/ActionTransformerModels/test1
	 * Please do not change those files in order for the test to pass!
	 * <br> Tests for each layer:
	 * <br> - add new transformer
	 * <br> - for same transformer - extend input/output/paramData elements
	 * <br> - discover synonym at PIM
	 */
	@Test		
	public void testMergeActionTransformers_Test1() {
		if(!TESTS_ENABLED){
			assertTrue("AdaptationEngineTest disabled", true);
			return;
		}
		modelHandler = new ModelLoader();
		String file = resourcesDir + File.separator+  ActionTransformer_MODELS4TEST_DIR +File.separator+"test1"+
						File.separator+  "ActionTransformerBase.xml";
		DomainModel baseDM = loadDomainModel(file, modelHandler);

		file = resourcesDir + File.separator+  ActionTransformer_MODELS4TEST_DIR +File.separator+"test1"+
						File.separator+  "ActionTransformerNew.xml";
		DomainModel newDM = loadDomainModel(file, modelHandler);
		
		String destination = resourcesDir + File.separator+  ActionTransformer_MODELS4TEST_DIR +File.separator+"test1"+
						File.separator+  "ActionTransformerDestination.xml";
		
		
		
		AdaptationController ac = new AdaptationController();
		ac.setBaseDomainModel(baseDM);
		ac.setNewDomainModel(newDM);
		int updatedElements = 0;
		try {
			updatedElements = ac.mergeDomainModels();
		} catch (DomainMergeException e) {
			fail(e.getMessage());
		}
		
		logger.info("UPDATED ELEMENTS: " + updatedElements);
		System.out.println("=============TEST RESULT===========");
		System.out.println(baseDM);
		try {
			modelHandler.storeXmlDomainModel(destination, baseDM);
		} catch (InvalidDomainModelFormatException e) {
			fail(e.getMessage());
		}
		
		int pimData = baseDM.getPimLayer().getDataContainers().size();
		int psmContainers = baseDM.getPsmLayer().getDataContainers().size();
		int ismContainers = baseDM.getIsmLayer().getDataContainers().size();
		assertEquals("data",2,pimData);
		assertEquals("psm containers", 6,psmContainers);
		assertEquals("ism containers", 6,ismContainers);
		int pimAction = baseDM.getPimLayer().getActionTransformers().size();
		int psmTransformers = baseDM.getPsmLayer().getActionTransformers().size();
		int ismTransformers = baseDM.getIsmLayer().getActionTransformers().size();		
		assertEquals("actions", 2,pimAction);
		assertEquals("psm transformers", 7,psmTransformers);
		assertEquals("ism transformers", 10,ismTransformers);
		int psmSystems = baseDM.getPsmLayer().getSystems().size();
		int ismSystems = baseDM.getIsmLayer().getSystems().size();		
		assertEquals("psm systems", 3, psmSystems);
		assertEquals("ism systems", 3, ismSystems);
		
		String expected = resourcesDir + File.separator+  ActionTransformer_MODELS4TEST_DIR +File.separator+"test1"+
				File.separator+  "ActionTransformerExpected.xml";
		
		DomainModel expectedDM = loadDomainModel(expected, modelHandler);
		ac.setNewDomainModel(expectedDM);
		int updatedElementsMerged = 0;
		try {
			updatedElementsMerged = ac.mergeDomainModels();
		} catch (DomainMergeException e) {
			fail(e.getMessage());
		}
		assertEquals("update-expected match", 0, updatedElementsMerged);
	}
	
	
	private DomainModel loadDomainModel(String file, ModelLoader modelHandler){
		DomainModel expectedDM = null;
		try {
			expectedDM = modelHandler.loadDomainModel(file);
		} catch (InvalidDomainModelFormatException e1) {
			fail(e1.getMessage());
		}
		return expectedDM;
	}
	
	/**
	 * The domain models used are in ./src/test/resources/models4test/ActionTransformerModels/test2
	 * Please do not change those files in order for the test to pass!
	 * <br> Tests for each layer:
	 * <br> - extend inner/cross SET ref 
	 * <br> - discover synonym at PIM
	 */
	@Test		
	public void testMergeActionTransformers_Test2() {
		if(!TESTS_ENABLED){
			assertTrue("AdaptationEngineTest disabled", true);
			return;
		}
		modelHandler = new ModelLoader();
		String file = resourcesDir + File.separator+  ActionTransformer_MODELS4TEST_DIR +File.separator+"test2"+File.separator+  "ActionTransformerBase.xml";
		DomainModel baseDM = null;
		try {
			baseDM = modelHandler.loadDomainModel(file);
		} catch (InvalidDomainModelFormatException e1) {			
			fail(e1.getMessage());
		}

		file = resourcesDir + File.separator+  ActionTransformer_MODELS4TEST_DIR +File.separator+"test2"+File.separator+  "ActionTransformerNew.xml";
		DomainModel newDM = null;
		try {
			newDM = modelHandler.loadDomainModel(file);
		} catch (InvalidDomainModelFormatException e1) {
			fail(e1.getMessage());
		}
		
		String destination = resourcesDir + File.separator+  ActionTransformer_MODELS4TEST_DIR +File.separator+"test2"+File.separator+  "ActionTransformerDestination.xml";
		
		AdaptationController ac = new AdaptationController();
		ac.setBaseDomainModel(baseDM);
		ac.setNewDomainModel(newDM);
		int updatedElements = 0;
		try {
			updatedElements = ac.mergeDomainModels();
		} catch (DomainMergeException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		logger.info("UPDATED ELEMENTS: " + updatedElements);
		System.out.println("=============TEST RESULT===========");
		System.out.println(baseDM);
		try {
			modelHandler.storeXmlDomainModel(destination, baseDM);
		} catch (InvalidDomainModelFormatException e) {
			fail(e.getMessage());
		}
		
		int pimData = baseDM.getPimLayer().getDataContainers().size();
		int psmContainers = baseDM.getPsmLayer().getDataContainers().size();
		int ismContainers = baseDM.getIsmLayer().getDataContainers().size();
		assertEquals(2,pimData);
		assertEquals(6,psmContainers);
		assertEquals(6,ismContainers);
		int pimAction = baseDM.getPimLayer().getActionTransformers().size();
		int psmTransformers = baseDM.getPsmLayer().getActionTransformers().size();
		int ismTransformers = baseDM.getIsmLayer().getActionTransformers().size();		
		assertEquals(2,pimAction);
		assertEquals(6,psmTransformers);
		assertEquals(8,ismTransformers);
		int psmSystems = baseDM.getPsmLayer().getSystems().size();
		int ismSystems = baseDM.getIsmLayer().getSystems().size();		
		assertEquals(3, psmSystems);
		assertEquals(3, ismSystems);
		
	}
	
	/**
	 * The domain models used are in ./src/test/resources/models4test/ActionTransformerModels/test2
	 * Please do not change those files in order for the test to pass!
	 * <br> Tests for each layer:
	 * <br> - extend inner/cross SET ref 
	 * <br> - discover synonym at PIM
	 */
	@Test		
	public void testMergeActionTransformers_Test3() {
		if(!TESTS_ENABLED){
			assertTrue("AdaptationEngineTest disabled", true);
			return;
		}
		modelHandler = new ModelLoader();
		String file = resourcesDir + File.separator+  ActionTransformer_MODELS4TEST_DIR +File.separator+"test3"+File.separator+  "ActionTransformerBase.xml";
		DomainModel baseDM = null;
		try {
			baseDM = modelHandler.loadDomainModel(file);
		} catch (InvalidDomainModelFormatException e1) {			
			fail(e1.getMessage());
		}

		file = resourcesDir + File.separator+  ActionTransformer_MODELS4TEST_DIR +File.separator+"test3"+File.separator+  "ActionTransformerNew.xml";
		DomainModel newDM = null;
		try {
			newDM = modelHandler.loadDomainModel(file);
		} catch (InvalidDomainModelFormatException e1) {
			fail(e1.getMessage());
		}
		
		String destination = resourcesDir + File.separator+  ActionTransformer_MODELS4TEST_DIR +File.separator+"test3"+File.separator+  "ActionTransformerDestination.xml";
		
		AdaptationController ac = new AdaptationController();
		ac.setBaseDomainModel(baseDM);
		ac.setNewDomainModel(newDM);
		int updatedElements = 0;
		try {
			updatedElements = ac.mergeDomainModels();
		} catch (DomainMergeException e) {
			fail(e.getMessage());
		}
		
		logger.info("UPDATED ELEMENTS: " + updatedElements);
		System.out.println("=============TEST RESULT===========");
		System.out.println(baseDM);
		try {
			modelHandler.storeXmlDomainModel(destination, baseDM);
		} catch (InvalidDomainModelFormatException e) {
			fail(e.getMessage());
		}
		
		int pimData = baseDM.getPimLayer().getDataContainers().size();
		int psmContainers = baseDM.getPsmLayer().getDataContainers().size();
		int ismContainers = baseDM.getIsmLayer().getDataContainers().size();
		assertTrue(pimData == 2);
		assertTrue(psmContainers == 6);
		assertTrue(ismContainers == 6);
		int pimAction = baseDM.getPimLayer().getActionTransformers().size();
		int psmTransformers = baseDM.getPsmLayer().getActionTransformers().size();
		int ismTransformers = baseDM.getIsmLayer().getActionTransformers().size();		
		assertTrue(pimAction == 2);
		assertTrue(psmTransformers == 5);
		assertTrue(ismTransformers == 8);
		
	}
	
	/**
	 * The domain models used are in ./src/test/resources/models4test/SystemModels/test1/
	 * Please do not change those files in order for the test to pass!
	 * <br> Tests for each layer:
	 * <br> - add new transformer
	 * <br> - add new system
	 * <br> - add new system association
	 * <br> - add new cross refinement
	 */
	@Test		
	public void testMergeSystems_1() {
		if(!TESTS_ENABLED){
			assertTrue("AdaptationEngineTest disabled", true);
			return;
		}
		modelHandler = new ModelLoader();
		String file = resourcesDir + File.separator+  System_MODELS4TEST_DIR +File.separator+"test1"+File.separator+  "SystemBase.xml";
		DomainModel baseDM = null;
		try {
			baseDM = modelHandler.loadDomainModel(file);
		} catch (InvalidDomainModelFormatException e1) {			
			fail(e1.getMessage());
		}

		file = resourcesDir + File.separator+  System_MODELS4TEST_DIR +File.separator+"test1"+File.separator+  "SystemNew.xml";
		DomainModel newDM = null;
		try {
			newDM = modelHandler.loadDomainModel(file);
		} catch (InvalidDomainModelFormatException e1) {
			fail(e1.getMessage());
		}
		
		String destination = resourcesDir + File.separator+  System_MODELS4TEST_DIR +File.separator+"test1"+File.separator+  "SystemDestination.xml";
		
		AdaptationController ac = new AdaptationController();
		ac.setBaseDomainModel(baseDM);
		ac.setNewDomainModel(newDM);
		int updatedElements = 0;
		try {
			updatedElements = ac.mergeDomainModels();
		} catch (DomainMergeException e) {
			fail(e.getMessage());
		}
		
		logger.info("UPDATED ELEMENTS: " + updatedElements);
		System.out.println("=============TEST RESULT===========");
		System.out.println(baseDM);
		try {
			modelHandler.storeXmlDomainModel(destination, baseDM);
		} catch (InvalidDomainModelFormatException e) {
			fail(e.getMessage());
		}
		
		int pimData = baseDM.getPimLayer().getDataContainers().size();
		int psmContainers = baseDM.getPsmLayer().getDataContainers().size();
		int ismContainers = baseDM.getIsmLayer().getDataContainers().size();
		assertEquals(2, pimData);
		assertEquals(6, psmContainers);
		assertEquals(6, ismContainers);
		int pimAction = baseDM.getPimLayer().getActionTransformers().size();
		int psmTransformers = baseDM.getPsmLayer().getActionTransformers().size();
		int ismTransformers = baseDM.getIsmLayer().getActionTransformers().size();		
		assertEquals(2, pimAction);
		assertEquals(7, psmTransformers);
		assertEquals(12, ismTransformers);
		int psmSystems = baseDM.getPsmLayer().getSystems().size();
		int ismSystems = baseDM.getIsmLayer().getSystems().size();		
		assertEquals(6, psmSystems);
		assertEquals(7, ismSystems);
		
	}
	
	
	/**
	 * The domain models used are in ./src/test/resources/models4test/DummyModels/test1/
	 * Please do not change those files in order for the test to pass!
	 * <br> Tests for each layer:
	 * <br> - add new transformer
	 * <br> - add new system
	 * <br> - add new system association
	 * <br> - add new cross refinement
	 */
	@Test		
	public void testMergeDummyModels_1() {
		if(!TESTS_ENABLED){
			assertTrue("AdaptationEngineTest disabled", true);
			return;
		}
		modelHandler = new ModelLoader();
		String file = resourcesDir + File.separator+  Dummy_MODELS4TEST_DIR +File.separator+"test1"+File.separator+  "DummyBase.xml";
		DomainModel baseDM = null;
		try {
			baseDM = modelHandler.loadDomainModel(file);
		} catch (InvalidDomainModelFormatException e1) {			
			fail(e1.getMessage());
		}

		file = resourcesDir + File.separator+  Dummy_MODELS4TEST_DIR +File.separator+"test1"+File.separator+  "DummyNew.xml";
		DomainModel newDM = null;
		try {
			newDM = modelHandler.loadDomainModel(file);
		} catch (InvalidDomainModelFormatException e1) {
			fail(e1.getMessage());
		}
		
		String destination = resourcesDir + File.separator+  Dummy_MODELS4TEST_DIR +File.separator+"test1"+File.separator+  "DummyDestination.xml";
		
		AdaptationController ac = new AdaptationController();
		ac.setBaseDomainModel(baseDM);
		ac.setNewDomainModel(newDM);
		int updatedElements = 0;
		try {
			updatedElements = ac.mergeDomainModels();
		} catch (DomainMergeException e) {
			fail(e.getMessage());
		}
		
		logger.info("UPDATED ELEMENTS: " + updatedElements);
		System.out.println("=============TEST RESULT===========");
		System.out.println(baseDM);
		try {
			modelHandler.storeXmlDomainModel(destination, baseDM);
		} catch (InvalidDomainModelFormatException e) {
			fail(e.getMessage());
		}
		
		int pimData = baseDM.getPimLayer().getDataContainers().size();
		int psmContainers = baseDM.getPsmLayer().getDataContainers().size();
		int ismContainers = baseDM.getIsmLayer().getDataContainers().size();
		assertEquals(5, pimData);
		assertEquals(6, psmContainers);
		assertEquals(7, ismContainers);
		int pimAction = baseDM.getPimLayer().getActionTransformers().size();
		int psmTransformers = baseDM.getPsmLayer().getActionTransformers().size();
		int ismTransformers = baseDM.getIsmLayer().getActionTransformers().size();		
		assertEquals(9, pimAction);
		assertEquals(5, psmTransformers);
		assertEquals(6, ismTransformers);
		int psmSystems = baseDM.getPsmLayer().getSystems().size();
		int ismSystems = baseDM.getIsmLayer().getSystems().size();		
		assertEquals(2, psmSystems);
		assertEquals(2, ismSystems);
		
	}
	
	
	/**
	 * The domain models used are in ./src/test/resources/models4test/DummyModels/test1/
	 * Please do not change those files in order for the test to pass!
	 * <br> Tests for each layer:
	 * <br> - add new transformer
	 * <br> - add new system
	 * <br> - add new system association
	 * <br> - add new cross refinement
	 */
	@Test		
	public void testMergeDummyModels_2() {
		if(!TESTS_ENABLED){
			assertTrue("AdaptationEngineTest disabled", true);
			return;
		}
		modelHandler = new ModelLoader();
		String file = resourcesDir + File.separator+  Dummy_MODELS4TEST_DIR +File.separator+"test2"+File.separator+  "DummyBase.xml";
		DomainModel baseDM = null;
		try {
			baseDM = modelHandler.loadDomainModel(file);
		} catch (InvalidDomainModelFormatException e1) {			
			fail(e1.getMessage());
		}

		file = resourcesDir + File.separator+  Dummy_MODELS4TEST_DIR +File.separator+"test2"+File.separator+  "DummyNew.xml";
		DomainModel newDM = null;
		try {
			newDM = modelHandler.loadDomainModel(file);
		} catch (InvalidDomainModelFormatException e1) {
			fail(e1.getMessage());
		}
		
		String expectedFile = resourcesDir + File.separator+  Dummy_MODELS4TEST_DIR +File.separator+"test2"+File.separator+  "DummyExpected.xml";
		DomainModel exptectedDM = null;
		try {
			exptectedDM = modelHandler.loadDomainModel(expectedFile);
		} catch (InvalidDomainModelFormatException e1) {
			fail(e1.getMessage());
		}
		
		String destination = resourcesDir + File.separator+  Dummy_MODELS4TEST_DIR +File.separator+"test2"+File.separator+  "DummyDestination.xml";
		
		AdaptationController ac = new AdaptationController();
		ac.setBaseDomainModel(baseDM);
		ac.setNewDomainModel(newDM);
		int updatedElements = 0;
		try {
			updatedElements = ac.mergeDomainModels();
		} catch (DomainMergeException e) {
			fail(e.getMessage());
		}
		
		logger.info("UPDATED ELEMENTS: " + updatedElements);
		System.out.println("=============TEST RESULT===========");
		System.out.println(baseDM);
		try {
			modelHandler.storeXmlDomainModel(destination, baseDM);
		} catch (InvalidDomainModelFormatException e) {
			fail(e.getMessage());
		}
		
		ac.setNewDomainModel(exptectedDM);
		try {
			updatedElements = ac.mergeDomainModels();
		} catch (DomainMergeException e) {
			fail(e.getMessage());
		}
		assertEquals(0, updatedElements);
		
		int pimData = baseDM.getPimLayer().getDataContainers().size();
		int psmContainers = baseDM.getPsmLayer().getDataContainers().size();
		int ismContainers = baseDM.getIsmLayer().getDataContainers().size();
		assertEquals(5, pimData);
		assertEquals(6, psmContainers);
		assertEquals(7, ismContainers);
		int pimAction = baseDM.getPimLayer().getActionTransformers().size();
		int psmTransformers = baseDM.getPsmLayer().getActionTransformers().size();
		int ismTransformers = baseDM.getIsmLayer().getActionTransformers().size();		
		assertEquals(9, pimAction);
		assertEquals(12, psmTransformers);
		assertEquals(11, ismTransformers);
		int psmSystems = baseDM.getPsmLayer().getSystems().size();
		int ismSystems = baseDM.getIsmLayer().getSystems().size();		
		assertEquals(2, psmSystems);
		assertEquals(2, ismSystems);
		
	}
	
	/**
	 * The domain models used are in ./src/test/resources/models4test/DummyModels/test1/
	 * Please do not change those files in order for the test to pass!
	 * <br> Tests for each layer:
	 * <br> - add new transformer
	 * <br> - add new system
	 * <br> - add new system association
	 * <br> - add new cross refinement
	 */
	@Test		
	public void testResetDummyModels_1() {
		if(!TESTS_ENABLED){
			assertTrue("AdaptationEngineTest disabled", true);
			return;
		}
		modelHandler = new ModelLoader();
		String file = resourcesDir + File.separator+  Dummy_MODELS4TEST_DIR +File.separator+"reset1"+File.separator+  "BaseDomain.xml";
		DomainModel baseDM = null;
		try {
			baseDM = modelHandler.loadDomainModel(file);
		} catch (InvalidDomainModelFormatException e1) {			
			fail(e1.getMessage());
		}

		file = resourcesDir + File.separator+  Dummy_MODELS4TEST_DIR +File.separator+"reset1"+File.separator+  "NewDomain.xml";
		DomainModel newDM = null;
		try {
			newDM = modelHandler.loadDomainModel(file);
		} catch (InvalidDomainModelFormatException e1) {
			fail(e1.getMessage());
		}
		
		String expectedFile = resourcesDir + File.separator+  Dummy_MODELS4TEST_DIR +File.separator+"reset"+File.separator+  "DestinationDomain.xml";
		DomainModel exptectedDM = null;
		try {
			exptectedDM = modelHandler.loadDomainModel(expectedFile);
		} catch (InvalidDomainModelFormatException e1) {
			fail(e1.getMessage());
		}
		
		String destination = resourcesDir + File.separator+  Dummy_MODELS4TEST_DIR +File.separator+"test2"+File.separator+  "DummyDestination.xml";
		
		AdaptationController ac = new AdaptationController();
		ac.setBaseDomainModel(baseDM);
		ac.setNewDomainModel(newDM);
		int updatedElements = 0;
		try {
			updatedElements = ac.mergeDomainModels();
		} catch (DomainMergeException e) {
			fail(e.getMessage());
		}
		
		logger.info("UPDATED ELEMENTS: " + updatedElements);
		System.out.println("=============TEST RESULT===========");
		System.out.println(baseDM);
		try {
			modelHandler.storeXmlDomainModel(destination, baseDM);
		} catch (InvalidDomainModelFormatException e) {
			fail(e.getMessage());
		}
		
		ac.setNewDomainModel(exptectedDM);
		try {
			updatedElements = ac.mergeDomainModels();
		} catch (DomainMergeException e) {
			fail(e.getMessage());
		}
		assertEquals(0, updatedElements);
		
		int pimData = baseDM.getPimLayer().getDataContainers().size();
		int psmContainers = baseDM.getPsmLayer().getDataContainers().size();
		int ismContainers = baseDM.getIsmLayer().getDataContainers().size();
		assertEquals(5, pimData);
		assertEquals(6, psmContainers);
		assertEquals(7, ismContainers);
		int pimAction = baseDM.getPimLayer().getActionTransformers().size();
		int psmTransformers = baseDM.getPsmLayer().getActionTransformers().size();
		int ismTransformers = baseDM.getIsmLayer().getActionTransformers().size();		
		assertEquals(9, pimAction);
		assertEquals(12, psmTransformers);
		assertEquals(11, ismTransformers);
		int psmSystems = baseDM.getPsmLayer().getSystems().size();
		int ismSystems = baseDM.getIsmLayer().getSystems().size();		
		assertEquals(2, psmSystems);
		assertEquals(2, ismSystems);
		
	}
	
	@Test
	public void testStoreTransformersXML(){
		if(!TESTS_ENABLED){
			assertTrue("AdaptationEngineTest disabled", true);
			return;
		}
		modelHandler = new ModelLoader();
		String baseFile = resourcesDir + File.separator+  ActionTransformer_MODELS4TEST_DIR +File.separator+  "ActionTransformerBase.xml";
		String destination = resourcesDir + File.separator+  ActionTransformer_MODELS4TEST_DIR +File.separator+  "ActionTransformerDestination.xml";
		DomainModel baseDM = null;
		try {
			baseDM = modelHandler.loadDomainModel(baseFile);
		} catch (InvalidDomainModelFormatException e1) {
			fail(e1.getMessage());
		}
		
		try {
			modelHandler.storeXmlDomainModel(destination, baseDM);
		} catch (InvalidDomainModelFormatException e1) {
			fail(e1.getMessage());
		}
	}
	
	@Test
	public void testStoreContainersXML(){
		if(!TESTS_ENABLED){
			assertTrue("AdaptationEngineTest disabled", true);
			return;
		}
		modelHandler = new ModelLoader();
		String baseFile = resourcesDir + File.separator+  DataContainer_MODELS4TEST_DIR +File.separator+  "DataContainerBase.xml";
		String destination = resourcesDir + File.separator+  DataContainer_MODELS4TEST_DIR +File.separator+  "DataContainerDestination.xml";
		DomainModel baseDM = null;
		try {
			baseDM = modelHandler.loadDomainModel(baseFile);
		} catch (InvalidDomainModelFormatException e1) {
			fail(e1.getMessage());
		}
		
		try {
			modelHandler.storeXmlDomainModel(destination, baseDM);
		} catch (InvalidDomainModelFormatException e1) {
			fail(e1.getMessage());
		}
	}
	
	@Test
	public void testBackUpOriginalDomainModelXML(){
		if(!TESTS_ENABLED){
			assertTrue("AdaptationEngineTest disabled", true);
			return;
		}
		modelHandler = new ModelLoader();
		String baseFile = resourcesDir + File.separator+  DataContainer_MODELS4TEST_DIR +File.separator+  "DataContainerBase.xml";
		modelHandler.backupBaseDomainModel();
		modelHandler.backupBaseDomainModel(baseFile);
	}
	
	@Test
	public void testMergeAndStoreContainersXML(){
		if(!TESTS_ENABLED){
			assertTrue("AdaptationEngineTest disabled", true);
			return;
		}
		modelHandler = new ModelLoader();
		String baseFile = resourcesDir + File.separator+  DataContainer_MODELS4TEST_DIR +File.separator+  "DataContainerBase.xml";
		String newFile = resourcesDir + File.separator+  DataContainer_MODELS4TEST_DIR +File.separator+  "DataContainerNew.xml";
		String destination = resourcesDir + File.separator+  DataContainer_MODELS4TEST_DIR +File.separator+  "DataContainerDestination.xml";
		DomainModel baseDM = null;
		try {
			baseDM = modelHandler.loadDomainModel(baseFile);
		} catch (InvalidDomainModelFormatException e1) {
			fail(e1.getMessage());
		}
		DomainModel newDM = null;
		try {
			newDM = modelHandler.loadDomainModel(newFile);
		} catch (InvalidDomainModelFormatException e1) {
			fail(e1.getMessage());
		}
		
		AdaptationController ac = new AdaptationController();
		ac.setBaseDomainModel(baseDM);
		ac.setNewDomainModel(newDM);
		try {
			ac.mergeDomainModels();
		} catch (DomainMergeException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		try {
			modelHandler.storeXmlDomainModel(destination, baseDM);
		} catch (InvalidDomainModelFormatException e1) {
			fail(e1.getMessage());
		}
	}
	
}