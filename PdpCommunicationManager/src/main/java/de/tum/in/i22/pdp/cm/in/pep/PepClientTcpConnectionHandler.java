package de.tum.in.i22.pdp.cm.in.pep;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

import de.tum.in.i22.uc.cm.in.ClientTcpConnectionHandler;
import de.tum.in.i22.uc.cm.in.MessageTooLargeException;
import de.tum.in.i22.uc.cm.out.TcpConnector;

public class PepClientTcpConnectionHandler extends PepClientConnectionHandler {
	private final Socket _socket;

	public PepClientTcpConnectionHandler(Socket socket) throws IOException {
		super(new DataInputStream(new BufferedInputStream(socket.getInputStream())),
				new BufferedOutputStream(socket.getOutputStream()));
		_socket = socket;
	}

	@Override
	protected void disconnect() {
		if (_socket != null) {
			try {
				_socket.close();
			} catch (IOException e) {
				_logger.warn("Unable to close socket.", e);
			}
		}
	}
}
