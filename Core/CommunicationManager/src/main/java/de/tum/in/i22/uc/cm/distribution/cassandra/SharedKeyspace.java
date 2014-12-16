package de.tum.in.i22.uc.cm.distribution.cassandra;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.RegularStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SimpleStatement;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.utils.UUIDs;
import com.google.common.collect.Sets;

import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.AtomicOperator;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IOperator;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.generic.MyBase64;
import de.tum.in.i22.uc.pdp.core.operators.EventMatchOperator;
import de.tum.in.i22.uc.pdp.core.operators.StateBasedOperator;


/**
 *
 * @author Florian Kelbert
 *
 */
class SharedKeyspace extends Keyspace implements ISharedKeyspace {
	protected static final Logger _logger = LoggerFactory.getLogger(SharedKeyspace.class);

	private static final String TABLE_DATA = "hasdata";
	private static final String TABLE_OP_NOTIFIED = "opnotified";
	private static final String TABLE_MECHANISMS = "mechanisms";
	private static final String TABLE_POLICIES = "policies";
	private static final String TABLE_LOCK = "lock";

	private static final List<String> _tables;

	private final XmlPolicy _policy;

	private final Map<IOperator,Long> _lastInsert;

	static {
		_tables = new LinkedList<>();
		_tables.add(
				"CREATE TABLE IF NOT EXISTS " + TABLE_DATA + " ("
						+ "data text,"
						+ "locations set<text>,"
						+ "PRIMARY KEY (data)"
						+ ");");
		_tables.add(
				"CREATE TABLE IF NOT EXISTS " + TABLE_OP_NOTIFIED + " ("
						+ "opid text,"
						+ "time timeuuid,"
						+ "location text,"
						+ "timestep bigint,"
						+ "dummyttl int,"		// dummy column that is not part of the PRIMARY KEY in order to allow for retrieval of TTL value
						+ "PRIMARY KEY (opid,timestep,time,location)) "
						+ "WITH CLUSTERING ORDER BY (timestep DESC);");
		_tables.add(
				"CREATE TABLE IF NOT EXISTS " + TABLE_POLICIES + " ("
						+ "policyName text,"
						+ "policy text,"
						+ "PRIMARY KEY (policyName));");
		_tables.add(
				"CREATE TABLE IF NOT EXISTS " + TABLE_MECHANISMS + " ("
						+ "mechanismName text,"
						+ "firstTick bigint,"
						+ "PRIMARY KEY (mechanismName));");
		_tables.add(
				"CREATE TABLE IF NOT EXISTS " + TABLE_LOCK + " ("
						+ "locked boolean,"
						+ "lockedby text,"
						+ "PRIMARY KEY (locked));");
	};


	SharedKeyspace(XmlPolicy policy, Cluster cluster) {
		super(policy.getName(), cluster);
		_policy = policy;
		_lastInsert = new HashMap<>();

		initialize();
	}

	@Override
	List<String> getTables() {
		return _tables;
	}

	@Override
	IPreparedStatementId[] getPrepareStatements() {
		return Prepared.values();
	}


	void initialize() {
		_session.execute(Prepared._prepInsertPolicy.get().bind(_name, MyBase64.toBase64(_policy.getXml())));
	}

	@Override
	public Set<String> getLocations() {
		// Retrieve current information about the keyspace
		ResultSet rows = _session.execute(Prepared._prepSelectStrategyOptions.get().bind(_name));

		// Build the set of locations that are currently known within the keyspace
		Set<String> allLocations = new HashSet<>();
		rows.forEach(r -> {
			String[] entries = r.getString(0).replaceAll("[{}\"]", "").split(",");
			for (String loc : entries) {
				allLocations.add(loc.split(":")[0]);
			}
		});

		return allLocations;
	}


	/**
	 * Acquires a lock for this keyspace. This is
	 * important if we alter the keyspace's metadata.
	 * However, this is expensive. Cf. Cassandra lightweight
	 * transactions:
	 * http://www.datastax.com/dev/blog/lightweight-transactions-in-cassandra-2-0
	 * http://www.opencredo.com/2014/01/06/new-features-in-cassandra-2-0-lightweight-transactions-on-insert/
	 */
	private void acquireLock() {
		boolean locked = false;

		_logger.debug("Trying to lock keyspace {}.", _name);
		while (!locked) {
			Row result = _session.execute(Prepared._prepInsertLock.get().bind(true, IPLocation.localIpLocation.getHost())).one();

			locked = result.getBool("[applied]");
			if (!locked) {
				/*
				 * We did not get the lock. But maybe we had it already ...
				 */
				if (result.getString("lockedby").equals(IPLocation.localIpLocation.getHost())) {
					locked = true;
					_logger.debug("Lock acquired. We had it already.");
				}
				else {
					_logger.debug("Lock is kept by {}. Waiting.", result.getString("by"));
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {}
				}
			}
		}
	}


	private void releaseLock() {
		_session.execute(Prepared._prepDeleteLock.get().bind(true));
	}



