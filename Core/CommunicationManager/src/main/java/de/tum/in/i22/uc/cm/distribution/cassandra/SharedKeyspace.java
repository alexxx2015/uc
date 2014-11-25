package de.tum.in.i22.uc.cm.distribution.cassandra;

import java.sql.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.AlreadyExistsException;
import com.datastax.driver.core.exceptions.UnavailableException;
import com.datastax.driver.core.utils.UUIDs;

import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.AtomicOperator;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IOperator;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.Threading;
import de.tum.in.i22.uc.pdp.core.operators.EventMatchOperator;
import de.tum.in.i22.uc.pdp.core.operators.StateBasedOperator;

public class SharedKeyspace extends Keyspace {
	protected static final Logger _logger = LoggerFactory.getLogger(SharedKeyspace.class);

	private static final String TABLE_SHARED_DATA = "hasdata";
	private static final String TABLE_SHARED_OP_NOTIFIED = "opnotified";
	private static final String TABLE_SHARED_MECHANISMS = "mechanisms";
	private static final String TABLE_SHARED_POLICIES = "policies";

	private static final List<String> _tablesShared;

	private final XmlPolicy _policy;

	private final Map<IOperator,Long> _lastInsert;

	static {
		_tablesShared = new LinkedList<>();
		_tablesShared.add(
				"CREATE TABLE " + TABLE_SHARED_DATA + " ("
						+ "data text,"
						+ "locations set<text>,"
						+ "PRIMARY KEY (data)"
						+ ");");
		_tablesShared.add(
				"CREATE TABLE " + TABLE_SHARED_OP_NOTIFIED + " ("
						+ "opid text,"
						+ "time timeuuid,"
						+ "location text,"
						+ "timestep bigint,"
						+ "PRIMARY KEY (opid,timestep,time,location)) "
						+ "WITH CLUSTERING ORDER BY (timestep DESC);");
		_tablesShared.add(
				"CREATE TABLE " + TABLE_SHARED_POLICIES + " ("
						+ "policyName text,"
						+ "policy text,"
						+ "PRIMARY KEY (policyName));");
		_tablesShared.add(
				"CREATE TABLE " + TABLE_SHARED_MECHANISMS + " ("
						+ "mechanismName text,"
						+ "firstTick bigint,"
						+ "PRIMARY KEY (mechanismName));");
	};


	public SharedKeyspace(XmlPolicy policy, Session session, Cluster cluster) {
		super(policy.getName().toLowerCase(), session, cluster);
		_policy = policy;
		_lastInsert = new HashMap<>();
		init();
	}


	/**
	 * Creates a keyspace that is shared by all entities enforcing
	 * the specified policy.
	 *
	 * @param policy the policy for which a keyspace is to be created.
	 */
	private void init() {
		boolean existed = false;

		/*
		 * Try to create the Keyspace.
		 */
		try {
			_session.execute("CREATE KEYSPACE " + _name
					+ " WITH replication = {'class':'NetworkTopologyStrategy','" + IPLocation.localIpLocation.getHost() + "':1}");
		}
		catch (AlreadyExistsException e) {
			/*
			 * If the keyspace did already existed, there is nothing to do.
			 */
			existed = true;
		}


		if (!existed) {
			/*
			 * The keyspace did not exist before.
			 */
			Session newSession = _cluster.connect(_name);

			/*
			 * Create all tables. In parallel.
			 */
			CompletionService<?> _completionService = new ExecutorCompletionService<>(Threading.instance());
			_tablesShared.forEach(t -> _completionService.submit(() -> newSession.execute(t), null));
			_tablesShared.forEach(t -> Threading.take(_completionService));

			try {
				/*
				 * Insert policy into the corresponding table.
				 */
				newSession.execute("INSERT INTO " + TABLE_SHARED_POLICIES + " (policyName,policy)"
						+ " VALUES ('" + _policy.getName() + "','" + CassandraDistributionManager.toBase64(_policy.getXml()) + "');");
			}
			catch (Exception e) {
				_logger.error("Error inserting policy {}: {}.", _policy.getName(), e.getMessage());
			}
		}
	}



