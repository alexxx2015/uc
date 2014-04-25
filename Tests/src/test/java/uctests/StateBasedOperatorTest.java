package uctests;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import commonj.sdo.ChangeSummary.Setting;

import de.tum.in.i22.uc.cm.datatypes.basic.ConditionBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.ConflictResolutionFlagBasic.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.basic.DataBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.DataEventMapBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.MechanismBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.OslFormulaBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.PipDeployerBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.SimplifiedTemporalLogicBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IResponse;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.factories.IMessageFactory;
import de.tum.in.i22.uc.cm.factories.MessageFactoryCreator;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pdp;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pip;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pmp;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pdp.PdpHandler;
import de.tum.in.i22.uc.pip.PipHandler;
import de.tum.in.i22.uc.pmp.PmpHandler;

public class StateBasedOperatorTest {

	private static Logger _logger = LoggerFactory
			.getLogger(StateBasedOperatorTest.class);

	private static IAny2Pdp _pdp;
	private static IAny2Pip _pip;
	private static IAny2Pmp _pmp;
	private static IMessageFactory mf;

	public StateBasedOperatorTest() {
		// TODO Auto-generated constructor stub
	}

	@BeforeClass
	public static void beforeClass() {
		sayMyName("Initialization of StateBasedOperatorTest suite");
		
		PdpHandler pdpImp = new PdpHandler();
		PipHandler pipImp = new PipHandler();
		PmpHandler pmpImp = new PmpHandler();
		_pdp = pdpImp;
		_pip = pipImp;
		_pmp = pmpImp;

		pdpImp.init(pipImp, pmpImp);
		pipImp.init(pdpImp, pmpImp);
		pmpImp.init(pipImp, pdpImp);

		mf = MessageFactoryCreator.createMessageFactory();
	}

	@Test
	public void testIsNotIn() throws Exception {
		sayMyName("testIsNotIn");
		String eventName = "testSBOp";
		Map<String, String> map = createDummyMap();
		map.put("name", "secondContainer");
		map.put("curTest", "isNotIn");
		IEvent event = mf.createDesiredEvent(eventName, map);

		// 0: deploy policy
		String path = StateBasedOperatorTest.class.getResource(
				"/testStateBasedOperators.xml").getPath();
		_logger.debug("deploying policy " + path + "...");
		_logger.debug("deploying policy " + path + " result : "
				+ _pmp.deployPolicyURIPmp(path));

		// 1: send the event. Param name should match but param value does not
		IResponse response1 = _pdp.notifyEventSync(event);
		_logger.debug("1: Received response as reply to " + event + ":"
				+ response1);
		Assert.assertNotNull(response1);
		Assert.assertEquals(EStatus.ALLOW, response1.getAuthorizationAction()
				.getEStatus());

		// artificially add the same data to secondContainer
		_pip.initialRepresentation(new NameBasic("secondContainer"),
				_pip.getDataInContainer(new NameBasic("initialContainer")));

		// 2: send the event. This time param name and parm value match but
		// condition is false
		IResponse response2 = _pdp.notifyEventSync(event);
		_logger.debug("2: Received response as reply to " + event + ":"
				+ response2);
		Assert.assertNotNull(response2);
		Assert.assertEquals(EStatus.ALLOW, response2.getAuthorizationAction()
				.getEStatus());

		// deploy semantics for a new event "testEvent" that empties
		// initialcontainer
		File file = FileUtils.getFile("src/test/resources/updateIF.jar");
		_logger.debug("deployment of new semantics....: "
				+ _pip.updateInformationFlowSemantics(null, file,
						EConflictResolution.OVERWRITE));

		// send "testevent". set appropriate pep in order to match event
		// definition package.
		Map<String, String> dm = createDummyMap();
		dm.put(Settings.getInstance().getPepParameterKey(), "test");
		IResponse response3 = _pdp.notifyEventSync(mf.createActualEvent(
				"TestEvent", dm));
		_logger.debug("2.5: Received response as reply to testEvent event:"
				+ response3);
		Assert.assertNotNull(response3);
		Assert.assertEquals(EStatus.ALLOW, response3.getAuthorizationAction()
				.getEStatus());

		// 3: send the event. This time param name and parm value match and
		// condition should be true. thus the event should be inhibited
		IResponse response4 = _pdp.notifyEventSync(event);
		_logger.debug("4: Received response as reply to " + event + ":"
				+ response4);
		Assert.assertNotNull(response4);
		Assert.assertEquals(EStatus.INHIBIT, response4.getAuthorizationAction()
				.getEStatus());

	}

