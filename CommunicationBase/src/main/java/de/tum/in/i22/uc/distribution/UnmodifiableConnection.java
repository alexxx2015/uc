package de.tum.in.i22.uc.distribution;

import java.io.IOException;

public class UnmodifiableConnection extends Connection {
	private final Connection _connection;

	public UnmodifiableConnection(Connection connection) {
		_connection = connection;
	}

	@Override
	public boolean isConnected() {
		return _connection.isConnected();
	}

	@Override
	public void connect() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void disconnect() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public UnclosableInputStream getInputStream() throws IOException {
		return _connection.getInputStream();
	}

	@Override
	public UnclosableOutputStream getOutputStream() throws IOException {
		return _connection.getOutputStream();
	}

	@Override
	public int hashCode() {
		return _connection.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return _connection.equals(obj);
	}
}
