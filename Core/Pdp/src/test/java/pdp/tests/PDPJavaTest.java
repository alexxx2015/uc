package pdp.tests;

import java.util.LinkedList;
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
		e = new Event("testCPMEvent", null, true, System.currentTimeMillis());
		e.addStringParameter("name1", "xyz");
		e.addStringParameter("name2", "value2");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);

		log.debug("Notifying event");
		e = new Event("testCPMEvent", null, true, System.currentTimeMillis());
		e.addStringParameter("name1", "value123");
		e.addStringParameter("name2", "value2");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);






		log.info("\nTest Element in list Comparison Operator:");

		log.debug("Notifying event");
		e = new Event("testEvent", null, true, System.currentTimeMillis());
		e.addStringParameter("name1", "15 17 18");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);

		log.debug("Notifying event");
		e = new Event("testEvent", null, true, System.currentTimeMillis());
		e.addStringParameter("name1", "15 16 17 18");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);






		log.info("\nTest Ends with Comparison Operator:");

		log.debug("Notifying event");
		e = new Event("testEvent", null, true, System.currentTimeMillis());
		e.addStringParameter("name1", "play");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);

		log.debug("Notifying event");
		e = new Event("testEvent", null, true, System.currentTimeMillis());
		e.addStringParameter("name1", "playing");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);




		log.info("\nTest Equals Comparison Operator:");

		log.debug("Notifying event");
		e = new Event("testEvent", null, true, System.currentTimeMillis());
		e.addStringParameter("name1", "the same");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);

		log.debug("Notifying event");
		e = new Event("testEvent", null, true, System.currentTimeMillis());
		e.addStringParameter("name1", "not the same");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);




		log.info("\nTest default Comparison Operator (equals):");

		log.debug("Notifying event");
		e = new Event("testEvent", null, true, System.currentTimeMillis());
		e.addStringParameter("name1", "default value");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);

		log.debug("Notifying event");
		e = new Event("testEvent", null, true, System.currentTimeMillis());
		e.addStringParameter("name1", "not the default value");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);






		log.info("\nTest Equals Ignore case Comparison Operator:");

		log.debug("Notifying event");
		e = new Event("testEvent", null, true, System.currentTimeMillis());
		e.addStringParameter("name1", "value2");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);

		log.debug("Notifying event");
		e = new Event("testEvent", null, true, System.currentTimeMillis());
		e.addStringParameter("name1", "value");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);




		log.info("\nTest Math Comparison Operators (gt, ge, lt and le):");

		log.debug("Notifying event");
		e = new Event("testEventge", null, true, System.currentTimeMillis());
		e.addStringParameter("name1", "33");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);

		log.debug("Notifying event");
		e = new Event("testEventge", null, true, System.currentTimeMillis());
		e.addStringParameter("name1", "34");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);

		log.debug("Notifying event");
		e = new Event("testEventge", null, true, System.currentTimeMillis());
		e.addStringParameter("name1", "35");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);

		log.debug("Notifying event");
		e = new Event("testEventgt", null, true, System.currentTimeMillis());
		e.addStringParameter("name1", "55");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);

		log.debug("Notifying event");
		e = new Event("testEventgt", null, true, System.currentTimeMillis());
		e.addStringParameter("name1", "56");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);

		log.debug("Notifying event");
		e = new Event("testEventgt", null, true, System.currentTimeMillis());
		e.addStringParameter("name1", "57");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);

		log.debug("Notifying event");
		e = new Event("testEventle", null, true, System.currentTimeMillis());
		e.addStringParameter("name1", "33");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);

		log.debug("Notifying event");
		e = new Event("testEventle", null, true, System.currentTimeMillis());
		e.addStringParameter("name1", "34");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);

		log.debug("Notifying event");
		e = new Event("testEventle", null, true, System.currentTimeMillis());
		e.addStringParameter("name1", "35");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);

		log.debug("Notifying event");
		e = new Event("testEventlt", null, true, System.currentTimeMillis());
		e.addStringParameter("name1", "55");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);

		log.debug("Notifying event");
		e = new Event("testEventlt", null, true, System.currentTimeMillis());
		e.addStringParameter("name1", "56");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);

		log.debug("Notifying event");
		e = new Event("testEventlt", null, true, System.currentTimeMillis());
		e.addStringParameter("name1", "57");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);



		log.info("\nTest List in list Comparison Operator:");


		log.debug("Notifying event");
		e = new Event("testEventList", null, true, System.currentTimeMillis());
		e.addStringParameter("name1", "D E F");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);

		log.debug("Notifying event");
		e = new Event("testEventList", null, true, System.currentTimeMillis());
		e.addStringParameter("name1", "A B D E F");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);

		log.debug("Notifying event");
		e = new Event("testEventList", null, true, System.currentTimeMillis());
		e.addStringParameter("name1", "D A E B C F");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);


		log.info("\nTest Not Equals Comparison Operator:");

		log.debug("Notifying event");
		e = new Event("testEventNeq", null, true, System.currentTimeMillis());
		e.addStringParameter("name1", "ABC");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);

		log.debug("Notifying event");
		e = new Event("testEventNeq", null, true, System.currentTimeMillis());
		e.addStringParameter("name1", "Banana");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);



		log.info("\nTest Starts With Comparison Operator:");

		log.debug("Notifying event");
		e = new Event("testEventSW", null, true, System.currentTimeMillis());
		e.addStringParameter("name1", "RABC");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);

		log.debug("Notifying event");
		e = new Event("testEventSW", null, true, System.currentTimeMillis());
		e.addStringParameter("name1", "ABCR");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);




		log.info("Test Substring Comparison Operator:");

		log.debug("Notifying event");
		e = new Event("testEventSubstr", null, true, System.currentTimeMillis());
		e.addStringParameter("name1", "ARBRC");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		log.info("e: " + e + "    d: " + d);

		log.debug("Notifying event");
		e = new Event("testEventSubstr", null, true, System.currentTimeMillis());
		e.addStringParameter("name1", "RABCR");
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
