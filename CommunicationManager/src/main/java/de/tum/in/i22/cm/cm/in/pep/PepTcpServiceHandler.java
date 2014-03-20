package de.tum.in.i22.cm.cm.in.pep;

import java.io.IOException;
import java.net.Socket;

import de.tum.in.i22.uc.cm.in.TcpServiceHandler;

public class PepTcpServiceHandler extends TcpServiceHandler {

	public PepTcpServiceHandler(int port) {
		super(port);
	}

	@Override
	protected void doHandleClientConnection(Socket client) throws IOException {
		PepClientConnectionHandler pepClientConnHandler = new PepClientTcpConnectionHandler(client);
		//TODO improve this code, use new features introduced in java 1.7, thread pools
		Thread thread = new Thread(pepClientConnHandler);
		thread.start();
	}

	@Override
	protected String getServerInfo() {
		return "PEPlistener";
	}
}
