package de.tum.in.i22.uc.cm.distribution.cassandra;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorCompletionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.utils.UUIDs;
import com.google.common.collect.Sets;

import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.AtomicOperator;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IOperator;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.Threading;
import de.tum.in.i22.uc.generic.MyBase64;
import de.tum.in.i22.uc.pdp.core.operators.EventMatchOperator;
import de.tum.in.i22.uc.pdp.core.operators.StateBasedOperator;

public class SharedKeyspace extends Keyspace implements ISharedKeyspace {
	protected static final Logger _logger = LoggerFactory.getLogger(SharedKeyspace.class);

	private static final String TABLE_DATA = "hasdata";
	private static final String TABLE_OP_NOTIFIED = "opnotified";
	private static final String TABLE_MECHANISMS = "mechanisms";
	private static final String TABLE_POLICIES = "policies";

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
	};

	private PreparedStatement _prepSelectFirstTick;
	private PreparedStatement _prepSelectHasData;
	private PreparedStatement _prepSelectNotifiedAllAt;
	private PreparedStatement _prepSelectNotifiedOneAt;
	private PreparedStatement _prepSelectNotifiedAllSince;
	private PreparedStatement _prepSelectStrategyOptions;

	private PreparedStatement _prepInsertData;
	private PreparedStatement _prepInsertFirstTick;
	private PreparedStatement _prepInsertNotified;
	private PreparedStatement _prepInsertPolicy;

	private PreparedStatement _prepUpdateData;


	SharedKeyspace(XmlPolicy policy, Cluster cluster) {
		super(policy.getName().toLowerCase(), cluster);
		_policy = policy;
		_lastInsert = new HashMap<>();

		initialize();
	}

	@Override
	List<String> getTables() {
		return _tables;
	}

	@Override
	void prepareStatements() {
		ExecutorCompletionService<?> cs = new ExecutorCompletionService<>(Threading.instance());

		/*
		 * SELECTs
		 */
		_prepSelectStrategyOptions = _session.prepare("SELECT strategy_options FROM system.schema_keyspaces "
				+ " WHERE keyspace_name = '" + _name + "';");

		_prepSelectFirstTick = _session.prepare("SELECT firstTick FROM " + TABLE_MECHANISMS
				+ " WHERE mechanismName = ? LIMIT 1;")
				.setConsistencyLevel(readConsistency);

		_prepSelectNotifiedAllAt = _session.prepare("SELECT opid FROM " + TABLE_OP_NOTIFIED
				+ " WHERE opid = ?"
				+ " AND timestep = ?;")
				.setConsistencyLevel(readConsistency);

		_prepSelectNotifiedOneAt = _session.prepare("SELECT opid FROM " + TABLE_OP_NOTIFIED
				+ " WHERE opid = ?"
				+ " AND timestep = ?"
				+ " LIMIT 1;")
				.setConsistencyLevel(readConsistency);

		_prepSelectNotifiedAllSince = _session.prepare("SELECT opid FROM " + TABLE_OP_NOTIFIED
				+ " WHERE opid = ?"
				+ " AND timestep >= ?;")
				.setConsistencyLevel(readConsistency);

		_prepSelectHasData = _session.prepare("SELECT data from " + TABLE_DATA
				+ " WHERE data = ?;")
				.setConsistencyLevel(readConsistency);


		/*
		 * INSERTs
		 */
		_prepInsertFirstTick = _session.prepare("INSERT INTO " + TABLE_MECHANISMS
				+ " (mechanismName,firstTick) VALUES (?,?);")
				.setConsistencyLevel(writeConsistency);

		_prepInsertNotified = _session.prepare("INSERT INTO " + TABLE_OP_NOTIFIED
				+ " (opid, location, timestep, time, dummyttl) VALUES (?,?,?,?,0)"
				+ " USING TTL ?;")
				.setConsistencyLevel(writeConsistency);

		_prepInsertData = _session.prepare("INSERT INTO " + TABLE_DATA
				+ " (data, locations) VALUES (?,?);")
				.setConsistencyLevel(writeConsistency);

		_prepInsertPolicy = _session.prepare("INSERT INTO " + TABLE_POLICIES
				+ " (policyName,policy) VALUES (?,?);");


		/*
		 * UPDATEs
		 */
		_prepUpdateData = _session.prepare("UPDATE " + TABLE_DATA
				+ " SET locations = locations + ? WHERE data = ?;")
				.setConsistencyLevel(writeConsistency);
	}


	void initialize() {
		_session.execute(_prepInsertPolicy.bind(_name, MyBase64.toBase64(_policy.getXml())));
	}


	private Set<String> getLocations() {
		// Retrieve current information about the keyspace
		ResultSet rows = _session.execute(_prepSelectStrategyOptions.bind());

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

	private void alterKeyspace(Set<String> locations) {
		StringBuilder query = new StringBuilder(locations.size() * 22 + 100);

		query.append("ALTER KEYSPACE " + _name + " WITH replication ");
		query.append("= {'class':'NetworkTopologyStrategy',");

		locations.forEach(l ->
			query.append("'" + l + "':1,")
		);

		query.deleteCharAt(query.length() - 1);
		query.append("}");

		_session.execute(query.toString());
	}


	@Override
	public boolean enlargeBy(IPLocation location) {
		Set<String> allLocations = getLocations();

		if (allLocations.add(location.getHost())) {
			alterKeyspace(allLocations);
			return true;
		}

		return false;
	}

	@Override
	public boolean diminishBy(IPLocation location) {
		Set<String> allLocations = getLocations();

		if (allLocations.remove(location.getHost())) {
			alterKeyspace(allLocations);
			return true;
		}

		return false;
	}

	@Override
	public void setFirstTick(String mechanismName, long firstTick) {
		if (_session.execute(_prepSelectFirstTick.bind(mechanismName)).isExhausted()) {
			_session.execute(_prepInsertFirstTick.bind(mechanismName, firstTick));
		}
	}

	@Override
	public long getFirstTick(String mechanismName) {
		ResultSet rs = _session.execute(_prepSelectFirstTick.bind(mechanismName));
		return rs.isExhausted() ? Long.MIN_VALUE : rs.iterator().next().getLong("firstTick");
	}

	@Override
	public boolean wasNotifiedAtTimestep(AtomicOperator operator, long timestep) {
		_logger.debug("wasNotifiedAtTimestep({}, {})", operator, timestep);

		ResultSet rs = _session.execute(_prepSelectNotifiedOneAt.bind(operator.getFullId(), timestep));
		return operator.getPositivity().value() != rs.isExhausted();
	}

	@Override
	public int howOftenNotifiedAtTimestep(AtomicOperator operator, long timestep) {
		_logger.debug("howOftenNotifiedAtTimestep({}, {})", operator, timestep);
		return _session.execute(_prepSelectNotifiedAllAt.bind(operator.getFullId(), timestep)).all().size();
	}

	@Override
	public int howOftenNotifiedSinceTimestep(AtomicOperator operator, long timestep) {
		_logger.debug("howOftenNotifiedSinceTimestep({}, {})", operator, timestep);
		return _session.execute(_prepSelectNotifiedAllSince.bind(operator.getFullId(), timestep)).all().size();
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

			_session.execute(_prepInsertNotified.bind(op.getFullId(), IPLocation.localIpLocation.getHost(), timestep, time, (int) (op.getTTL() / 1000)));
		}
	}

	@Override
	public void addData(IData data, IPLocation location) {
		// Check whether there already exists an entry for this dataID.
		if (_session.execute(_prepSelectHasData.bind(data.getId())).isExhausted()) {
			// If such an entry did not exist, create an initial entry.
			_session.execute(_prepInsertData.bind(data.getId(),
					Sets.newHashSet(IPLocation.localIpLocation.getHost(),location.getHost())));
		}
		else {
			// If an entry existed, then update the existing one.
			_session.execute(_prepUpdateData.bind(Collections.singleton(location.getHost()), data.getId()));
		}
	}
}
