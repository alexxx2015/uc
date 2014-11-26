package de.tum.in.i22.uc.cm.distribution.cassandra;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;

import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.distribution.Network;
import de.tum.in.i22.uc.generic.MyBase64;

class PrivateKeyspace extends Keyspace {
	protected static final Logger _logger = LoggerFactory.getLogger(PrivateKeyspace.class);

	private static final String TABLE_POLICIES = "policies";

	private static final List<String> _tables;

	static {
		_tables = new LinkedList<>();
		_tables.add(
				"CREATE TABLE IF NOT EXISTS " + TABLE_POLICIES + " ("
						+ "policyName text,"
						+ "policy text,"
						+ "PRIMARY KEY (policyName));");
	};

	private PreparedStatement _prepSelectPolicies;
	private PreparedStatement _prepSelectPolicyName;

	private PreparedStatement _prepInsertPolicy;

	private PreparedStatement _prepDeletePolicy;


	PrivateKeyspace(Cluster cluster) {
		super(Network.getHostname(), cluster);
	}

	@Override
	List<String> getTables() {
		return _tables;
	}

	@Override
	void prepareStatements() {

		_prepSelectPolicies = _session.prepare("SELECT policy FROM " + TABLE_POLICIES + ";")
			.setConsistencyLevel(readConsistency);

		_prepSelectPolicyName = _session.prepare("SELECT policyName FROM " + TABLE_POLICIES
			+ " WHERE policyName = ?;")
			.setConsistencyLevel(readConsistency);

		_prepInsertPolicy = _session.prepare("INSERT INTO " + TABLE_POLICIES
			+ " (policyName,policy) VALUES (?,?);")
			.setConsistencyLevel(writeConsistency);

		_prepDeletePolicy = _session.prepare("DELETE FROM " + TABLE_POLICIES
			+ " WHERE policyName = ?;")
			.setConsistencyLevel(writeConsistency);
	}

	Collection<String> getPolicies() {
		Collection<String> policies = new LinkedList<>();

		_session.execute(_prepSelectPolicies.bind()).forEach(
				r -> policies.add(MyBase64.fromBase64(r.getString("policy"))));

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
		if (_session.execute(_prepSelectPolicyName.bind(policy.getName())).isExhausted()) {
			// TODO: Encrypt what we are writing, so that others don't know which policies we are enforcing
			_session.execute(_prepInsertPolicy.bind(policy.getName(), MyBase64.toBase64(policy.getXml())));
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
		_session.execute(_prepDeletePolicy.bind(policyName));
	}
}
