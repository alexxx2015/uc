package de.tum.in.i22.uc.cm.out;

import java.io.IOException;
import java.net.Socket;
import java.util.Objects;

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
			setOutputStream(_clientSocket.getOutputStream());
			setInputStream(_clientSocket.getInputStream());
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
				close();
				_clientSocket.close();
				_clientSocket = null;
				_logger.info("Connection closed!");
			} catch (IOException e) {
				_logger.error("Error occurred when closing the connection.", e);
			}
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(_address, _port);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TcpConnector) {
			TcpConnector o = (TcpConnector) obj;
			return Objects.equals(_address, o._address)
					&& Objects.equals(_port, o._port);
		}
		return false;
	}
}
