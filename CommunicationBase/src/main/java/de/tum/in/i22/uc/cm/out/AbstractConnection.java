package de.tum.in.i22.uc.cm.out;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public abstract class AbstractConnection implements IConnector {
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
