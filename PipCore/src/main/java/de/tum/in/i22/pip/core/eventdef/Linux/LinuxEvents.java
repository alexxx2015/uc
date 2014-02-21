package de.tum.in.i22.pip.core.eventdef.Linux;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.tum.in.i22.pip.core.Name;

/**
 * This class provides functionalities used by multiple events originating from a Linux PEP.
 *
 * @author Florian Kelbert
 *
 */
public class LinuxEvents {

	static final String IP_UNSPEC = "unspec";

	static final Set<String> SUPPORTED_SOCKET_FAMILIES = new HashSet<String>(Arrays.asList("AF_INET"));

	private static final Set<String> LOCAL_IP_ADDRESSES = new HashSet<String>(Arrays.asList(
														"127.0.0.1",
														"localhost",
														"0000:0000:0000:0000:0000:0000:0000:0001",
														"::1"));

	static Name createFiledescrIdentifier(String host, String pid, String fd) {
		return new Name(host + "x" + pid + "x" + fd);
	}

	static Name createSocketIdentifier(String host, String pid, String localIP, String localPort, String remoteIP, String remotePort) {
		if (LOCAL_IP_ADDRESSES.contains(localIP) || LOCAL_IP_ADDRESSES.contains(remoteIP)) {
			return new Name(host + "x" + pid + ";" + localIP + ":" + localPort + "x" + remoteIP + ":" + remotePort);
		}

		return new Name(localIP + ":" + localPort + "x" + remoteIP + ":" + remotePort);
	}
}
