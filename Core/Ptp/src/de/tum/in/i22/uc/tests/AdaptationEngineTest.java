package de.tum.in.i22.uc.tests;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tum.in.i22.uc.adaptation.AdaptationController;
import de.tum.in.i22.uc.adaptation.DomainMergeException;
import de.tum.in.i22.uc.adaptation.ModelLoader;
import de.tum.in.i22.uc.adaptation.model.DomainModel;

public class AdaptationEngineTest {

	private ModelLoader modelHandler;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testLoadBaseModel() {
		modelHandler = new ModelLoader();
		DomainModel base = modelHandler.loadBaseDomainModel();
		assertNotNull(base);
	}

	@Test
	public void testLoadFileDomainModel0() {
		modelHandler = new ModelLoader();
		String file = System.getProperty("user.dir") + File.separator + "doc" + File.separator + "models4test" + File.separator + "BaseDomainModel0.xml";
		DomainModel base = modelHandler.loadDomainModel(file);
		assertNotNull(base);
	}
	
	@Test
	public void testLoadFileDomainModel1() {
		modelHandler = new ModelLoader();
		String file = System.getProperty("user.dir") + File.separator + "doc" + File.separator + "models4test" + File.separator + "BaseDomainModel1.xml";
		DomainModel base = modelHandler.loadDomainModel(file);
		assertNotNull(base);
	}
	
	@Test
	public void testLoadFileDomainModel2() {
		modelHandler = new ModelLoader();
		String file = System.getProperty("user.dir") + File.separator + "doc" + File.separator + "models4test" + File.separator + "BaseDomainModel2.xml";
		DomainModel base = modelHandler.loadDomainModel(file);
		assertNotNull(base);
	}
	
	@Test		
	public void testMergeDataContainers1() {
		modelHandler = new ModelLoader();
		String file = System.getProperty("user.dir") + File.separator + "doc" + File.separator + "models4test" + File.separator + "DataContainerBase.xml";
		DomainModel baseDM = modelHandler.loadDomainModel(file);

		file = System.getProperty("user.dir") + File.separator + "doc" + File.separator + "models4test" + File.separator + "DataContainerNew.xml";
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
