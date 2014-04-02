package de.tum.in.i22.uc.distribution;

import java.util.Objects;


public class IPLocation extends Location {
	private final String _host;
	private final int _port;

	public IPLocation(String host, int port) {
		super(ELocation.IP);
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
		return Objects.hash(_host, _port);
	}

	public String getHost() {
		return _host;
	}

	public int getPort() {
		return _port;
	}

	public static IPLocation from(String s) {
		String[] arr;
		IPLocation result = null;

		if (s != null && (arr = s.split(":")).length == 2) {
			try {
				result = new IPLocation(arr[0], Integer.valueOf(arr[1]));
			} catch (Exception e) {	}
		}

		return result;
	}

	public enum ELocation {
		LOCAL,
		IP;
	}
}
