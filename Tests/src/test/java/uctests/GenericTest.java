package uctests;

import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.Controller;
import de.tum.in.i22.uc.cm.commandLineOptions.CommandLineOptions;
import de.tum.in.i22.uc.cm.factories.IMessageFactory;
import de.tum.in.i22.uc.cm.factories.MessageFactoryCreator;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pdp;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pip;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pmp;
import de.tum.in.i22.uc.thrift.client.ThriftClientFactory;
import de.tum.in.i22.uc.thrift.server.ThriftServerFactory;

public abstract class GenericTest {

	private static Logger _logger = LoggerFactory.getLogger(GenericTest.class);

	protected static IAny2Pdp pdp;
	protected static IAny2Pip pip;
	protected static IAny2Pmp pmp;
	protected static Controller box;

	protected static IMessageFactory mf;
	protected static ThriftClientFactory thriftClientFactory;
	protected static ThriftServerFactory thriftServerFactory;

	protected final static int PDP_LISTENER_PORT_IN_PIP = 60011;
	protected static final int PMP_LISTENER_PORT_IN_PIP = 60017;
	protected final static String PIP_ADDRESS = "localhost";
	protected final static int PMP_LISTENER_PORT_NUM = 50008;
	protected final static int PEP_LISTENER_PORT_NUM = 50009;

	protected static int PDP_SERVER_PORT = 40010;
	protected static int PIP_SERVER_PORT = 40011;
	protected static int PMP_SERVER_PORT = 40012;
	protected static int ANY_SERVER_PORT = 40013;

	protected static boolean hasBeenSetUpByAllTests = false;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		if (hasBeenSetUpByAllTests == false) {
			_logger.debug("\n\n NEW TEST CLASS START \n");
			mf = MessageFactoryCreator.createMessageFactory();
			thriftClientFactory = new ThriftClientFactory();
			thriftServerFactory = new ThriftServerFactory();
			String[] args = {
					"--"
							+ CommandLineOptions.OPTION_LOCAL_PDP_LISTENER_PORT_LONG,
							Integer.toString(PDP_SERVER_PORT),
							"--"
									+ CommandLineOptions.OPTION_LOCAL_PIP_LISTENER_PORT_LONG,
									Integer.toString(PIP_SERVER_PORT),
									"--"
											+ CommandLineOptions.OPTION_LOCAL_PMP_LISTENER_PORT_LONG,
											Integer.toString(PMP_SERVER_PORT),
											"--"
													+ CommandLineOptions.OPTION_LOCAL_ANY_LISTENER_PORT_LONG,
													Integer.toString(ANY_SERVER_PORT), };

			box = new Controller(args);
			pmp = box;
			pdp = box;
			pip = box;
			box.start();
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		if (hasBeenSetUpByAllTests == false) {
			_logger.debug("\n\n TEST CLASS END \n");
			box.stop();
			// give it a second to free the sockets
			//Thread.sleep(1000);
		}
	}

	@Before
	public void setUp() throws Exception {
		if (hasBeenSetUpByAllTests == false) {
			_logger.debug("\n\n Resetting box status for new test: new ports are ("
					+ PDP_SERVER_PORT
					+ ","
					+ PIP_SERVER_PORT
					+ ","
					+ PMP_SERVER_PORT + ")\n\n");
			box.resetOnlyRequestHandler();
		}
	}

	/**
	 * http://goo.gl/JLYmlS
	 * 
	 * 1:10 (1:48)
	 */

	protected static void sayMyName(String Heisenberg) {
		_logger.info("");
		_logger.info("******************");
		_logger.info("******************");
		_logger.info(Heisenberg);
		_logger.info("******************");
		_logger.info("******************");
		_logger.info("");
	}

	protected static Map<String, String> createDummyMap() {
		Map<String, String> map = new HashMap<String, String>();
		// add some entries
		map.put("key1", "value1");
		map.put("key2", "value2");
		map.put("key3", "value3");
		return map;
	}

}
