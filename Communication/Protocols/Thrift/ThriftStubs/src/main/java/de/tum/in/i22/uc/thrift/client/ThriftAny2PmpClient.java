package de.tum.in.i22.uc.thrift.client;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IPtpResponse;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.client.Any2PmpClient;
import de.tum.in.i22.uc.cm.processing.PmpProcessor;
import de.tum.in.i22.uc.thrift.ThriftConnector;
import de.tum.in.i22.uc.thrift.types.TAny2Pmp;

/**
 * The client side of a remote Thrift {@link PmpProcessor} server.
 *
 * Create a instance of this class, connect it (using
 * {@link Any2PmpClient#connect()}) and do calls on a remote
 * {@link PmpProcessor}.
 *
 * Use {@link ThriftClientFactory} to get an instance.
 *
 * @author Florian Kelbert
 *
 */
class ThriftAny2PmpClient extends Any2PmpClient {
	protected static final Logger _logger = LoggerFactory
			.getLogger(ThriftAny2PmpClient.class);

	private final ThriftConnector<TAny2Pmp.Client> _connector;

	private ThriftAny2PmpImpl _impl;

	/**
	 * Creates a {@link ThriftAny2PmpClient} that will be connected (upon
	 * calling {@link Any2PmpClient#connect()}) the the specified thrift server
	 * on the specified address/port.
	 *
	 * Use {@link ThriftClientFactory} to get an instance.
	 *
	 * @param address
	 *            the address of the remote point
	 * @param port
	 *            the port of the remote point
	 */
	private ThriftAny2PmpClient(String address, int port) {
		this(new ThriftConnector<>(address, port, TAny2Pmp.Client.class));
	}

	/**
	 * Creates a new {@link ThriftAny2PmpClient} that will be connected to the
	 * specified {@link IPLocation}.
	 *
	 * Use {@link ThriftClientFactory} to get an instance.
	 *
	 * @param location
	 *            the location of the remote point
	 */
	ThriftAny2PmpClient(IPLocation location) {
		this(location.getHost(), location.getPort());
	}

	private ThriftAny2PmpClient(ThriftConnector<TAny2Pmp.Client> connector) {
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
	public Map<String, Set<String>> listMechanismsPmp() {
		return _impl.listMechanismsPmp();
	}

	@Override
	public IStatus deployPolicyRawXMLPmp(String xml) {
		return _impl.deployPolicyRawXMLPmp(xml);
	}

	@Override
	public Set<XmlPolicy> getPolicies(IData data) {
		return _impl.getPolicies(data);
	}

	@Override
	public IPtpResponse translatePolicy(String requestId, Map<String, String> parameters, XmlPolicy xmlPolicy) {
		return _impl.translatePolicy(requestId, parameters, xmlPolicy);
	}

	@Override
	public IPtpResponse updateDomainModel(String requestId,	Map<String, String> parameters, XmlPolicy xmlDomainModel) {
		return _impl.updateDomainModel(requestId, parameters, xmlDomainModel);
	}

	@Override
	public Set<XmlPolicy> listPoliciesPmp() {
		return _impl.listPoliciesPmp();
	}

	@Override
	public IStatus remotePolicyTransfer(String xml, String from) {
		return _impl.remotePolicyTransfer(xml, from);
	}

}
