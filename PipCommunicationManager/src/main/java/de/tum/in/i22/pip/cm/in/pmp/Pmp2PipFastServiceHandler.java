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
		// invoke run directly (do not create separate thread)
		// this means that the requests from PMP will be served sequentially 
		pmpClientConnHandler.run();
	}

	@Override
	protected String getServerInfo() {
		return "PMPlistener";
	}
}