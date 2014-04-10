package de.tum.in.i22.uc.cm.distribution;

import java.security.InvalidParameterException;
import java.util.Objects;

import de.tum.in.i22.uc.cm.datatypes.IName;


public class IPLocation extends Location {
	private final String _host;
	private final int _port;

	/**
	 * Creates an {@link IPLocation} from
	 * a string of format <host>:<port>, as
	 * returned by {@link IPLocation#asString()}.
	 *
	 * @see IPLocation#asString()
	 *
	 * @param s the string
	 */
	public IPLocation(String s) {
		super(ELocation.IP);

		String[] arr;

		if (s != null && (arr = s.split(":")).length == 2) {
			try {
				_host = arr[0];
				_port = Integer.valueOf(arr[1]);
			} catch (Exception e) {
				throw new InvalidParameterException("Unable to create IPLocation out of string [" + s + "].");
			}
		}
		else {
			throw new InvalidParameterException("Unable to create IPLocation out of string [" + s + "].");
		}
	}

	public IPLocation(String host, int port) {
		super(ELocation.IP);

		if (host == null || port < 0) {
			throw new InvalidParameterException("[" + host + ":" + port + "]");
		}
		_host = host;
		_port = port;
	}

	@Override
	public String toString() {
		return com.google.common.base.Objects.toStringHelper(this)
				.add("_host", _host)
				.add("_port", _port)
				.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IPLocation) {
			IPLocation o = (IPLocation) obj;
			return Objects.equals(_host, o._host)
					&& Objects.equals(_port, o._port);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return _host.hashCode() ^ _port;
	}

	public String getHost() {
		return _host;
	}

	public int getPort() {
		return _port;
	}



	/**
	 * As prescribed by {@link Location}.
	 * Returns <host>:<port>.
	 *
	 * @see IPLocation#IPLocation(String)
	 */
	@Override
	public String asString() {
		return _host + ":" + _port;
	}

	/**
	 * As prescribed by {@link IName}.
	 * Returns this {@link IPLocation}'s host
	 * prefixed by {@link Location#PREFIX_LOCATION}.
	 */
	@Override
	public String getName() {
		return PREFIX_LOCATION + _host;
	}
}
