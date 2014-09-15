package de.tum.in.i22.uc.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tum.in.i22.uc.adaptation.engine.AdaptationController;
import de.tum.in.i22.uc.adaptation.engine.DomainMergeException;
import de.tum.in.i22.uc.adaptation.engine.ModelLoader;
import de.tum.in.i22.uc.adaptation.model.DomainModel;
import de.tum.in.i22.uc.policy.translation.Config;

public class AdaptationEngineTest {

	private ModelLoader modelHandler;
	/**
	 * The tests were used only in development.
	 * The tests could potentially affect configuration files unless
	 * only test files are correctly specified.
	 */
	private static final boolean TESTS_ENABLED = true;
	
	private static String DataContainer_MODELS4TEST_DIR = "src"
			+File.separator+"test"+File.separator+"resources"
			+File.separator+"models4test"
			+File.separator+"DataContainerModels";
	
	private static String ActionTransformer_MODELS4TEST_DIR = "src"
			+File.separator+"test"+File.separator+"resources"
			+File.separator+"models4test"
			+File.separator+"ActionTransformerModels";
	
	private static String userDir = "";
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Config config = new Config();
		userDir = config.getUserDir();
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
		DomainModel base = modelHandler.loadBaseDomainModel();
		assertNotNull(base);
	}

	@Test
	public void testLoadFileActionTransformerDomainModelBase() {
		if(!TESTS_ENABLED){
			assertTrue("AdaptationEngineTest disabled", true);
			return;
		}
		modelHandler = new ModelLoader();
		String file = userDir + File.separator+ ActionTransformer_MODELS4TEST_DIR + File.separator+  "ActionTransformerBase.xml";
		DomainModel base = modelHandler.loadDomainModel(file);
		assertNotNull(base);
		System.out.println("=============LOADING RESULT===========");
		System.out.println(base);
	}
	
	@Test
	public void testLoadFileDomainModel0() {
		if(!TESTS_ENABLED){
			assertTrue("AdaptationEngineTest disabled", true);
			return;
		}
		modelHandler = new ModelLoader();
		String file = userDir + File.separator+  DataContainer_MODELS4TEST_DIR + File.separator+  "BaseDomainModel0.xml";
		DomainModel base = modelHandler.loadDomainModel(file);
		assertNotNull(base);
	}
	
	
	@Test
	public void testLoadFileDomainModel1() {
		if(!TESTS_ENABLED){
			assertTrue("AdaptationEngineTest disabled", true);
			return;
		}
		modelHandler = new ModelLoader();
		String file = userDir + File.separator+  DataContainer_MODELS4TEST_DIR +File.separator+  "BaseDomainModel1.xml";
		DomainModel base = modelHandler.loadDomainModel(file);
		assertNotNull(base);
	}
	
	@Test
	public void testLoadFileDomainModel2() {
		if(!TESTS_ENABLED){
			assertTrue("AdaptationEngineTest disabled", true);
			return;
		}
		modelHandler = new ModelLoader();
		String file = userDir + File.separator+  DataContainer_MODELS4TEST_DIR +File.separator+  "BaseDomainModel2.xml";
		DomainModel base = modelHandler.loadDomainModel(file);
		assertNotNull(base);
	}
	
	@Test		
	public void testMergeDataContainers1() {
		if(!TESTS_ENABLED){
			assertTrue("AdaptationEngineTest disabled", true);
			return;
		}
		modelHandler = new ModelLoader();
		String file = userDir + File.separator+  DataContainer_MODELS4TEST_DIR +File.separator+  "DataContainerBase.xml";
		DomainModel baseDM = modelHandler.loadDomainModel(file);

		file = userDir + File.separator+  DataContainer_MODELS4TEST_DIR +File.separator+  "DataContainerNew.xml";
		DomainModel newDM = modelHandler.loadDomainModel(file);
		
		AdaptationController ac = new AdaptationController();
		ac.setBaseDomainModel(baseDM);
		ac.setNewDomainModel(newDM);
		try {
			ac.mergeDomainModels();
		} catch (DomainMergeException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		System.out.println("=============TEST RESULT===========");
		System.out.println(baseDM);
		
		int pimData = baseDM.getPimLayer().getDataContainers().size();
		int psmContainers = baseDM.getPsmLayer().getDataContainers().size();
		int ismContainers = baseDM.getIsmLayer().getDataContainers().size();
		assertTrue(pimData == 4);
		assertTrue(psmContainers == 2);
		assertTrue(ismContainers == 6);
	}
	
	@Test		
	public void testMergeActionTransformers1() {
		if(!TESTS_ENABLED){
			assertTrue("AdaptationEngineTest disabled", true);
			return;
		}
		modelHandler = new ModelLoader();
		String file = userDir + File.separator+  ActionTransformer_MODELS4TEST_DIR +File.separator+  "ActionTransformerBase.xml";
		DomainModel baseDM = modelHandler.loadDomainModel(file);

		file = userDir + File.separator+  ActionTransformer_MODELS4TEST_DIR +File.separator+  "ActionTransformerNew.xml";
		DomainModel newDM = modelHandler.loadDomainModel(file);
		
		AdaptationController ac = new AdaptationController();
		ac.setBaseDomainModel(baseDM);
		ac.setNewDomainModel(newDM);
		try {
			ac.mergeDomainModels();
		} catch (DomainMergeException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		System.out.println("=============TEST RESULT===========");
		System.out.println(baseDM);
		
		int pimData = baseDM.getPimLayer().getDataContainers().size();
		int psmContainers = baseDM.getPsmLayer().getDataContainers().size();
		int ismContainers = baseDM.getIsmLayer().getDataContainers().size();
		assertTrue(pimData == 4);
		assertTrue(psmContainers == 2);
		assertTrue(ismContainers == 6);
	}
	
	@Test
	public void testStoreTransformersXML(){
		if(!TESTS_ENABLED){
			assertTrue("AdaptationEngineTest disabled", true);
			return;
		}
		modelHandler = new ModelLoader();
		String baseFile = userDir + File.separator+  ActionTransformer_MODELS4TEST_DIR +File.separator+  "ActionTransformerBase.xml";
		String destination = userDir + File.separator+  ActionTransformer_MODELS4TEST_DIR +File.separator+  "ActionTransformerDestination.xml";
		DomainModel baseDM = modelHandler.loadDomainModel(baseFile);
		
		modelHandler.storeXmlDomainModel(destination, baseDM);
	}
	
	@Test
	public void testStoreContainersXML(){
		if(!TESTS_ENABLED){
			assertTrue("AdaptationEngineTest disabled", true);
			return;
		}
		modelHandler = new ModelLoader();
		String baseFile = userDir + File.separator+  DataContainer_MODELS4TEST_DIR +File.separator+  "DataContainerBase.xml";
		String destination = userDir + File.separator+  DataContainer_MODELS4TEST_DIR +File.separator+  "DataContainerDestination.xml";
		DomainModel baseDM = modelHandler.loadDomainModel(baseFile);
		
		modelHandler.storeXmlDomainModel(destination, baseDM);
	}
	
	@Test
	public void testBackUpOriginalDomainModelXML(){
		if(!TESTS_ENABLED){
			assertTrue("AdaptationEngineTest disabled", true);
			return;
		}
		modelHandler = new ModelLoader();
		String baseFile = userDir + File.separator+  DataContainer_MODELS4TEST_DIR +File.separator+  "DataContainerBase.xml";
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
		String baseFile = userDir + File.separator+  DataContainer_MODELS4TEST_DIR +File.separator+  "DataContainerBase.xml";
		String newFile = userDir + File.separator+  DataContainer_MODELS4TEST_DIR +File.separator+  "DataContainerNew.xml";
		String destination = userDir + File.separator+  DataContainer_MODELS4TEST_DIR +File.separator+  "DataContainerDestination.xml";
		DomainModel baseDM = modelHandler.loadDomainModel(baseFile);
		DomainModel newDM = modelHandler.loadDomainModel(newFile);
		
		AdaptationController ac = new AdaptationController();
		ac.setBaseDomainModel(baseDM);
		ac.setNewDomainModel(newDM);
		try {
			ac.mergeDomainModels();
		} catch (DomainMergeException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		modelHandler.storeXmlDomainModel(destination, baseDM);
	}
	
}
