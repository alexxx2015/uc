package de.tum.in.i22.uc.cm.distribution;

import java.util.Set;

import com.google.common.collect.Sets;

/**
 *
 * @author Florian Kelbert
 *
 */
public class Network {
	public static final Set<String> LOCAL_IP_ADDRESSES = Sets.newHashSet(
				"127.0.0.1",
				"localhost",
				"0000:0000:0000:0000:0000:0000:0000:0001",
				"::1");
}