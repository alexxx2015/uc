package de.tum.in.i22.uc.distribution;

import java.io.IOException;

public abstract class Location  {
	private static final ConnectionPool _connectionPool = ConnectionPool.getInstance();

	private final Connection _connection;

	public Location(Connection connection) {
		_connection = connection;
	}

	public final Connection obtainConnection() throws IOException {
		return _connectionPool.obtainConnection(_connection);
	}

	public final void releaseConnection() throws IOException {
		_connectionPool.releaseConnection(_connection);
	}


	@Override
	public abstract boolean equals(Object obj);

	@Override
	public abstract int hashCode();
}
