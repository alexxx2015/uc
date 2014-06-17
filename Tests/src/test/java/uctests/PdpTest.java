package uctests;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.ConflictResolutionFlagBasic.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.basic.DataBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.PipDeployerBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.excel.CellName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IResponse;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.factories.IMessageFactory;
import de.tum.in.i22.uc.cm.factories.MessageFactoryCreator;

public class PdpTest extends GenericTest{

	private static Logger _logger = LoggerFactory.getLogger(PdpTest.class);

	// num of calls from pep thread
	private final static int NUM_OF_CALLS_FROM_PEP = 10;
	// num of calls from pmp thread
	private final static int NUM_OF_CALLS_FROM_PMP = 10;

	private static Thread _threadPep;
	private static Thread _threadPmp;

	public PdpTest() {
		// TODO Auto-generated constructor stub
	}

	@BeforeClass
    public static void beforeClass() throws Exception  {
		startPepClient();
	}

	@Test
	public void testNotifyTwoEvents() throws Exception {
		sayMyName(Thread.currentThread().getStackTrace()[1].getMethodName());

		// create event
		IMessageFactory mf = MessageFactoryCreator.createMessageFactory();
		String eventName1 = "event1";
		String eventName2 = "event2";
		Map<String, String> map = createDummyMap();
		IEvent event1 = mf.createEvent(eventName1, map);
		IEvent event2 = mf.createEvent(eventName2, map);

		IResponse response1 = pdp.notifyEventSync(event1);
		_logger.debug("Received response as reply to event 1: " + response1);

		IResponse response2 = pdp.notifyEventSync(event2);
		_logger.debug("Received response as reply to event 2: " + response2);


		// check if status is not null
		Assert.assertNotNull(response1);
		Assert.assertNotNull(response2);
	}
	


	@Test
	public void multipleInvocationsOfNotifyEvent() throws Exception {
		sayMyName(Thread.currentThread().getStackTrace()[1].getMethodName());

		for (int i = 0; i < 20; i++) {
			testNotifyTwoEvents();
		}
	}

	@Test
	public void testNotifyEventDelegatedToPipWhenActualEvent() throws Exception {
		sayMyName(Thread.currentThread().getStackTrace()[1].getMethodName());

		IEvent event = new EventBasic("x", Collections.EMPTY_MAP);

		IResponse response = pdp.notifyEventSync(event);

		Assert.assertNotNull(response);
	}


	@Test
	public void testPrachisPolicy() throws Exception {
		sayMyName(Thread.currentThread().getStackTrace()[1].getMethodName());

		pmp.deployPolicyURIPmp("src/test/resources/ExamplePolicies/prachis-policy.xml");
		
		Map<String,String> map = new HashMap<String,String>();
	
		map.put("object","city");
		map.put("name", "");
		map.put("scope", "539e9b555235f");
		map.put("class", "customPolicy");
		map.put("id", "city");
			
		IEvent event = mf.createDesiredEvent("cmd_copy", map);
		
		IResponse response = pdp.notifyEventSync(event);

		Assert.assertNotNull(response);
	}

	
	
