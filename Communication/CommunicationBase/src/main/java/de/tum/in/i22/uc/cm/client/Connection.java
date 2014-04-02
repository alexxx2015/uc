package de.tum.in.i22.uc.cm.client;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class Connection implements IConnection {
	protected static final Logger _logger = LoggerFactory.getLogger(Connection.class);

	private final Connector _connector;

	public Connection(Connector connector) {
		_connector = connector;
	}

	final void connect() throws Exception {
		_connector.connect();
	}

	final void disconnect() {
		_connector.disconnect();
	}

	@Override
	public int hashCode() {
		return Objects.hash(getClass(), _connector);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Connection) {
			Connection o = (Connection) obj;
			return Objects.equals(getClass(), o.getClass())
					&& Objects.equals(_connector, o._connector);
		}
		return false;
	}
}
