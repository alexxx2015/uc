package de.tum.in.i22.uc.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

<<<<<<< HEAD
import de.tum.in.i22.uc.ptp.adaptation.engine.AdaptationController;
import de.tum.in.i22.uc.ptp.adaptation.engine.DomainMergeException;
import de.tum.in.i22.uc.ptp.adaptation.engine.InvalidDomainModelFormatException;
import de.tum.in.i22.uc.ptp.adaptation.engine.ModelLoader;
import de.tum.in.i22.uc.ptp.adaptation.model.DomainModel;
import de.tum.in.i22.uc.ptp.utilities.Config;
=======
import de.tum.in.i22.uc.adaptation.engine.AdaptationController;
import de.tum.in.i22.uc.adaptation.engine.DomainMergeException;
import de.tum.in.i22.uc.adaptation.engine.ModelLoader;
import de.tum.in.i22.uc.adaptation.model.DomainModel;
>>>>>>> 34241d9247322206d6bbc20a064b95ba0d3a6264

public class AdaptationEngineTest {

	private ModelLoader modelHandler;
	/**
	 * The tests were used only in development.
	 * The tests could potentially affect configuration files unless
	 * only test files are correctly specified.
	 */
	private static final boolean TESTS_ENABLED = false;
<<<<<<< HEAD
	
	private static final Logger logger = LoggerFactory.getLogger(AdaptationEngineTest.class);
	
	private static String DataContainer_MODELS4TEST_DIR = "src"
			+File.separator+"test"+File.separator+"resources"
			+File.separator+"models4test"
			+File.separator+"DataContainerModels";
	
	private static String ActionTransformer_MODELS4TEST_DIR = "src"
			+File.separator+"test"+File.separator+"resources"
			+File.separator+"models4test"
			+File.separator+"ActionTransformerModels";
	
	private static String System_MODELS4TEST_DIR = "src"
			+File.separator+"test"+File.separator+"resources"
			+File.separator+"models4test"
			+File.separator+"SystemModels";
	
	private static String Dummy_MODELS4TEST_DIR = "src"
			+File.separator+"test"+File.separator+"resources"
			+File.separator+"models4test"
			+File.separator+"DummyModels";
	
	private static String userDir = "";
=======
	
	private static String MODELS4TEST_DIR = "src/test/resources/models4test/DataContainerModels/";
>>>>>>> 34241d9247322206d6bbc20a064b95ba0d3a6264
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
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
		assertNotNull(base);
	}

	@Test
<<<<<<< HEAD
	public void testLoadFileActionTransformerDomainModelBase() {
		if(!TESTS_ENABLED){
			assertTrue("AdaptationEngineTest disabled", true);
			return;
		}
		modelHandler = new ModelLoader();
		String file = userDir + File.separator+ ActionTransformer_MODELS4TEST_DIR + File.separator+  "ActionTransformerBase.xml";
		DomainModel base = null;
		try {
			base = modelHandler.loadDomainModel(file);
		} catch (InvalidDomainModelFormatException e1) {
			fail(e1.getMessage());
		}
		assertNotNull(base);
		System.out.println("=============LOADING RESULT===========");
		System.out.println(base);
	}
	
	@Test
