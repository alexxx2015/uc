package de.tum.in.i22.uc.distribution;

import java.io.InputStream;
import java.io.OutputStream;

import de.tum.in.i22.uc.cm.out.Connector;
import de.tum.in.i22.uc.cm.out.IConnector;

public abstract class AbstractConnection implements IConnector {
	private final Connector _connector;

	public AbstractConnection(Connector connector) {
		_connector = connector;
	}

	@Override
	public void connect() throws Exception {
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
}
