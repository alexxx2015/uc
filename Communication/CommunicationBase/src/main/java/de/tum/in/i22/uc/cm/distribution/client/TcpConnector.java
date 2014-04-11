package de.tum.in.i22.uc.cm.distribution.client;

import java.util.Objects;


/**
 * This class represents a {@link Connector} that can be connected to remote
 * TCP locations.
 *
 * @author Florian Kelbert
 *
 * @param <HandleType>
 */
public abstract class TcpConnector<HandleType> extends Connector<HandleType> {
	protected String _address;
	protected int _port;

	/**
	 * A {@link TcpConnector} featuring the specified address and port
	 * @param address the remote address, e.g. an IP address or a URL
	 * @param port the remote port to connect to
	 */
	public TcpConnector(String address, int port) {
		_address = address;
		_port = port;
	}

	@Override
	public int hashCode() {
		return Objects.hash(_address, _port);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this.getClass().equals(obj.getClass())) {
			TcpConnector<?> o = (TcpConnector<?>) obj;
			return Objects.equals(_address, o._address)
					&& Objects.equals(_port, o._port);
		}
		return false;
	}

}
