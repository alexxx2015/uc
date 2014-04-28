package uctests;

import java.util.Collections;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.DataBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pip;
import de.tum.in.i22.uc.pip.PipHandler;

public class TestPmp2PipCommunication extends GenericTest{

	private static Logger _logger = LoggerFactory.getLogger(TestPmp2PipCommunication.class);

	@Test
	public void testInitialRepresentation() throws Exception {
		sayMyName(Thread.currentThread().getStackTrace()[1].getMethodName());

		IData data = new DataBasic("x");
		IStatus status = pip.initialRepresentation(new NameBasic("X"), Collections.singleton(data));
		_logger.debug("Received status: " + status);

		Assert.assertEquals(EStatus.OKAY, status.getEStatus());
	}
}