=======
>>>>>>> 34241d9247322206d6bbc20a064b95ba0d3a6264
	public void testLoadFileDomainModel0() {
		if(!TESTS_ENABLED){
			assertTrue("AdaptationEngineTest disabled", true);
			return;
		}
		modelHandler = new ModelLoader();
<<<<<<< HEAD
		String file = userDir + File.separator+  DataContainer_MODELS4TEST_DIR + File.separator+  "BaseDomainModel0.xml";
		DomainModel base = null;
		try {
			base = modelHandler.loadDomainModel(file);
		} catch (InvalidDomainModelFormatException e1) {
			fail(e1.getMessage());
		}
=======
		String file = MODELS4TEST_DIR + "BaseDomainModel0.xml";
		DomainModel base = modelHandler.loadDomainModel(file);
>>>>>>> 34241d9247322206d6bbc20a064b95ba0d3a6264
		assertNotNull(base);
	}
	
	@Test
	public void testLoadFileDomainModel1() {
		if(!TESTS_ENABLED){
			assertTrue("AdaptationEngineTest disabled", true);
			return;
		}
		modelHandler = new ModelLoader();
<<<<<<< HEAD
		String file = userDir + File.separator+  DataContainer_MODELS4TEST_DIR +File.separator+  "BaseDomainModel1.xml";
		DomainModel base = null;
		try {
			base = modelHandler.loadDomainModel(file);
		} catch (InvalidDomainModelFormatException e1) {
			fail(e1.getMessage());
		}
=======
		String file = MODELS4TEST_DIR + "BaseDomainModel1.xml";
		DomainModel base = modelHandler.loadDomainModel(file);
>>>>>>> 34241d9247322206d6bbc20a064b95ba0d3a6264
		assertNotNull(base);
	}
	
	@Test
	public void testLoadFileDomainModel2() {
		if(!TESTS_ENABLED){
			assertTrue("AdaptationEngineTest disabled", true);
			return;
		}
		modelHandler = new ModelLoader();
<<<<<<< HEAD
		String file = userDir + File.separator+  DataContainer_MODELS4TEST_DIR +File.separator+  "BaseDomainModel2.xml";
		DomainModel base = null;
		try {
			base = modelHandler.loadDomainModel(file);
		} catch (InvalidDomainModelFormatException e1) {
			fail(e1.getMessage());
		}
=======
		String file = MODELS4TEST_DIR + "BaseDomainModel2.xml";
		DomainModel base = modelHandler.loadDomainModel(file);
>>>>>>> 34241d9247322206d6bbc20a064b95ba0d3a6264
		assertNotNull(base);
	}
	
	@Test		
	public void testMergeDataContainers1() {
		if(!TESTS_ENABLED){
			assertTrue("AdaptationEngineTest disabled", true);
			return;
		}
		modelHandler = new ModelLoader();
<<<<<<< HEAD
		String file = userDir + File.separator+  DataContainer_MODELS4TEST_DIR +File.separator+  "DataContainerBase.xml";
		DomainModel baseDM = null;
		try {
			baseDM = modelHandler.loadDomainModel(file);
		} catch (InvalidDomainModelFormatException e1) {
			fail(e1.getMessage());
		}

		file = userDir + File.separator+  DataContainer_MODELS4TEST_DIR +File.separator+  "DataContainerNew.xml";
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
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		logger.info("UPDATED ELEMENTS: " + updatedElements);
		System.out.println("=============TEST RESULT===========");
		System.out.println(baseDM);
		
		int pimData = baseDM.getPimLayer().getDataContainers().size();
		int psmContainers = baseDM.getPsmLayer().getDataContainers().size();
		int ismContainers = baseDM.getIsmLayer().getDataContainers().size();
		assertTrue(pimData == 4);
		assertTrue(psmContainers == 2);
		assertTrue(ismContainers == 6);
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
		String file = userDir + File.separator+  ActionTransformer_MODELS4TEST_DIR +File.separator+"test1"+File.separator+  "ActionTransformerBase.xml";
		DomainModel baseDM = null;
		try {
			baseDM = modelHandler.loadDomainModel(file);
		} catch (InvalidDomainModelFormatException e2) {
			fail(e2.getMessage());
		}

		file = userDir + File.separator+  ActionTransformer_MODELS4TEST_DIR +File.separator+"test1"+File.separator+  "ActionTransformerNew.xml";
		DomainModel newDM = null;
		try {
			newDM = modelHandler.loadDomainModel(file);
		} catch (InvalidDomainModelFormatException e1) {
			fail(e1.getMessage());
		}
		
		String destination = userDir + File.separator+  ActionTransformer_MODELS4TEST_DIR +File.separator+"test1"+File.separator+  "ActionTransformerDestination.xml";
=======
		String file = MODELS4TEST_DIR + "DataContainerBase.xml";
		DomainModel baseDM = modelHandler.loadDomainModel(file);

		file = MODELS4TEST_DIR + "DataContainerNew.xml";
		DomainModel newDM = modelHandler.loadDomainModel(file);
>>>>>>> 34241d9247322206d6bbc20a064b95ba0d3a6264
		
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
		assertEquals(2,pimData);
		assertEquals(6,psmContainers);
		assertEquals(6,ismContainers);
		int pimAction = baseDM.getPimLayer().getActionTransformers().size();
		int psmTransformers = baseDM.getPsmLayer().getActionTransformers().size();
		int ismTransformers = baseDM.getIsmLayer().getActionTransformers().size();		
		assertEquals(2,pimAction);
		assertEquals(6,psmTransformers);
		assertEquals(9,ismTransformers);
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
	public void testMergeActionTransformers_Test2() {
		if(!TESTS_ENABLED){
			assertTrue("AdaptationEngineTest disabled", true);
			return;
		}
		modelHandler = new ModelLoader();
		String file = userDir + File.separator+  ActionTransformer_MODELS4TEST_DIR +File.separator+"test2"+File.separator+  "ActionTransformerBase.xml";
		DomainModel baseDM = null;
		try {
			baseDM = modelHandler.loadDomainModel(file);
		} catch (InvalidDomainModelFormatException e1) {			
			fail(e1.getMessage());
		}

		file = userDir + File.separator+  ActionTransformer_MODELS4TEST_DIR +File.separator+"test2"+File.separator+  "ActionTransformerNew.xml";
		DomainModel newDM = null;
		try {
			newDM = modelHandler.loadDomainModel(file);
		} catch (InvalidDomainModelFormatException e1) {
			fail(e1.getMessage());
		}
		
		String destination = userDir + File.separator+  ActionTransformer_MODELS4TEST_DIR +File.separator+"test2"+File.separator+  "ActionTransformerDestination.xml";
		
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
		String file = userDir + File.separator+  ActionTransformer_MODELS4TEST_DIR +File.separator+"test3"+File.separator+  "ActionTransformerBase.xml";
		DomainModel baseDM = null;
		try {
			baseDM = modelHandler.loadDomainModel(file);
		} catch (InvalidDomainModelFormatException e1) {			
			fail(e1.getMessage());
		}

		file = userDir + File.separator+  ActionTransformer_MODELS4TEST_DIR +File.separator+"test3"+File.separator+  "ActionTransformerNew.xml";
		DomainModel newDM = null;
		try {
			newDM = modelHandler.loadDomainModel(file);
		} catch (InvalidDomainModelFormatException e1) {
			fail(e1.getMessage());
		}
		
		String destination = userDir + File.separator+  ActionTransformer_MODELS4TEST_DIR +File.separator+"test3"+File.separator+  "ActionTransformerDestination.xml";
		
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
		String file = userDir + File.separator+  System_MODELS4TEST_DIR +File.separator+"test1"+File.separator+  "SystemBase.xml";
		DomainModel baseDM = null;
		try {
			baseDM = modelHandler.loadDomainModel(file);
		} catch (InvalidDomainModelFormatException e1) {			
			fail(e1.getMessage());
		}

		file = userDir + File.separator+  System_MODELS4TEST_DIR +File.separator+"test1"+File.separator+  "SystemNew.xml";
		DomainModel newDM = null;
		try {
			newDM = modelHandler.loadDomainModel(file);
		} catch (InvalidDomainModelFormatException e1) {
			fail(e1.getMessage());
		}
		
		String destination = userDir + File.separator+  System_MODELS4TEST_DIR +File.separator+"test1"+File.separator+  "SystemDestination.xml";
		
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
		String file = userDir + File.separator+  Dummy_MODELS4TEST_DIR +File.separator+"test1"+File.separator+  "DummyBase.xml";
		DomainModel baseDM = null;
		try {
			baseDM = modelHandler.loadDomainModel(file);
		} catch (InvalidDomainModelFormatException e1) {			
			fail(e1.getMessage());
		}

		file = userDir + File.separator+  Dummy_MODELS4TEST_DIR +File.separator+"test1"+File.separator+  "DummyNew.xml";
		DomainModel newDM = null;
		try {
			newDM = modelHandler.loadDomainModel(file);
		} catch (InvalidDomainModelFormatException e1) {
			fail(e1.getMessage());
		}
		
		String destination = userDir + File.separator+  Dummy_MODELS4TEST_DIR +File.separator+"test1"+File.separator+  "DummyDestination.xml";
		
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
		String file = userDir + File.separator+  Dummy_MODELS4TEST_DIR +File.separator+"test2"+File.separator+  "DummyBase.xml";
		DomainModel baseDM = null;
		try {
			baseDM = modelHandler.loadDomainModel(file);
		} catch (InvalidDomainModelFormatException e1) {			
			fail(e1.getMessage());
		}

		file = userDir + File.separator+  Dummy_MODELS4TEST_DIR +File.separator+"test2"+File.separator+  "DummyNew.xml";
		DomainModel newDM = null;
		try {
			newDM = modelHandler.loadDomainModel(file);
		} catch (InvalidDomainModelFormatException e1) {
			fail(e1.getMessage());
		}
		
		String expectedFile = userDir + File.separator+  Dummy_MODELS4TEST_DIR +File.separator+"test2"+File.separator+  "DummyExpected.xml";
		DomainModel exptectedDM = null;
		try {
			exptectedDM = modelHandler.loadDomainModel(expectedFile);
		} catch (InvalidDomainModelFormatException e1) {
			fail(e1.getMessage());
		}
		
		String destination = userDir + File.separator+  Dummy_MODELS4TEST_DIR +File.separator+"test2"+File.separator+  "DummyDestination.xml";
		
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
		String file = userDir + File.separator+  Dummy_MODELS4TEST_DIR +File.separator+"reset1"+File.separator+  "BaseDomain.xml";
		DomainModel baseDM = null;
		try {
			baseDM = modelHandler.loadDomainModel(file);
		} catch (InvalidDomainModelFormatException e1) {			
			fail(e1.getMessage());
		}

		file = userDir + File.separator+  Dummy_MODELS4TEST_DIR +File.separator+"reset1"+File.separator+  "NewDomain.xml";
		DomainModel newDM = null;
		try {
			newDM = modelHandler.loadDomainModel(file);
		} catch (InvalidDomainModelFormatException e1) {
			fail(e1.getMessage());
		}
		
		String expectedFile = userDir + File.separator+  Dummy_MODELS4TEST_DIR +File.separator+"reset"+File.separator+  "DestinationDomain.xml";
		DomainModel exptectedDM = null;
		try {
			exptectedDM = modelHandler.loadDomainModel(expectedFile);
		} catch (InvalidDomainModelFormatException e1) {
			fail(e1.getMessage());
		}
		
		String destination = userDir + File.separator+  Dummy_MODELS4TEST_DIR +File.separator+"test2"+File.separator+  "DummyDestination.xml";
		
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
<<<<<<< HEAD
	public void testStoreTransformersXML(){
		if(!TESTS_ENABLED){
			assertTrue("AdaptationEngineTest disabled", true);
			return;
		}
		modelHandler = new ModelLoader();
		String baseFile = userDir + File.separator+  ActionTransformer_MODELS4TEST_DIR +File.separator+  "ActionTransformerBase.xml";
		String destination = userDir + File.separator+  ActionTransformer_MODELS4TEST_DIR +File.separator+  "ActionTransformerDestination.xml";
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
=======
>>>>>>> 34241d9247322206d6bbc20a064b95ba0d3a6264
	public void testStoreContainersXML(){
		if(!TESTS_ENABLED){
			assertTrue("AdaptationEngineTest disabled", true);
			return;
		}
		modelHandler = new ModelLoader();
<<<<<<< HEAD
		String baseFile = userDir + File.separator+  DataContainer_MODELS4TEST_DIR +File.separator+  "DataContainerBase.xml";
		String destination = userDir + File.separator+  DataContainer_MODELS4TEST_DIR +File.separator+  "DataContainerDestination.xml";
		DomainModel baseDM = null;
		try {
			baseDM = modelHandler.loadDomainModel(baseFile);
		} catch (InvalidDomainModelFormatException e1) {
			fail(e1.getMessage());
		}
=======
		String baseFile = MODELS4TEST_DIR + "DataContainerBase.xml";
		String destination = MODELS4TEST_DIR + "DataContainerDestination.xml";
		DomainModel baseDM = modelHandler.loadDomainModel(baseFile);
>>>>>>> 34241d9247322206d6bbc20a064b95ba0d3a6264
		
		try {
			modelHandler.storeXmlDomainModel(destination, baseDM);
		} catch (InvalidDomainModelFormatException e1) {
			fail(e1.getMessage());
		}
	}
	
	@Test
	public void testMergeAndStoreContainersXML(){
		if(!TESTS_ENABLED){
			assertTrue("AdaptationEngineTest disabled", true);
			return;
		}
		modelHandler = new ModelLoader();
<<<<<<< HEAD
		String baseFile = userDir + File.separator+  DataContainer_MODELS4TEST_DIR +File.separator+  "DataContainerBase.xml";
		String newFile = userDir + File.separator+  DataContainer_MODELS4TEST_DIR +File.separator+  "DataContainerNew.xml";
		String destination = userDir + File.separator+  DataContainer_MODELS4TEST_DIR +File.separator+  "DataContainerDestination.xml";
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
=======
		String baseFile = MODELS4TEST_DIR + "DataContainerBase.xml";
		String newFile = MODELS4TEST_DIR + "DataContainerNew.xml";
		String destination = MODELS4TEST_DIR + "DataContainerDestination.xml";
		DomainModel baseDM = modelHandler.loadDomainModel(baseFile);
		DomainModel newDM = modelHandler.loadDomainModel(newFile);
>>>>>>> 34241d9247322206d6bbc20a064b95ba0d3a6264
		
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
