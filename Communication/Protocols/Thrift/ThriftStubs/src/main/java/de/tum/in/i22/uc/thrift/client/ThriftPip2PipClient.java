package de.tum.in.i22.uc.thrift.client;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IChecksum;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.client.Pip2PipClient;
import de.tum.in.i22.uc.thrift.ThriftConnector;
import de.tum.in.i22.uc.thrift.types.TAny2Pip;


class ThriftPip2PipClient extends Pip2PipClient {
	protected static final Logger _logger = LoggerFactory.getLogger(ThriftPip2PipClient.class);

	private final ThriftConnector<TAny2Pip.Client> _connector;

	private ThriftAny2PipImpl _impl;

	private ThriftPip2PipClient(String address, int port) {
		this(new ThriftConnector<>(address, port, TAny2Pip.Client.class));
	}

	ThriftPip2PipClient(IPLocation location) {
		this(location.getHost(), location.getPort());
	}

	private ThriftPip2PipClient(ThriftConnector<TAny2Pip.Client> connector) {
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
	public IStatus initialRepresentation(IName containerName, Set<IData> data) {
		return _impl.initialRepresentation(containerName, data);
	}

	@Override
	public IData newStructuredData(Map<String, Set<IData>> structure) {
		return _impl.newStructuredData(structure);
	}

	@Override
	public Map<String, Set<IData>> getStructureOf(IData data) {
		return _impl.getStructureOf(data);
	}

	@Override
	public Set<IData> flattenStructure(IData data) {
		return _impl.flattenStructure(data);
	}

	@Override
	public IData getDataFromId(String id) {
		return _impl.getDataFromId(id);
	}

	@Override
	public boolean newChecksum(IData data, IChecksum checksum, boolean overwrite) {
		return _impl.newChecksum(data, checksum, overwrite);
	}

	@Override
	public IChecksum getChecksumOf(IData data) {
		return _impl.getChecksumOf(data);
	}

	@Override
	public boolean deleteChecksum(IData d) {
		return _impl.deleteChecksum(d);
	}

	@Override
	public boolean deleteStructure(IData d) {
		return _impl.deleteStructure(d);
	}
}
