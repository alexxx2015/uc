package de.tum.in.i22.uc.pip.test;

import java.util.HashMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tum.in.i22.uc.cm.basic.DataBasic;
import de.tum.in.i22.uc.cm.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.linux.FiledescrName;
import de.tum.in.i22.uc.cm.datatypes.linux.ProcessName;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pip;
import de.tum.in.i22.uc.pip.PipHandler;
import de.tum.in.i22.uc.pip.core.ifm.BasicInformationFlowModel;
import de.tum.in.i22.uc.pip.core.ifm.InformationFlowModelManager;

public class LinuxEventTest {
	private static IAny2Pip _pipHandler = new PipHandler();

	private static final InformationFlowModelManager _ifModelManager = InformationFlowModelManager.getInstance();

	private static final BasicInformationFlowModel _ifModel = _ifModelManager.getBasicInformationFlowModel();

	private static final String PEP_PARAMETER_LINUX = "Linux";

	private final static String INET = "AF_INET";
	private final static String STREAM = "SOCK_STREAM";

	private final static int serverPid = 12345;
	private final static int clientPid = 5432;

	private final static int serverPort = 8080;
	private final static int clientPort = 56841;

	private final static String serverHost = "server";
	private final static String clientHost = "client";

	private final static String serverIP = "192.168.100.3";
	private final static String clientIP = "192.168.100.5";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
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

	private static IEvent createLinuxSocketEvent(final String host, final int pid,
					final String domain, final String type, final int fd, boolean isActual) {
		return new EventBasic("Socket",
				new HashMap<String, String>(){
						private static final long serialVersionUID = 1L;
					{
				        put(EventBasic.PEP_PARAMETER_KEY, PEP_PARAMETER_LINUX);
				        put("host", host);
				        put("pid", pid+"");
				        put("domain", domain);
				        put("type", type);
				        put("fd", fd+"");
				    }},
				isActual);
	}

	private static IEvent createLinuxAcceptEvent(final String host, final int pid, final String localIP,
					final int localPort, final String remoteIP, final int remotePort,
					final int oldfd, final int newfd, boolean isActual) {
		return new EventBasic("Accept",
				new HashMap<String, String>(){
						private static final long serialVersionUID = 1L;
					{
				        put(EventBasic.PEP_PARAMETER_KEY, PEP_PARAMETER_LINUX);
				        put("host", host);
				        put("pid", pid+"");
				        put("localIP", localIP);
				        put("localPort", localPort+"");
				        put("remoteIP", remoteIP);
				        put("remotePort", remotePort+"");
				        put("oldfd", oldfd+"");
				        put("newfd", newfd+"");
				    }},
				isActual);
	}

	private static IEvent createLinuxConnectEvent(final String host, final int pid, final String localIP,
			final int localPort, final String remoteIP, final int remotePort,
			final int fd, boolean isActual) {
		return new EventBasic("Connect",
				new HashMap<String, String>(){
						private static final long serialVersionUID = 1L;
					{
				        put(EventBasic.PEP_PARAMETER_KEY, PEP_PARAMETER_LINUX);
				        put("host", host);
				        put("pid", pid+"");
				        put("localIP", localIP);
				        put("localPort", localPort+"");
				        put("remoteIP", remoteIP);
				        put("remotePort", remotePort+"");
				        put("fd", fd+"");
				    }},
				isActual);
	}

	private static IEvent createLinuxWriteEvent(final String host, final int pid, final int fd, boolean isActual) {
		return new EventBasic("Write",
				new HashMap<String, String>(){
						private static final long serialVersionUID = 1L;
					{
				        put(EventBasic.PEP_PARAMETER_KEY, PEP_PARAMETER_LINUX);
				        put("host", host);
				        put("pid", pid+"");
				        put("fd", fd+"");
				    }},
				isActual);
	}

	private static IEvent createLinuxReadEvent(final String host, final int pid, final int fd, boolean isActual) {
		return new EventBasic("Read",
				new HashMap<String, String>(){
						private static final long serialVersionUID = 1L;
					{
				        put(EventBasic.PEP_PARAMETER_KEY, PEP_PARAMETER_LINUX);
				        put("host", host);
				        put("pid", pid+"");
				        put("fd", fd+"");
				    }},
				isActual);
	}

	private static IEvent createLinuxExecveEvent(final String host, final int pid, final String filename, boolean isActual) {
		return new EventBasic("Execve",
				new HashMap<String, String>(){
						private static final long serialVersionUID = 1L;
					{
				        put(EventBasic.PEP_PARAMETER_KEY, PEP_PARAMETER_LINUX);
				        put("host", host);
				        put("pid", pid+"");
				        put("filename", filename);
				    }},
				isActual);
	}


