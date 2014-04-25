package uctests;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.Controller;
import de.tum.in.i22.uc.cm.datatypes.basic.ConditionBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.DataBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.DataEventMapBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.MechanismBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.OslFormulaBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.SimplifiedTemporalLogicBasic;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IMechanism;
import de.tum.in.i22.uc.cm.factories.IMessageFactory;
import de.tum.in.i22.uc.cm.factories.MessageFactoryCreator;
import de.tum.in.i22.uc.cm.handlers.RequestHandler;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pdp;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pip;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pmp;
import de.tum.in.i22.uc.cm.processing.IRequestHandler;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.thrift.client.ThriftClientFactory;
import de.tum.in.i22.uc.thrift.server.IThriftServer;
import de.tum.in.i22.uc.thrift.server.ThriftServerFactory;

public class AllTests {

	private static Logger _logger = LoggerFactory.getLogger(AllTests.class);

	protected  static IAny2Pdp pdp;
	protected  static IAny2Pip pip;
	protected  static IAny2Pmp pmp;
	protected  static RequestHandler box;
	
	protected  static IThriftServer pdpServer;
	protected  static IThriftServer pipServer;
	protected  static IThriftServer pmpServer;
	
	protected static IMessageFactory mf;
	protected static ThriftClientFactory thriftClientFactory;
	protected static ThriftServerFactory thriftServerFactory;

	protected final static int PDP_LISTENER_PORT_IN_PIP = 60011;
	protected static final int PMP_LISTENER_PORT_IN_PIP = 60017;
	protected final static String PIP_ADDRESS = "localhost";
	protected final static int PMP_LISTENER_PORT_NUM = 50008;
	protected final static int PEP_LISTENER_PORT_NUM = 50009;
		
	protected static int PDP_SERVER_PORT = Settings.getInstance().getPdpListenerPort();
	protected static int PIP_SERVER_PORT = Settings.getInstance().getPipListenerPort();
	protected static int PMP_SERVER_PORT = Settings.getInstance().getPmpListenerPort();
	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		_logger.debug("\n NEW TEST CLASS START \n");
		mf = MessageFactoryCreator.createMessageFactory();
		thriftClientFactory = new ThriftClientFactory();
		thriftServerFactory = new ThriftServerFactory();
		box = new RequestHandler();
		pmp = box;
		pdp = box;
		pip = box;
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		_logger.debug("\n TEST CLASS END \n");
	}

	@Before
	public void setUp() throws Exception {
//		PDP_SERVER_PORT += 3;
//		PIP_SERVER_PORT += 3;
//		PMP_SERVER_PORT += 3;
		_logger.debug("\n\n Resetting box status for new test: new ports are ("+PDP_SERVER_PORT+","+PIP_SERVER_PORT+","+PMP_SERVER_PORT+")\n\n");
		box.reset();
//		pdpServer = ThriftServerFactory.createPdpThriftServer(PDP_SERVER_PORT, box);
//		pipServer = ThriftServerFactory.createPipThriftServer(PIP_SERVER_PORT, box);
//		pmpServer = ThriftServerFactory.createPmpThriftServer(PMP_SERVER_PORT, box);
//		new Thread(pdpServer).start();
//		new Thread(pipServer).start();
//		new Thread(pmpServer).start();
	}

	
	/**
	 * http://goo.gl/JLYmlS
	 * 
	 * 1:10 (1:48)
	 */
	
	protected static void sayMyName(String Heisenberg) {
		System.out.println("");
		System.out.println("******************");
		System.out.println("******************");
		System.out.println(Heisenberg);
		System.out.println("******************");
		System.out.println("******************");
		System.out.println("");
	}
	
	protected static Map<String, String> createDummyMap() {
		Map<String, String> map = new HashMap<String, String>();
		// add some entries
		map.put("key1", "value1");
		map.put("key2", "value2");
		map.put("key3", "value3");
		return map;
	}

	protected static IMechanism createMechanism() {
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
}
