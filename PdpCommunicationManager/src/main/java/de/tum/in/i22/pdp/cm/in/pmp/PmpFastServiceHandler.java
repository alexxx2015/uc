package de.tum.in.i22.pdp.cm.in.pmp;
import java.net.Socket;

import de.tum.in.i22.uc.cm.in.FastServiceHandler;


public class PmpFastServiceHandler extends FastServiceHandler {

	public PmpFastServiceHandler(int port) {
		super(port);
	}

	@Override
	protected void doHandleClientConnection(Socket client) {
		PmpClientConnectionHandler pmpClientConnHandler = 
				new PmpClientConnectionHandler(client);
		//TODO improve this code, use new features introduced in java 1.7, thread pools
		Thread thread = new Thread(pmpClientConnHandler);
		thread.start();
	}
	
	@Override
	protected String getServerInfo() {
		return "PMPlistener";
	}
	
}
