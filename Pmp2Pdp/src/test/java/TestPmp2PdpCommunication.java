import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;

import de.tum.in.i22.pdp.PdpController;
import de.tum.in.i22.pdp.datatypes.IData;
import de.tum.in.i22.pdp.datatypes.IEvent;
import de.tum.in.i22.pdp.datatypes.IMechanism;
import de.tum.in.i22.pdp.datatypes.basic.ConditionBasic;
import de.tum.in.i22.pdp.datatypes.basic.DataBasic;
import de.tum.in.i22.pdp.datatypes.basic.DataEventMapBasic;
import de.tum.in.i22.pdp.datatypes.basic.EventBasic;
import de.tum.in.i22.pdp.datatypes.basic.MechanismBasic;
import de.tum.in.i22.pdp.datatypes.basic.OslFormulaBasic;
import de.tum.in.i22.pdp.datatypes.basic.SimplifiedTemporalLogicBasic;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpStatus.EStatus;
import de.tum.in.i22.pmp2pdp.IPmp2PdpFast;
import de.tum.in.i22.pmp2pdp.Pmp2PdpFastImp;

public class TestPmp2PdpCommunication {

	private static Logger _logger = Logger.getRootLogger();

	private static IPmp2PdpFast _pdpProxy;

	static {
		PdpController pdp = new PdpController();
		pdp.start(50001, 50003);

		try {
			_logger.debug("Pause the main thread for 1s (PDP starting).");
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			_logger.error("Main thread interrupted.", e);
		}

		_pdpProxy = new Pmp2PdpFastImp("localhost", 50003);
	}

	@Test
	public void testDeployMechanism() throws Exception {
		// connect to pdp
		_pdpProxy.connect();

		// deploy mechanism
		IMechanism m = createMechanism();
		EStatus status = _pdpProxy.deployMechanism(m);
		_logger.debug("Received status: " + status);

		// disconnect from pdp
		_pdpProxy.disconnect();

		// check if status is not null
		Assert.assertNotNull(status);
	}
	
	@Test
	public void testDeployTwoMechanisms() throws Exception {
		// connect to pdp
		_pdpProxy.connect();

		// deploy mechanism
		IMechanism m = createMechanism();
		EStatus status = _pdpProxy.deployMechanism(m);
		_logger.debug("Received status: " + status);
		// check if status is not null
		Assert.assertNotNull(status);
		
		m = createMechanism();
		
		status = _pdpProxy.deployMechanism(m);
		_logger.debug("Received status: " + status);
		Assert.assertNotNull(status);
		

		// disconnect from pdp
		_pdpProxy.disconnect();


	}

	@Test
	public void testExportMechanism() throws Exception {
		// connect to pdp
		_pdpProxy.connect();

		// revoke
		IMechanism mechanism = _pdpProxy.exportMechanism("param2");
		_logger.debug("Received mechanism: " + mechanism);

		// disconnect from pdp
		_pdpProxy.disconnect();

		// check if status is not null
		Assert.assertNotNull(mechanism);
	}

	@Test
	public void testRevokeMechanism() throws Exception {
		// connect to pdp
		_pdpProxy.connect();

		// revoke
		EStatus status = _pdpProxy.revokeMechanism("param1");
		_logger.debug("Received status: " + status);

		// disconnect from pdp
		_pdpProxy.disconnect();

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
