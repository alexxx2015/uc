package de.tum.in.i22.uc.cm.datatypes.linux;

import com.google.common.base.MoreObjects;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;

/**
 * Class for naming sockets.
 * Corresponds to set F_N x F_N in CODASPY'13 paper.
 *
 * @author Florian Kelbert
 *
 */
public class SocketName extends NameBasic {

	public static final String PREFIX = "SOCK_";

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
		return new SocketName(localIP, localPort, remoteIP, remotePort,
				PREFIX + localIP + ":" + localPort + "_" + remoteIP + ":" + remotePort);
	}

	public static SocketName create(String s) {
		if (!s.startsWith(PREFIX)) {
			throw new IllegalArgumentException("Invalid SocketName format.");
		}

		s = s.substring(PREFIX.length());

		String[] parts = s.split("_");
		if (parts.length != 2) {
			throw new IllegalArgumentException("Invalid SocketName format.");
		}

		String[] local = parts[0].split(":");
		String[] remote = parts[1].split(":");

		if (local.length != 2 || remote.length != 2) {
			throw new IllegalArgumentException("Invalid SocketName format.");
		}

		return SocketName.create(local[0], Integer.parseInt(local[1]), remote[0], Integer.parseInt(remote[1]));
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
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("_localIP", _localIP)
				.add("_localPort", _localPort)
				.add("_remoteIP", _remoteIP)
				.add("_remotePort", _remotePort)
				.toString();
	}
}