package uctests;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import testutil.DummyMessageGen;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pip;
import de.tum.in.i22.uc.pip.core.PipHandler;

public class TestPmp2PipCommunication {

	private static Logger _logger = LoggerFactory.getLogger(TestPmp2PipCommunication.class);

	private static IAny2Pip _pip;


	@BeforeClass
	public static void beforeClass() {
		_pip = PipHandler.getInstance();
	}

	@Test
	public void testInitialRepresentation() throws Exception {
		IContainer container = DummyMessageGen.createContainer();
		IData data = DummyMessageGen.createData();
		IStatus status = _pip.initialRepresentation(container, data);
		_logger.debug("Received status: " + status);

		Assert.assertEquals(EStatus.OKAY, status.getEStatus());
	}
}
