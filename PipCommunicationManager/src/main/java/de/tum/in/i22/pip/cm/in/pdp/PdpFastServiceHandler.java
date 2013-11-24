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
		// invoke run directly (do not create separate thread)
		// this means that the requests from PDP will be served sequentially 
		pdpClientConnHandler.run();
	}

	@Override
	protected String getServerInfo() {
		return "PDPlistener";
	}

}
