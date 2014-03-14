package de.tum.in.i22.uc.distribution;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Objects;

/**
 * Represents and manages a TCP connection.
 * In contrast to {@link Socket}
 * this connection can be reconnected after closing it.
 *
 * @author Florian Kelbert
 *
 */
public class TCPConnection extends Connection {
	private final InetSocketAddress _address;
	private Socket _socket;

	public TCPConnection(InetAddress address, int port) {
		if (address == null) {
			throw new NullPointerException("Address was null.");
		}

		_address = new InetSocketAddress(address, port);
	}

	public TCPConnection(String host, int port) {
		if (host == null) {
			throw new NullPointerException("Host was null.");
		}

		_address = new InetSocketAddress(host, port);
	}

	@Override
	public boolean isConnected() {
		return _socket == null ? false : _socket.isClosed();
	}

	@Override
	void connect() throws IOException {
		_socket.connect(_address);
	}

	@Override
	void disconnect() throws IOException {
		_socket.close();
		_socket = new Socket();
	}

	@Override
	public UnclosableInputStream getInputStream() throws IOException {
		return new UnclosableInputStream(_socket.getInputStream());
	}

	@Override
	public UnclosableOutputStream getOutputStream() throws IOException {
		return new UnclosableOutputStream(_socket.getOutputStream());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(_address);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TCPConnection) {
			return _address.equals(((TCPConnection) obj)._address);
		}
		return false;
	}

	@Override
	public String toString() {
		return com.google.common.base.Objects.toStringHelper(this)
				.add("_address", _address)
				.toString();
	}
}
