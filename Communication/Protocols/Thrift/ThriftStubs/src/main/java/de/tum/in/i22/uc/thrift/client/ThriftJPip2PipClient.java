package de.tum.in.i22.uc.thrift.client;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.client.JPip2PipClient;
import de.tum.in.i22.uc.thrift.ThriftConnector;
import de.tum.in.i22.uc.thrift.types.TJPip2Pip;



class ThriftJPip2PipClient extends JPip2PipClient {
	protected static final Logger _logger = LoggerFactory.getLogger(ThriftJPip2PipClient.class);

	private final ThriftConnector<TJPip2Pip.Client> _connector;

	private ThriftJPip2PipImpl _impl;

	private ThriftJPip2PipClient(String address, int port) {
		this(new ThriftConnector<>(address, port, TJPip2Pip.Client.class));
	}

	ThriftJPip2PipClient(IPLocation location) {
		this(location.getHost(), location.getPort());
	}

	private ThriftJPip2PipClient(ThriftConnector<TJPip2Pip.Client> connector) {
		super(connector);
		_connector = connector;
	}

	@Override
	public void connect() throws IOException {
		_impl = new ThriftJPip2PipImpl(_connector.connect());
	}

	@Override
	public void disconnect() {
		_connector.disconnect();
		_impl = null;
	}

	@Override
	public IStatus addJPIPListener(String ip, int port, String id, String filter) {
		return _impl.addJPIPListener(ip, port, id, filter);
	}

	@Override
	public IStatus setUpdateFrequency(int msec, String id) {
		return _impl.setUpdateFrequency(msec, id);
	}


}
