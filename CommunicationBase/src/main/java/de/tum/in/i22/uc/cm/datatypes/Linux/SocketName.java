package de.tum.in.i22.uc.cm.datatypes.Linux;

import java.util.Objects;

import de.tum.in.i22.uc.cm.basic.NameBasic;
import de.tum.in.i22.uc.distribution.Network;

public class SocketName extends NameBasic {

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
	public boolean equals(Object obj) {
		if (obj instanceof SocketName) {
			SocketName o = (SocketName) obj;
			return Objects.equals(_host, o._host)
					&& Objects.equals(_localIP, o._localIP)
					&& Objects.equals(_localPort, o._localPort)
					&& Objects.equals(_pid, o._pid)
					&& Objects.equals(_remoteIP, o._remoteIP)
					&& Objects.equals(_remotePort, o._remotePort);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return Objects.hash(_host, _localIP, _localPort, _pid, _remoteIP, _remotePort);
	}

	@Override
	public String toString() {
		return com.google.common.base.Objects.toStringHelper(this)
				.add("_host", _host)
				.add("_pid", _pid)
				.add("_localIP", _localIP)
				.add("_localPort", _localPort)
				.add("_remoteIP", _remoteIP)
				.add("_remotePort", _remotePort)
				.toString();
	}
}