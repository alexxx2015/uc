package uctests;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.ConditionBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.DataBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.DataEventMapBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.MechanismBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.OslFormulaBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.SimplifiedTemporalLogicBasic;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pdp;
import de.tum.in.i22.uc.pdp.PdpHandler;

public class TestPmp2PdpCommunication extends GenericTest{

	private static Logger _logger = LoggerFactory.getLogger(TestPmp2PdpCommunication.class);


	@Test
	public void testExportMechanism() throws Exception {
		sayMyName(Thread.currentThread().getStackTrace()[1].getMethodName());

		// revoke
		IMechanism mechanism = pdp.exportMechanism("param2");
		_logger.debug("Received mechanism: " + mechanism);

		// TODO Implementation is missing
//		Assert.assertNotNull(mechanism);
	}

	@Test
	public void testRevokeMechanism() throws Exception {
		sayMyName(Thread.currentThread().getStackTrace()[1].getMethodName());
		// revoke
		IStatus status = pdp.revokePolicy("param1");
		_logger.debug("Received status: " + status);

		// check if status is not null
		// TODO Implementation is missing
//		Assert.assertNotNull(status);
	}


}