	@Test
	public void testExcel() throws Exception {
		sayMyName(Thread.currentThread().getStackTrace()[1].getMethodName());

		Map<String,String> map = new HashMap<String,String>();
		
		map.put("PEP", "excel");
		map.put("Target","wb1!ws1!3!4");
		map.put("allowImpliesActual", "true");
		
		

		pip.initialRepresentation(new CellName("wb2!ws2!4!4"), Collections.singleton((IData)(new DataBasic("D44"))));
		pip.initialRepresentation(new CellName("wb2!ws2!4!5"), Collections.singleton((IData)(new DataBasic("D45"))));
		pip.initialRepresentation(new CellName("wb2!ws2!4!6"), Collections.singleton((IData)(new DataBasic("D46"))));
		pip.initialRepresentation(new CellName("wb2!ws2!4!7"), Collections.singleton((IData)(new DataBasic("D47"))));

		pip.initialRepresentation(new CellName("wb2!ws2!5!4"), Collections.singleton((IData)(new DataBasic("D54"))));
		pip.initialRepresentation(new CellName("wb2!ws2!5!5"), Collections.singleton((IData)(new DataBasic("D55"))));
		pip.initialRepresentation(new CellName("wb2!ws2!5!6"), Collections.singleton((IData)(new DataBasic("D56"))));
		pip.initialRepresentation(new CellName("wb2!ws2!5!7"), Collections.singleton((IData)(new DataBasic("D57"))));

		pip.initialRepresentation(new CellName("wb2!ws2!6!4"), Collections.singleton((IData)(new DataBasic("D64"))));
		pip.initialRepresentation(new CellName("wb2!ws2!6!5"), Collections.singleton((IData)(new DataBasic("D65"))));
		pip.initialRepresentation(new CellName("wb2!ws2!6!6"), Collections.singleton((IData)(new DataBasic("D66"))));
		pip.initialRepresentation(new CellName("wb2!ws2!6!7"), Collections.singleton((IData)(new DataBasic("D67"))));

		pip.initialRepresentation(new CellName("wb2!ws2!7!4"), Collections.singleton((IData)(new DataBasic("D74"))));
		pip.initialRepresentation(new CellName("wb2!ws2!7!5"), Collections.singleton((IData)(new DataBasic("D75"))));
		pip.initialRepresentation(new CellName("wb2!ws2!7!6"), Collections.singleton((IData)(new DataBasic("D76"))));
		pip.initialRepresentation(new CellName("wb2!ws2!7!7"), Collections.singleton((IData)(new DataBasic("D77"))));

		pip.newInitialRepresentation(new CellName("wb1!ws1!3!3"));
		pip.newInitialRepresentation(new CellName("wb1!ws1!3!4"));
		pip.newInitialRepresentation(new CellName("wb1!ws1!3!5"));
		
		pip.newInitialRepresentation(new NameBasic("SystemClipboard(Excel)"));
		
			
		IEvent event = new EventBasic("Print", map);
		IResponse response = pdp.notifyEventSync(event);
				
		map = new HashMap<String,String>();
		map.put("PEP", "excel");
		map.put("Target","wb1!ws1!3!4*wb1!ws1!3!3");
		map.put("allowImpliesActual", "true");
		
		event = new EventBasic("CopyInternal", map);

		response = pdp.notifyEventSync(event);

		response = pdp.notifyEventSync(event);
		

//		map = new HashMap<String,String>();
//		map.put("PEP", "excel");
//		map.put("Target","wb1!ws1!2!1*wb1!ws1!1!1");
//		map.put("allowImpliesActual", "true");
//		
//		event = new EventBasic("CopyInternal", map);
//
//		response = pdp.notifyEventSync(event);
//
//		response = pdp.notifyEventSync(event);

		
		
		map = new HashMap<String,String>();
		map.put("PEP", "excel");
		map.put("Target","wb1!ws1!5!5*wb1!ws1!4!6");
		map.put("allowImpliesActual", "true");
		
		event = new EventBasic("Paste", map);

		response = pdp.notifyEventSync(event);


		
		
		map = new HashMap<String,String>();
		map.put("PEP", "excel");
		map.put("Target","wb1!ws1!8!1*wb1!ws1!8!8");
		map.put("position","2");
		map.put("allowImpliesActual", "true");
		
		event = new EventBasic("PasteNOcb", map);

		response = pdp.notifyEventSync(event);
		

		map = new HashMap<String,String>();
		map.put("PEP", "excel");
		map.put("position","5");
		map.put("allowImpliesActual", "true");
		
		event = new EventBasic("DeleteNOcb", map);

		response = pdp.notifyEventSync(event);


		
		map = new HashMap<String,String>();
		map.put("PEP", "excel");
		map.put("srcCoordinate","wb2!ws2!4!4");
		map.put("RowCount", "4");
		map.put("ColCount","4");
		map.put("srcWorkbookName", "wb2");
		map.put("srcSheetName", "ws2");
		map.put("RowDiff", "5");		
		map.put("ColDiff", "5");
		map.put("destWorkbookName", "wb3");
		map.put("destSheetName", "ws3");		
		map.put("allowImpliesActual", "true");
		
		
		event = new EventBasic("DragAndDrop", map);

		response = pdp.notifyEventSync(event);

		
		
//		
//		map = new HashMap<String,String>();
//		map.put("PEP", "excel");
//		map.put("workbookName","wb1");
//		map.put("sheetName","ws1");
//		map.put("RowNumber","4");
//		
//		event = new EventBasic("DeleteRow", map);
//
//		response = pdp.notifyEventSync(event);
//		
//		
		
		
		
//		map = new HashMap<String,String>();
//		map.put("PEP", "excel");
//		map.put("workbookName","wb2");
//		map.put("sheetName","ws2");
//		map.put("RowNumber","6");
//		map.put("allowImpliesActual", "true");
//		
//		event = new EventBasic("DeleteRow", map);
//
//		response = pdp.notifyEventSync(event);
//		
//		response = pdp.notifyEventSync(event);
//		
//		response = pdp.notifyEventSync(event);
//		
		
//		map = new HashMap<String,String>();
//		map.put("PEP", "excel");
//		map.put("workbookName","wb2");
//		map.put("sheetName","ws2");
//		map.put("ColNumber","5");
//		map.put("allowImpliesActual", "true");
//		
//		event = new EventBasic("InsertColumn", map);
//
//		response = pdp.notifyEventSync(event);
//		
//		
//		
//		Assert.assertNotNull(response);
		
		map = new HashMap<String,String>();
		map.put("PEP", "excel");
		map.put("targetWb","wb3");
		map.put("externalFile","extF1");
		map.put("allowImpliesActual", "true");
		
		event = new EventBasic("Save", map);

		response = pdp.notifyEventSync(event);
		

		
		Assert.assertNotNull(response);
		
		
		
	}

	/**
	 * IfFlow stands for Information Flow
	 */
	@Test
	public void testUpdateIfFlowSemantics() throws Exception {
		sayMyName(Thread.currentThread().getStackTrace()[1].getMethodName());

		IPipDeployer pipDeployer = new PipDeployerBasic("nameXYZ");
		File file = FileUtils.toFile(TestPep2PdpCommunication.class.getResource("/test.jar"));

		IStatus status = pip.updateInformationFlowSemantics(
				pipDeployer,
				file,
				EConflictResolution.OVERWRITE);

		Assert.assertEquals(EStatus.OKAY, status.getEStatus());
	}

	private static Thread startPepClient() {
		_threadPep = new Thread(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < NUM_OF_CALLS_FROM_PEP; i++) {
					String eventName1 = "event1";
					Map<String, String> map = createDummyMap();
					IMessageFactory mf = MessageFactoryCreator.createMessageFactory();
					IEvent event1 = mf.createEvent(eventName1, map);

					pdp.notifyEventSync(event1);

					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						_logger.error(e.toString());
					}
				}
			}
		});

		_threadPep.start();

		return _threadPep;
	}

}
