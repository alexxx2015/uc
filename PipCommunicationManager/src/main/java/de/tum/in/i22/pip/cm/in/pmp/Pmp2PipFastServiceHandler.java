package de.tum.in.i22.pip.cm.in.pmp;

import java.net.Socket;

import de.tum.in.i22.uc.cm.in.FastServiceHandler;

public class Pmp2PipFastServiceHandler extends FastServiceHandler {
	
	public Pmp2PipFastServiceHandler(int port) {
		super(port);
	}

	@Override
	protected void doHandleClientConnection(Socket clientSocket) {
		Pmp2PipClientConnectionHandler pmpClientConnHandler = 
				new Pmp2PipClientConnectionHandler(clientSocket);
		//TODO improve this code, use new features introduced in java 1.7, thread pools
		Thread thread = new Thread(pmpClientConnHandler);
		thread.start();
	}

	@Override
	protected String getServerInfo() {
		return "PMPlistener";
	}
}
