package de.tum.in.i22.uc.distribution;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class IPLocation implements ILocation {
	private final InetAddress _address;

	public IPLocation(InetAddress address) {
		_address = address;
	}

	public static IPLocation createIPLocation(String ip) {
		try {
			return new IPLocation(InetAddress.getByName(ip));
		} catch (UnknownHostException e) {
			return null;
		}
	}

	public InetAddress getAddress() {
		return _address;
	}

	public boolean isIP4() {
		return _address instanceof Inet4Address;
	}

	public boolean isIP6() {
		return _address instanceof Inet6Address;
	}

	@Override
	public String toString() {
		return com.google.common.base.Objects.toStringHelper(this)
				.add("_address", _address)
				.toString();
	}
}
