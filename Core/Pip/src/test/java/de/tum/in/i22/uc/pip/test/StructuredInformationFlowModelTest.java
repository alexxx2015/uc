package de.tum.in.i22.uc.pip.test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.factories.IMessageFactory;
import de.tum.in.i22.uc.cm.factories.MessageFactoryCreator;
import de.tum.in.i22.uc.cm.pip.EInformationFlowModel;
import de.tum.in.i22.uc.cm.pip.ifm.IStructuredInformationFlowModel;
import de.tum.in.i22.uc.pip.core.ifm.BasicInformationFlowModel;
import de.tum.in.i22.uc.pip.core.ifm.InformationFlowModelManager;

public class StructuredInformationFlowModelTest {
	protected final IMessageFactory _messageFactory = MessageFactoryCreator
			.createMessageFactory();

	private static final Logger _logger = LoggerFactory
			.getLogger(StructuredInformationFlowModelTest.class);

	private static final InformationFlowModelManager _structureIfm = new InformationFlowModelManager();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void structureTest() {

		/*
		 * Create new structure
		 * 
		 * lbl1 --> A, B
		 * 
		 * lbl2 --> C
		 * 
		 * lbl3 --> empty
		 * 
		 * lbl4 --> A, B, C
		 */

		HashMap<String,Set<IData>> map= new HashMap<String,Set<IData>>();
		
		IData a = _messageFactory.createData();
		IData b = _messageFactory.createData();
		IData c = _messageFactory.createData();
		
		HashSet<IData> set1= new HashSet<IData>();
		HashSet<IData> set2= new HashSet<IData>();
		HashSet<IData> set3= new HashSet<IData>();
		
		set1.add(a);
		set1.add(b);
		set2.add(c);
		set3.add(a);
		set3.add(b);
		set3.add(c);
		
		map.put("lbl1", set1);
		map.put("lbl2", set2);
		map.put("lbl3", new HashSet<IData>());
		map.put("lbl4", set3);
		
		Map<String,Set<IData>> res=_structureIfm.getStructureOf(a);
		Assert.assertNotNull(res);
		Assert.assertEquals(res.size(),0);
		
		
		IData structData = _structureIfm.newStructuredData(map);
		Assert.assertNotNull(structData);

		res=_structureIfm.getStructureOf(a);
		Assert.assertNotNull(res);
		Assert.assertEquals(res.size(),0);

		res=_structureIfm.getStructureOf(structData);
		Assert.assertNotNull(res);
		_logger.debug("map="+res);
		Assert.assertEquals(res.containsKey("lbl1"), true);
		Assert.assertEquals(res.get("lbl1").contains(a), true);
		Assert.assertEquals(res.get("lbl1").contains(b), true);
		Assert.assertEquals(res.get("lbl1").contains(c), false);
		
		
		Set<IData> flat= _structureIfm.flattenStructure(a);
		Assert.assertNotNull(flat);
		Assert.assertEquals(flat.contains(a), true);
		Assert.assertEquals(flat.contains(b), false);
		Assert.assertEquals(flat.contains(c), false);
		
		flat= _structureIfm.flattenStructure(structData);
		Assert.assertNotNull(flat);
		Assert.assertEquals(flat.contains(a), true);
		Assert.assertEquals(flat.contains(b), true);
		Assert.assertEquals(flat.contains(c), true);
		Assert.assertEquals(flat.contains(structData), true);

	
		HashSet<IData> set4= new HashSet<IData>();
		HashSet<IData> set5= new HashSet<IData>();
		
		set4.add(a);
		set4.add(structData);
		set5.add(b);
		set5.add(structData);
				
		HashMap<String,Set<IData>> map2= new HashMap<String,Set<IData>>();
		map2.put("lbl1",set4);
		map2.put("lbl2",set5);
		IData structData2 = _structureIfm.newStructuredData(map2);
		
		flat= _structureIfm.flattenStructure(structData2);
		Assert.assertNotNull(flat);
		Assert.assertEquals(flat.contains(a), true);
		Assert.assertEquals(flat.contains(b), true);
		Assert.assertEquals(flat.contains(c), true);
		Assert.assertEquals(flat.contains(structData), true);

	}

}
