package de.tum.in.i22.pdp.handlers.pmp;

import java.io.IOException;
import java.net.Socket;

public class PmpClientTcpConnectionHandler extends PmpClientConnectionHandler {
	private final Socket _socket;

	public PmpClientTcpConnectionHandler(Socket socket) throws IOException {
		super(socket.getInputStream(), socket.getOutputStream());
		_socket = socket;
	}

	@Override
	protected void disconnect() {
		if (_socket != null) {
			try {
				_socket.close();
			} catch (IOException e) {
				_logger.warn("Unable to close socket", e);
			}
		}
	}
}
