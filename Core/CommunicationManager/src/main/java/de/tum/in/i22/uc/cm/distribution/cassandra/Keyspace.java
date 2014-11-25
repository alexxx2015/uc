package de.tum.in.i22.uc.cm.distribution.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

public abstract class Keyspace {
	protected final Session _session;
	protected final Cluster _cluster;
	protected final String _name;

	public Keyspace(String name, Session session, Cluster cluster) {
		_name = name;
		_session = session;
		_cluster = cluster;
	}
}