	// FIXME Execution of this method should actually be atomic, as otherwise
	// we might experience lost updates, etc. Can this be realized? Would it even
	// be bad if we lose an update or can we compensate for it later?
	/**
	 *
	 * @param name the keyspace on which to work on
	 * @param location the location to add or remove to the keyspace
	 * @param add whether the location ought to be added or removed.
	 * 		If the value is true, the location will be added; otherwise,
	 * 		it will be removed
	 * @return returns true if the keyspace was in fact adjusted,
	 * 		i.e. if the provided location was added or removed.
	 */
	boolean adjustSharedKeyspace(String name, IPLocation location, boolean add) {

		name = name.toLowerCase();

		// (1) We retrieve the current information about the keyspace
		ResultSet rows = _session.execute("SELECT strategy_options FROM system.schema_keyspaces "
				+ "WHERE keyspace_name = '" + name + "'");

		// (2) We build the set of locations that are currently known within the keyspace
		Set<String> allLocations = new HashSet<>();
		rows.forEach(r -> {
			String[] entries = r.getString("strategy_options").replaceAll("[{}\"]", "").split(",");
			for (String loc : entries) {
				allLocations.add(loc.split(":")[0]);
			}
		});

		// (3) According to parameter 'add', we add or remove the provided location from the set of locations
		if ((add && !allLocations.add(location.getHost())) || (!add && !allLocations.remove(location.getHost()))) {
			// (a) If we are supposed to add, but adding fails, then the location was already present and we are done
			// (b) If we are supposed to remove, but removing fails, then the location was not present and we are done
			return false;
		}


		// (3) We make a string out of all locations and create the query
		StringBuilder query = new StringBuilder(allLocations.size() * 22 + 100);
		query.append("ALTER KEYSPACE " + name + " WITH replication ");
		query.append("= {'class':'NetworkTopologyStrategy',");

		allLocations.forEach(l ->
			query.append("'" + l + "':1,")
		);

		query.deleteCharAt(query.length() - 1);
		query.append("}");

		// (4) We execute the query
		_session.execute(query.toString());

		return true;
	}

	public void setFirstTick(String mechanismName, long firstTick) {
		if (_session.execute("SELECT mechanismName FROM " + _name + "." + TABLE_SHARED_MECHANISMS
				+ " WHERE mechanismName = '" + mechanismName + "' LIMIT 1;").isExhausted()) {

			_session.execute("INSERT INTO " + _name + "." + TABLE_SHARED_MECHANISMS
				+ " (mechanismName,firstTick) VALUES "
				+ "('" + mechanismName + "', " + firstTick + ");");
		}
	}

	public long getFirstTick(String mechanismName) {
		ResultSet rs = _session.execute("SELECT firstTick FROM " + _name + "." + TABLE_SHARED_MECHANISMS
				+ " WHERE mechanismName = '" + mechanismName + "' LIMIT 1;");

		if (rs.isExhausted()) {
			return Long.MIN_VALUE;
		}

		return rs.iterator().next().getLong("firstTick");
	}

	public boolean wasNotifiedAtTimestep(AtomicOperator operator, long timestep) {
		_logger.debug("wasNotifiedAtTimestep({}, {})", operator, timestep);
		ResultSet rs = _session.execute("SELECT opid FROM " + operator.getMechanism().getPolicyName() + "." + TABLE_SHARED_OP_NOTIFIED
				+ " WHERE opid = '" + operator.getFullId() + "'"
				+ " AND timestep = " + timestep
				+ " LIMIT 1;");
		return operator.getPositivity().value() != rs.isExhausted();
	}

	public boolean wasNotifiedInBetween(AtomicOperator operator, long from, long to) {
		_logger.debug("wasNotifiedInBetween({}, {}, {})", operator, from, to);
		ResultSet rs = _session.execute("SELECT opid FROM " + operator.getMechanism().getPolicyName() + "." + TABLE_SHARED_OP_NOTIFIED
				+ " WHERE opid = '" + operator.getFullId() + "'"
				+ " AND time > maxTimeuuid('" + CassandraDistributionManager.sdf.format(new Date(from)) + "')"
				+ " AND time < minTimeuuid('" + CassandraDistributionManager.sdf.format(new Date(to)) + "')"
				+ " LIMIT 1;");
		return operator.getPositivity().value() != rs.isExhausted();
	}

