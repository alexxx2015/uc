package de.tum.in.i22.pdp.cm.in.pip;
import java.net.Socket;

import de.tum.in.i22.uc.cm.in.FastServiceHandler;


public class PipFastServiceHandler extends FastServiceHandler {

	public PipFastServiceHandler(int port) {
		super(port);
	}

	@Override
	protected void doHandleClientConnection(Socket client) {
		PipClientConnectionHandler pipClientConnHandler =
				new PipClientConnectionHandler(client);
		//TODO improve this code, use new features introduced in java 1.7, thread pools
		Thread thread = new Thread(pipClientConnHandler);
		thread.start();
	}

	@Override
	protected String getServerInfo() {
		return "PIPlistener";
	}

}
