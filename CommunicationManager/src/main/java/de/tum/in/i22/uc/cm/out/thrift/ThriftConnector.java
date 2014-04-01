package de.tum.in.i22.uc.cm.out.thrift;

import java.util.Objects;

import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import de.tum.in.i22.uc.cm.out.Connector;

/**
 *
 * @author Florian Kelbert
 *
 */
public class ThriftConnector<T extends TServiceClient> extends Connector<T> {

	private final String _address;
	private final int _port;
	private TTransport _transport;
	private final Class<T> _iface;

	public ThriftConnector(String address, int port, Class<T> iface) {
		_address = address;
		_port = port;
		_iface = iface;
	}


	@Override
	public T connect() throws Exception {
		T handle = null;
		_transport = new TSocket(_address, _port);

		try {
			_transport.open();
			handle = _iface.getConstructor(TProtocol.class).newInstance(new TBinaryProtocol(_transport));
		} catch (Exception e) {
			_logger.debug("Failed to establish connection.", e);
			disconnect();
			throw e;
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

	@Override
	public int hashCode() {
		return Objects.hash(_address, _port);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ThriftConnector) {
			ThriftConnector<?> o = (ThriftConnector<?>) obj;
			return Objects.equals(_address, o._address)
					&& Objects.equals(_port, o._port);
		}
		return false;
	}

}