	public int howOftenNotifiedAtTimestep(AtomicOperator operator, long timestep) {
		_logger.debug("howOftenNotifiedAtTimestep({}, {})", operator, timestep);
		ResultSet rs = _session.execute("SELECT opid FROM " + operator.getMechanism().getPolicyName() + "." + TABLE_SHARED_OP_NOTIFIED
				+ " WHERE opid = '" + operator.getFullId() + "'"
				+ " AND timestep = " + timestep + ";");

		return rs.all().size();
	}

	public int howOftenNotifiedInBetween(AtomicOperator operator, long from, long to) {
		_logger.debug("howOftenNotifiedInBetween({}, {}, {})", operator, from, to);
		ResultSet rs = _session.execute("SELECT opid FROM " + operator.getMechanism().getPolicyName() + "." + TABLE_SHARED_OP_NOTIFIED
				+ " WHERE opid = '" + operator.getFullId() + "'"
				+ " AND time > maxTimeuuid('" + CassandraDistributionManager.sdf.format(new Date(from)) + "')"
				+ " AND time < minTimeuuid('" + CassandraDistributionManager.sdf.format(new Date(to)) + "');");

		return rs.all().size();
	}

	public int howOftenNotifiedSinceTimestep(AtomicOperator operator, long timestep) {
		_logger.debug("howOftenNotifiedAtTimestep({}, {})", operator, timestep);
		ResultSet rs = _session.execute("SELECT opid FROM " + operator.getMechanism().getPolicyName() + "." + TABLE_SHARED_OP_NOTIFIED
				+ " WHERE opid = '" + operator.getFullId() + "'"
				+ " AND timestep >= " + timestep + ";");

		return rs.all().size();
	}

	public void update(IOperator op, boolean endOfTimestep) {
		/*
		 * Each INSERT statement takes approx. 180 characters,
		 * plus some constant overhead of 50 characters.
		 */
		StringBuilder batchJob = new StringBuilder(230);
//		batchJob.append("BEGIN UNLOGGED BATCH ");

		/*
		 * Calculate the time when this operator happened.
		 * If we are not at the end of a timestep, simply use now().
		 * Otherwise, create a UUID corresponding to lastTick.
		 */
		String time = "";
		long timestep = op.getMechanism().getTimestep();

		boolean doInsert = false;

		if (op instanceof EventMatchOperator) {
			doInsert = true;
			time = endOfTimestep
					? UUIDs.endOf(op.getMechanism().getLastTick() - 1).toString()
					: "now()";
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
				time = UUIDs.startOf(timeToInsert).toString();
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
					time = "now()";
					_lastInsert.put(op, System.currentTimeMillis());
				}
			}
		}

		if (doInsert) {
			_logger.info("Updating Cassandra state at time {} with event {}",
					 CassandraDistributionManager.sdf.format(new Date(time.equals("now()") ? System.currentTimeMillis() : UUIDs.unixTimestamp(UUID.fromString(time)))),
					op.getFullId());

			batchJob.append("INSERT INTO " + _name + "." + TABLE_SHARED_OP_NOTIFIED
					+ " (opid, location, timestep, time) VALUES ("
					+ "'" + op.getFullId() + "',"
					+ "'" + IPLocation.localIpLocation.getHost() + "',"
					+ timestep + ","
					+ time
					+ ") USING TTL " + op.getTTL() / 1000 + ";");
		}

//		batchJob.append(" APPLY BATCH;");

		_session.execute(batchJob.toString());
	}

	void addData(IData data, IPLocation location) {
		boolean done = false;
		while (!done) {
			try {
				// Check whether there already exists an entry for this dataID ...
				if (_session.execute("SELECT data from " + _name + "." + TABLE_SHARED_DATA + " WHERE data = '" + data.getId() + "';").isExhausted()) {
					// ... if not, insert the corresponding row
					_session.execute("INSERT INTO " + _name + "." + TABLE_SHARED_DATA
							+ " (data, locations) VALUES ('" + data.getId() + "',{'"
							+ IPLocation.localIpLocation.getHost() + "','"
							+ location.getHost() + "'})");
				}
				else {
					// ... otherwise add the additional location to the existing row
					_session.execute("UPDATE " + _name + "." + TABLE_SHARED_DATA + " SET locations = locations + {'"
							+ location.getHost()	+ "'} WHERE data = '" + data.getId() + "'");
				}
				done = true;
			}
			catch (UnavailableException e) {
				_logger.warn("Cannot execute query: {}.", e.getMessage());
			}
		}
	}
}
