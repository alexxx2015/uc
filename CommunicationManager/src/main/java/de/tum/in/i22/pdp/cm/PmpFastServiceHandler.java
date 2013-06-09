package de.tum.in.i22.pdp.cm;
import java.io.IOException;
import java.net.Socket;

import de.tum.in.i22.pdp.cm.FastServiceHandler;


public class PmpFastServiceHandler extends FastServiceHandler {

	public PmpFastServiceHandler(int port) {
		super(port);
	}

	/**
	 * Loops until the the server should be closed.
	 */
	@Override
	public void run() {
		_logger.debug("Run method entered.");
		_running = initializeServer();
		if (_serverSocket != null) {
			while (isRunning()) {
				try {
					_logger.debug("Wait pmp client connection.");
					Socket client = _serverSocket.accept();
					
					_logger.info("Client connection from "
							+ client.getInetAddress().getHostName()
							+ " on port " + client.getPort());

					PmpClientConnectionHandler pmpClientConnHandler = 
							new PmpClientConnectionHandler(client);
					//TODO improve this code, use new features introduced in java 1.7, thread pools
					Thread thread = new Thread(pmpClientConnHandler);
					thread.start();
				} catch (IOException e) {
					_logger.error("Error! "
							+ "Unable to establish connection. \n", e);
				}
			}
		}
		_logger.info("Server stopped.");
	}

}
