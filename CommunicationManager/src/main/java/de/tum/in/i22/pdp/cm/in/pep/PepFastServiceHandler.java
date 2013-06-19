package de.tum.in.i22.pdp.cm.in.pep;

import java.net.Socket;

import de.tum.in.i22.pdp.cm.in.FastServiceHandler;

public class PepFastServiceHandler extends FastServiceHandler {
	
	public PepFastServiceHandler(int port) {
		super(port);
	}
	
	@Override
	protected void doHandleClientConnection(Socket client) {
		PepClientConnectionHandler pepClientConnHandler = new PepClientConnectionHandler(
				client);
		//TODO improve this code, use new features introduced in java 1.7, thread pools
		Thread thread = new Thread(pepClientConnHandler);
		thread.start();
	}
}
