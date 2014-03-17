package uctests;


import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.google.inject.Guice;
import com.google.inject.Injector;

import de.tum.in.i22.pdp.PdpController;
import de.tum.in.i22.pdp.PdpSettings;
import de.tum.in.i22.pip.PipController;
import de.tum.in.i22.pip.PipSettings;

@RunWith(Suite.class)
//@SuiteClasses({ PipTest.class, PdpTest.class, TestPep2PdpCommunication.class, TestPmp2PipCommunication.class,
//		TestPmp2PdpCommunication.class })
@SuiteClasses({PipTest.class, PdpTest.class, TestPep2PdpCommunication.class, TestPmp2PipCommunication.class, TestPmp2PdpCommunication.class})
public class AllTests {

	private static Logger _logger = Logger.getLogger(AllTests.class);

	private static Injector _injector = null;

	@BeforeClass
    public static void setUpClass() {
		 /*
	     * Guice.createInjector() takes your Modules, and returns a new Injector
	     * instance.
	     */
		_injector = Guice.createInjector(new PdpTestModule());

		startPip();
		startPdp();
    }

    @AfterClass public static void tearDownClass() {
        System.out.println("Master tearDown");
    }

	private static void startPdp() {
		final PdpController pdp = _injector.getInstance(PdpController.class);
		_logger.debug("Start PDP. Listen from incoming PEP connections on port " + TestSettings.PEP_LISTENER_PORT_NUM);
		PdpSettings pdpSettings = PdpController.getPdpSettings();
		pdpSettings.setPepGPBListenerPortNum(TestSettings.PEP_LISTENER_PORT_NUM);
		pdpSettings.setPmpListenerPortNum(TestSettings.PMP_LISTENER_PORT_NUM);
		pdpSettings.setPipPortNum(TestSettings.PDP_LISTENER_PORT_IN_PIP);
		pdpSettings.setPipAddress(TestSettings.PIP_ADDRESS);
		pdp.start();
		_logger.debug("PDP started");
	}

	private static void startPip() {
		final PipController pipController = _injector.getInstance(PipController.class);
		_logger.debug("Start PIP. Listen from incoming PDP connections on port " + TestSettings.PDP_LISTENER_PORT_IN_PIP);
		PipSettings pipSettings = pipController.getPipSettings();
		pipSettings.setPdpListenerPortNum(TestSettings.PDP_LISTENER_PORT_IN_PIP);
		pipSettings.setPmpListenerPortNum(TestSettings.PMP_LISTENER_PORT_IN_PIP);
		pipController.start();
		_logger.debug("PIP started");
	}

}
