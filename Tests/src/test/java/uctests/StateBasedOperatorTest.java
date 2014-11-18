package uctests;

import java.io.File;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.ConflictResolutionFlagBasic.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IResponse;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.settings.Settings;

public class StateBasedOperatorTest extends GenericTest {

	private static Logger _logger = LoggerFactory.getLogger(StateBasedOperatorTest.class);

	protected void deployPolicy(String file) throws Exception {
		String path = StateBasedOperatorTest.class.getResource(file).getPath();
		IStatus status = pmp.deployPolicyURIPmp(path);
		_logger.debug("deploying policy " + path + ": " + status);
	}


	protected void deployNewEventAndEmptyInitialContainer() {
		/*
		 * Deploy semantics for a new event "testEvent" that empties
		 * initialcontainer
		 */
		File file = FileUtils.getFile("src/test/resources/updateIF.jar");
		IStatus status = pip.updateInformationFlowSemantics(null, file, EConflictResolution.OVERWRITE);
		_logger.debug("deployment of new semantics....: " + status.toString());


		/*
		 *  Send "testEvent".
		 *  Set appropriate pep in order to match event
		 *  definition package.
		 */
		Map<String, String> dm = createDummyMap();
		dm.put(Settings.getInstance().getPep(), "test");
		IResponse response = pdp.notifyEventSync(mf.createActualEvent("TestEvent", dm));
		_logger.debug("Received response as reply to testEvent event:" + response);
		Assert.assertNotNull(response);
		Assert.assertTrue(response.isAuthorizationAction(EStatus.ALLOW));
	}


}
