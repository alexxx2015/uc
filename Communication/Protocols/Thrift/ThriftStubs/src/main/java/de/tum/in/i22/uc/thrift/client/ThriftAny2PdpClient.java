package de.tum.in.i22.uc.thrift.client;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.basic.PxpSpec;
import de.tum.in.i22.uc.cm.client.Any2PdpClient;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.server.PdpProcessor;
import de.tum.in.i22.uc.thrift.ThriftConnector;
import de.tum.in.i22.uc.thrift.ThriftConverter;
import de.tum.in.i22.uc.thrift.types.TAny2Pdp;


/**
 * The client side of a remote Thrift {@link PdpProcessor} server.
 *
 * Create a instance of this class, connects it and
 * does calls on a remote {@link PdpProcessor}.
 *
 * The goal of this class is usually to convert types from/to thrift types,
 * invoke the respective dual method "on the other side", and convert back the result
 *
 * Use {@link ThriftClientFactory} to get an instance.
 *
 * @author Florian Kelbert & Enrico Lovat
 *
 */
class ThriftAny2PdpClient extends Any2PdpClient {
	protected static final Logger _logger = LoggerFactory.getLogger(ThriftAny2PdpClient.class);

	private TAny2Pdp.Client _handle;

	private final ThriftConnector<TAny2Pdp.Client> _connector;

	/**
	 * Creates a {@link ThriftAny2PdpClient} that will be
	 * connected (upon calling {@link Any2PdpClient#connect()})
	 * the the specified thrift server on the specified address/port.
	 *
	 * Use {@link ThriftClientFactory} to get an instance.
	 *
	 * @param address the address of the remote point
	 * @param port the port of the remote point
	 */
	private ThriftAny2PdpClient(String address, int port) {
		this(new ThriftConnector<>(address, port, TAny2Pdp.Client.class));
	}

	/**
	 * Creates a new {@link ThriftAny2PdpClient} that will be connected
	 * to the specified {@link IPLocation}.
	 *
	 * Use {@link ThriftClientFactory} to get an instance.
	 *
	 * @param location the location of the remote point
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
		_handle = _connector.connect();
	}

	@Override
	public void disconnect() {
		_connector.disconnect();
		_handle = null;
	}

	@Override
	public IMechanism exportMechanism(String par) {
		// TODO Method not yet supported
		_logger.error("exportMechanism method not yet supported");
		return null;
	}

	@Override
	public IStatus revokePolicy(String policyName) {
		_logger.debug("revoke policy (Pdp client)");
		try {
			return ThriftConverter.fromThrift(_handle.revokePolicy(policyName));
		} catch (TException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public IStatus revokeMechanism(String policyName, String mechName) {
		_logger.debug("revoke mechanism (Pdp client)");
		try {
			return ThriftConverter.fromThrift(_handle.revokeMechanism(policyName, mechName));
		} catch (TException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public IStatus deployPolicyURI(String policyFilePath) {
		_logger.debug("deploy policy (Pdp client)");
		try {
			return ThriftConverter.fromThrift(_handle.deployPolicyURI(policyFilePath));
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public IStatus deployPolicyXML(String XMLPolicy) {
		_logger.debug("deploy policy (Pdp client)");
		try {
			return ThriftConverter.fromThrift(_handle.deployPolicyXML(XMLPolicy));
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Map<String, List<String>> listMechanisms() {
		_logger.debug("listMechanisms (Pdp client)");
		try {
			return _handle.listMechanisms();
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Collections.emptyMap();
	}

	@Override
	public boolean registerPxp(PxpSpec pxp) {
		_logger.debug("registerPxp (Pdp client)");
		boolean b =false;
		try {
			b=_handle.registerPxp(ThriftConverter.toThrift(pxp));
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return b;
	}

	@Override
	public void notifyEventAsync(IEvent event) {
		_logger.debug("notify event async (Pdp client)");
		try{
			_handle.notifyEventAsync(ThriftConverter.toThrift(event));
		} catch (TException e){
			e.printStackTrace();
		}
	}

	@Override
	public IResponse notifyEventSync(IEvent event) {
		_logger.debug("notify event sync (Pdp client)");
		try {
			return ThriftConverter.fromThrift(
					_handle.
					notifyEventSync(
							ThriftConverter.
							toThrift(event)));
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}
