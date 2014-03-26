package de.tum.in.i22.uc.distribution;

import java.net.InetAddress;
import java.util.Objects;


public class IPLocation extends Location {
	private final InetAddress _address;
	private final int _port;

	public IPLocation(InetAddress address, int port) {
		super(ELocation.IP);
		_address = address;
		_port = port;
	}

	@Override
	public String toString() {
		return com.google.common.base.Objects.toStringHelper(this)
				.add("_address", _address)
				.add("_port", _port)
				.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IPLocation) {
			IPLocation o = (IPLocation) obj;
			return Objects.equals(_address, o._address)
					&& Objects.equals(_port, o._port);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(_address, _port);
	}

	public static IPLocation from(String s) {
		String[] arr;
		IPLocation result = null;

		if (s != null && (arr = s.split(":")).length == 2) {
			try {
				result = new IPLocation(InetAddress.getByName(arr[0]), Integer.valueOf(arr[1]));
			} catch (Exception e) {	}
		}

		return result;
	}
}
