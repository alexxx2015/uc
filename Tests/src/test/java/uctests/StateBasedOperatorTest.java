package uctests;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.ConflictResolutionFlagBasic.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IResponse;
import de.tum.in.i22.uc.cm.settings.Settings;

public class StateBasedOperatorTest extends GenericTest{

	private static Logger _logger = LoggerFactory
			.getLogger(StateBasedOperatorTest.class);

	@Before
	public void deployPolicy() throws Exception {
		String path = StateBasedOperatorTest.class.getResource(
				"/testStateBasedOperators.xml").getPath();
		_logger.debug("deploying policy " + path + "...");
		_logger.debug("deploying policy " + path + " result : "
				+ pmp.deployPolicyURIPmp(path));
		
		_logger.debug(pip.toString());
	}
	
	@Test
	public void testIsNotIn() throws Exception {
		sayMyName(Thread.currentThread().getStackTrace()[1].getMethodName());
		String eventName = "testSBOp";
		Map<String, String> map = createDummyMap();
		map.put("name", "secondContainer");
		map.put("curTest", "isNotIn");
		IEvent event = mf.createDesiredEvent(eventName, map);

		// 1: send the event. Param name should match but param value does not
		IResponse response1 = pdp.notifyEventSync(event);
		_logger.debug("1: Received response as reply to " + event + ":"
				+ response1);
		Assert.assertNotNull(response1);
		Assert.assertEquals(EStatus.ALLOW, response1.getAuthorizationAction()
				.getEStatus());

		// artificially add the same data to secondContainer
		pip.initialRepresentation(new NameBasic("secondContainer"),
				pip.getDataInContainer(new NameBasic("initialContainer")));

		// 2: send the event. This time param name and parm value match but
		// condition is false
		IResponse response2 = pdp.notifyEventSync(event);
		_logger.debug("2: Received response as reply to " + event + ":"
				+ response2);
		Assert.assertNotNull(response2);
		Assert.assertEquals(EStatus.ALLOW, response2.getAuthorizationAction()
				.getEStatus());

		deployNewEventAndEmptyInitialContainer();
		
		// 3: send the event. This time param name and parm value match and
		// condition should be true. thus the event should be inhibited
		IResponse response4 = pdp.notifyEventSync(event);
		_logger.debug("4: Received response as reply to " + event + ":"
				+ response4);
		Assert.assertNotNull(response4);
		Assert.assertEquals(EStatus.INHIBIT, response4.getAuthorizationAction()
				.getEStatus());

	}

	@Test
	public void testIsOnlyIn() throws Exception {
		sayMyName(Thread.currentThread().getStackTrace()[1].getMethodName());
		String eventName = "testSBOp";
		Map<String, String> map = createDummyMap();
		map.put("name", "initialContainer2");
		map.put("curTest", "isOnlyIn");
		IEvent event = mf.createDesiredEvent(eventName, map);

		// 1: send the event. Condition should be false, thus the event is
		// allowed
		IResponse response1 = pdp.notifyEventSync(event);
		_logger.debug("1: Received response as reply to " + event + ":"
				+ response1);
		Assert.assertNotNull(response1);
		Assert.assertEquals(EStatus.ALLOW, response1.getAuthorizationAction()
				.getEStatus());

		deployNewEventAndEmptyInitialContainer();

		// 3: send the event again. This time the only container with data myId
		// should be initialcontainer2
		IResponse response3 = pdp.notifyEventSync(event);
		_logger.debug("3: Received response as reply to " + event + ":"
				+ response3);
		Assert.assertNotNull(response3);
		Assert.assertEquals(EStatus.INHIBIT, response3.getAuthorizationAction()
				.getEStatus());

	}

	@Test
	public void testIsCombinedWith() throws Exception {
		sayMyName(Thread.currentThread().getStackTrace()[1].getMethodName());
		String eventName = "testSBOp";
		Map<String, String> map = createDummyMap();
		map.put("name", "initialContainer");
		map.put("name2", "initialContainer");
		map.put("curTest", "isCombinedWith");
		IEvent event = mf.createDesiredEvent(eventName, map);

		// 1: send the event. Condition should be true, thus the event is
		// inhibited
		IResponse response1 = pdp.notifyEventSync(event);
		_logger.debug("1: Received response as reply to " + event + ":"
				+ response1);
		Assert.assertNotNull(response1);
		Assert.assertEquals(EStatus.INHIBIT, response1.getAuthorizationAction()
				.getEStatus());

		deployNewEventAndEmptyInitialContainer();

		// 3: send the event again. This time there is no container in the system with both data myId
		// and myid3. therefore the event is allowed, cause the condition is false.
		IResponse response3 = pdp.notifyEventSync(event);
		_logger.debug("3: Received response as reply to " + event + ":"
				+ response3);
		Assert.assertNotNull(response3);
		Assert.assertEquals(EStatus.ALLOW, response3.getAuthorizationAction()
				.getEStatus());

	}

	private void deployNewEventAndEmptyInitialContainer() {
		// deploy semantics for a new event "testEvent" that empties
		// initialcontainer
		File file = FileUtils.getFile("src/test/resources/updateIF.jar");
		_logger.debug("deployment of new semantics....: "
				+ pip.updateInformationFlowSemantics(null, file,
						EConflictResolution.OVERWRITE));

		// send "testevent". set appropriate pep in order to match event
		// definition package.
		Map<String, String> dm = createDummyMap();
		dm.put(Settings.getInstance().getPep(), "test");
		IResponse response = pdp.notifyEventSync(mf.createActualEvent(
				"TestEvent", dm));
		_logger.debug("Received response as reply to testEvent event:"
				+ response);
		Assert.assertNotNull(response);
		Assert.assertEquals(EStatus.ALLOW, response.getAuthorizationAction()
				.getEStatus());
	}

	
}
