package de.tum.in.i22.uc.thrift.client;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.client.Prp2PrpClient;
import de.tum.in.i22.uc.thrift.ThriftConnector;
import de.tum.in.i22.uc.thrift.types.TAny2Dmp;
import de.tum.in.i22.uc.thrift.types.TAny2Pep;
import de.tum.in.i22.uc.thrift.types.TAny2Prp;

public class ThriftPrp2PrpClient extends Prp2PrpClient{
	protected static final Logger _logger = LoggerFactory.getLogger(ThriftDmp2DmpClient.class);

	private final ThriftConnector<TAny2Prp.Client> _connector;

	private ThriftAny2PrpImpl _impl;

	private ThriftPrp2PrpClient(String address, int port) {
		this(new ThriftConnector<>(address, port, TAny2Prp.Client.class));
	}

	ThriftPrp2PrpClient(IPLocation location) {
		this(location.getHost(), location.getPort());
	}

	private ThriftPrp2PrpClient(ThriftConnector<TAny2Prp.Client> connector) {
		super(connector);
		_connector = connector;
	}

	@Override	
	public void connect() throws IOException {
		_impl = new ThriftAny2PrpImpl(_connector.connect());
	}

	@Override
	public void disconnect() {
		_connector.disconnect();
		_impl = null;
	}

	@Override
	public void deployReleaseMechanism(ByteBuffer mechanism) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ByteBuffer getMechanism(String mechanismName) {
		// TODO Auto-generated method stub
		return null;
	}
}
