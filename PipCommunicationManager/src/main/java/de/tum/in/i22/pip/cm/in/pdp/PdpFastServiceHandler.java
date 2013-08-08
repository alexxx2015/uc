package de.tum.in.i22.pip.cm.in.pdp;

import java.net.Socket;

import de.tum.in.i22.uc.cm.in.FastServiceHandler;

public class PdpFastServiceHandler extends FastServiceHandler {

	public PdpFastServiceHandler(int port) {
		super(port);
	}

	@Override
	protected void doHandleClientConnection(Socket clientSocket) {
		PdpClientConnectionHandler pdpClientConnHandler = 
				new PdpClientConnectionHandler(clientSocket);
		//TODO improve this code, use new features introduced in java 1.7, thread pools
		Thread thread = new Thread(pdpClientConnHandler);
		thread.start();
	}

	@Override
	protected String getServerInfo() {
		return "PDPlistener";
	}

}
