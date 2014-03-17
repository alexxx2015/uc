package uctests;
import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import testutil.DummyMessageGen;
import de.tum.in.i22.pmp2pip.IPmp2PipTcp;
import de.tum.in.i22.pmp2pip.Pmp2PipTcpImp;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.out.ConnectionManager;

public class TestPmp2PipCommunication {

	private static Logger _logger = Logger.getRootLogger();

	private static IPmp2PipTcp _pipProxy;


	@BeforeClass
	public static void beforeClass() {
		_pipProxy = new Pmp2PipTcpImp("localhost", TestSettings.PMP_LISTENER_PORT_IN_PIP);
	}

	@Test
	public void testInitialRepresentation() throws Exception {
		// connect to PIP
		ConnectionManager.obtainConnection(_pipProxy);
		IContainer container = DummyMessageGen.createContainer();
		IData data = DummyMessageGen.createData();
		IStatus status = _pipProxy.initialRepresentation(container, data);
		_logger.debug("Received status: " + status);

		// disconnect from PIP
		ConnectionManager.releaseConnection(_pipProxy);

		Assert.assertNotNull(status);
	}
}