	private void alterKeyspace(Set<String> locations) {
		StringBuilder query = new StringBuilder(locations.size() * 22 + 100);

		query.append("ALTER KEYSPACE " + _name + " WITH replication ");
		query.append("= {'class':'NetworkTopologyStrategy',");

		locations.forEach(l -> query.append("'" + l + "':1,"));

		query.deleteCharAt(query.length() - 1);
		query.append("}");

		_session.execute(new SimpleStatement(query.toString()).setConsistencyLevel(defaultConsistency));
	}


	@Override
	public boolean enlargeBy(IPLocation location) {
		boolean result = false;

		acquireLock();

		Set<String> allLocations = getLocations();
		if (allLocations.add(location.getHost())) {
			alterKeyspace(allLocations);
			result = true;
		}

		releaseLock();

		return result;
	}

	@Override
	public boolean diminishBy(IPLocation location) {
		boolean result = false;

		acquireLock();

		Set<String> allLocations = getLocations();
		if (allLocations.remove(location.getHost())) {
			alterKeyspace(allLocations);
			result = true;
		}

		releaseLock();

		return result;
	}

	@Override
	public void setFirstTick(String mechanismName, long firstTick) {
		if (_session.execute(Prepared._prepSelectFirstTick.get().bind(mechanismName)).isExhausted()) {
			_session.execute(Prepared._prepInsertFirstTick.get().bind(mechanismName, firstTick));
		}
	}

	@Override
	public long getFirstTick(String mechanismName) {
		ResultSet rs = _session.execute(Prepared._prepSelectFirstTick.get().bind(mechanismName));
		return rs.isExhausted() ? Long.MIN_VALUE : rs.iterator().next().getLong("firstTick");
	}

	@Override
	public boolean wasNotifiedAtTimestep(AtomicOperator operator, long timestep) {
		_logger.debug("wasNotifiedAtTimestep({}, {})", operator, timestep);

		ResultSet rs = _session.execute(Prepared._prepSelectNotifiedOneAt.get().bind(operator.getFullId(), timestep));
		return operator.getPositivity().value() != rs.isExhausted();
	}

	@Override
	public int howOftenNotifiedAtTimestep(AtomicOperator operator, long timestep) {
		_logger.debug("howOftenNotifiedAtTimestep({}, {})", operator, timestep);
		return _session.execute(Prepared._prepSelectNotifiedAllAt.get().bind(operator.getFullId(), timestep)).all().size();
	}

	@Override
	public int howOftenNotifiedSinceTimestep(AtomicOperator operator, long timestep) {
		_logger.debug("howOftenNotifiedSinceTimestep({}, {})", operator, timestep);
		return _session.execute(Prepared._prepSelectNotifiedAllSince.get().bind(operator.getFullId(), timestep)).all().size();
	}

	@Override
	public void notify(IOperator op, boolean endOfTimestep) {
		/*
		 * Calculate the time when this operator happened.
		 * If we are not at the end of a timestep, simply use now().
		 * Otherwise, create a UUID corresponding to lastTick.
		 */
		UUID time = null;
		long timestep = op.getMechanism().getTimestep();

		boolean doInsert = false;

		if (op instanceof EventMatchOperator) {
			doInsert = true;
			time = endOfTimestep
					? UUIDs.endOf(op.getMechanism().getLastTick() - 1)
					: UUIDs.timeBased();
		}
		else if (op instanceof StateBasedOperator) {
			if (endOfTimestep) {
				/*
				 * At the end of a timestep, StateBasedOperators are
				 * attributed to the next timestep. The reason is that
				 * the Operator will under any circumstances remain
				 * unchanged until the next event happens. And this next
				 * event will be strictly after the timestep. Hence, we
				 * +1 to the time below.
				 */
				doInsert = true;
				long timeToInsert = op.getMechanism().getLastTick() + 1;
				time = UUIDs.startOf(timeToInsert);
				_lastInsert.put(op, timeToInsert);
				timestep++;
			}
			else {
				/*
				 * If we are not at the end of a timestep, we only
				 * insert if the Operator was never inserted before,
				 * or if the last insertion was before the last tick.
				 */
				Long lastInsert = _lastInsert.get(op);
				if (lastInsert == null || lastInsert < op.getMechanism().getLastTick()) {
					doInsert = true;
					time = UUIDs.timeBased();
					_lastInsert.put(op, System.currentTimeMillis());
				}
			}
		}

		if (doInsert) {
			_logger.info("Updating Cassandra state at time {} with event {}",
					CassandraDistributionManager.sdf.format(UUIDs.unixTimestamp(time)),
					op.getFullId());

			_session.execute(Prepared._prepInsertNotified.get().bind(op.getFullId(), IPLocation.localIpLocation.getHost(), timestep, time, (int) (op.getTTL() / 1000)));
		}
	}

//	static boolean existsPhysically(Cluster cluster, String policyName) {
//		policyName = toValidKeyspaceName(policyName);
//
//		boolean exists = true;
//
//		try {
//			cluster.connect(policyName);
//			_logger.info("Keyspace {} exists.", policyName);
//		}
//		catch (Exception e) {
//			exists = false;
//			_logger.info("Keyspace {} does not exist.", policyName);
//		}
//
//		return exists;
//	}

