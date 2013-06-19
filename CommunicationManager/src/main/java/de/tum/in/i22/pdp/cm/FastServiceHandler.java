package de.tum.in.i22.pdp.cm;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;

import org.apache.log4j.Logger;

public abstract class FastServiceHandler implements
		Runnable {

	protected static Logger _logger = Logger.getRootLogger();

	protected int _port;
	protected ServerSocket _serverSocket;
	protected boolean _running;

	/**
	 * Constructs a Server object which listens to connection attempts at the
	 * given port.
	 * 
	 * @param port
	 *            a port number which the Server is listening to in order to
	 *            establish a socket connection to a client. The port number
	 *            should reside in the range of dynamic ports, i.e 49152 �
	 *            65535.
	 */
	public FastServiceHandler(int port) {
		this._port = port;
	}

	
	protected boolean isRunning() {
		return this._running;
	}

	/**
	 * Stops the server so that it won't listen at the given port any more.
	 */
	public void stopServer() {
		_running = false;
		try {
			_serverSocket.close();
		} catch (IOException e) {
			_logger.error("Error! " + "Unable to close socket on port: " + _port,
					e);
		}
	}

	protected boolean initializeServer() {
		_logger.info("Initialize server");
		try {
			_serverSocket = new ServerSocket(_port);
			_logger.info("Server listening on port: "
					+ _serverSocket.getLocalPort());
			return true;
		} catch (IOException e) {
			_logger.error("Error! Cannot open server socket:");
			if (e instanceof BindException) {
				_logger.error("Port " + _port + " is already bound!");
			}
			return false;
		}
	}
	
}
