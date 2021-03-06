package de.tum.in.i22.uc.thrift.client;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.client.Pmp2PdpClient;
import de.tum.in.i22.uc.thrift.ThriftConnector;
import de.tum.in.i22.uc.thrift.types.TAny2Pdp;



class ThriftPmp2PdpClient extends Pmp2PdpClient {
	private final ThriftConnector<TAny2Pdp.Client> _connector;

	private ThriftAny2PdpImpl _impl;

	private ThriftPmp2PdpClient(String address, int port) {
		this(new ThriftConnector<>(address, port, TAny2Pdp.Client.class));
	}

	ThriftPmp2PdpClient(IPLocation location) {
		this(location.getHost(), location.getPort());
	}

	private ThriftPmp2PdpClient(ThriftConnector<TAny2Pdp.Client> connector) {
		super(connector);
		_connector = connector;
	}


	@Override
	public void connect() throws IOException {
		_impl = new ThriftAny2PdpImpl(_connector.connect());
	}

	@Override
	public void disconnect() {
		_connector.disconnect();
		_impl = null;
	}

	@Override
	public IMechanism exportMechanism(String par) {
		return _impl.exportMechanism(par);
	}

	@Override
	public IStatus revokePolicy(String policyName) {
		return _impl.revokePolicy(policyName);
	}

	@Override
	public IStatus revokeMechanism(String policyName, String mechName) {
		return _impl.revokeMechanism(policyName, mechName);
	}

	@Override
	public IStatus deployPolicyXML(XmlPolicy XMLPolicy) {
		return _impl.deployPolicyXML(XMLPolicy);
	}

	@Override
	public Map<String, Set<String>> listMechanisms() {
		return _impl.listMechanisms();
	}
}