	@Test
	public void testIsOnlyIn() throws Exception {
		sayMyName("testIsOnlyIn");
		String eventName = "testSBOp";
		Map<String, String> map = createDummyMap();
		map.put("name", "initialContainer2");
		map.put("curTest", "isOnlyIn");
		IEvent event = mf.createDesiredEvent(eventName, map);

		// 0: deploy policy. This will create two containers (initialcontainer
		// and initialcontainer2) that contains data myId.
		String path = StateBasedOperatorTest.class.getResource(
				"/testStateBasedOperators.xml").getPath();
		_logger.debug("deploying policy " + path + "...");
		_logger.debug("deploying policy " + path + " result : "
				+ _pmp.deployPolicyURIPmp(path));

		_logger.debug(_pip.toString());
		
		
		// 1: send the event. Condition should be false, thus the event is
		// allowed
		IResponse response1 = _pdp.notifyEventSync(event);
		_logger.debug("1: Received response as reply to " + event + ":"
				+ response1);
		Assert.assertNotNull(response1);
		Assert.assertEquals(EStatus.ALLOW, response1.getAuthorizationAction()
				.getEStatus());

		// deploy semantics for a new event "testEvent" that empties
		// initialcontainer
		File file = FileUtils.getFile("src/test/resources/updateIF.jar");
		_logger.debug("deployment of new semantics....: "
				+ _pip.updateInformationFlowSemantics(null, file,
						EConflictResolution.OVERWRITE));

		// send "testevent". set appropriate pep in order to match event
		// definition package.
		Map<String, String> dm = createDummyMap();
		dm.put(Settings.getInstance().getPepParameterKey(), "test");
		IResponse response2 = _pdp.notifyEventSync(mf.createActualEvent(
				"TestEvent", dm));
		_logger.debug("2: Received response as reply to testEvent event:"
				+ response2);
		Assert.assertNotNull(response2);
		Assert.assertEquals(EStatus.ALLOW, response2.getAuthorizationAction()
				.getEStatus());

		// 3: send the event again. This time the only container with data myId
		// should be initialcontainer2
		IResponse response3 = _pdp.notifyEventSync(event);
		_logger.debug("3: Received response as reply to " + event + ":"
				+ response3);
		Assert.assertNotNull(response3);
		Assert.assertEquals(EStatus.INHIBIT, response3.getAuthorizationAction()
				.getEStatus());

	}

	@Test
	public void testIsCombinedWith() throws Exception {
		sayMyName("testIsCombinedWith");
		String eventName = "testSBOp";
		Map<String, String> map = createDummyMap();
		map.put("name", "initialContainer");
		map.put("name2", "initialContainer");
		map.put("curTest", "isCombinedWith");
		IEvent event = mf.createDesiredEvent(eventName, map);

		// 0: deploy policy. This will create two containers (initialcontainer
		// and initialcontainer2) that contains data myId.
		String path = StateBasedOperatorTest.class.getResource(
				"/testStateBasedOperators.xml").getPath();
		_logger.debug("deploying policy " + path + "...");
		_logger.debug("deploying policy " + path + " result : "
				+ _pmp.deployPolicyURIPmp(path));

		_logger.debug(_pip.toString());
		
		
		// 1: send the event. Condition should be true, thus the event is
		// inhibited
		IResponse response1 = _pdp.notifyEventSync(event);
		_logger.debug("1: Received response as reply to " + event + ":"
				+ response1);
		Assert.assertNotNull(response1);
		Assert.assertEquals(EStatus.INHIBIT, response1.getAuthorizationAction()
				.getEStatus());

		// deploy semantics for a new event "testEvent" that empties
		// initialcontainer
		File file = FileUtils.getFile("src/test/resources/updateIF.jar");
		_logger.debug("deployment of new semantics....: "
				+ _pip.updateInformationFlowSemantics(null, file,
						EConflictResolution.OVERWRITE));

		// send "testevent". set appropriate pep in order to match event
		// definition package.
		Map<String, String> dm = createDummyMap();
		dm.put(Settings.getInstance().getPepParameterKey(), "test");
		IResponse response2 = _pdp.notifyEventSync(mf.createActualEvent(
				"TestEvent", dm));
		_logger.debug("2: Received response as reply to testEvent event:"
				+ response2);
		Assert.assertNotNull(response2);
		Assert.assertEquals(EStatus.ALLOW, response2.getAuthorizationAction()
				.getEStatus());

		// 3: send the event again. This time there is no container in the system with both data myId
		// and myid3. therefore the event is allowed, cause the condition is false.
		IResponse response3 = _pdp.notifyEventSync(event);
		_logger.debug("3: Received response as reply to " + event + ":"
				+ response3);
		Assert.assertNotNull(response3);
		Assert.assertEquals(EStatus.ALLOW, response3.getAuthorizationAction()
				.getEStatus());

	}

	private Map<String, String> createDummyMap() {
		Map<String, String> map = new HashMap<String, String>();
		// add some entries
		map.put("key1", "value1");
		map.put("key2", "value2");
		map.put("key3", "value3");
		return map;
	}

	/**
	 * If you are wondering why the parameter of this function is called
	 * Heisenberg, you should watch better TV series. -E-
	 */

	private static void sayMyName(String Heisenberg) {
		System.out.println("");
		System.out.println("******************");
		System.out.println("******************");
		System.out.println(Heisenberg);
		System.out.println("******************");
		System.out.println("******************");
		System.out.println("");

	}
}
