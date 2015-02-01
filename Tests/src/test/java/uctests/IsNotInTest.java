package uctests;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IResponse;

public class IsNotInTest extends StateBasedOperatorTest {

	private static Logger _logger = LoggerFactory.getLogger(IsNotInTest.class);

	@Before
	public void deployPolicy() throws Exception {
		super.deployPolicy("/testIsNotIn.xml");
	}

	@Test
	public void testIsNotIn() throws Exception {
		/*
		 * IMPORTANT NOTE:
		 * This test is subject to timing constraints.
		 * The sleep() is crucial!
		 * -FK-
		 */

		sayMyName(Thread.currentThread().getStackTrace()[1].getMethodName());
		String eventName = "testSBOp";
		Map<String, String> map = createDummyMap();
		map.put("name", "secondContainer");
		IEvent event = mf.createDesiredEvent(eventName, map);

		/*
		 * 1st Test:
		 * Send the event
		 *  * Param name matches
		 *  * Param value does not match
		 *  ==> ALLOW
		 */
		IResponse response1 = pdp.notifyEventSync(event);
		_logger.debug("1: Received response as reply to " + event + ":" + response1);
		Assert.assertNotNull(response1);
		Assert.assertTrue(response1.isAuthorizationAction(EStatus.ALLOW));


		/*
		 * Copy the data from initialContainer to secondContainer
		 */
		pip.initialRepresentation(new NameBasic("secondContainer"),
				pip.getDataInContainer(new NameBasic("initialContainer")));


		/*
		 * 2nd Test:
		 * Send the event
		 *  * Param name matches
		 *  * Param value matches
		 *  * Condition is false
		 *  ==> ALLOW
		 */
		IResponse response2 = pdp.notifyEventSync(event);
		_logger.debug("2: Received response as reply to " + event + ":" + response2);
		Assert.assertNotNull(response2);
		Assert.assertTrue(response2.isAuthorizationAction(EStatus.ALLOW));


		/*
		 * Empty the initial container and
		 * wait for the next timestep. This
		 * sleep is crucial!
		 */
		deployNewEventAndEmptyInitialContainer();
		Thread.sleep(400);


		/*
		 * NEXT Timestep.
		 * 3rd Test:
		 * Send the event
		 *  * Param name matches
		 *  * Param value matches
		 *  * Condition is true
		 *  ==> INHIBIT
		 */
		IResponse response3 = pdp.notifyEventSync(event);
		_logger.debug("3: Received response as reply to " + event + ": " + response3);
		Assert.assertNotNull(response3);
		Assert.assertTrue(response3.isAuthorizationAction(EStatus.INHIBIT));

	}
}
