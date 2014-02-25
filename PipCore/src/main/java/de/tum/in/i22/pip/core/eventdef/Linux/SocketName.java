package de.tum.in.i22.pip.core.eventdef.Linux;

import de.tum.in.i22.uc.cm.basic.ContainerName;
import de.tum.in.i22.uc.distr.Network;;



class SocketName extends ContainerName {

	static final String PREFIX_SOCKET = "SOCK_";

	private final String _host;
	private final String _pid;
	private final String _localIP;
	private final String _localPort;
	private final String _remoteIP;
	private final String _remotePort;

	private SocketName(String host, String pid, String localIP, String localPort, String remoteIP, String remotePort, String name) {
		super(name);

		_host = host;
		_pid = pid;
		_localIP = localIP;
		_localPort = localPort;
		_remoteIP = remoteIP;
		_remotePort = remotePort;
	}

	public static SocketName create(String host, String pid, String localIP, String localPort, String remoteIP, String remotePort) {
		if (Network.LOCAL_IP_ADDRESSES.contains(localIP) || Network.LOCAL_IP_ADDRESSES.contains(remoteIP)) {
			return new SocketName(host, pid, localIP, localPort, remoteIP, remotePort,
					PREFIX_SOCKET + host + "x" + pid + ";" + localIP + ":" + localPort + "x" + remoteIP + ":" + remotePort);
		}

		return new SocketName(host, pid, localIP, localPort, remoteIP, remotePort,
				PREFIX_SOCKET + localIP + ":" + localPort + "x" + remoteIP + ":" + remotePort);
	}

	public String getHost() {
		return _host;
	}

	public String getPid() {
		return _pid;
	}

	public String getLocalIP() {
		return _localIP;
	}

	public String getLocalPort() {
		return _localPort;
	}

	public String getRemoteIP() {
		return _remoteIP;
	}

	public String getRemotePort() {
		return _remotePort;
	}

	@Override
	public String toString() {
		return super.toString();
	}
}