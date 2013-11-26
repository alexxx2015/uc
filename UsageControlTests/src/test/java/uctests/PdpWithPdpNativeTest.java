package uctests;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import de.tum.in.i22.pdp.PdpController;
import de.tum.in.i22.pdp.PdpSettings;
import de.tum.in.i22.pep2pdp.IPep2PdpFast;
import de.tum.in.i22.pep2pdp.Pep2PdpFastImp;
import de.tum.in.i22.pip.PipController;
import de.tum.in.i22.pip.PipSettings;
import de.tum.in.i22.uc.cm.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.IResponse;

public class PdpWithPdpNativeTest {

	private static Logger _logger = Logger.getRootLogger();
	private static IPep2PdpFast _pdpProxy;

	private final static int PDP_LISTENER_PORT_IN_PIP = 60011;
	private final static String PIP_ADDRESS = "localhost";
	private final static int PMP_LISTENER_PORT_NUM = 50008;
	private final static int PEP_LISTENER_PORT_NUM = 50009;

	private static Thread _t1;
	private static Thread _t2;
	
	private static Injector _injector;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		_injector = Guice.createInjector(new PdpTestModule());
		
		startPdp();
		startPip();

		try {
			_logger.debug("Pause the main thread for 1s so that PDP and PIP can be started.");
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			_logger.error("Main thread interrupted.", e);
		}

		_pdpProxy = new Pep2PdpFastImp("localhost", PEP_LISTENER_PORT_NUM);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		_logger.info("After class");
		try {
			_t1.join();
			_t2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testNotifyEvent() throws Exception {
		_pdpProxy.connect();
		// notify event1
		EventBasic event = new EventBasic();
		event.setName("Print");
		event.setActual(false);
		event.addParameter("Obj", "file1.txt");
		event.addParameter("Quality", "50%");
		IResponse response = _pdpProxy.notifyEvent(event);
		
		_logger.debug("Received response as reply to event 1: " + response);
		_pdpProxy.disconnect();
	}

	private static void startPdp() {
		_t1 = new Thread(new Runnable() {
				public void run() {
				PdpController pdp =  _injector.getInstance(PdpController.class);
				PdpSettings pdpSettings = pdp.getPdpSettings();
				pdpSettings.setPepListenerPortNum(PEP_LISTENER_PORT_NUM);
				pdpSettings.setPmpListenerPortNum(PMP_LISTENER_PORT_NUM);
				pdpSettings.setPipPortNum(PDP_LISTENER_PORT_IN_PIP);
				pdpSettings.setPipAddress(PIP_ADDRESS);
				pdp.start();
			}
		});
		_t1.start();
		
	}

	private static void startPip() {
		_t2 = new Thread(new Runnable() {
			@Override
			public void run() {
				_logger.debug("Start PIP. Listen from incoming PDP connections on port " + PDP_LISTENER_PORT_IN_PIP);
				PipController pipController =  _injector.getInstance(PipController.class);
				PipSettings pipSettings = pipController.getPipSettings();
				pipSettings.setPdpListenerPortNum(PDP_LISTENER_PORT_IN_PIP);
				pipController.start();
			}
		});

		_t2.start();
	}

}
