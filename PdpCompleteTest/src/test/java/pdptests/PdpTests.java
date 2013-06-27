package pdptests;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Test;

import de.tum.in.i22.pdp.PdpController;
import de.tum.in.i22.pdp.cm.in.IMessageFactory;
import de.tum.in.i22.pdp.cm.in.MessageFactory;
import de.tum.in.i22.pdp.datatypes.IData;
import de.tum.in.i22.pdp.datatypes.IEvent;
import de.tum.in.i22.pdp.datatypes.IMechanism;
import de.tum.in.i22.pdp.datatypes.IResponse;
import de.tum.in.i22.pdp.datatypes.basic.ConditionBasic;
import de.tum.in.i22.pdp.datatypes.basic.DataBasic;
import de.tum.in.i22.pdp.datatypes.basic.DataEventMapBasic;
import de.tum.in.i22.pdp.datatypes.basic.EventBasic;
import de.tum.in.i22.pdp.datatypes.basic.MechanismBasic;
import de.tum.in.i22.pdp.datatypes.basic.OslFormulaBasic;
import de.tum.in.i22.pdp.datatypes.basic.SimplifiedTemporalLogicBasic;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpStatus.EStatus;
import de.tum.in.i22.pep2pdp.IPep2PdpFast;
import de.tum.in.i22.pep2pdp.Pep2PdpFastImp;
import de.tum.in.i22.pmp2pdp.IPmp2PdpFast;
import de.tum.in.i22.pmp2pdp.Pmp2PdpFastImp;

public class PdpTests {

	private static Logger _logger = Logger.getRootLogger();
	private static IPep2PdpFast _pdpProxy;

	// num of calls from pep thread
	private final static int NUM_OF_CALLS_FROM_PEP = 10;
	// num of calls from pmp thread
	private final static int NUM_OF_CALLS_FROM_PMP = 10;
	
	private static Thread _t1;
	private static Thread _t2;

	public PdpTests() {
		// TODO Auto-generated constructor stub
	}

	static {
		startPdpServer();
		startPepClient();
		startPmpClient();

		_pdpProxy = new Pep2PdpFastImp("localhost", 50001);
	}

	@Test
	public void testNotifyTwoEvents() throws Exception {
		// create event
		IMessageFactory mf = MessageFactory.getInstance();
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
		pdp.start(50001, 50002);

		try {
			_logger.debug("Pause the main thread for 1s (PDP starting).");
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			_logger.error("Main thread interrupted.", e);
		}
	}

	private static Thread startPepClient() {
		_t1 = new Thread(new Runnable() {

			@Override
			public void run() {
				IPep2PdpFast pdpProxyOne = new Pep2PdpFastImp("localhost",
						50001);
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
					IMessageFactory mf = MessageFactory.getInstance();
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
				IPmp2PdpFast pdpProxyTwo = new Pmp2PdpFastImp("localhost", 50002);
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
					EStatus status = pdpProxyTwo.deployMechanism(m);
					_logger.debug("Received status: " + status);
					
					try {
						Thread.sleep(30);
					} catch (InterruptedException e) {
						_logger.error(e);
					}
				}
				_pdpProxy.disconnect();
			}
		});

		_t2.start();
		
		return _t2;
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
			// wait for max 5s for t1 and t2 to die
			_t1.join();
			_t2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }

}
