package de.tum.in.i22.pdp.cm.in;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

/**
 * Template class
 * @author Stoimenov
 *
 */
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
	 *            should reside in the range of dynamic ports, i.e 49152 -
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

	private boolean initializeServer() {
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
	
	/**
	 * Loops until the the server should be closed.
	 */
	@Override
	public void run() {
		_running = initializeServer();
		if (_serverSocket != null) {
			while (isRunning()) {
				Socket client = null;
				try {
					client = _serverSocket.accept();
					_logger.info("Client connection from "
							+ client.getInetAddress().getHostName()
							+ " on port " + client.getPort());

				} catch (IOException e) {
					_logger.error("Error! "
							+ "Unable to establish connection. \n", e);
				}
				
				if (client != null) {
					_logger.info("Handle client connection");
					doHandleClientConnection(client);
				}
			}
		}
		_logger.info("Server stopped.");
	}
	
	protected abstract void doHandleClientConnection(Socket client);
	
}
