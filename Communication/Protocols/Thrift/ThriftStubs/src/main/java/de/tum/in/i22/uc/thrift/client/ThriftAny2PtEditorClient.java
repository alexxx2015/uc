package de.tum.in.i22.uc.thrift.client;

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.client.Any2PmpClient;
import de.tum.in.i22.uc.cm.interfaces.IAny2PtEditor;
import de.tum.in.i22.uc.cm.processing.PmpProcessor;
import de.tum.in.i22.uc.thrift.ThriftConnector;
import de.tum.in.i22.uc.thrift.types.TAny2PtEditor;

/**
 * The client side of a remote Thrift {@link PmpProcessor} server.
 *
 * Create a instance of this class, connect it (using
 * {@link Any2PtEditorClient#connect()}) and do calls on a remote
 * {@link PmpProcessor}.
 *
 * Use {@link ThriftClientFactory} to get an instance.
 *
 * @author Florian Kelbert
 *
 */
public class ThriftAny2PtEditorClient implements IAny2PtEditor {
	protected static final Logger _logger = LoggerFactory
			.getLogger(ThriftAny2PtEditorClient.class);

	private final ThriftConnector<TAny2PtEditor.Client> _connector;

	private ThriftAny2PtEditorImpl _impl;

	/**
	 * Creates a {@link ThriftAny2PtEditorClient} that will be connected (upon
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
	public ThriftAny2PtEditorClient(String address, int port) {
		this(new ThriftConnector<>(address, port, TAny2PtEditor.Client.class));
	}

	/**
	 * Creates a new {@link ThriftAny2PtEditorClient} that will be connected to the
	 * specified {@link IPLocation}.
	 *
	 * Use {@link ThriftClientFactory} to get an instance.
	 *
	 * @param location
	 *            the location of the remote point
	 */
	ThriftAny2PtEditorClient(IPLocation location) {
		this(location.getHost(), location.getPort());
	}

	private ThriftAny2PtEditorClient(ThriftConnector<TAny2PtEditor.Client> connector) {
		_connector = connector;
	}

	public void connect() throws IOException {
		_impl = new ThriftAny2PtEditorImpl(_connector.connect());
	}

	public void disconnect() {
		_connector.disconnect();
		_impl = null;
	}

	@Override
	public IStatus specifyPolicyFor(Set<IContainer> representations,
			String dataClass) {
		return _impl.specifyPolicyFor(representations, dataClass);
	}

}