	@Override
	public void addData(IData data, IPLocation location) {
		// Check whether there already exists an entry for this dataID.
		if (_session.execute(Prepared._prepSelectHasData.get().bind(data.getId())).isExhausted()) {
			// If such an entry did not exist, create an initial entry.
			_session.execute(Prepared._prepInsertData.get().bind(data.getId(),
					Sets.newHashSet(IPLocation.localIpLocation.getHost(),location.getHost())));
		}
		else {
			// If an entry existed, then update the existing one.
			_session.execute(Prepared._prepUpdateData.get().bind(Collections.singleton(location.getHost()), data.getId()));
		}
	}

	private enum Prepared implements IPreparedStatementId {
		/*
		 * SELECTs
		 */
		_prepSelectStrategyOptions(
				QueryBuilder
				.select("strategy_options")
				.from("system","schema_keyspaces")
				.where(QueryBuilder.eq("keyspace_name", QueryBuilder.bindMarker())),
				defaultConsistency),

		_prepSelectFirstTick(
				QueryBuilder
				.select("firstTick")
				.from(TABLE_MECHANISMS)
				.where(QueryBuilder.eq("mechanismName", QueryBuilder.bindMarker()))
				.limit(1),
				readConsistency),

		_prepSelectNotifiedAllAt(
				QueryBuilder
				.select("opid")
				.from(TABLE_OP_NOTIFIED)
				.where(QueryBuilder.eq("opid", QueryBuilder.bindMarker()))
					.and(QueryBuilder.eq("timestep", QueryBuilder.bindMarker())),
				readConsistency),

		_prepSelectNotifiedOneAt(
				QueryBuilder
				.select("opid")
				.from(TABLE_OP_NOTIFIED)
				.where(QueryBuilder.eq("opid", QueryBuilder.bindMarker()))
					.and(QueryBuilder.eq("timestep", QueryBuilder.bindMarker()))
				.limit(1),
				readConsistency),

		_prepSelectNotifiedAllSince(
				QueryBuilder
				.select("opid")
				.from(TABLE_OP_NOTIFIED)
				.where(QueryBuilder.eq("opid", QueryBuilder.bindMarker()))
					.and(QueryBuilder.gte("timestep", QueryBuilder.bindMarker())),
				readConsistency),

		_prepSelectHasData(
				QueryBuilder
				.select("data")
				.from(TABLE_DATA)
				.where(QueryBuilder.eq("data", QueryBuilder.bindMarker())),
				readConsistency),

		_prepSelectPolicy(
				QueryBuilder
				.select("policyName")
				.from(TABLE_POLICIES)
				.limit(1),
				readConsistency),

		/*
		 * INSERTs
		 */
		_prepInsertFirstTick(
				QueryBuilder
				.insertInto(TABLE_MECHANISMS)
				.value("mechanismName", QueryBuilder.bindMarker())
				.value("firstTick", QueryBuilder.bindMarker()),
				writeConsistency),

		_prepInsertNotified(
				QueryBuilder
				.insertInto(TABLE_OP_NOTIFIED)
				.value("opid", QueryBuilder.bindMarker())
				.value("location", QueryBuilder.bindMarker())
				.value("timestep", QueryBuilder.bindMarker())
				.value("time", QueryBuilder.bindMarker())
				.value("dummyttl", 0)
				.using(QueryBuilder.ttl(QueryBuilder.bindMarker())),
				writeConsistency),

		_prepInsertData(
				QueryBuilder
				.insertInto(TABLE_DATA)
				.value("data", QueryBuilder.bindMarker())
				.value("locations", QueryBuilder.bindMarker()),
				writeConsistency),

		_prepInsertPolicy(
				QueryBuilder
				.insertInto(TABLE_POLICIES)
				.value("policyName", QueryBuilder.bindMarker())
				.value("policy", QueryBuilder.bindMarker()),
				writeConsistency),

		_prepInsertLock(
				QueryBuilder
				.insertInto(TABLE_LOCK)
				.value("locked", QueryBuilder.bindMarker())
				.value("lockedby", QueryBuilder.bindMarker())
				.ifNotExists(),
				writeConsistency),

		/*
		 * UPDATEs
		 */
		_prepUpdateData(
				QueryBuilder
				.update(TABLE_DATA)
				.with(QueryBuilder.add("locations", QueryBuilder.bindMarker()))
				.where(QueryBuilder.eq("data", QueryBuilder.bindMarker())),
				writeConsistency),

		/*
		 * DELETEs
		 */
		_prepDeleteLock(
				QueryBuilder
				.delete()
				.from(TABLE_LOCK)
				.where(QueryBuilder.eq("locked",QueryBuilder.bindMarker())),
				writeConsistency),

		;

		private Prepared(RegularStatement regular, ConsistencyLevel cl) {
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

		/*
		 * For Enums, attributes go to the end.
		 */
		private PreparedStatement _prepared;
		private RegularStatement _regular;
		private ConsistencyLevel _cl;
	}
}
