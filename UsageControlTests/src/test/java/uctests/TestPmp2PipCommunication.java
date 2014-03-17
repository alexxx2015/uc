package uctests;
import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import testutil.DummyMessageGen;
import de.tum.in.i22.pip.cm.in.pmp.IPmp2Pip;
import de.tum.in.i22.pmp2pip.Pmp2PipTcpImp;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.out.ConnectionManager;

public class TestPmp2PipCommunication {

	private static Logger _logger = Logger.getRootLogger();

	private static IPmp2Pip _pipProxy;


	@BeforeClass
	public static void beforeClass() {
		_pipProxy = new Pmp2PipTcpImp("localhost", TestSettings.PMP_LISTENER_PORT_IN_PIP);
	}

	@Test
	public void testInitialRepresentation() throws Exception {
		// connect to PIP
		_pipProxy = ConnectionManager.obtain(_pipProxy);
		IContainer container = DummyMessageGen.createContainer();
		IData data = DummyMessageGen.createData();
		IStatus status = _pipProxy.initialRepresentation(container, data);
		_logger.debug("Received status: " + status);

		// disconnect from PIP
		ConnectionManager.release(_pipProxy);

		Assert.assertNotNull(status);
	}
}
