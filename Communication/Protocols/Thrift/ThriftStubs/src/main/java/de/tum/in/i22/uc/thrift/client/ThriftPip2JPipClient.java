package de.tum.in.i22.uc.thrift.client;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.client.Pip2JPipClient;
import de.tum.in.i22.uc.thrift.ThriftConnector;
import de.tum.in.i22.uc.thrift.types.TPip2JPip;



class ThriftPip2JPipClient extends Pip2JPipClient {
	protected static final Logger _logger = LoggerFactory.getLogger(ThriftPip2JPipClient.class);

	private final ThriftConnector<TPip2JPip.Client> _connector;

	private ThriftAny2JPipImpl _impl;

	private ThriftPip2JPipClient(String address, int port) {
		this(new ThriftConnector<>(address, port, TPip2JPip.Client.class));
	}

	ThriftPip2JPipClient(IPLocation location) {
		this(location.getHost(), location.getPort());
	}

	private ThriftPip2JPipClient(ThriftConnector<TPip2JPip.Client> connector) {
		super(connector);
		_connector = connector;
	}

	@Override
	public void connect() throws IOException {
		_impl = new ThriftAny2JPipImpl(_connector.connect());
	}

	@Override
	public void disconnect() {
		_connector.disconnect();
		_impl = null;
	}

	@Override
	public void notifyAsync(IEvent updateEvent) {
		_impl.notifyAsync(updateEvent);
	}

}
