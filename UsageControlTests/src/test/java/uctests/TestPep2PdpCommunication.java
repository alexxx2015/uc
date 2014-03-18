package uctests;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tum.in.i22.pep2pdp.Pep2PdpTcpImp;
import de.tum.in.i22.uc.cm.IMessageFactory;
import de.tum.in.i22.uc.cm.MessageFactoryCreator;
import de.tum.in.i22.uc.cm.basic.PipDeployerBasic;
import de.tum.in.i22.uc.cm.datatypes.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.interfaces.IPep2Pdp;
import de.tum.in.i22.uc.cm.out.ConnectionManager;



public class TestPep2PdpCommunication {

	private static Logger _logger = Logger.getRootLogger();

	private static IPep2Pdp _pdpProxy;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		_pdpProxy = new Pep2PdpTcpImp("localhost", TestSettings.PEP_LISTENER_PORT_NUM);
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

		// connect to pdp
		_pdpProxy = ConnectionManager.MAIN.obtain(_pdpProxy);
		// notify event1
		IResponse response1 = _pdpProxy.notifyEvent(event1);
		_logger.debug("Received response as reply to event 1: " + response1);

		IResponse response2 = _pdpProxy.notifyEvent(event2);
		_logger.debug("Received response as reply to event 2: " + response2);
		// disconnect from pdp
		ConnectionManager.MAIN.release(_pdpProxy);

		// check if status is not null
		Assert.assertNotNull(response1);
		Assert.assertNotNull(response2);
	}

	/**
	 * IfFlow stands for Information Flow
	 */
	@Test
	public void testUpdateIfFlowSemantics() throws Exception {
		// connect to pdp
		_pdpProxy = ConnectionManager.MAIN.obtain(_pdpProxy);
		IPipDeployer pipDeployer = new PipDeployerBasic("nameXYZ");
		File file = FileUtils.toFile(TestPep2PdpCommunication.class.getResource("/test.jar"));
		byte[] jarFileBytes = FileUtils.readFileToByteArray(file);
		IStatus status = _pdpProxy.updateInformationFlowSemantics(
				pipDeployer,
				jarFileBytes,
				EConflictResolution.OVERWRITE);
		// disconnect from pdp
		ConnectionManager.MAIN.release(_pdpProxy);
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
