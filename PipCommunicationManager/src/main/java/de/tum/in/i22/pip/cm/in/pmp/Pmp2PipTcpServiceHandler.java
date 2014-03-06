package de.tum.in.i22.pip.cm.in.pmp;

import java.io.IOException;
import java.net.Socket;

import de.tum.in.i22.uc.cm.in.TcpServiceHandler;

public class Pmp2PipTcpServiceHandler extends TcpServiceHandler {

	public Pmp2PipTcpServiceHandler(int port) {
		super(port);
	}

	@Override
	protected void doHandleClientConnection(Socket clientSocket) throws IOException {
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
