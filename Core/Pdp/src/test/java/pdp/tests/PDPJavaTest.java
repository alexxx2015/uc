package pdp.tests;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.pdp.core.PolicyDecisionPoint;
import de.tum.in.i22.uc.pdp.core.shared.Decision;
import de.tum.in.i22.uc.pdp.core.shared.Event;
import de.tum.in.i22.uc.pdp.core.shared.IPolicyDecisionPoint;

public class PDPJavaTest {
	private static Logger log = LoggerFactory.getLogger(PDPJavaTest.class);

	private static IPolicyDecisionPoint lpdp = null;

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


				
		System.out.println("\nTest Condtion Parameter Match Operator:");

		log.debug("Notifying event");
		e = new Event("testCPMEvent", true, System.currentTimeMillis());
		e.addStringParameter("name1", "xyz");
		e.addStringParameter("name2", "value2");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		System.out.println(e + "\n" + d + "\n");

		log.debug("Notifying event");
		e = new Event("testCPMEvent", true, System.currentTimeMillis());
		e.addStringParameter("name1", "value123");
		e.addStringParameter("name2", "value2");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		System.out.println(e + "\n" + d + "\n");

		
		
		
		
		
		System.out.println("\nTest Element in list Comparison Operator:");

		log.debug("Notifying event");
		e = new Event("testEvent", true, System.currentTimeMillis());
		e.addStringParameter("name1", "15 17 18");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		System.out.println(e + "\n" + d + "\n");

		log.debug("Notifying event");
		e = new Event("testEvent", true, System.currentTimeMillis());
		e.addStringParameter("name1", "15 16 17 18");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		System.out.println(e + "\n" + d + "\n");


		
		
		
		
		System.out.println("\nTest Ends with Comparison Operator:");
		
		log.debug("Notifying event");
		e = new Event("testEvent", true, System.currentTimeMillis());
		e.addStringParameter("name1", "play");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		System.out.println(e + "\n" + d + "\n");

		log.debug("Notifying event");
		e = new Event("testEvent", true, System.currentTimeMillis());
		e.addStringParameter("name1", "playing");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		System.out.println(e + "\n" + d + "\n");

		
		
		
		System.out.println("\nTest Equals Comparison Operator:");

		log.debug("Notifying event");
		e = new Event("testEvent", true, System.currentTimeMillis());
		e.addStringParameter("name1", "the same");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		System.out.println(e + "\n" + d + "\n");

		log.debug("Notifying event");
		e = new Event("testEvent", true, System.currentTimeMillis());
		e.addStringParameter("name1", "not the same");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		System.out.println(e + "\n" + d + "\n");

		
		
		
		System.out.println("\nTest default Comparison Operator (equals):");

		log.debug("Notifying event");
		e = new Event("testEvent", true, System.currentTimeMillis());
		e.addStringParameter("name1", "default value");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		System.out.println(e + "\n" + d + "\n");

		log.debug("Notifying event");
		e = new Event("testEvent", true, System.currentTimeMillis());
		e.addStringParameter("name1", "not the default value");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		System.out.println(e + "\n" + d + "\n");

		
		
		
			
		
		System.out.println("\nTest Equals Ignore case Comparison Operator:");

		log.debug("Notifying event");
		e = new Event("testEvent", true, System.currentTimeMillis());
		e.addStringParameter("name1", "value2");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		System.out.println(e + "\n" + d + "\n");

		log.debug("Notifying event");
		e = new Event("testEvent", true, System.currentTimeMillis());
		e.addStringParameter("name1", "value");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		System.out.println(e + "\n" + d + "\n");

		
		
		
		System.out.println("\nTest Math Comparison Operators (gt, ge, lt and le):");
			
		log.debug("Notifying event");
		e = new Event("testEventge", true, System.currentTimeMillis());
		e.addStringParameter("name1", "33");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		System.out.println(e + "\n" + d + "\n");

		log.debug("Notifying event");
		e = new Event("testEventge", true, System.currentTimeMillis());
		e.addStringParameter("name1", "34");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		System.out.println(e + "\n" + d + "\n");

		log.debug("Notifying event");
		e = new Event("testEventge", true, System.currentTimeMillis());
		e.addStringParameter("name1", "35");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		System.out.println(e + "\n" + d + "\n");

