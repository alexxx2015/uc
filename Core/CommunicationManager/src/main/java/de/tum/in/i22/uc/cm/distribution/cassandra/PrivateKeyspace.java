package de.tum.in.i22.uc.cm.distribution.cassandra;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.AlreadyExistsException;

import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.Threading;

class PrivateKeyspace extends Keyspace {
	protected static final Logger _logger = LoggerFactory.getLogger(PrivateKeyspace.class);

	private static final String TABLE_POLICIES = "policies";

	private static final List<String> _tables;

	static {
		_tables = new LinkedList<>();
		_tables.add(
				"CREATE TABLE " + TABLE_POLICIES + " ("
						+ "policyName text,"
						+ "policy text,"
						+ "PRIMARY KEY (policyName));");
	};

	private PrivateKeyspace(String name, Cluster cluster) {
		super(name, cluster);
	}

	static PrivateKeyspace create(Cluster cluster) {
		String hostname;
		try {
			hostname = InetAddress.getLocalHost().getHostName().replaceAll("[^a-zA-Z0-9]","");
		} catch (UnknownHostException e) {
			_logger.warn("Unable to retrieve hostname.");
			throw new RuntimeException("Unable to retrieve hostname.");
		}

		boolean existed = false;
		try {
			cluster.connect().execute("CREATE KEYSPACE " + hostname
					+ " WITH replication = {'class':'NetworkTopologyStrategy','" + IPLocation.localIpLocation.getHost() + "':1}");
		}
		catch (AlreadyExistsException e) {
			existed = true;
		}

		if (!existed) {
			Session newSession = cluster.connect(hostname);

			CompletionService<?> cs = new ExecutorCompletionService<>(Threading.instance());
			_tables.forEach(t -> cs.submit(() -> newSession.execute(t), null));
			Threading.waitFor(_tables.size(), cs);
		}

		return new PrivateKeyspace(hostname, cluster);
	}


	Collection<String> getPolicies() {
		ResultSet rs = _session.execute("SELECT policy FROM " + TABLE_POLICIES);

		Collection<String> policies = new LinkedList<>();

		rs.forEach(r -> policies.add(CassandraDistributionManager.fromBase64(r.getString("policy"))));

		return policies;
	}



	/**
	 * This is to make locally deployed policies persisent. As such,
	 * deployed policies can and will be reloaded after shutdown and
	 * restart of the Pdp/Pmp. If the policy was already present in
	 * the Database, nothing will happen.
	 *
	 * @param policy the policy to be made persistent.
	 */
	void add(XmlPolicy policy) {
		ResultSet rs = _session.execute("SELECT policyName FROM " + TABLE_POLICIES
				+ " WHERE policyName = '" + policy.getName() + "';");

		if (rs.isExhausted()) {
			/*
			 * TODO: Encrypt what we are writing, so that others don't know which policies we are enforcing
			 */
			_session.execute("INSERT INTO " + TABLE_POLICIES + " (policyName,policy)"
					+ " VALUES ('" + policy.getName() + "','" + CassandraDistributionManager.toBase64(policy.getXml()) + "');");
		}
	}

	/**
	 * Deletes the specified policyName (and the associated policy)
	 * from the Database, such that it will no longer be loaded after
	 * shutdown/restart of Pdp/Pmp.
	 *
	 * @param policyName the policy to be deleted.
	 */
	void delete(String policyName) {
		_session.execute("DELETE FROM " + TABLE_POLICIES
				+ " WHERE policyName = '" + policyName + "';");
	}
}
