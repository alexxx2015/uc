package helper;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;
import java.net.*;

public class PIPTCPServer {

	private static PIPTCPServer tcpServer;
	private IPIPCommunication pipLib;

	private List<PIPTCPConnectionHandler> connections = new ArrayList<PIPTCPConnectionHandler>();

	private boolean running = true;
	private final int PORT = 10001;

	private static Logger log = Logger.getLogger("PIP TCP");

	public PIPTCPServer(IPIPCommunication pipLib) {
		this.pipLib = pipLib;
	}

	// / <summary>
	// / Start PIP TCP Server with the default port 10001
	// / </summary>
	//FIXME: What to do with the Exception?
	public void startTCPServer() throws IOException {
		// TcpListener serverSocket;
		ServerSocket serverSocket;

		if (pipLib == null)
			return;

		// serverSocket = new TcpListener(IPAddress.Any, PORT);
		// in java the socket listens on every IP Adress by default.
		serverSocket = new ServerSocket(PORT);

		// TcpClient clientSocket = default(TcpClient);
		Socket clientSocket = null;

		running = true;

		while (running) {
			clientSocket = serverSocket.accept();

			PIPTCPConnectionHandler connection = new PIPTCPConnectionHandler(clientSocket, pipLib);
			connections.add(connection);

//			log.log(
//					Level.INFO,
//					"PIP: Accepted new client["
//							+ clientSocket.getLocalAddress()
//							+ clientSocket.getLocalPort() + "] to PIPinterface");
		
//			System.out.println("PIP: Accepted new client["
//					+ clientSocket.getLocalAddress()
//					+ clientSocket.getLocalPort() + "] to PIPinterface");
		}
		if (clientSocket != null)
		    clientSocket.close();
		serverSocket.close();
	}

	// / <summary>
	// / Tears down all open TCP connections.
	// / </summary>
	public void stopTCPServer() {
		for (PIPTCPConnectionHandler con : connections) {
			con.abortInternalThread();
		}
		running = false;
	}

	// / <summary>
	// / Returns the tcpServer singleton object.
	// / </summary>
	// / <param name="pipLib"></param>
	// / <returns></returns>
	public static PIPTCPServer getPIPTCPServerSingleton(IPIPCommunication pipLib) {
		if (tcpServer == null)
			tcpServer = new PIPTCPServer(pipLib);

		return tcpServer;
	}
}
