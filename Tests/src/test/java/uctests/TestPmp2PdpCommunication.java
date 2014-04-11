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

public class TestPmp2PdpCommunication {

	private static Logger _logger = LoggerFactory.getLogger(TestPmp2PdpCommunication.class);

	private static IAny2Pdp _pdp;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		_pdp = new PdpHandler();
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
		IStatus status = _pdp.revokePolicy("param1");
		_logger.debug("Received status: " + status);

		// check if status is not null
		// TODO Implementation is missing
//		Assert.assertNotNull(status);
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
