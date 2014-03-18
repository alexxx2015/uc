package uctests;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tum.in.i22.pdp.core.IPmp2Pdp;
import de.tum.in.i22.pmp2pdp.Pmp2PdpTcpImp;
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
import de.tum.in.i22.uc.cm.out.ConnectionManager;

public class TestPmp2PdpCommunication {

	private static Logger _logger = Logger.getRootLogger();

	private static IPmp2Pdp _pdpProxy;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		_pdpProxy = new Pmp2PdpTcpImp("localhost", TestSettings.PMP_LISTENER_PORT_NUM);
	}

	@Test
	public void testDeployMechanism() throws Exception {
		// connect to pdp
		_pdpProxy = ConnectionManager.MAIN.obtain(_pdpProxy);

		// deploy mechanism
		IMechanism m = createMechanism();
		IStatus status = _pdpProxy.deployMechanism(m);
		_logger.debug("Received status: " + status);

		// disconnect from pdp
		ConnectionManager.MAIN.release(_pdpProxy);

		// check if status is not null
		Assert.assertNotNull(status);
	}

	@Test
	public void testDeployTwoMechanisms() throws Exception {
		// connect to pdp
		_pdpProxy = ConnectionManager.MAIN.obtain(_pdpProxy);

		// deploy mechanism
		IMechanism m = createMechanism();
		IStatus status = _pdpProxy.deployMechanism(m);
		_logger.debug("Received status: " + status);
		// check if status is not null
		Assert.assertNotNull(status);

		m = createMechanism();

		status = _pdpProxy.deployMechanism(m);
		_logger.debug("Received status: " + status);
		Assert.assertNotNull(status);


		// disconnect from pdp
		ConnectionManager.MAIN.release(_pdpProxy);


	}

	@Test
	public void testExportMechanism() throws Exception {
		// connect to pdp
		_pdpProxy = ConnectionManager.MAIN.obtain(_pdpProxy);

		// revoke
		IMechanism mechanism = _pdpProxy.exportMechanism("param2");
		_logger.debug("Received mechanism: " + mechanism);

		// disconnect from pdp
		ConnectionManager.MAIN.release(_pdpProxy);

		// check if status is not null
		Assert.assertNotNull(mechanism);
	}

	@Test
	public void testRevokeMechanism() throws Exception {
		// connect to pdp
		_pdpProxy = ConnectionManager.MAIN.obtain(_pdpProxy);

		// revoke
		IStatus status = _pdpProxy.revokeMechanism("param1");
		_logger.debug("Received status: " + status);

		// disconnect from pdp
		ConnectionManager.MAIN.release(_pdpProxy);

		// check if status is not null
		Assert.assertNotNull(status);
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
