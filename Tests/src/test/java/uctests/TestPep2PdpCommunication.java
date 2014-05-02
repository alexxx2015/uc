package uctests;

import java.io.File;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.ConflictResolutionFlagBasic.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.basic.PipDeployerBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IResponse;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;

public class TestPep2PdpCommunication extends GenericTest{

	private static Logger _logger = LoggerFactory
			.getLogger(TestPep2PdpCommunication.class);

	@Test
	public void testNotifyTwoEvents() throws Exception {
		sayMyName(Thread.currentThread().getStackTrace()[1].getMethodName());
		// create event
		String eventName1 = "event1";
		String eventName2 = "event2";
		Map<String, String> map = createDummyMap();
		IEvent event1 = mf.createEvent(eventName1, map);
		IEvent event2 = mf.createEvent(eventName2, map);

		// notify event1
		IResponse response1 = pdp.notifyEventSync(event1);
		_logger.debug("Received response as reply to event 1: " + response1);

		IResponse response2 = pdp.notifyEventSync(event2);
		_logger.debug("Received response as reply to event 2: " + response2);

		// check if status is not null
		Assert.assertNotNull(response1);
		Assert.assertNotNull(response2);
	}

	@Test
	public void testDeployPolicyString() throws Exception {
		sayMyName(Thread.currentThread().getStackTrace()[1].getMethodName());
		_logger.debug (pmp.deployPolicyRawXMLPmp("<?xml version='1.0' standalone='yes' ?><policy xmlns=\"http://www.iese.fhg.de/pef/1.0/enforcementLanguage\" xmlns:tns=\"http://www.iese.fhg.de/pef/1.0/enforcementLanguage\" xmlns:a=\"http://www.iese.fhg.de/pef/1.0/action\" xmlns:e=\"http://www.iese.fhg.de/pef/1.0/event\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" name=\"testPolicy\"> <preventiveMechanism name=\"testTUM\"> <description>...</description> <timestep amount=\"10\" unit=\"SECONDS\" /> <trigger action=\"testEvent\" tryEvent=\"true\"> <paramMatch name=\"name1\" value=\"value1\" /> <paramMatch name=\"name2\" value=\"value2\" /> </trigger> <condition> <or> <and> <not> <false /> </not> <before amount=\"20\" unit=\"SECONDS\"> <true /> </before> </and> <implies> <within amount=\"30\" unit=\"SECONDS\"> <false /> </within> <during amount=\"15\" unit=\"SECONDS\"> <eventMatch action=\"testEvent\" tryEvent=\"false\"> <paramMatch name=\"param1\" value=\"value1\"/> </eventMatch> </during> </implies> </or> </condition> <authorizationAction name=\"default\" start=\"true\" fallback=\"fallback\"> <allow> <delay amount=\"5\" unit=\"MINUTES\" /> <modify> <parameter name=\"name1\" value=\"modified1\" /> <parameter name=\"name2\" value=\"blub\" /> </modify> <executeSyncAction name=\"deployPolicy\"> <parameter name=\"file\" value=\"abc.xml\" /> </executeSyncAction> </allow> </authorizationAction> <authorizationAction name=\"fallback\" fallback=\"fallback2\"> <allow> <modify> <parameter name=\"path\" value=\"dev/null\" /> </modify> </allow> </authorizationAction> <authorizationAction name=\"fallback2\"> <inhibit /> </authorizationAction> <executeAsyncAction name=\"log\" processor=\"pep\"> <parameter name=\"file\" value=\"abc.xml\" /> </executeAsyncAction> <executeAsyncAction name=\"log2\" processor=\"pxp\"> <parameter name=\"file\" value=\"abc.xml\" /> </executeAsyncAction> </preventiveMechanism> <preventiveMechanism name=\"prev2\"> <description>...</description> <timestep amount=\"10\" unit=\"SECONDS\" /> <trigger action=\"testEvent\" tryEvent=\"true\"> <paramMatch name=\"name1\" value=\"value1\" /> <paramMatch name=\"name2\" value=\"value2\" /> </trigger> <condition> <or> <and> <eval type=\"XPATH\"> <content>an arbitrary xpath formula</content> </eval> <eval type=\"MATH\"><content> 3+2=5 </content></eval> </and> <stateBasedFormula operator=\"isNotIn\" param1=\"src_java.io.FileInputStream.read()I\" param2=\"val2\" param3=\"val3\" /> </or> </condition> <authorizationAction name=\"default\" start=\"true\" fallback=\"fallback\"> <allow> <delay amount=\"5\" unit=\"MINUTES\" /> <modify> <parameter name=\"name1\" value=\"modified2\" /> </modify> <executeSyncAction name=\"blub\"> <a:parameter name=\"file\" value=\"bla.xml\" /> </executeSyncAction> </allow> </authorizationAction> </preventiveMechanism></policy>").toString());

	}

	@Test
	public void testDeployPolicyFile() throws Exception {
		sayMyName(Thread.currentThread().getStackTrace()[1].getMethodName());
		_logger.debug(pdp.deployPolicyURI("src/test/resources/testTUM.xml").toString());

	}


	/**
	 * IfFlow stands for Information Flow
	 */
	@Test
	public void testUpdateIfFlowSemantics() throws Exception {
		sayMyName(Thread.currentThread().getStackTrace()[1].getMethodName());
		IPipDeployer pipDeployer = new PipDeployerBasic("nameXYZ");
		File file = FileUtils.toFile(TestPep2PdpCommunication.class
				.getResource("/test.jar"));

		IStatus status = pip.updateInformationFlowSemantics(pipDeployer, file,
				EConflictResolution.OVERWRITE);

		Assert.assertEquals(EStatus.OKAY, status.getEStatus());
	}

	@Test
	public void multipleInvocationsOfNotifyEvent() throws Exception {
		sayMyName(Thread.currentThread().getStackTrace()[1].getMethodName());
		for (int i = 0; i < 20; i++) {
			testNotifyTwoEvents();
		}
	}

}
