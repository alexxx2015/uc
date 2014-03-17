package pip.core.test;

import java.io.File;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tum.in.i22.pip.core.IPdp2Pip;
import de.tum.in.i22.pip.core.PipHandlerMock;
import de.tum.in.i22.uc.cm.IMessageFactory;
import de.tum.in.i22.uc.cm.MessageFactoryCreator;
import de.tum.in.i22.uc.cm.datatypes.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class PipCoreClassReloadingTest {

	private static final Logger _logger = Logger.getLogger(PipCoreClassReloadingTest.class);

	private static IPdp2Pip _pipHandler;
	private static IMessageFactory _messageFactory;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		_pipHandler = new PipHandlerMock();
		_messageFactory = MessageFactoryCreator.createMessageFactory();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		_logger.info("Test class reloading");

		// Classes that are present at startup at class path:
		// TestAe -> returns ERROR
		// TestBe -> returns ERROR_EVENT_PARAMETER_MISSING
		// TestCe -> not present
		// TestAe can be but must not be additionally present in the database and it returns ERROR
		// TestBe can be but must not be additionally present in the database and it returns ERROR_EVENT_PARAMETER_MISSING
		// Class definitions are present in the database if this test has been run already.
		// The database can be manually deleted.

		// 1. Invoke TestAe, return status is ERROR
		// 2. Execute update:
		//		TestBe -> returns ERROR
		//		TestCe -> returns ERROR_EVENT_PARAMETER_MISSING
		// 3. Invoke TestBe, return status is ERROR
		// 4. Invoke TestCe, return status is ERROR_EVEN_PARAMETER_MISSING
		// 5. Execute update:
		//		TestAe -> returns ERROR
		//		TestBe -> returns ERROR_EVENT_PARAMETER_MISSING
		//		TestCe -> returns ALLOW - // not every return status is possible, this is only for testing purposes
		// 6. Invoke TestCe -> returns ALLOW


		IEvent event = _messageFactory.createActualEvent("TestAe", null);
		IStatus status = _pipHandler.notifyActualEvent(event);
		Assert.assertEquals(EStatus.OKAY, status.getEStatus());

		_pipHandler.updateInformationFlowSemantics(null, getJarFile("eventHandlerDefTest.jar"), EConflictResolution.OVERWRITE);

		event = _messageFactory.createActualEvent("TestBe", null);
		status = _pipHandler.notifyActualEvent(event);
		Assert.assertEquals(EStatus.OKAY, status.getEStatus());

		event = _messageFactory.createActualEvent("TestCe", null);
		status = _pipHandler.notifyActualEvent(event);
		Assert.assertEquals(EStatus.OKAY, status.getEStatus());

		_pipHandler.updateInformationFlowSemantics(null, getJarFile("eventHandlerDefTest1.jar"), EConflictResolution.OVERWRITE);
		event = _messageFactory.createActualEvent("TestCe", null);
		status = _pipHandler.notifyActualEvent(event);
		Assert.assertEquals(EStatus.OKAY, status.getEStatus());

		//FIXME: change expected results previous tests

	}

	private static File getJarFile(String fileName) {
		File file = FileUtils.toFile(PipCoreTest.class.getResource("/" + fileName));
		return file;
	}

}