	@Test
	public void testLocalConnectionEstablishment() {
		IContainer clientCont;
		IContainer serverCont;

		IEvent eventServerSocket = createLinuxSocketEvent(serverHost, serverPid, INET, STREAM, 4, true);
		IEvent eventServerAccept = createLinuxAcceptEvent(serverHost, serverPid, serverIP, serverPort, serverIP, clientPort, 4, 5, true);

		// Note: As we want to simulate local behavior, we also use serverHost/serverIP for the client
		IEvent eventClientSocket = createLinuxSocketEvent(serverHost, clientPid, INET, STREAM, 3, true);
		IEvent eventClientConnect = createLinuxConnectEvent(serverHost, clientPid, serverIP, clientPort, serverIP, serverPort, 3, true);

		/*
		 * 1st test:
		 * Do connect() before accept().
		 * Test: Is the connection reflected in PIP (i.e. alias between the two)?
		 */
		_ifModelManager.reset();
		_pipHandler.update(eventClientSocket);
		_pipHandler.update(eventServerSocket);
		_pipHandler.update(eventClientConnect);
		_pipHandler.update(eventServerAccept);

		clientCont = _ifModel.getContainer(FiledescrName.create(serverHost, Integer.valueOf(clientPid), 3));
		serverCont = _ifModel.getContainer(FiledescrName.create(serverHost, Integer.valueOf(serverPid), 5));
		Assert.assertEquals(true, _ifModel.getAliasesFrom(clientCont).contains(serverCont));
		Assert.assertEquals(true, _ifModel.getAliasesFrom(serverCont).contains(clientCont));



		/*
		 * 2nd test:
		 * Do accept() before connect().
		 * Test: Is the connection reflected in PIP (i.e. alias between the two)?
		 */
		_ifModelManager.reset();
		_pipHandler.update(eventClientSocket);
		_pipHandler.update(eventServerSocket);
		_pipHandler.update(eventServerAccept);
		_pipHandler.update(eventClientConnect);

		clientCont = _ifModel.getContainer(FiledescrName.create(serverHost, Integer.valueOf(clientPid), 3));
		serverCont = _ifModel.getContainer(FiledescrName.create(serverHost, Integer.valueOf(serverPid), 5));
		Assert.assertEquals(true, _ifModel.getAliasesFrom(clientCont).contains(serverCont));
		Assert.assertEquals(true, _ifModel.getAliasesFrom(serverCont).contains(clientCont));
	}


	@Test
	public void testRemoteConnectionEstablishment() {
//		IContainer serverCont;
//		IContainer clientCont;
//
//		IEvent eventServerSocket = createLinuxSocketEvent(serverHost, serverPid, INET, STREAM, 4, true);
//		IEvent eventServerAccept = createLinuxAcceptEvent(serverHost, serverPid, serverIP, serverPort, clientIP, clientPort, 4, 5, true);
//
//		IEvent eventClientSocket = createLinuxSocketEvent(clientHost, clientPid, INET, STREAM, 3, true);
//		IEvent eventClientConnect = createLinuxConnectEvent(clientHost, clientPid, clientIP, clientPort, serverIP, serverPort, 3, true);
//
//		/*
//		 * 1st test:
//		 * We are the server and observe accept() only.
//		 * Test: Has the newly created socket/container an alias to another socket/container
//		 *       which is named remoteIP:remotePort:localIP:remotePort?
//		 */
//		_ifModel.reset();
//		_pipHandler.notifyActualEvent(eventServerSocket);
//		_pipHandler.notifyActualEvent(eventServerAccept);
//
//		serverCont = _ifModel.getContainer(FiledescrName.create(serverHost, Integer.valueOf(serverPid), 5));
//		clientCont = _ifModel.getContainer(SocketName.create(clientIP, Integer.valueOf(clientPort), serverIP, Integer.valueOf(serverPort)));
//		Assert.assertEquals(true, _ifModel.getAliasesFrom(serverCont).contains(clientCont));
//
//		/*
//		 * 2nd test:
//		 * We are the client and observe connect() only.
//		 * Test: Has the connected socket/container an alias to another socket/container
//		 *       which is named remoteIP:remotePort:localIP:remotePort?
//		 */
//		_ifModel.reset();
//		_pipHandler.notifyActualEvent(eventClientSocket);
//		_pipHandler.notifyActualEvent(eventClientConnect);
//
//		clientCont = _ifModel.getContainer(FiledescrName.create(clientHost, Integer.valueOf(clientPid), 3));
//		serverCont = _ifModel.getContainer(SocketName.create(serverIP, Integer.valueOf(serverPort), clientIP, Integer.valueOf(clientPort)));
//		Assert.assertEquals(true, _ifModel.getAliasesFrom(clientCont).contains(serverCont));
	}

