package uctests;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.basic.ConditionBasic;
import de.tum.in.i22.uc.cm.basic.DataBasic;
import de.tum.in.i22.uc.cm.basic.DataEventMapBasic;
import de.tum.in.i22.uc.cm.basic.EventBasic;
import de.tum.in.i22.uc.cm.basic.MechanismBasic;
import de.tum.in.i22.uc.cm.basic.OslFormulaBasic;
import de.tum.in.i22.uc.cm.basic.SimplifiedTemporalLogicBasic;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pdp;
import de.tum.in.i22.uc.pdp.PdpHandler;

public class TestPmp2PdpCommunication {

	private static Logger _logger = LoggerFactory.getLogger(TestPmp2PdpCommunication.class);

	private static IAny2Pdp _pdp;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		_pdp = PdpHandler.getInstance();
	}

	@Test
	public void testDeployMechanism() throws Exception {

		// deploy mechanism
		IMechanism m = createMechanism();
		IStatus status = _pdp.deployMechanism(m);
		_logger.debug("Received status: " + status);


		// TODO Implementation is missing
//		Assert.assertNotNull(status);
	}

	@Test
	public void testDeployTwoMechanisms() throws Exception {
		// deploy mechanism
		IMechanism m = createMechanism();
		IStatus status = _pdp.deployMechanism(m);
		_logger.debug("Received status: " + status);

		// TODO Implementation is missing
//		Assert.assertNotNull(status);

		m = createMechanism();

		status = _pdp.deployMechanism(m);
		_logger.debug("Received status: " + status);

		// TODO Implementation is missing
//		Assert.assertNotNull(status);
	}

	@Test
	public void testExportMechanism() throws Exception {
		// revoke
		IMechanism mechanism = _pdp.exportMechanism("param2");
		_logger.debug("Received mechanism: " + mechanism);

		// TODO Implementation is missing
//		Assert.assertNotNull(mechanism);
	}

	@Test
	public void testRevokeMechanism() throws Exception {

		// revoke
		IStatus status = _pdp.revokeMechanism("param1");
		_logger.debug("Received status: " + status);

		// check if status is not null
		// TODO Implementation is missing
//		Assert.assertNotNull(status);
	}

	@Test
	public void testMultipleInvocations() throws Exception {
		for (int i = 0; i < 100; i++) {
			double r = Math.random();
			if (r <= 0.2) {
				testDeployMechanism();
			} else if (r <= 0.66) {
				testExportMechanism();
			} else {
				testRevokeMechanism();
			}
		}
	}

	private static IMechanism createMechanism() {
		MechanismBasic m = new MechanismBasic();

		// * set condition
		ConditionBasic condition = new ConditionBasic();
		// ** set condition condition
		OslFormulaBasic formula = new OslFormulaBasic("Formula xxxx");
		condition.setCondition(formula);
		// ** set condition conditionSimp
		SimplifiedTemporalLogicBasic conditionSimp = new SimplifiedTemporalLogicBasic();
		conditionSimp.setFormula(new OslFormulaBasic("Formula yyyy"));

		// *** set condition conditionSimp dataEventMap
		Map<IData, IEvent> map1 = new HashMap<IData, IEvent>();
		map1.put(new DataBasic("id1"), new EventBasic("event1", null));
		DataEventMapBasic dataEventMap = new DataEventMapBasic(map1);
		conditionSimp.setDataEventMap(dataEventMap);
		condition.setConditionSimp(conditionSimp);
		m.setCondition(condition);

		return m;
	}
}
