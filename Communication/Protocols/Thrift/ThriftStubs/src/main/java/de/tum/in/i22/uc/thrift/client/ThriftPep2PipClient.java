package de.tum.in.i22.uc.thrift.client;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.ConflictResolutionFlagBasic.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.client.Pep2PipClient;
import de.tum.in.i22.uc.thrift.ThriftConnector;
import de.tum.in.i22.uc.thrift.types.TAny2Pip;



class ThriftPep2PipClient extends Pep2PipClient {
	protected static final Logger _logger = LoggerFactory.getLogger(ThriftPep2PipClient.class);

	private final ThriftConnector<TAny2Pip.Client> _connector;

	private ThriftAny2PipImpl _impl;

	private ThriftPep2PipClient(String address, int port) {
		this(new ThriftConnector<>(address, port, TAny2Pip.Client.class));
	}

	ThriftPep2PipClient(IPLocation location) {
		this(location.getHost(), location.getPort());
	}

	private ThriftPep2PipClient(ThriftConnector<TAny2Pip.Client> connector) {
		super(connector);
		_connector = connector;
	}

	@Override
	public void connect() throws IOException {
		_impl = new ThriftAny2PipImpl(_connector.connect());
	}

	@Override
	public void disconnect() {
		_connector.disconnect();
		_impl = null;
	}

	@Override
	public IStatus updateInformationFlowSemantics(IPipDeployer deployer, File jarFile,
			EConflictResolution flagForTheConflictResolution) {
		return _impl.updateInformationFlowSemantics(deployer, jarFile, flagForTheConflictResolution);
	}
}
