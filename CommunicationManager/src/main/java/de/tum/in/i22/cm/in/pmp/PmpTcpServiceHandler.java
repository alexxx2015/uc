package de.tum.in.i22.cm.in.pmp;
import java.io.IOException;
import java.net.Socket;

import de.tum.in.i22.uc.cm.in.TcpServiceHandler;


public class PmpTcpServiceHandler extends TcpServiceHandler {

	public PmpTcpServiceHandler(int port) {
		super(port);
	}

	@Override
	protected void doHandleClientConnection(Socket client) throws IOException {
		PmpClientConnectionHandler pmpClientConnHandler = new PmpClientTcpConnectionHandler(client);
		//TODO improve this code, use new features introduced in java 1.7, thread pools
		Thread thread = new Thread(pmpClientConnHandler);
		thread.start();
	}

	@Override
	protected String getServerInfo() {
		return "PMPlistener";
	}
}
