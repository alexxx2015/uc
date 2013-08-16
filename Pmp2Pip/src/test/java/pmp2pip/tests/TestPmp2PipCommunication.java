package pmp2pip.tests;
import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import testutil.DummyMessageGen;
import de.tum.in.i22.pip.PipController;
import de.tum.in.i22.pip.PipSettings;
import de.tum.in.i22.pmp2pip.IPmp2PipFast;
import de.tum.in.i22.pmp2pip.Pmp2PipFastImp;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class TestPmp2PipCommunication {

	private static Logger _logger = Logger.getRootLogger();

	private static IPmp2PipFast _pipProxy;
	private static final String PIP_ADDRESS = "localhost";
	private static final int PIP_PDP_LISTENER_PORT = 60015;
	private static final int PIP_PMP_LISTENER_PORT = 60017;

	
	@BeforeClass
	public static void beforeClass() {
		PipController pipController = new PipController();
		PipSettings settings = pipController.getPipSettings();
		settings.setPdpListenerPortNum(PIP_PDP_LISTENER_PORT);
		settings.setPmpListenerPortNum(PIP_PMP_LISTENER_PORT);
		pipController.start();

		try {
			_logger.debug("Pause the main thread for 1s (PIP starting).");
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			_logger.error("Main thread interrupted.", e);
		}

		_pipProxy = new Pmp2PipFastImp(PIP_ADDRESS, PIP_PMP_LISTENER_PORT);
	}

	@Test
	public void testInitialRepresentation() throws Exception {
		// connect to PIP
		_pipProxy.connect();
		IContainer container = DummyMessageGen.createContainer();
		IData data = DummyMessageGen.createData();
		IStatus status = _pipProxy.initialRepresentation(container, data);
		_logger.debug("Received status: " + status);

		// disconnect from PIP
		_pipProxy.disconnect();

		Assert.assertNotNull(status);
	}
}
