package de.tum.in.i22.cm.in.pip;
import java.io.IOException;
import java.net.Socket;

import de.tum.in.i22.uc.cm.in.TcpServiceHandler;


public class PipTcpServiceHandler extends TcpServiceHandler {

	public PipTcpServiceHandler(int port) {
		super(port);
	}

	@Override
	protected void doHandleClientConnection(Socket client) throws IOException {
		PipClientTcpConnectionHandler pipClientConnHandler =
				new PipClientTcpConnectionHandler(client);
		//TODO improve this code, use new features introduced in java 1.7, thread pools
		Thread thread = new Thread(pipClientConnHandler);
		thread.start();
	}

	@Override
	protected String getServerInfo() {
		return "PIPlistener";
	}

}
