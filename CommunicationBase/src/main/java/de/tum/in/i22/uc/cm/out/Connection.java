package de.tum.in.i22.uc.cm.out;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

import org.apache.log4j.Logger;

public abstract class Connection implements IConnection {
	protected static final Logger _logger = Logger.getLogger(Connection.class);

	private final Connector _connector;

	public Connection(Connector connector) {
		_connector = connector;
	}

	final void connect() throws IOException {
		_connector.connect();
	}

	final void disconnect() {
		_connector.disconnect();
	}

	protected OutputStream getOutputStream() {
		return _connector.getOutputStream();
	}

	protected InputStream getInputStream() {
		return _connector.getInputStream();
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
