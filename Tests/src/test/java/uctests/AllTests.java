package uctests;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.Controller;
import de.tum.in.i22.uc.cm.commandLineOptions.CommandLineOptions;
import de.tum.in.i22.uc.cm.factories.MessageFactoryCreator;
import de.tum.in.i22.uc.thrift.client.ThriftClientFactory;
import de.tum.in.i22.uc.thrift.server.ThriftServerFactory;

/****
 * This is the only class executed
 * 
 * @author moka
 * 
 */

@RunWith(Suite.class)
@SuiteClasses({ PdpTest.class, PipTest.class, PmpTest.class,
		StateBasedOperatorTest.class, TestConnectionManager.class,
		TestPep2PdpCommunication.class, TestPmp2PdpCommunication.class,
		TestPmp2PipCommunication.class, TestThriftPep.class, ThriftTest.class })
public class AllTests extends GenericTest {

	private static Logger _logger = LoggerFactory.getLogger(AllTests.class);

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
			hasBeenSetUpByAllTests = true;
			_logger.debug("\n NEW TEST CLASS START \n");
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

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		_logger.debug("\n TEST CLASS END \n");
		box.stop();
		// give it a second to free the sockets
		Thread.sleep(1000);
	}

	@Before
	public void setUp() throws Exception {
		_logger.debug("\n\n Resetting box status for new test: new ports are ("
				+ PDP_SERVER_PORT + "," + PIP_SERVER_PORT + ","
				+ PMP_SERVER_PORT + ")\n\n");
		box.resetOnlyRequestHandler();
	}

}
