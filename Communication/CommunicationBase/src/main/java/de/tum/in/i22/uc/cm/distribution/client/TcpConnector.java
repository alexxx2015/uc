package de.tum.in.i22.uc.cm.distribution.client;

import java.util.Objects;

import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.Location;


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
	private Location _location;

	/**
	 * A {@link TcpConnector} featuring the specified address and port
	 * @param address the remote address, e.g. an IP address or a URL
	 * @param port the remote port to connect to
	 */
	public TcpConnector(String address, int port) {
		this(new IPLocation(address, port));
	}

	private TcpConnector(IPLocation location) {
		_location = location;
		_address = location.getHost();
		_port = location.getPort();
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


	@Override
	public Location getLocation() {
		return _location;
	}

	@Override
	public String toString() {
		return com.google.common.base.Objects.toStringHelper(this)
				.add("_address", _address)
				.add("_port", _port)
				.toString();
	}
}
