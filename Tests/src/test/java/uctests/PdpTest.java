package uctests;

import java.io.File;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.ConflictResolutionFlagBasic.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.PipDeployerBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IResponse;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.factories.IMessageFactory;
import de.tum.in.i22.uc.cm.factories.MessageFactoryCreator;

public class PdpTest extends GenericTest{

	private static Logger _logger = LoggerFactory.getLogger(PdpTest.class);

	// num of calls from pep thread
	private final static int NUM_OF_CALLS_FROM_PEP = 10;
	// num of calls from pmp thread
	private final static int NUM_OF_CALLS_FROM_PMP = 10;

	private static Thread _threadPep;
	private static Thread _threadPmp;

	public PdpTest() {
		// TODO Auto-generated constructor stub
	}

	@BeforeClass
    public static void beforeClass() throws Exception  {
		startPepClient();
	}

	@Test
	public void testNotifyTwoEvents() throws Exception {
		sayMyName(Thread.currentThread().getStackTrace()[1].getMethodName());

		// create event
		IMessageFactory mf = MessageFactoryCreator.createMessageFactory();
		String eventName1 = "event1";
		String eventName2 = "event2";
		Map<String, String> map = createDummyMap();
		IEvent event1 = mf.createEvent(eventName1, map);
		IEvent event2 = mf.createEvent(eventName2, map);

		IResponse response1 = pdp.notifyEventSync(event1);
		_logger.debug("Received response as reply to event 1: " + response1);

		IResponse response2 = pdp.notifyEventSync(event2);
		_logger.debug("Received response as reply to event 2: " + response2);


		// check if status is not null
		Assert.assertNotNull(response1);
		Assert.assertNotNull(response2);
	}
	


	@Test
	public void multipleInvocationsOfNotifyEvent() throws Exception {
		sayMyName(Thread.currentThread().getStackTrace()[1].getMethodName());

		for (int i = 0; i < 20; i++) {
			testNotifyTwoEvents();
		}
	}

	@Test
	public void testNotifyEventDelegatedToPipWhenActualEvent() throws Exception {
		sayMyName(Thread.currentThread().getStackTrace()[1].getMethodName());

		IEvent event = new EventBasic("x", Collections.EMPTY_MAP);

		IResponse response = pdp.notifyEventSync(event);

		Assert.assertNotNull(response);
	}

	/**
	 * IfFlow stands for Information Flow
	 */
	@Test
	public void testUpdateIfFlowSemantics() throws Exception {
		sayMyName(Thread.currentThread().getStackTrace()[1].getMethodName());

		IPipDeployer pipDeployer = new PipDeployerBasic("nameXYZ");
		File file = FileUtils.toFile(TestPep2PdpCommunication.class.getResource("/test.jar"));

		IStatus status = pip.updateInformationFlowSemantics(
				pipDeployer,
				file,
				EConflictResolution.OVERWRITE);

		Assert.assertEquals(EStatus.OKAY, status.getEStatus());
	}


	private static Thread startPepClient() {
		_threadPep = new Thread(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < NUM_OF_CALLS_FROM_PEP; i++) {
					String eventName1 = "event1";
					Map<String, String> map = createDummyMap();
					IMessageFactory mf = MessageFactoryCreator.createMessageFactory();
					IEvent event1 = mf.createEvent(eventName1, map);

					pdp.notifyEventSync(event1);

					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						_logger.error(e.toString());
					}
				}
			}
		});

		_threadPep.start();

		return _threadPep;
	}

}
