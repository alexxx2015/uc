package pdp.tests;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IResponse;
import de.tum.in.i22.uc.pdp.core.PolicyDecisionPoint;

public class OccurMinEventTest {
	private static Logger _logger = LoggerFactory.getLogger(PDPJavaTest.class);

	private static PolicyDecisionPoint _pdp = null;

	private static Map<String,String> _params = new HashMap<>();

	private File settingsFile = new File("uc.properties");

	private static Map<String,String> params = new HashMap<>();

	@After
	public void destroySettings() {
		settingsFile.delete();
	}


	private void sleep(long millis) {
		if (millis <= 0) {
			return;
		}

		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			new RuntimeException(e.getMessage());
		}
	}

	private void exitOnException(Exception e) {
		e.printStackTrace();
		destroySettings();
		System.exit(1);
	}

	private void createSettings(Map<String,String> settings) {
		if (settingsFile.exists()) {
			throw new IllegalStateException("uc.properties file must not exist for this test to be executed.");
		}

		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(settingsFile));
		} catch (IOException e) {
			exitOnException(e);
		}

		for (Entry<String, String> s : settings.entrySet()) {
			try {
				writer.write(s.getKey() + "=" + s.getValue());
				writer.newLine();
			} catch (IOException e) {
				exitOnException(e);
			}
		}

		try {
			writer.flush();
			writer.close();
		} catch (IOException e) {
			exitOnException(e);
		}
	}

	private void toNextTimestep(PolicyDecisionPoint pdp) {
		sleep(100);
		pdp.deactivateMechanism("testOccurMinEvent", "testOccurMinEvent");
		sleep(100);
		pdp.activateMechanism("testOccurMinEvent", "testOccurMinEvent");
		sleep(100);
	}

	@SuppressWarnings("serial")
	@Test
	public void test() {
		/*
		 * ******************************************
		 * IMPORTANT NOTE:
		 *
		 * This test is subject to timing constraints.
		 * I performed several runs of the test and
		 * adjusted the sleeps carefully. It may be the
		 * case, however, the the test case fails on
		 * machines that are slower or faster than mine.
		 * In this case, please come talk to me. -FK-
		 * ******************************************
		 */

		createSettings(new HashMap<String,String>() {{
//			put("distributionEnabled","true");
		}});

		_pdp = new PolicyDecisionPoint();

		/*
		 * This policy inhibits the event (action,{(name,value)}),
		 * if it happened at least two times in the last 1000 milliseconds.
		 * Evaluation is performed every 500 milliseconds.
		 */
		_pdp.deployPolicyURI("src/test/resources/testPolicies/testOccurMinEvent.xml");

		params.clear();
		params.put("name", "match");

		toNextTimestep(_pdp);

		/*
		 * TEST 1.
		 * The event happens four times. The first occurrence is allowed,
		 * while the subsequent aren't. The events match both the trigger
		 * event and the event mentioned in the condition. Therefore,
		 * they event is happening way too often.
		 */

		IResponse r = _pdp.notifyEvent(new EventBasic("action", params, true));
		Assert.assertTrue(r.isAuthorizationAction(EStatus.ALLOW));

		r = _pdp.notifyEvent(new EventBasic("action", params, true));
		Assert.assertTrue(r.isAuthorizationAction(EStatus.INHIBIT));

		r = _pdp.notifyEvent(new EventBasic("action", params, true));
		Assert.assertTrue(r.isAuthorizationAction(EStatus.INHIBIT));

		r = _pdp.notifyEvent(new EventBasic("action", params, true));
		Assert.assertTrue(r.isAuthorizationAction(EStatus.INHIBIT));

		toNextTimestep(_pdp);

		/*
		 * The following event happens in the next (2nd) timestep and does not
		 * even match the event within the condition. Still the event is inhibited,
		 * because the trigger event matches and the condition is still true.
		 */

		params.put("name", "doesntmatch");
		r = _pdp.notifyEvent(new EventBasic("action", params, true));
		Assert.assertTrue(r.isAuthorizationAction(EStatus.INHIBIT));

		toNextTimestep(_pdp);

		/*
		 * The following event happens in the next (3rd) and matches both
		 * the trigger event and the event within the condition. The event is allowed,
		 * because the huge amount of earlier events is no longer relevant.
		 */

		params.put("name", "match");
		r = _pdp.notifyEvent(new EventBasic("action", params, true));
		Assert.assertTrue(r.isAuthorizationAction(EStatus.ALLOW));
	}

}
