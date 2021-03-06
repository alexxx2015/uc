package de.tum.in.i22.uc.thrift;

import java.io.IOException;

import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.distribution.client.TcpConnector;
import de.tum.in.i22.uc.cm.settings.Settings;

/**
 * A class representing a {@link ThriftConnector}, i.e. a {@link TcpConnector}
 * that will connect to a remote Thrift server.
 *
 * @author Florian Kelbert
 *
 */
public class ThriftConnector<HandleType extends TServiceClient> extends TcpConnector<HandleType> {
	protected static final Logger _logger = LoggerFactory.getLogger(ThriftConnector.class);

	private TTransport _transport;
	private final Class<HandleType> _iface;

	/**
	 * Creates a new {@link ThriftConnector} that will connect to the
	 * specified address and port using the specified Thrift interface.
	 *
	 * @param address the address of the remote thrift server to connect to, such
	 * 			as an IP address or a URL
	 * @param port the server's port
	 * @param iface the Thrift interface to be used for the connection
	 */
	public ThriftConnector(String address, int port, Class<HandleType> iface) {
		super(address, port);
		_iface = iface;
	}


	@Override
	public HandleType connect() throws IOException {
		HandleType handle = null;

		try {
			if (Settings.getInstance().isSslEnabled()) {
				TSSLTransportParameters params = new TSSLTransportParameters();
				params.setTrustStore(Settings.getInstance().getSslTruststore(), Settings.getInstance().getSslTruststorePassword(), null, null);
				_transport = TSSLTransportFactory.getClientSocket(_address, _port, 0, params);
			}
			else {
				_transport = new TSocket(_address, _port);
				_transport.open();
			}
			handle = _iface.getConstructor(TProtocol.class).newInstance(new TBinaryProtocol(_transport));
		} catch (Exception e) {
			_logger.debug("Failed to establish connection to " + this + ".");
			disconnect();
			throw new IOException(e.getMessage(), e);
		}

		return handle;
	}

	@Override
	public void disconnect() {
		if (_transport != null) {
			_transport.close();
			_transport = null;
		}
	}
}
