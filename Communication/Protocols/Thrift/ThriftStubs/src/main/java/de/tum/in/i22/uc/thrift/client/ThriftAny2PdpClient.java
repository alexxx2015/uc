package de.tum.in.i22.uc.thrift.client;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.PxpSpec;
import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IResponse;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.client.Any2PdpClient;
import de.tum.in.i22.uc.cm.processing.PdpProcessor;
import de.tum.in.i22.uc.thrift.ThriftConnector;
import de.tum.in.i22.uc.thrift.types.TAny2Pdp;

/**
 * The client side of a remote Thrift {@link PdpProcessor} server.
 *
 * Create a instance of this class, connects it and does calls on a remote
 * {@link PdpProcessor}.
 *
 * The goal of this class is usually to convert types from/to thrift types,
 * invoke the respective dual method "on the other side", and convert back the
 * result
 *
 * Use {@link ThriftClientFactory} to get an instance.
 *
 * @author Florian Kelbert & Enrico Lovat
 *
 */
class ThriftAny2PdpClient extends Any2PdpClient {

	private final ThriftConnector<TAny2Pdp.Client> _connector;

	private ThriftAny2PdpImpl _impl;

	/**
	 * Creates a {@link ThriftAny2PdpClient} that will be connected (upon
	 * calling {@link Any2PdpClient#connect()}) the the specified thrift server
	 * on the specified address/port.
	 *
	 * Use {@link ThriftClientFactory} to get an instance.
	 *
	 * @param address
	 *            the address of the remote point
	 * @param port
	 *            the port of the remote point
	 */
	private ThriftAny2PdpClient(String address, int port) {
		this(new ThriftConnector<>(address, port, TAny2Pdp.Client.class));
	}

	/**
	 * Creates a new {@link ThriftAny2PdpClient} that will be connected to the
	 * specified {@link IPLocation}.
	 *
	 * Use {@link ThriftClientFactory} to get an instance.
	 *
	 * @param location
	 *            the location of the remote point
	 */
	ThriftAny2PdpClient(IPLocation location) {
		this(location.getHost(), location.getPort());
	}

	private ThriftAny2PdpClient(ThriftConnector<TAny2Pdp.Client> connector) {
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
	public void notifyEventAsync(IEvent event) {
		_impl.notifyEventAsync(event);
	}

	@Override
	public IResponse notifyEventSync(IEvent event) {
		return _impl.notifyEventSync(event);
	}

	@Override
	public boolean registerPxp(PxpSpec pxp) {
		return _impl.registerPxp(pxp);
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
	public IStatus deployPolicyURI(String policyFilePath) {
		return _impl.deployPolicyURI(policyFilePath);
	}

	@Override
	public IStatus deployPolicyXML(XmlPolicy XMLPolicy) {
		return _impl.deployPolicyXML(XMLPolicy);
	}

	@Override
	public Map<String, Set<String>> listMechanisms() {
		return _impl.listMechanisms();
	}

	@Override
	public void processEventAsync(IEvent pepEvent) {
		_impl.processEventAsync(pepEvent);
	}

	@Override
	public IResponse processEventSync(IEvent pepEvent) {
		return _impl.processEventSync(pepEvent);
	}
}
