package de.tum.in.i22.uc.thrift.client;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.client.Pmp2PmpClient;
import de.tum.in.i22.uc.thrift.ThriftConnector;
import de.tum.in.i22.uc.thrift.types.TAny2Pmp;



class ThriftPmp2PmpClient extends Pmp2PmpClient {
	private final ThriftConnector<TAny2Pmp.Client> _connector;

	private ThriftAny2PmpImpl _impl;

	private ThriftPmp2PmpClient(String address, int port) {
		this(new ThriftConnector<>(address, port, TAny2Pmp.Client.class));
	}

	ThriftPmp2PmpClient(IPLocation location) {
		this(location.getHost(), location.getPort());
	}

	private ThriftPmp2PmpClient(ThriftConnector<TAny2Pmp.Client> connector) {
		super(connector);
		_connector = connector;
	}


	@Override
	public void connect() throws IOException {
		_impl = new ThriftAny2PmpImpl(_connector.connect());
	}

	@Override
	public void disconnect() {
		_connector.disconnect();
		_impl = null;
	}

	@Override
	public IMechanism exportMechanismPmp(String par) {
		return _impl.exportMechanismPmp(par);
	}

	@Override
	public IStatus revokePolicyPmp(String policyName) {
		return _impl.revokePolicyPmp(policyName);
	}

	@Override
	public IStatus revokeMechanismPmp(String policyName, String mechName) {
		return _impl.revokeMechanismPmp(policyName, mechName);
	}

	@Override
	public IStatus deployPolicyURIPmp(String policyFilePath) {
		return _impl.deployPolicyURIPmp(policyFilePath);
	}

	@Override
	public IStatus deployPolicyXMLPmp(XmlPolicy XMLPolicy) {
		return _impl.deployPolicyXMLPmp(XMLPolicy);
	}

	@Override
	public Map<String, List<String>> listMechanismsPmp() {
		return _impl.listMechanismsPmp();
	}

	@Override
	public IStatus deployPolicyRawXMLPmp(String xml) {
		return _impl.deployPolicyRawXMLPmp(xml);
	}

	@Override
	public IStatus specifyPolicyFor(Set<IContainer> representations,
			String dataClass) {
		return _impl.specifyPolicyFor(representations, dataClass);

	}

}
