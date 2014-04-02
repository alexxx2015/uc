package de.tum.in.i22.uc.cm.datatypes.linux;

import java.util.Objects;

import de.tum.in.i22.uc.cm.basic.NameBasic;
import de.tum.in.i22.uc.cm.distribution.Network;

/**
 * Class for naming sockets.
 * Corresponds to set F_N x F_N in CODASPY'13 paper.
 *
 * @author Florian Kelbert
 *
 */
public class SocketName extends NameBasic {

	static final String PREFIX_SOCKET = "SOCK_";

	private final String _localIP;
	private final int _localPort;
	private final String _remoteIP;
	private final int _remotePort;

	private SocketName(String localIP, int localPort, String remoteIP, int remotePort, String name) {
		super(name);

		_localIP = localIP;
		_localPort = localPort;
		_remoteIP = remoteIP;
		_remotePort = remotePort;
	}

	public static SocketName create(String localIP, int localPort, String remoteIP, int remotePort) {
		if (Network.LOCAL_IP_ADDRESSES.contains(localIP) || Network.LOCAL_IP_ADDRESSES.contains(remoteIP)) {
			return new SocketName(localIP, localPort, remoteIP, remotePort,
					PREFIX_SOCKET + localIP + ":" + localPort + "_" + remoteIP + ":" + remotePort);
		}

		return new SocketName(localIP, localPort, remoteIP, remotePort,
				PREFIX_SOCKET + localIP + ":" + localPort + "_" + remoteIP + ":" + remotePort);
	}

	public String getLocalIP() {
		return _localIP;
	}

	public int getLocalPort() {
		return _localPort;
	}

	public String getRemoteIP() {
		return _remoteIP;
	}

	public int getRemotePort() {
		return _remotePort;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SocketName) {
			SocketName o = (SocketName) obj;
			return Objects.equals(_localIP, o._localIP)
					&& Objects.equals(_localPort, o._localPort)
					&& Objects.equals(_remoteIP, o._remoteIP)
					&& Objects.equals(_remotePort, o._remotePort);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return Objects.hash(_localIP, _localPort, _remoteIP, _remotePort);
	}

	@Override
	public String toString() {
		return com.google.common.base.Objects.toStringHelper(this)
				.add("_localIP", _localIP)
				.add("_localPort", _localPort)
				.add("_remoteIP", _remoteIP)
				.add("_remotePort", _remotePort)
				.toString();
	}
}