	@Test
	public void testLocalSocketDataTransferBeforeConnectionEstablished() {
		IContainer procCont;
		IData data = new DataBasic("data");

		// We simulate local behavior. Thus only serverHost/serverPid are used.

		IEvent eventServerExecve = createLinuxExecveEvent(serverHost, serverPid, "/bin/server.exe", true);
		IEvent eventServerSocket = createLinuxSocketEvent(serverHost, serverPid, INET, STREAM, 4, true);
		IEvent eventServerAccept = createLinuxAcceptEvent(serverHost, serverPid, serverIP, serverPort, serverIP, clientPort, 4, 5, true);
		IEvent eventServerWrite = createLinuxWriteEvent(serverHost, serverPid, 5, false);
		IEvent eventServerRead = createLinuxReadEvent(serverHost, serverPid, 5, true);

		IEvent eventClientExecve = createLinuxExecveEvent(serverHost, clientPid, "/bin/client.exe", true);
		IEvent eventClientSocket = createLinuxSocketEvent(serverHost, clientPid, INET, STREAM, 3, true);
		IEvent eventClientConnect = createLinuxConnectEvent(serverHost, clientPid, serverIP, clientPort, serverIP, serverPort, 3, true);
		IEvent eventClientWrite = createLinuxWriteEvent(serverHost, clientPid, 3, false);
		IEvent eventClientRead = createLinuxReadEvent(serverHost, clientPid, 3, true);

		/*
		 * 1st test:
		 * Local order of events: server:accept(), server:write(), client:connect(), client:read()
		 * Test: Is server processes' data propagated to client process?
		 */
		_ifModelManager.reset();
		_pipHandler.update(eventServerExecve);
		_pipHandler.update(eventClientExecve);
		_ifModel.addData(data, _ifModel.getContainer(ProcessName.create(serverHost, serverPid)));
		_pipHandler.update(eventClientSocket);
		_pipHandler.update(eventServerSocket);
		_pipHandler.update(eventServerAccept);
		_pipHandler.update(eventServerWrite);
		_pipHandler.update(eventClientConnect);
		_pipHandler.update(eventClientRead);

		procCont = _ifModel.getContainer(ProcessName.create(serverHost, clientPid));
		Assert.assertEquals(true, _ifModel.getData(procCont).contains(data));


		/*
		 * 2nd test:
		 * Local order of events: client:connect(), client:write(), server:accept(), server:read()
		 * Test: Is client processes' data propagated to server process?
		 */
		_ifModelManager.reset();
		_pipHandler.update(eventServerExecve);
		_pipHandler.update(eventClientExecve);
		_ifModel.addData(data, _ifModel.getContainer(ProcessName.create(serverHost, clientPid)));
		_pipHandler.update(eventClientSocket);
		_pipHandler.update(eventServerSocket);
		_pipHandler.update(eventClientConnect);
		_pipHandler.update(eventClientWrite);
		_pipHandler.update(eventServerAccept);
		_pipHandler.update(eventServerRead);

		procCont = _ifModel.getContainer(ProcessName.create(serverHost, serverPid));
		Assert.assertEquals(true, _ifModel.getData(procCont).contains(data));
	}

	@Test
	public void testRemoteDataTransfer() {
		IData data = new DataBasic("data");

		IEvent eventServerExecve = createLinuxExecveEvent(serverHost, serverPid, "/bin/server.exe", true);
		IEvent eventServerSocket = createLinuxSocketEvent(serverHost, serverPid, INET, STREAM, 4, true);
		IEvent eventServerAccept = createLinuxAcceptEvent(serverHost, serverPid, serverIP, serverPort, clientIP, clientPort, 4, 5, true);
		IEvent eventServerWrite = createLinuxWriteEvent(serverHost, serverPid, 5, false);

		IEvent eventClientExecve = createLinuxExecveEvent(clientHost, clientPid, "/bin/client.exe", true);
		IEvent eventClientSocket = createLinuxSocketEvent(clientHost, clientPid, INET, STREAM, 3, true);
		IEvent eventClientConnect = createLinuxConnectEvent(clientHost, clientPid, clientIP, clientPort, serverIP, serverPort, 3, true);

//		_ifModelManager.reset();
//		_pipHandler.notifyActualEvent(eventServerExecve);
//		_pipHandler.notifyActualEvent(eventClientExecve);
//		_ifModel.addDataToContainer(data, _ifModel.getContainer(ProcessName.create(serverHost, serverPid)));
//		_pipHandler.notifyActualEvent(eventClientSocket);
//		_pipHandler.notifyActualEvent(eventServerSocket);
//		_pipHandler.notifyActualEvent(eventClientConnect);
//		System.out.println(_ifModel.niceString());
//		_pipHandler.notifyActualEvent(eventServerAccept);
//		System.out.println(_ifModel.niceString());
//		_pipHandler.notifyActualEvent(eventServerWrite);
//		System.out.println(_ifModel.niceString());
	}
}
