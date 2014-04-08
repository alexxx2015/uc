package uctests;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AllTests {

	private static Logger _logger = LoggerFactory.getLogger(AllTests.class);

	@BeforeClass
	public static void setUpClass() {
		// /*
		// * Guice.createInjector() takes your Modules, and returns a new
		// Injector
		// * instance.
		// */
		// _injector = Guice.createInjector(new PdpTestModule());
		//
		// startPip();
		// startPdp();
	}

	@AfterClass
	public static void tearDownClass() {
		System.out.println("Master tearDown");
	}

	private static void startPdp() {
		// final PdpController pdp = _injector.getInstance(PdpController.class);
		// _logger.debug("Start PDP. Listen from incoming PEP connections on port "
		// + TestSettings.PEP_LISTENER_PORT_NUM);
		// GlobalSettings pdpSettings = PdpController.getPdpSettings();
		// pdpSettings.setPepGPBListenerPortNum(TestSettings.PEP_LISTENER_PORT_NUM);
		// pdpSettings.setPmpListenerPortNum(TestSettings.PMP_LISTENER_PORT_NUM);
		// pdpSettings.setPipPortNum(TestSettings.PDP_LISTENER_PORT_IN_PIP);
		// pdpSettings.setPipAddress(TestSettings.PIP_ADDRESS);
		// pdp.start();
		// _logger.debug("PDP started");
	}

	private static void startPip() {
		// PipController pipController = new PipController(new
		// PipHandlerMock());
		// _logger.debug("Start PIP. Listen from incoming PDP connections on port "
		// + TestSettings.PDP_LISTENER_PORT_IN_PIP);
		// PipSettings pipSettings = PipSettings.getInstance();
		// pipSettings.setPdpListenerPortNum(TestSettings.PDP_LISTENER_PORT_IN_PIP);
		// pipSettings.setPmpListenerPortNum(TestSettings.PMP_LISTENER_PORT_IN_PIP);
		// pipController.start();
		// _logger.debug("PIP started");
	}

}
