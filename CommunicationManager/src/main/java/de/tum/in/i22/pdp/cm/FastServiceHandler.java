package de.tum.in.i22.pdp.cm;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

import de.tum.in.i22.pdp.datatypes.IEvent;
import de.tum.in.i22.pdp.datatypes.IMechanism;
import de.tum.in.i22.pdp.datatypes.IResponse;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpStatus.EStatus;

public class FastServiceHandler extends CommunicationHandler implements
		Runnable {

	private static Logger logger = Logger.getRootLogger();

	private int port;
	private ServerSocket serverSocket;
	private boolean running;

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
		this.port = port;
	}

	/**
	 * Loops until the the server should be closed.
	 */
	public void run() {

		running = initializeServer();

		if (serverSocket != null) {
			while (isRunning()) {
				try {
					Socket client = serverSocket.accept();
					
					logger.info("Client connection from "
							+ client.getInetAddress().getHostName()
							+ " on port " + client.getPort());

					ClientConnectionHandler clientConnHandler = new ClientConnectionHandler(
							client);
					//TODO improve this code, use new features introduced in java 1.7, thread pools
					Thread thread = new Thread(clientConnHandler);
					thread.start();
				} catch (IOException e) {
					logger.error("Error! "
							+ "Unable to establish connection. \n", e);
				}
			}
		}
		logger.info("Server stopped.");
	}

	private boolean isRunning() {
		return this.running;
	}

	/**
	 * Stops the server so that it won't listen at the given port any more.
	 */
	public void stopServer() {
		running = false;
		try {
			serverSocket.close();
		} catch (IOException e) {
			logger.error("Error! " + "Unable to close socket on port: " + port,
					e);
		}
	}

	private boolean initializeServer() {
		logger.info("Initialize server");
		try {
			serverSocket = new ServerSocket(port);
			logger.info("Server listening on port: "
					+ serverSocket.getLocalPort());
			return true;
		} catch (IOException e) {
			logger.error("Error! Cannot open server socket:");
			if (e instanceof BindException) {
				logger.error("Port " + port + " is already bound!");
			}
			return false;
		}
	}

	public EStatus deployMechanism(IMechanism mechanism) {
		// TODO Auto-generated method stub
		return null;
	}

	public EStatus exportMechanism(String par) {
		// TODO Auto-generated method stub
		return null;
	}

	public EStatus revokeMechanism(String par) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IResponse notifyEvent(IEvent event) {
		// TODO Auto-generated method stub
		return null;
	}

	// /**
	// * Main entry point to the FastServiceHanlder (server) application.
	// */
	// public static void main(String[] args) {
	// Thread thread = new Thread(new FastServiceHandler(50001));
	// thread.start();
	// }
}
