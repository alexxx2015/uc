package pip.core.test;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tum.in.i22.pip.core.IPdp2Pip;
import de.tum.in.i22.pip.core.PipHandler;
import de.tum.in.i22.pip.core.manager.EConflictResolution;
import de.tum.in.i22.pip.core.manager.EventHandlerManager;
import de.tum.in.i22.pip.core.manager.IPipManager;
import de.tum.in.i22.pip.core.manager.PipManager;
import de.tum.in.i22.uc.cm.IMessageFactory;
import de.tum.in.i22.uc.cm.MessageFactoryCreator;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class PipCoreTest {
	
	private static IPdp2Pip _pipHandler;
	private static IMessageFactory _messageFactory;
	private static IPipManager _pipManager;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		EventHandlerManager actionHandlerManager = new EventHandlerManager();
		PipManager pipManager = new PipManager(actionHandlerManager);
		pipManager.initialize();
		
		_pipManager = pipManager;
		
		
		_pipHandler = new PipHandler(actionHandlerManager);
		_messageFactory = MessageFactoryCreator.createMessageFactory();
		File file = FileUtils.toFile(PipCoreTest.class.getResource("test.jar"));
		_pipManager.updateInformationFlowSemantics(null, file, EConflictResolution.OVERWRITE);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		_pipManager.updateInformationFlowSemantics(null, new File("D:/temp/test.jar"), EConflictResolution.OVERWRITE);
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
	
	@Test
	public void testWriteFileAction() {
		Map<String, String> map = new HashMap<>();
		map.put("InFileName", "notes1.txt");
		map.put("PID", "1293");
		map.put("ProcessName", "notepad.exe");
		IEvent event = _messageFactory.createActualEvent("WriteFile", map);
		IStatus status = _pipHandler.notifyActualEvent(event);
		Assert.assertEquals(EStatus.OKAY, status.getEStatus());
	}
	
	@Test
	public void testCreateProcessAction() {
		Map<String, String> map = new HashMap<>();
		map.put("PID_Child", "8101");
        map.put("PID", "7192");
        map.put("VisibleWindows", "8412");
        map.put("ChildProcessName", "java");
        map.put("ParentProcessName", "eclipse");
        IEvent event = _messageFactory.createActualEvent("CreateProcess", map);
		IStatus status = _pipHandler.notifyActualEvent(event);
		Assert.assertEquals(EStatus.OKAY, status.getEStatus());
		_pipManager.updateInformationFlowSemantics(null, new File("D:/temp/test.jar"), EConflictResolution.OVERWRITE);
	}
	
	@Test
	public void testKillProcessAction() {
		Map<String, String> map = new HashMap<>();
		map.put("PID_Child", "8101");
        IEvent event = _messageFactory.createActualEvent("KillProcess", map);
		IStatus status = _pipHandler.notifyActualEvent(event);
		Assert.assertEquals(EStatus.OKAY, status.getEStatus());
	}
	
	@Test
	public void testSetClipboardDataAction() {
		Map<String, String> map = new HashMap<>();
		map.put("PID", "8101");
		map.put("ProcessName", "thunderbird");
        IEvent event = _messageFactory.createActualEvent("SetClipboardData", map);
		IStatus status = _pipHandler.notifyActualEvent(event);
		Assert.assertEquals(EStatus.OKAY, status.getEStatus());
	}

}
