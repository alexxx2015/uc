package de.tum.in.i22.uc.distribution;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

public class IPLocation extends AbstractLocation {
	private final InetAddress _address;

	public IPLocation(InetAddress address) {
		super(new TCPConnection(address, 9999999));	// TODO: How to fix the port?!)
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

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IPLocation) {
			return Objects.equals(_address, ((IPLocation) obj)._address);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(_address);
	}
}
