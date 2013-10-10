package uctests;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import testutil.DummyMessageGen;
import de.tum.in.i22.pdp.PdpController;
import de.tum.in.i22.pdp.PdpSettings;
import de.tum.in.i22.pep2pdp.IPep2PdpFast;
import de.tum.in.i22.pep2pdp.Pep2PdpFastImp;
import de.tum.in.i22.pip.PipController;
import de.tum.in.i22.pip.PipSettings;
import de.tum.in.i22.pmp2pdp.IPmp2PdpFast;
import de.tum.in.i22.pmp2pdp.Pmp2PdpFastImp;
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

public class PdpTest {

	private static Logger _logger = Logger.getRootLogger();
	private static IPep2PdpFast _pdpProxy;

	// num of calls from pep thread
	private final static int NUM_OF_CALLS_FROM_PEP = 10;
	// num of calls from pmp thread
	private final static int NUM_OF_CALLS_FROM_PMP = 10;
	
	
	private final static int PDP_LISTENER_PORT_IN_PIP = 60011;
	private final static String PIP_ADDRESS = "localhost";
	private final static int PMP_LISTENER_PORT_NUM = 50008;
	private final static int PEP_LISTENER_PORT_NUM = 50009;
	
	private static Thread _t1;
	private static Thread _t2;
	private static Thread _t3;

	public PdpTest() {
		// TODO Auto-generated constructor stub
	}

	@BeforeClass
    public static void beforeClass() {
		startPdpServer();
		startPepClient();
		startPmpClient();
		startPip();
		
		try {
			_logger.debug("Pause the main thread for 1s so that PDP and PIP can be started.");
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			_logger.error("Main thread interrupted.", e);
		}

		_pdpProxy = new Pep2PdpFastImp("localhost", PEP_LISTENER_PORT_NUM);
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

		// connect to pdp
		_pdpProxy.connect();
		// notify event1
		IResponse response1 = _pdpProxy.notifyEvent(event1);
		_logger.debug("Received response as reply to event 1: " + response1);

		IResponse response2 = _pdpProxy.notifyEvent(event2);
		_logger.debug("Received response as reply to event 2: " + response2);
		// disconnect from pdp
		_pdpProxy.disconnect();

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
		_pdpProxy.connect();
		IResponse response = _pdpProxy.notifyEvent(event);
		_pdpProxy.disconnect();
		Assert.assertNotNull(response);
	}
	
	/**
	 * IfFlow stands for Information Flow
	 */
	@Test
	public void testUpdateIfFlowSemantics() throws Exception {
		// connect to pdp
		_pdpProxy.connect();
		IPipDeployer pipDeployer = new PipDeployerBasic("nameXYZ");
		File file = FileUtils.toFile(TestPep2PdpCommunication.class.getResource("/test.jar"));
		byte[] jarFileBytes = FileUtils.readFileToByteArray(file);
		IStatus status = _pdpProxy.updateInformationFlowSemantics(
				pipDeployer,
				jarFileBytes,
				EConflictResolution.OVERWRITE);
		// disconnect from pdp
		_pdpProxy.disconnect();
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

	private static void startPdpServer() {
		PdpController pdp = new PdpController();
		PdpSettings pdpSettings = pdp.getPdpSettings();
		pdpSettings.setPepListenerPortNum(PEP_LISTENER_PORT_NUM);
		pdpSettings.setPmpListenerPortNum(PMP_LISTENER_PORT_NUM);
		pdpSettings.setPipPortNum(PDP_LISTENER_PORT_IN_PIP);
		pdpSettings.setPipAddress(PIP_ADDRESS);
		pdp.start();
	}

	private static Thread startPepClient() {
		_t1 = new Thread(new Runnable() {

			@Override
			public void run() {
				IPep2PdpFast pdpProxyOne = new Pep2PdpFastImp("localhost",
						PEP_LISTENER_PORT_NUM);
				try {
					pdpProxyOne.connect();
				} catch (Exception e) {
					_logger.error(e);
					return;
				}
				for (int i = 0; i < NUM_OF_CALLS_FROM_PEP; i++) {
					String eventName1 = "event1";
					Map<String, String> map = new HashMap<String, String>();
					// add some entries
					map.put("key1", "value1");
					map.put("key2", "value2");
					map.put("key3", "value3");
					IMessageFactory mf = MessageFactoryCreator.createMessageFactory();
					IEvent event1 = mf.createEvent(eventName1, map);

					pdpProxyOne.notifyEvent(event1);

					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						_logger.error(e);
					}
				}
				pdpProxyOne.disconnect();
			}
		});

		_t1.start();
		
		return _t1;
	}
	
	private static Thread startPmpClient() {
		_t2 = new Thread(new Runnable() {
			@Override
			public void run() {
				IPmp2PdpFast pdpProxyTwo = new Pmp2PdpFastImp("localhost", PMP_LISTENER_PORT_NUM);
				try {
					pdpProxyTwo.connect();
				} catch (Exception e) {
					_logger.error(e);
					return;
				}
				for (int i = 0; i < NUM_OF_CALLS_FROM_PMP; i++) {
					//TODO invoke some methods
					// deploy mechanism
					IMechanism m = createMechanism();
					IStatus status = pdpProxyTwo.deployMechanism(m);
					_logger.debug("Received status: " + status);
					
					try {
						Thread.sleep(30);
					} catch (InterruptedException e) {
						_logger.error(e);
					}
				}
				pdpProxyTwo.disconnect();
			}
		});

		_t2.start();
		
		return _t2;
	}
	
	private static Thread startPip() {
		_t3 = new Thread(new Runnable() {
			@Override
			public void run() {
				_logger.debug("Start PIP. Listen from incoming PDP connections on port " + PDP_LISTENER_PORT_IN_PIP);
				PipController pipController = new PipController();
				PipSettings pipSettings = pipController.getPipSettings();
				pipSettings.setPdpListenerPortNum(PDP_LISTENER_PORT_IN_PIP);
				pipController.start();
			}
		});

		_t3.start();
		
		return _t3;
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
		_logger.info("After class");
		try {
			_t1.join();
			_t2.join();
			_t3.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }

}
