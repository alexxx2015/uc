package uctests;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import de.tum.in.i22.pdp.PdpController;
import de.tum.in.i22.pdp.PdpSettings;
import de.tum.in.i22.pep2pdp.IPep2PdpFast;
import de.tum.in.i22.pep2pdp.Pep2PdpFastImp;
import de.tum.in.i22.uc.cm.IMessageFactory;
import de.tum.in.i22.uc.cm.MessageFactoryCreator;
import de.tum.in.i22.uc.cm.basic.PipDeployerBasic;
import de.tum.in.i22.uc.cm.datatypes.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.datatypes.IStatus;



public class TestPep2PdpCommunication {
	
	private static Logger _logger = Logger.getRootLogger();

	private static IPep2PdpFast _pdpProxy;
	private static int _pepListenerPortNum = 50007;
	private static int _pmpListenerPortNum = 50008;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Injector injector = Guice.createInjector(new PdpTestModule());
		PdpController pdp = injector.getInstance(PdpController.class);
		PdpSettings pdpSettings = pdp.getPdpSettings();
		pdpSettings.setPepListenerPortNum(_pepListenerPortNum);
		pdpSettings.setPmpListenerPortNum(_pmpListenerPortNum);
		pdp.start();
		
		try {
			_logger.debug("Pause the main thread for 1s (PDP starting).");
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			_logger.error("Main thread interrupted.", e);
		}
		
		_pdpProxy = new Pep2PdpFastImp("localhost", _pepListenerPortNum);
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
		_pdpProxy.connect();
		// notify event1
		IResponse response1 = _pdpProxy.notifyEvent(event1);
		_logger.debug("Received response as reply to event 1: " + response1);
		
		IResponse response2 = _pdpProxy.notifyEvent(event2);
		_logger.debug("Received response as reply to event 2: " + response2);
		// disconnect from pdp
		_pdpProxy.disconnect();
		
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
		_pdpProxy.connect();
		IPipDeployer pipDeployer = new PipDeployerBasic("nameXYZ");
		File file = FileUtils.toFile(TestPep2PdpCommunication.class.getResource("/test.jar"));
		byte[] jarFileBytes = FileUtils.readFileToByteArray(file);
		IStatus status = _pdpProxy.updateInformationFlowSemantics(
				pipDeployer,
				jarFileBytes,
				EConflictResolution.OVERWRITE);
		// disconnect from pdp
		_pdpProxy.disconnect();
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
