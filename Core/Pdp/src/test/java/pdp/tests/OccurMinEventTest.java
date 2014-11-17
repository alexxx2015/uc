package pdp.tests;

import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IResponse;
import de.tum.in.i22.uc.pdp.core.PolicyDecisionPoint;

public class OccurMinEventTest extends TestParent {
	private static Logger _logger = LoggerFactory.getLogger(PDPJavaTest.class);

	private static PolicyDecisionPoint _pdp = null;
	private static Map<String,String> _params = new HashMap<>();

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

	@SuppressWarnings("serial")
	@Test
	public void test() {
		createSettings(new HashMap<String,String>() {{

		}});

		_pdp = new PolicyDecisionPoint();

		/*
		 * This policy inhibits the event (action,{(name,value)}),
		 * if it happened at least two times in the last 1000 milliseconds.
		 * Evaluation is performed every 500 milliseconds.
		 */
		_pdp.deployPolicyURI("src/test/resources/testPolicies/testOccurMinEvent.xml");

		_params.clear();
		_params.put("name", "match");

		sleep(100);

		/*
		 * TEST 1.
		 * The event happens four times. The first occurrence is allowed,
		 * while the subsequent aren't. The events match both the trigger
		 * event and the event mentioned in the condition. Therefore,
		 * they event is happening way too often.
		 */

		IResponse r = _pdp.notifyEvent(new EventBasic("action", _params, true));
		Assert.assertTrue(r.isAuthorizationAction(EStatus.ALLOW));

		r = _pdp.notifyEvent(new EventBasic("action", _params, true));
		Assert.assertTrue(r.isAuthorizationAction(EStatus.INHIBIT));

		r = _pdp.notifyEvent(new EventBasic("action", _params, true));
		Assert.assertTrue(r.isAuthorizationAction(EStatus.INHIBIT));

		r = _pdp.notifyEvent(new EventBasic("action", _params, true));
		Assert.assertTrue(r.isAuthorizationAction(EStatus.INHIBIT));

		sleep(500);

		/*
		 * The following event happens in the next (2nd) timestep and does not
		 * even match the event within the condition. Still the event is inhibited,
		 * because the trigger event matches and the condition is still true.
		 */

		_params.put("name", "doesntmatch");
		r = _pdp.notifyEvent(new EventBasic("action", _params, true));
		Assert.assertTrue(r.isAuthorizationAction(EStatus.INHIBIT));

		sleep(500);

		/*
		 * The following event happens in the next (3rd) and matches both
		 * the trigger event and the event within the condition. The event is allowed,
		 * because the huge amount of earlier events is no longer relevant.
		 */

		_params.put("name", "match");
		r = _pdp.notifyEvent(new EventBasic("action", _params, true));
		Assert.assertTrue(r.isAuthorizationAction(EStatus.ALLOW));
	}

}
