package de.tum.in.i22.uc.distribution;

import java.io.IOException;


public abstract class AbstractStrategy {
	private final ConnectionPool _connectionPool;

	protected AbstractStrategy(ConnectionPool connectionPool) {
		_connectionPool = connectionPool;
	}

	protected IConnection obtainConnection(ILocation location) throws IOException {
		return _connectionPool.obtainConnection(location);
	}

	protected void releaseConnection(ILocation location) throws IOException {
		_connectionPool.releaseConnection(location.getConnection());
	}
}
