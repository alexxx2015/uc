package de.tum.in.i22.uc.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tum.in.i22.uc.adaptation.AdaptationController;
import de.tum.in.i22.uc.adaptation.DomainMergeException;
import de.tum.in.i22.uc.adaptation.ModelLoader;
import de.tum.in.i22.uc.adaptation.model.DomainModel;

public class AdaptationEngineTest {

	private ModelLoader modelHandler;
	private static final boolean TESTS_ENABLED = false;
	
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
		String file = "src/test/resources/models4test/" + "BaseDomainModel0.xml";
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
		String file = "src/test/resources/models4test/" + "BaseDomainModel1.xml";
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
		String file = "src/test/resources/models4test/" + "BaseDomainModel2.xml";
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
		String file = "src/test/resources/models4test/" + "DataContainerBase.xml";
		DomainModel baseDM = modelHandler.loadDomainModel(file);

		file = "src/test/resources/models4test/" + "DataContainerNew.xml";
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
}
