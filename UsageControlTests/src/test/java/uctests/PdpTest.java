package uctests;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import testutil.DummyMessageGen;
import de.tum.in.i22.uc.cm.IMessageFactory;
import de.tum.in.i22.uc.cm.MessageFactoryCreator;
import de.tum.in.i22.uc.cm.basic.ConditionBasic;
import de.tum.in.i22.uc.cm.basic.DataBasic;
import de.tum.in.i22.uc.cm.basic.DataEventMapBasic;
import de.tum.in.i22.uc.cm.basic.EventBasic;
import de.tum.in.i22.uc.cm.basic.MechanismBasic;
import de.tum.in.i22.uc.cm.basic.OslFormulaBasic;
import de.tum.in.i22.uc.cm.basic.PipDeployerBasic;
import de.tum.in.i22.uc.cm.basic.SimplifiedTemporalLogicBasic;
import de.tum.in.i22.uc.cm.datatypes.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pdp;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pip;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pmp;
import de.tum.in.i22.uc.pdp.PdpHandler;
import de.tum.in.i22.uc.pip.PipHandler;
import de.tum.in.i22.uc.pmp.PmpHandler;

public class PdpTest {

	private static Logger _logger = LoggerFactory.getLogger(PdpTest.class);

	// num of calls from pep thread
	private final static int NUM_OF_CALLS_FROM_PEP = 10;
	// num of calls from pmp thread
	private final static int NUM_OF_CALLS_FROM_PMP = 10;

	private static IAny2Pdp _pdp;
	private static IAny2Pip _pip;
	private static IAny2Pmp _pmp;

	private static Thread _threadPep;
	private static Thread _threadPmp;

	public PdpTest() {
		// TODO Auto-generated constructor stub
	}

	@BeforeClass
    public static void beforeClass() {
		_pdp = new PdpHandler();
		_pip = new PipHandler();
		_pmp = new PmpHandler();

		startPepClient();
		startPmpClient();
	}

	@Test
	public void testNotifyTwoEvents() throws Exception {
		// create event
		IMessageFactory mf = MessageFactoryCreator.createMessageFactory();
		String eventName1 = "event1";
		String eventName2 = "event2";
		Map<String, String> map = createDummyMap();
		IEvent event1 = mf.createEvent(eventName1, map);
		IEvent event2 = mf.createEvent(eventName2, map);

		IResponse response1 = _pdp.notifyEvent(event1);
		_logger.debug("Received response as reply to event 1: " + response1);

		IResponse response2 = _pdp.notifyEvent(event2);
		_logger.debug("Received response as reply to event 2: " + response2);


		// check if status is not null
		Assert.assertNotNull(response1);
		Assert.assertNotNull(response2);
	}

	@Test
	public void multipleInvocationsOfNotifyEvent() throws Exception {
		for (int i = 0; i < 20; i++) {
			testNotifyTwoEvents();
		}
	}

	@Test
	public void testNotifyEventDelegatedToPipWhenActualEvent() throws Exception {
		IEvent event = DummyMessageGen.createActualEvent();

		IResponse response = _pdp.notifyEvent(event);

		Assert.assertNotNull(response);
	}

	/**
	 * IfFlow stands for Information Flow
	 */
	@Test
	public void testUpdateIfFlowSemantics() throws Exception {

		IPipDeployer pipDeployer = new PipDeployerBasic("nameXYZ");
		File file = FileUtils.toFile(TestPep2PdpCommunication.class.getResource("/test.jar"));

		IStatus status = _pip.updateInformationFlowSemantics(
				pipDeployer,
				file,
				EConflictResolution.OVERWRITE);

		Assert.assertEquals(EStatus.OKAY, status.getEStatus());
	}

	private Map<String, String> createDummyMap() {
		Map<String, String> map = new HashMap<String, String>();
		// add some entries
		map.put("key1", "value1");
		map.put("key2", "value2");
		map.put("key3", "value3");
		return map;
	}

	private static Thread startPepClient() {
		_threadPep = new Thread(new Runnable() {

			@Override
			public void run() {
				for (int i = 0; i < NUM_OF_CALLS_FROM_PEP; i++) {
					String eventName1 = "event1";
					Map<String, String> map = new HashMap<String, String>();
					// add some entries
					map.put("key1", "value1");
					map.put("key2", "value2");
					map.put("key3", "value3");
					IMessageFactory mf = MessageFactoryCreator.createMessageFactory();
					IEvent event1 = mf.createEvent(eventName1, map);

					_pdp.notifyEvent(event1);

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

	private static Thread startPmpClient() {
		_threadPmp = new Thread(new Runnable() {
			@Override
			public void run() {

				for (int i = 0; i < NUM_OF_CALLS_FROM_PMP; i++) {
					//TODO invoke some methods
					// deploy mechanism
					IMechanism m = createMechanism();
					IStatus status = _pdp.deployMechanism(m);
					_logger.debug("Received status: " + status);

					try {
						Thread.sleep(30);
					} catch (InterruptedException e) {
						_logger.error(e.toString());
					}
				}
			}
		});

		_threadPmp.start();

		return _threadPmp;
	}

	private static IMechanism createMechanism() {
		MechanismBasic m = new MechanismBasic();

		// * set condition
		ConditionBasic condition = new ConditionBasic();
		// ** set condition condition
		OslFormulaBasic formula = new OslFormulaBasic("Formula xxxx");
		condition.setCondition(formula);
		// ** set condition conditionSimp
		SimplifiedTemporalLogicBasic conditionSimp = new SimplifiedTemporalLogicBasic();
		conditionSimp.setFormula(new OslFormulaBasic("Formula yyyy"));

		// *** set condition conditionSimp dataEventMap
		Map<IData, IEvent> map1 = new HashMap<IData, IEvent>();
		map1.put(new DataBasic("id1"), new EventBasic("event1", null));
		DataEventMapBasic dataEventMap = new DataEventMapBasic(map1);
		conditionSimp.setDataEventMap(dataEventMap);
		condition.setConditionSimp(conditionSimp);
		m.setCondition(condition);

		return m;
	}


	@AfterClass
    public static void afterClass() {
//		_logger.info("After class");
//		try {
//			_t1.join();
//			_t2.join();
//			_t3.join();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
    }

}
