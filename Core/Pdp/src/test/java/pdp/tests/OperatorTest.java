package pdp.tests;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IResponse;
import de.tum.in.i22.uc.pdp.core.PolicyDecisionPoint;

public class OperatorTest extends TestParent {
	private static Logger log = LoggerFactory.getLogger(OperatorTest.class);
	private static PolicyDecisionPoint lpdp = null;

	private static Map<String, String> params = new HashMap<>();

	public OperatorTest() {
		log.info("OperatorTest");
		lpdp = new PolicyDecisionPoint();
		log.debug("lpdp: " + lpdp);
	}

	public void sleep(int mseconds) {
		try {
			log.debug("sleeping {} msecs", mseconds);
			Thread.sleep(mseconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// @Test
	public void testBefore() {
		log.info("testBefore");
		boolean ret = lpdp.deployPolicyURI("src/test/resources/testPolicies/testBefore.xml");
		log.debug("Deploying test-policy returned: {}", ret);
		assert (ret == true);

		log.debug("Starting before-test");
		params.clear();
		params.put("val1", "value1");
		IEvent levent = new EventBasic("action1", params, false);

		for (int a = 0; a < 3; a++) {
			log.info("Notifying event");
			IResponse d = lpdp.notifyEvent(levent);
			log.debug("Decision: {}", d);
			assert (a < 2 == (d.getAuthorizationAction().isStatus(EStatus.ALLOW)));

			sleep(3000);
		}
	}

	// @Test
	public void testWithin() {
		log.info("testWithin");
		boolean ret = lpdp.deployPolicyURI("src/test/resources/testPolicies/testWithin.xml");
		log.debug("Deploying test-policy returned: {}", ret);
		assert (ret == true);

		log.info("Starting within-test");
		params.clear();
		params.put("val1", "value1");
		IEvent levent = new EventBasic("action1", params, false);

		for (int a = 0; a < 3; a++) {
			log.info("Notifying event");
			IResponse d = lpdp.notifyEvent(levent);
			log.debug("Decision: {}", d);
			assert ((a == 0) == (d.getAuthorizationAction().isStatus(EStatus.ALLOW)));

			sleep(3000);
		}
	}

	// @Test
	public void testDuring() {
		log.info("testDuring");
		boolean ret = lpdp.deployPolicyURI("src/test/resources/testPolicies/testDuring.xml");
		log.debug("Deploying test-policy returned: {}", ret);
		assert (ret);

		log.info("Starting during-test");
		params.clear();
		params.put("val1", "value1");
		IEvent levent = new EventBasic("action1", params, false);

		sleep(3000);
		for (int a = 0; a < 5; a++) {
			log.info("Notifying event");
			IResponse d = lpdp.notifyEvent(levent);
			log.debug("Decision: {}", d);
			assert ((a < 4) == (d.getAuthorizationAction().isStatus(EStatus.ALLOW)));

			sleep(3000);
		}
	}

	// @Test
	public void testAlways() {
		log.info("testAlways");
		boolean ret = lpdp.deployPolicyURI("src/test/resources/testPolicies/testAlways.xml");
		log.debug("Deploying test-policy returned: {}", ret);
		assert (ret);

		log.info("Starting always-test");
		params.clear();
		params.put("val1", "value1");
		IEvent levent = new EventBasic("action1", params, false);

		for (int a = 0; a < 5; a++) {
			log.info("Notifying event");
			IResponse d = lpdp.notifyEvent(levent);
			log.debug("Decision: {}", d);
			assert ((a < 3) == (d.getAuthorizationAction().isStatus(EStatus.INHIBIT)));

			sleep(3000 + (a == 2 ? 3000 : 0));
		}
	}

	// @Test
	public void testRepMax() {
		log.info("testRepMax");
		boolean ret = lpdp.deployPolicyURI("src/test/resources/testPolicies/testRepmax.xml");
		log.debug("Deploying test-policy returned: {}", ret);
		assert (ret);

		log.info("Starting repmax-test");
		params.clear();
		params.put("val1", "value1");
		IEvent levent = new EventBasic("action1", params, false);

		sleep(3000);
		for (int a = 0; a < 5; a++) {
			log.info("Notifying event");
			IResponse d = lpdp.notifyEvent(levent);
			log.debug("Decision: {}", d);
			assert ((a < 3) == (d.getAuthorizationAction().isStatus(EStatus.INHIBIT)));

			sleep(3000);
		}
	}

	// @Test
	public void testRepLim() {
		log.info("testRepLim");
		boolean ret = lpdp.deployPolicyURI("src/test/resources/testPolicies/testReplim.xml");
		log.debug("Deploying test-policy returned: {}", ret);
		assert (ret);

		log.info("Starting replim-test");
		params.clear();
		params.put("val1", "value1");
		IEvent levent = new EventBasic("action1", params, false);

		sleep(1000);
		for (int a = 0; a < 5; a++) {
			log.debug("##################################");
			log.info("Notifying event");
			IResponse d = lpdp.notifyEvent(levent);
			log.debug("Decision: {}", d);
			assert ((a == 0 || a > 2) == (d.getAuthorizationAction().isStatus(EStatus.ALLOW)));
			log.debug("##################################");
			sleep(3000);
		}
		// wait 3 timesteps...
		sleep(9000);

		for (int a = 0; a < 5; a++) {
			log.debug("##################################");
			log.info("Notifying event");
			IResponse d = lpdp.notifyEvent(levent);
			log.debug("Decision: {}", d);
			assert ((a < 3) == (d.getAuthorizationAction().isStatus(EStatus.INHIBIT)));
			log.debug("##################################");
			sleep(3000);
		}
	}

	// @Test
	public void testSince() {
		try {
			log.info("testSince");
			boolean ret = lpdp.deployPolicyURI("src/test/resources/testPolicies/testSince.xml");
			log.debug("Deploying test-policy returned: {}", ret);
			assertTrue("Deploying test-policy failed", ret);

			log.info("Starting since-test");
			params.clear();
			params.put("val1", "value1");
			IEvent levent = new EventBasic("action1", params, false);

			params.clear();
			params.put("val2", "value2");
			IEvent levent2 = new EventBasic("action2", params, false);

			sleep(1000);
			for (int a = 0; a < 3; a++) {
				log.debug("##################################");
				log.info("Notifying event");
				IResponse d = lpdp.notifyEvent(levent2);
				log.debug("Decision: {}", d);
				// this event doesn't trigger any mechanism, not possible to
				// check the internal condition state which should be
				// set to TRUE due to this event...
				assertTrue(d.getAuthorizationAction().isStatus(EStatus.ALLOW));
				log.debug("##################################");
				sleep(3000);
			}
			// just wait some time...
			sleep(6000);
			log.debug("##################################");
			log.info("Notifying event");
			IResponse d = lpdp.notifyEvent(levent);
			log.debug("Decision: {}", d);
			assertTrue(d.getAuthorizationAction().isStatus(EStatus.INHIBIT));
			log.debug("##################################");

			sleep(3000);
			log.debug("##################################");
			log.info("Notifying event");
			d = lpdp.notifyEvent(levent);
			log.debug("Decision: {}", d);
			assertTrue(d.getAuthorizationAction().isStatus(EStatus.INHIBIT));
			log.debug("##################################");
			sleep(3000);

			for (int a = 0; a < 6; a++) {
				log.debug("##################################");
				log.info("Notifying event");
				d = lpdp.notifyEvent(levent2);
				log.debug("Decision: {}", d);
				assertTrue(d.getAuthorizationAction().isStatus(EStatus.ALLOW));
				log.debug("##################################");
				sleep(3000);
			}

			sleep(9000);
			log.debug("##################################");
			log.info("Notifying event");
			d = lpdp.notifyEvent(levent);
			log.debug("Decision: {}", d);
			assertTrue(d.getAuthorizationAction().isStatus(EStatus.INHIBIT));
			log.debug("##################################");
			sleep(3000);

			for (int a = 0; a < 3; a++) {
				log.debug("##################################");
				log.info("Notifying event");
				d = lpdp.notifyEvent(levent2);
				log.debug("Decision: {}", d);
				// this event doesn't trigger any mechanism, not possible to
				// check the internal condition state which should be
				// set to TRUE due to this event...
				// can be checked with some optional executeAction which should
				// be triggered at the end
				// of each timestep...
				assertTrue(d.getAuthorizationAction().isStatus(EStatus.ALLOW));
				log.debug("##################################");
				sleep(3000);
			}
		} catch (AssertionError e) {
			log.error("Assertion Error during test: " + e.getMessage());
			fail();
		}
	}

	// @Test
	public void testRepSince() {
		try {
			log.info("testRepSince");
			boolean ret = lpdp.deployPolicyURI("src/test/resources/testPolicies/testRepSince.xml");
			log.debug("Deploying test-policy returned: {}", ret);
			assertTrue("Deploying test-policy failed", ret);

			log.info("Starting repSince-test");
			params.clear();
			params.put("val1", "value1");
			IEvent levent = new EventBasic("action1", params, false);

			params.clear();
			params.put("val2", "value2");
			IEvent levent2 = new EventBasic("action2", params, false);

			sleep(1000);
			log.debug("##################################");
			log.info("Notifying event");
			IResponse d = lpdp.notifyEvent(levent);
			log.debug("Decision: {}", d);
			assertTrue(d.getAuthorizationAction().isStatus(EStatus.INHIBIT));
			log.debug("##################################");
			sleep(3000);

			for (int a = 0; a < 6; a++) {
				log.debug("##################################");
				log.info("Notifying event");
				d = lpdp.notifyEvent(levent2);
				log.debug("Decision: {}", d);
				assertTrue(d.getAuthorizationAction().isStatus(EStatus.ALLOW));
				log.debug("##################################");
				sleep(3000);
			}
			// just wait some time...
			sleep(6000);
			log.debug("##################################");
			log.info("Notifying event");
			d = lpdp.notifyEvent(levent);
			log.debug("Decision: {}", d);
			assertTrue(d.getAuthorizationAction().isStatus(EStatus.INHIBIT));
			log.debug("##################################");

			sleep(3000);
			for (int a = 0; a < 3; a++) {
				log.debug("##################################");
				log.info("Notifying event");
				d = lpdp.notifyEvent(levent2);
				log.debug("Decision: {}", d);
				assertTrue(d.getAuthorizationAction().isStatus(EStatus.ALLOW));
				log.debug("##################################");
				sleep(3000);
			}
		} catch (AssertionError e) {
			log.error("Assertion Error during test: " + e.getMessage());
			fail();
		}
	}

}
