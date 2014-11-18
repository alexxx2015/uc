package uctests;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IResponse;

public class IsCombinedWithTest extends StateBasedOperatorTest {

	private static Logger _logger = LoggerFactory.getLogger(IsCombinedWithTest.class);

	@Before
	public void deployPolicy() throws Exception {
		super.deployPolicy("/testIsCombinedWith.xml");
	}

	@Test
	public void testIsCombinedWith() throws Exception {
		/*
		 * IMPORTANT NOTE:
		 * This test is subject to timing constraints.
		 * The sleep() is crucial!
		 * -FK-
		 */

		sayMyName(Thread.currentThread().getStackTrace()[1].getMethodName());
		String eventName = "testSBOp";
		Map<String, String> map = createDummyMap();
		map.put("name", "initialContainer");
		map.put("name2", "initialContainer");
		IEvent event = mf.createDesiredEvent(eventName, map);

		/*
		 * 1st Test:
		 * Send the event
		 *  * Condition is true
		 *  ==> INHIBIT
		 */
		IResponse response1 = pdp.notifyEventSync(event);
		_logger.debug("1: Received response as reply to " + event + ": " + response1);
		Assert.assertNotNull(response1);
		Assert.assertTrue(response1.isAuthorizationAction(EStatus.INHIBIT));


		/*
		 * Empty the initial container and
		 * wait for the next timestep. This
		 * sleep is crucial!
		 */
		deployNewEventAndEmptyInitialContainer();
		Thread.sleep(400);


		/*
		 * 2nd Test:
		 * Send the event
		 *  * There is no container containing both myId2 and myId3,
		 *  * hence the Condition is false
		 *  ==> ALLOW
		 */
		IResponse response2 = pdp.notifyEventSync(event);
		_logger.debug("2: Received response as reply to " + event + ": " + response2);
		Assert.assertNotNull(response2);
		Assert.assertTrue(response2.isAuthorizationAction(EStatus.ALLOW));

	}
}
