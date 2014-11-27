package de.tum.in.i22.uc.cm.distribution.cassandra;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.RegularStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.QueryBuilder;

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

	PrivateKeyspace(Cluster cluster) {
		super(Network.getHostname(), cluster);
	}

	@Override
	List<String> getTables() {
		return _tables;
	}

	@Override
	IPreparedStatementId[] getPrepareStatements() {
		return Prepared.values();
	}

	Collection<String> getPolicies() {
		Collection<String> policies = new LinkedList<>();

		_session.execute(Prepared._prepSelectPolicies.get().bind()).forEach(
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
		if (_session.execute(Prepared._prepSelectPolicyName.get().bind(policy.getName())).isExhausted()) {
			// TODO: Encrypt what we are writing, so that others don't know which policies we are enforcing
			_session.execute(Prepared._prepInsertPolicy.get().bind(policy.getName(), MyBase64.toBase64(policy.getXml())));
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
		_session.execute(Prepared._prepDeletePolicy.get().bind(policyName));
	}


	private enum Prepared implements IPreparedStatementId {

		_prepSelectPolicies(
				QueryBuilder
				.select("policy")
				.from(TABLE_POLICIES),
				readConsistency),

		_prepSelectPolicyName(
				QueryBuilder
				.select("policyName")
				.from(TABLE_POLICIES)
				.where(QueryBuilder.eq("policyName", QueryBuilder.bindMarker())),
				readConsistency),

		_prepInsertPolicy(
				QueryBuilder
				.insertInto(TABLE_POLICIES)
				.value("policyName", QueryBuilder.bindMarker())
				.value("policy", QueryBuilder.bindMarker()),
				writeConsistency),

		_prepDeletePolicy(
				QueryBuilder
				.delete()
				.all()
				.from(TABLE_POLICIES)
				.where(QueryBuilder.eq("policyName", QueryBuilder.bindMarker())),
				writeConsistency),
		;

		Prepared(RegularStatement regular, ConsistencyLevel cl) {
			_regular = regular;
			_cl = cl;
		}

		@Override
		public void prepare(Session session) {
			_prepared = session.prepare(_regular).setConsistencyLevel(_cl);
		}

		@Override
		public PreparedStatement get() {
			return _prepared;
		}

		private PreparedStatement _prepared;
		private RegularStatement _regular;
		private ConsistencyLevel _cl;
	}
}
