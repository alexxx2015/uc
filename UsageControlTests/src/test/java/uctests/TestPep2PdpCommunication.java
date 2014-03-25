package uctests;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.IMessageFactory;
import de.tum.in.i22.uc.cm.MessageFactoryCreator;
import de.tum.in.i22.uc.cm.basic.PipDeployerBasic;
import de.tum.in.i22.uc.cm.datatypes.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pdp;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pip;
import de.tum.in.i22.uc.pdp.PdpHandler;
import de.tum.in.i22.uc.pip.core.PipHandler;



public class TestPep2PdpCommunication {

	private static Logger _logger = LoggerFactory.getLogger(TestPep2PdpCommunication.class);

	private static IAny2Pdp _pdp;
	private static IAny2Pip _pip;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		_pdp = PdpHandler.getInstance();
		_pip = PipHandler.getInstance();
	}

	@Test
	public void testNotifyTwoEvents() throws Exception{
		// create event
		IMessageFactory mf = MessageFactoryCreator.createMessageFactory();
		String eventName1 = "event1";
		String eventName2 = "event2";
		Map<String, String> map = createDummyMap();
		IEvent event1 = mf.createEvent(eventName1, map);
		IEvent event2 = mf.createEvent(eventName2, map);

		// notify event1
		IResponse response1 = _pdp.notifyEvent(event1);
		_logger.debug("Received response as reply to event 1: " + response1);

		IResponse response2 = _pdp.notifyEvent(event2);
		_logger.debug("Received response as reply to event 2: " + response2);

		// check if status is not null
		Assert.assertNotNull(response1);
		Assert.assertNotNull(response2);
	}

	/**
	 * IfFlow stands for Information Flow
	 */
	@Test
	public void testUpdateIfFlowSemantics() throws Exception {

		IPipDeployer pipDeployer = new PipDeployerBasic("nameXYZ");
		File file = FileUtils.toFile(TestPep2PdpCommunication.class.getResource("/test.jar"));

		IStatus status = _pip.updateInformationFlowSemantics(
				pipDeployer,
				file,
				EConflictResolution.OVERWRITE);

		Assert.assertEquals(EStatus.OKAY, status.getEStatus());
	}


	@Test
	public void multipleInvocationsOfNotifyEvent() throws Exception {
		for (int i = 0; i < 20; i++) {
			testNotifyTwoEvents();
		}
	}


	private  Map<String, String> createDummyMap() {
		Map<String, String> map = new HashMap<String, String>();
		// add some entries
		map.put("key1", "value1");
		map.put("key2", "value2");
		map.put("key3", "value3");
		return map;
	}

}
