package de.tum.in.i22.uc.cm.out;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TcpConnector extends Connector {

	private final String _address;
	private final int _port;
	private Socket _clientSocket;

 	public TcpConnector(String address, int port) {
		_address = address;
		_port = port;
	}

	@Override
	public void connect() throws IOException {
		_logger.debug("Establish connection to " + _address + ":" + _port);
		_clientSocket = new Socket(_address, _port);

		try {
			_logger.debug("Get i/o streams.");
			_outputStream = new BufferedOutputStream(_clientSocket.getOutputStream());
			_inputStream = new BufferedInputStream(_clientSocket.getInputStream());
			_logger.debug("Connection established.");
		} catch(Exception e) {
			_logger.debug("Failed to establish connection.", e);
			throw e;
		}
	}

	@Override
	public void disconnect() {
		if (_clientSocket != null) {
			_logger.info("Tear down the connection");
			try {
				_inputStream.close();
				_outputStream.close();
				_clientSocket.close();
				_clientSocket = null;
				_logger.info("Connection closed!");
			} catch (IOException e) {
				_logger.error("Error occurred when closing the connection.", e);
			}
		}
	}
}
