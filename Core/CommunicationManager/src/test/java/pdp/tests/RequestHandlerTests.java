package pdp.tests;

import org.junit.Test;

import de.tum.in.i22.uc.cm.handlers.NativeHandler;

public class RequestHandlerTests {
	@Test
	public void testNativeHandler() throws InterruptedException {
		String[] paramKeys = {"p1"};
		String[] paramVals = {"v1"};

		/*
		 * As of now, this test should just succeed, without
		 * throwing an exception, going into a while(true)-loop, etc.
		 */

		NativeHandler.notifyEvent("ev1", paramKeys, paramVals, false);
		NativeHandler.notifyEvent("ev1", paramKeys, paramVals, false);
		NativeHandler.notifyEvent("ev1", paramKeys, paramVals, false);
	}
}
