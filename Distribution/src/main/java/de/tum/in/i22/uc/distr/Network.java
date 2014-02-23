package de.tum.in.i22.uc.distr;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 *
 * @author Florian Kelbert
 *
 */
public class Network {

	public static final String IP_UNSPEC = "unspec";

	public static final Set<String> SUPPORTED_SOCKET_FAMILIES = new HashSet<String>(Arrays.asList("AF_INET"));

	public static final Set<String> LOCAL_IP_ADDRESSES = new HashSet<String>(Arrays.asList(
														"127.0.0.1",
														"localhost",
														"0000:0000:0000:0000:0000:0000:0000:0001",
														"::1"));
}
