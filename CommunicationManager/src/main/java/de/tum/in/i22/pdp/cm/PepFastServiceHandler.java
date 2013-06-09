package de.tum.in.i22.pdp.cm;

import java.io.IOException;
import java.net.Socket;

public class PepFastServiceHandler extends FastServiceHandler {
	
	public PepFastServiceHandler(int port) {
		super(port);
	}

	/**
	 * Loops until the the server should be closed.
	 */
	@Override
	public void run() {
		_running = initializeServer();
		if (_serverSocket != null) {
			while (isRunning()) {
				try {
					Socket client = _serverSocket.accept();
					
					_logger.info("Client connection from "
							+ client.getInetAddress().getHostName()
							+ " on port " + client.getPort());

					PepClientConnectionHandler pepClientConnHandler = new PepClientConnectionHandler(
							client);
					//TODO improve this code, use new features introduced in java 1.7, thread pools
					Thread thread = new Thread(pepClientConnHandler);
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