		log.debug("Notifying event");
		e = new Event("testEventgt", true, System.currentTimeMillis());
		e.addStringParameter("name1", "55");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		System.out.println(e + "\n" + d + "\n");

		log.debug("Notifying event");
		e = new Event("testEventgt", true, System.currentTimeMillis());
		e.addStringParameter("name1", "56");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		System.out.println(e + "\n" + d + "\n");

		log.debug("Notifying event");
		e = new Event("testEventgt", true, System.currentTimeMillis());
		e.addStringParameter("name1", "57");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		System.out.println(e + "\n" + d + "\n");

		log.debug("Notifying event");
		e = new Event("testEventle", true, System.currentTimeMillis());
		e.addStringParameter("name1", "33");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		System.out.println(e + "\n" + d + "\n");

		log.debug("Notifying event");
		e = new Event("testEventle", true, System.currentTimeMillis());
		e.addStringParameter("name1", "34");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		System.out.println(e + "\n" + d + "\n");

		log.debug("Notifying event");
		e = new Event("testEventle", true, System.currentTimeMillis());
		e.addStringParameter("name1", "35");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		System.out.println(e + "\n" + d + "\n");

		log.debug("Notifying event");
		e = new Event("testEventlt", true, System.currentTimeMillis());
		e.addStringParameter("name1", "55");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		System.out.println(e + "\n" + d + "\n");

		log.debug("Notifying event");
		e = new Event("testEventlt", true, System.currentTimeMillis());
		e.addStringParameter("name1", "56");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		System.out.println(e + "\n" + d + "\n");

		log.debug("Notifying event");
		e = new Event("testEventlt", true, System.currentTimeMillis());
		e.addStringParameter("name1", "57");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		System.out.println(e + "\n" + d + "\n");

	
		
		System.out.println("\nTest List in list Comparison Operator:");

		
		log.debug("Notifying event");
		e = new Event("testEventList", true, System.currentTimeMillis());
		e.addStringParameter("name1", "D E F");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		System.out.println(e + "\n" + d + "\n");

		log.debug("Notifying event");
		e = new Event("testEventList", true, System.currentTimeMillis());
		e.addStringParameter("name1", "A B D E F");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		System.out.println(e + "\n" + d + "\n");

		log.debug("Notifying event");
		e = new Event("testEventList", true, System.currentTimeMillis());
		e.addStringParameter("name1", "D A E B C F");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		System.out.println(e + "\n" + d + "\n");

		
		System.out.println("\nTest Not Equals Comparison Operator:");
		
		log.debug("Notifying event");
		e = new Event("testEventNeq", true, System.currentTimeMillis());
		e.addStringParameter("name1", "ABC");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		System.out.println(e + "\n" + d + "\n");

		log.debug("Notifying event");
		e = new Event("testEventNeq", true, System.currentTimeMillis());
		e.addStringParameter("name1", "Banana");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		System.out.println(e + "\n" + d + "\n");

		
		
		System.out.println("\nTest Starts With Comparison Operator:");

		log.debug("Notifying event");
		e = new Event("testEventSW", true, System.currentTimeMillis());
		e.addStringParameter("name1", "RABC");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		System.out.println(e + "\n" + d + "\n");

		log.debug("Notifying event");
		e = new Event("testEventSW", true, System.currentTimeMillis());
		e.addStringParameter("name1", "ABCR");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		System.out.println(e + "\n" + d + "\n");

		
		
		
		System.out.println("\nTest Substring Comparison Operator:");

		log.debug("Notifying event");
		e = new Event("testEventSubstr", true, System.currentTimeMillis());
		e.addStringParameter("name1", "ARBRC");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		System.out.println(e + "\n" + d + "\n");

		log.debug("Notifying event");
		e = new Event("testEventSubstr", true, System.currentTimeMillis());
		e.addStringParameter("name1", "RABCR");
		d = lpdp.notifyEvent(e);
		log.debug("d: [{}]", d);
		System.out.println(e + "\n" + d + "\n");

		log.debug("revoking test mechanism...");
		ret = lpdp.revokePolicy("testPolicy");
		log.info("terminating...");

		assert (true);
	}

}
