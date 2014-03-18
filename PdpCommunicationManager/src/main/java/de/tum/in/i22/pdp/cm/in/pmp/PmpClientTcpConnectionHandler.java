package de.tum.in.i22.pdp.cm.in.pmp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class PmpClientTcpConnectionHandler extends PmpClientConnectionHandler {
	private Socket _socket;

	public PmpClientTcpConnectionHandler(Socket socket) throws IOException {
		super(new DataInputStream(new BufferedInputStream(socket.getInputStream())),
				new BufferedOutputStream(socket.getOutputStream()));
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
