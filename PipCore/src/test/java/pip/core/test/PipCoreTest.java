package pip.core.test;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tum.in.i22.pip.core.IPdp2Pip;
import de.tum.in.i22.pip.core.PipHandler;
import de.tum.in.i22.uc.cm.IMessageFactory;
import de.tum.in.i22.uc.cm.MessageFactoryCreator;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class PipCoreTest {
	
	private static IPdp2Pip _pipHandler;
	private static IMessageFactory _messageFactory;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		_pipHandler = new PipHandler();
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
	public void testReadFileActionMissingParameters() {
		IEvent event = _messageFactory.createActualEvent("ReadFile", null);
		IStatus status = _pipHandler.notifyActualEvent(event);
		Assert.assertEquals(EStatus.ERROR_EVENT_PARAMETER_MISSING, status.getEStatus());
	}
	
	@Test
	public void testReadFileAction() {
		Map<String, String> map = new HashMap<>();
		map.put("InFileName", "notes1.txt");
		map.put("PID", "8293");
		map.put("ProcessName", "notepad.exe");
		IEvent event = _messageFactory.createActualEvent("ReadFile", map);
		IStatus status = _pipHandler.notifyActualEvent(event);
		Assert.assertEquals(EStatus.OKAY, status.getEStatus());
	}

}
