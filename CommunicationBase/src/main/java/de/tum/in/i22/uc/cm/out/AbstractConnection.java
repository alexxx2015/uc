package de.tum.in.i22.uc.cm.out;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

import org.apache.log4j.Logger;

public abstract class AbstractConnection implements IConnector {
//	private static final ConnectionPool _connectionPool = ConnectionPool.getInstance();

	protected static final Logger _logger = Logger.getLogger(AbstractConnection.class);

	private final Connector _connector;

	public AbstractConnection(Connector connector) {
		_connector = connector;
	}

	@Override
	public void connect() throws IOException {
		_connector.connect();
	}

	@Override
	public void disconnect() {
		_connector.disconnect();
	}

	protected OutputStream getOutputStream() {
		return _connector.getOutputStream();
	}

	protected InputStream getInputStream() {
		return _connector.getInputStream();
	}

	public final AbstractConnection obtainConnection() throws IOException {
		return ConnectionPool.obtainConnection(this);
	}

	public final void releaseConnection() throws IOException {
		ConnectionPool.releaseConnection(this);
	}

	@Override
	public int hashCode() {
		return Objects.hash(getClass(), _connector);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AbstractConnection) {
			AbstractConnection o = (AbstractConnection) obj;
			return Objects.equals(getClass(), o.getClass())
					&& Objects.equals(_connector, o._connector);
		}
		return false;
	}
}
