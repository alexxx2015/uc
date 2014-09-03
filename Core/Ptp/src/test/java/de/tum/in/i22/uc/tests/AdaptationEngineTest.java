package de.tum.in.i22.uc.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tum.in.i22.uc.adaptation.engine.AdaptationController;
import de.tum.in.i22.uc.adaptation.engine.DomainMergeException;
import de.tum.in.i22.uc.adaptation.engine.ModelLoader;
import de.tum.in.i22.uc.adaptation.model.DomainModel;

public class AdaptationEngineTest {

	private ModelLoader modelHandler;
	private static final boolean TESTS_ENABLED = false;
	
	private static String MODELS4TEST_DIR = "src/test/resources/models4test/DataContainerModels/";
	
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
		DomainModel base = modelHandler.loadBaseDomainModel();
		assertNotNull(base);
	}

	@Test
	public void testLoadFileDomainModel0() {
		if(!TESTS_ENABLED){
			assertTrue("AdaptationEngineTest disabled", true);
			return;
		}
		modelHandler = new ModelLoader();
		String file = MODELS4TEST_DIR + "BaseDomainModel0.xml";
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
		String file = MODELS4TEST_DIR + "BaseDomainModel1.xml";
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
		String file = MODELS4TEST_DIR + "BaseDomainModel2.xml";
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
		String file = MODELS4TEST_DIR + "DataContainerBase.xml";
		DomainModel baseDM = modelHandler.loadDomainModel(file);

		file = MODELS4TEST_DIR + "DataContainerNew.xml";
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
	public void testStoreContainersXML(){
		modelHandler = new ModelLoader();
		String baseFile = MODELS4TEST_DIR + "DataContainerBase.xml";
		String destination = MODELS4TEST_DIR + "DataContainerDestination.xml";
		DomainModel baseDM = modelHandler.loadDomainModel(baseFile);
		
		modelHandler.storeXmlDomainModel(destination, baseDM);
	}
	
	@Test
	public void testMergeAndStoreContainersXML(){
		modelHandler = new ModelLoader();
		String baseFile = MODELS4TEST_DIR + "DataContainerBase.xml";
		String newFile = MODELS4TEST_DIR + "DataContainerNew.xml";
		String destination = MODELS4TEST_DIR + "DataContainerDestination.xml";
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
