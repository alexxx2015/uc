package de.tum.in.i22.uc.cm.distribution.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

public abstract class Keyspace {
	protected final Session _session;
	protected final Cluster _cluster;
	protected final String _name;

	public Keyspace(String name, Cluster cluster) {
		_name = name;
		_cluster = cluster;
		_session = cluster.connect(name);
	}
}
