package pdp.tests;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.pdp.core.PolicyDecisionPoint;
import de.tum.in.i22.uc.pdp.core.shared.Decision;
import de.tum.in.i22.uc.pdp.core.shared.Event;

public class PDPJavaTest {
	private static Logger log = LoggerFactory.getLogger(PDPJavaTest.class);

	private static PolicyDecisionPoint lpdp = null;

	private static Map<String,String> params = new HashMap<>();

	@Test
	public void test() {
		/*
		 * For a more detailed output remember to change the log level.
		 * As of now, it is disabled (level ERROR)
		 */

		Event e;
		Decision d;
		log.debug("PDPJavaTest");
		lpdp = new PolicyDecisionPoint();

		log.debug("lpdp: " + lpdp);
		boolean ret = lpdp.deployPolicyURI("src/test/resources/testTUM.xml");
		log.debug("Deploying policy returned: " + ret);

		log.debug("Deployed Mechanisms: [{}]", lpdp.listDeployedMechanisms());



		log.info("Test Condtion Parameter Match Operator:");

		log.debug("Notifying event");
		params.clear();
		params.put("name1", "xyz");
		params.put("name2", "value2");
		e = new Event("testCPMEvent", params, false, System.currentTimeMillis());
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);

		log.debug("Notifying event");
		params.clear();
		params.put("name1", "value123");
		params.put("name2", "value2");
		e = new Event("testCPMEvent", params, false, System.currentTimeMillis());
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);






		log.info("\nTest Element in list Comparison Operator:");

		log.debug("Notifying event");
		params.clear();
		params.put("name1", "15 17 18");
		e = new Event("testEvent", params, false, System.currentTimeMillis());
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);

		log.debug("Notifying event");
		params.clear();
		params.put("name1", "15 16 17 18");
		e = new Event("testEvent", params, false, System.currentTimeMillis());
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);






		log.info("\nTest Ends with Comparison Operator:");

		log.debug("Notifying event");
		params.clear();
		params.put("name1", "play");
		e = new Event("testEvent", params, false, System.currentTimeMillis());
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);

		log.debug("Notifying event");
		params.clear();
		params.put("name1", "playing");
		e = new Event("testEvent", params, false, System.currentTimeMillis());
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);




		log.info("\nTest Equals Comparison Operator:");

		log.debug("Notifying event");
		params.clear();
		params.put("name1", "the same");
		e = new Event("testEvent", params, false, System.currentTimeMillis());
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);

		log.debug("Notifying event");
		params.clear();
		params.put("name1", "not the same");
		e = new Event("testEvent", params, false, System.currentTimeMillis());
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);




		log.info("\nTest default Comparison Operator (equals):");

		log.debug("Notifying event");
		params.clear();
		params.put("name1", "default value");
		e = new Event("testEvent", params, false, System.currentTimeMillis());
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);

		log.debug("Notifying event");
		params.clear();
		params.put("name1", "not the default value");
		e = new Event("testEvent", params, false, System.currentTimeMillis());
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);






		log.info("\nTest Equals Ignore case Comparison Operator:");

		log.debug("Notifying event");
		params.clear();
		params.put("name1", "value2");
		e = new Event("testEvent", params, false, System.currentTimeMillis());
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);

		log.debug("Notifying event");
		params.clear();
		params.put("name1", "value");
		e = new Event("testEvent", params, false, System.currentTimeMillis());
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);




		log.info("\nTest Math Comparison Operators (gt, ge, lt and le):");

		log.debug("Notifying event");
		params.clear();
		params.put("name1", "33");
		e = new Event("testEventge", params, false, System.currentTimeMillis());
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);

		log.debug("Notifying event");
		params.clear();
		params.put("name1", "34");
		e = new Event("testEventge", params, false, System.currentTimeMillis());
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);

		log.debug("Notifying event");
		params.clear();
		params.put("name1", "35");
		e = new Event("testEventge", params, false, System.currentTimeMillis());
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);

		log.debug("Notifying event");
		params.clear();
		params.put("name1", "55");
		e = new Event("testEventgt", params, false, System.currentTimeMillis());
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);

		log.debug("Notifying event");
		params.clear();
		params.put("name1", "56");
		e = new Event("testEventgt", params, false, System.currentTimeMillis());
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);

		log.debug("Notifying event");
		params.clear();
		params.put("name1", "57");
		e = new Event("testEventgt", params, false, System.currentTimeMillis());
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);

		log.debug("Notifying event");
		params.clear();
		params.put("name1", "33");
		e = new Event("testEventle", params, false, System.currentTimeMillis());
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);

		log.debug("Notifying event");
		params.clear();
		params.put("name1", "34");
		e = new Event("testEventle", params, false, System.currentTimeMillis());
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);

		log.debug("Notifying event");
		params.clear();
		params.put("name1", "35");
		e = new Event("testEventle", params, false, System.currentTimeMillis());
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);

		log.debug("Notifying event");
		params.clear();
		params.put("name1", "55");
		e = new Event("testEventlt", params, false, System.currentTimeMillis());
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);

		log.debug("Notifying event");
		params.clear();
		params.put("name1", "56");
		e = new Event("testEventlt", params, false, System.currentTimeMillis());
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);

		log.debug("Notifying event");
		params.clear();
		params.put("name1", "57");
		e = new Event("testEventlt", params, false, System.currentTimeMillis());
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);



		log.info("\nTest List in list Comparison Operator:");


		log.debug("Notifying event");
		params.clear();
		params.put("name1", "D E F");
		e = new Event("testEventList", params, false, System.currentTimeMillis());
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);

		log.debug("Notifying event");
		params.clear();
		params.put("name1", "A B D E F");
		e = new Event("testEventList", params, false, System.currentTimeMillis());
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);

		log.debug("Notifying event");
		params.clear();
		params.put("name1", "D A E B C F");
		e = new Event("testEventList", params, false, System.currentTimeMillis());
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);


		log.info("\nTest Not Equals Comparison Operator:");

		log.debug("Notifying event");
		params.clear();
		params.put("name1", "ABC");
		e = new Event("testEventNeq", params, false, System.currentTimeMillis());
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);

		log.debug("Notifying event");
		params.clear();
		params.put("name1", "Banana");
		e = new Event("testEventNeq", params, false, System.currentTimeMillis());
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);



		log.info("\nTest Starts With Comparison Operator:");

		log.debug("Notifying event");
		params.clear();
		params.put("name1", "RABC");
		e = new Event("testEventSW", params, false, System.currentTimeMillis());
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);

		log.debug("Notifying event");
		params.clear();
		params.put("name1", "ABCR");
		e = new Event("testEventSW", params, false, System.currentTimeMillis());
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);




		log.info("Test Substring Comparison Operator:");

		log.debug("Notifying event");
		params.clear();
		params.put("name1", "ARBRC");
		e = new Event("testEventSubstr", params, false, System.currentTimeMillis());
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);

		log.debug("Notifying event");
		params.clear();
		params.put("name1", "RABCR");
		e = new Event("testEventSubstr", params, false, System.currentTimeMillis());
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);

		log.debug("revoking test mechanism...");
		lpdp.revokePolicy("testPolicy");
		log.info("terminating...");

		assert (true);
	}


	@Test
	public void testDeployPolicy() {
		String policyName = "testPolicy";
		String policyURI = "src/test/resources/testTUM.xml";

		Set<String> deployedMechanisms1;
		Set<String> deployedMechanisms2;

		/*
		 * Create a new PDP and deploy a policy
		 */
		lpdp = new PolicyDecisionPoint();
		lpdp.deployPolicyURI(policyURI);

		/*
		 * Test 1: Retrieve all deployed mechanisms. There should be at
		 * least one mechanism that has been deployed.
		 */
		deployedMechanisms1 = lpdp.listDeployedMechanisms().get(policyName);
		Assert.assertTrue(deployedMechanisms1.size() > 0);

		/*
		 * Test 2: Revoke a mechanism and test whether exactly one mechanism has been revoked.
		 */
		lpdp.revokeMechanism(policyName, (new LinkedList<String>(deployedMechanisms1)).getFirst());
		deployedMechanisms2 = lpdp.listDeployedMechanisms().get(policyName);
		Assert.assertEquals(deployedMechanisms1.size() - 1, deployedMechanisms2.size());

		/*
		 * Test 3: Revoke a policy. No more mechanisms must be deployed thereafter.
		 */
		lpdp.revokePolicy(policyName);
		Assert.assertEquals(null, lpdp.listDeployedMechanisms().get(policyName));

		/*
		 * Test 4: Re-deploying the initial policy must result in the same amount of
		 * mechanisms to be deployed as in the beginning
		 */
		lpdp.deployPolicyURI(policyURI);
		deployedMechanisms2 = lpdp.listDeployedMechanisms().get(policyName);
		Assert.assertEquals(deployedMechanisms1.size(), deployedMechanisms2.size());
	}

}
