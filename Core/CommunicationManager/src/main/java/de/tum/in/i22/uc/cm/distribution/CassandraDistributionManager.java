package de.tum.in.i22.uc.cm.distribution;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.AtomicMonotonicTimestampGenerator;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.QueryOptions;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.AlreadyExistsException;
import com.datastax.driver.core.exceptions.UnavailableException;
import com.datastax.driver.core.utils.UUIDs;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.AtomicOperator;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IOperator;
import de.tum.in.i22.uc.cm.datatypes.linux.SocketContainer;
import de.tum.in.i22.uc.cm.datatypes.linux.SocketName;
import de.tum.in.i22.uc.cm.distribution.client.Pdp2PepClient;
import de.tum.in.i22.uc.cm.distribution.client.Pip2PipClient;
import de.tum.in.i22.uc.cm.distribution.client.Pmp2PmpClient;
import de.tum.in.i22.uc.cm.pip.RemoteDataFlowInfo;
import de.tum.in.i22.uc.cm.processing.PdpProcessor;
import de.tum.in.i22.uc.cm.processing.PipProcessor;
import de.tum.in.i22.uc.cm.processing.PmpProcessor;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pdp.core.operators.EventMatchOperator;
import de.tum.in.i22.uc.pdp.core.operators.StateBasedOperator;
import de.tum.in.i22.uc.thrift.client.ThriftClientFactory;

class CassandraDistributionManager implements IDistributionManager {
	protected static final Logger _logger = LoggerFactory.getLogger(CassandraDistributionManager.class);

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");

	private static final String TABLE_NAME_DATA = "hasdata";
	private static final String TABLE_NAME_OP_OBSERVED = "optrue";
	private static final String TABLE_NAME_POLICY = "policy";

	private static final List<String> _tables;

	static {
		_tables = new LinkedList<>();
		_tables.add(
				"CREATE TABLE " + TABLE_NAME_DATA + " ("
						+ "data text,"
						+ "locations set<text>,"
						+ "PRIMARY KEY (data)"
						+ ");");
		_tables.add(
				"CREATE TABLE " + TABLE_NAME_OP_OBSERVED + " ("
						+ "opid text,"
						+ "time timeuuid,"
						+ "location text,"
						+ "PRIMARY KEY (opid,time,location)) "
						+ "WITH CLUSTERING ORDER BY (time DESC);");
		_tables.add(
				"CREATE TABLE " + TABLE_NAME_POLICY + " ("
						+ "mechanismName text,"
						+ "firstTick bigint,"
						+ "PRIMARY KEY (mechanismName));");
	};

//	private final ConnectionManager<Pmp2PmpClient> _pmpConnectionManager;
//	private final ConnectionManager<Pip2PipClient> _pipConnectionManager;

	private final Session _defaultSession;

	private final Cluster _cluster;

	private PdpProcessor _pdp;
	private PipProcessor _pip;
	private PmpProcessor _pmp;

	private boolean _initialized = false;

	private String _hostname;

	private final Map<IOperator,Long> _lastInsert;

	/**
	 * Maps the IP of a PEP to its responsible PDP.
	 */
	private final Map<String, IPLocation> _responsiblePdps;

	public CassandraDistributionManager() {
		QueryOptions options = new QueryOptions().setConsistencyLevel(Settings.getInstance().getDistributionConsistencyLevel());

		InetAddress addr = null;
		try {
			addr = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			_logger.error("Unable to retrieve hostname: {}.", e.getMessage());
			throw new RuntimeException(e);
		}

		_cluster = Cluster.builder().withQueryOptions(options)
							.addContactPoint(addr.getHostAddress())
							.withTimestampGenerator(new AtomicMonotonicTimestampGenerator())
							.build();
		_defaultSession = _cluster.connect();
//		_pmpConnectionManager = new ConnectionManager<>(5);
//		_pipConnectionManager = new ConnectionManager<>(5);
		_responsiblePdps = new HashMap<>();
		try {
			_hostname = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			_hostname = "";
			_logger.warn("Unable to retrieve hostname.");
		}

		_lastInsert = new HashMap<>();
	}


	@Override
	public void init(PdpProcessor pdp, PipProcessor pip, PmpProcessor pmp) {
		if (_initialized) {
			throw new RuntimeException("Was already initialized");
		}

		_pdp = pdp;
		_pip = pip;
		_pmp = pmp;
		_initialized = true;
	}

	@Override
	public void registerPolicy(XmlPolicy policy) {
		createPolicyKeyspace(policy);
	}

	@Override
	public void unregisterPolicy(String policyName, IPLocation location) {
		adjustPolicyKeyspace(policyName, location, false);
	}

	@Override
	public void setFirstTick(String policyName, String mechanismName, long firstTick) {
		if (_defaultSession.execute("SELECT mechanismName FROM " + policyName + "." + TABLE_NAME_POLICY
				+ " WHERE mechanismName = '" + mechanismName + "' LIMIT 1;").isExhausted()) {

			_defaultSession.execute("INSERT INTO " + policyName + "." + TABLE_NAME_POLICY
				+ " (mechanismName,firstTick) VALUES "
				+ "('" + mechanismName + "', " + firstTick + ");");
		}
	}

	/**
	 * Transfers all specified {@link XmlPolicy}s to the specified {@link IPLocation}
	 * via Thrift interfaces.
	 *
	 * @param policies the policies to send
	 * @param pmpLocation the location to which the policies are sent
	 */
	private void doStickyPolicyTransfer(Set<XmlPolicy> policies, IPLocation pmpLocation) {
		_logger.debug("doStickyPolicyTransfer invoked: " + pmpLocation + ": " + policies);

		for (XmlPolicy policy : policies) {
			/*
			 * For each policy, the protocol is as follows:
			 * (1) extend the keyspace with the given location
			 * (2) if the keyspace was in fact extended
			 *     (meaning that the provided location was not yet aware of the policy),
			 *     then the policy is sent to the remote PMP.
			 * (3) if remote deployment of the policy fails, then the given location
			 *     is removed from the policy's keyspace
			 */

			if (adjustPolicyKeyspace(policy.getName(), pmpLocation, true)) {
				boolean success = true;

				// if the location was not yet part of the keyspace, then we need to
				// deploy the policy at the remote location
				try {
//					Pmp2PmpClient remotePmp = _pmpConnectionManager.obtain(new ThriftClientFactory().createPmp2PmpClient(pmpLocation));
					Pmp2PmpClient remotePmp = new ThriftClientFactory().createPmp2PmpClient(pmpLocation);
					remotePmp.connect();

					if (!remotePmp.deployPolicyRawXMLPmp(policy.getXml()).isStatus(EStatus.OKAY)) {
						success = false;
					}

//					_pmpConnectionManager.release(remotePmp);
					remotePmp.disconnect();
				} catch (IOException e) {
					success = false;
					_logger.error("Unable to deploy XML policy remotely at [" + pmpLocation + "]: " + e.getMessage());
				}

				// If remote deployment of the policy fails,
				// then we remove the location from the keyspace
				if (!success) {
					adjustPolicyKeyspace(policy.getName(), pmpLocation, false);
				}

				/*
				 * TODO: We need to deal with the fact that success == false at this place.
				 * Possible options: Retry, throw exception, cancel data transfer
				 */
			}
			else {
				_logger.debug("Not performing remote policy transfer. Policy should already be present remotely.");
			}
		}
	}

	/**
	 * Perform cross-system data flow tracking on a per-location granularity, i.e.
	 * remember which locations are aware of which data.
	 *
	 * @param data the set of data that has flown
	 * @param dstLocation the location to which the specified data has flown
	 */
	private void doCrossSystemDataTrackingCoarse(Set<IData> data, IPLocation dstLocation) {
		_logger.debug("doCrossSystemDataTrackingCoarse invoked: " + dstLocation + " -> " + data);

		for (IData d : data) {
			String dataID = d.getId();

			for (XmlPolicy p : _pmp.getPolicies(d)) {

				/*
				 * FIXME: The below statements may fail because the
				 * keyspace and tables might not have been created yet. Handle this.
				 */

				boolean done = false;

				while (!done) {
					try {
						// Check whether there already exists an entry for this dataID ...
						if (_defaultSession.execute("SELECT data from " + p.getName() + "." + TABLE_NAME_DATA + " WHERE data = '" + dataID + "';").isExhausted()) {
							// ... if not, insert the corresponding row
							_defaultSession.execute("INSERT INTO " + p.getName() + "." + TABLE_NAME_DATA
									+ " (data, locations) VALUES ('" + dataID + "',{'"
									+ IPLocation.localIpLocation.getHost() + "','"
									+ dstLocation.getHost() + "'})");
						}
						else {
							// ... otherwise add the additional location to the existing row
							_defaultSession.execute("UPDATE " + p.getName() + "." + TABLE_NAME_DATA + " SET locations = locations + {'"
									+ dstLocation.getHost()	+ "'} WHERE data = '" + dataID + "'");
						}
						done = true;
					}
					catch (UnavailableException e) {
						_logger.warn("Cannot execute query: {}.", e.getMessage());
					}
				}
			}
		}
	}


	/**
	 * Perform cross-system data flow tracking on a per-container basis.
	 * The mapping between containers (more precisely: their names) and the
	 * set of data being transferred to them is specified by parameter flows.
	 *
	 * @param pipLocation the location to which the data flow occurred.
	 * @param flows maps the destination container name to set of data it is receiving
	 */
	private void doCrossSystemDataTrackingFine(IPLocation pipLocation, SocketName socketName, Set<IData> data) {
		_logger.debug("doCrossSystemDataTrackingFine invoked: {}, {}, {}.", pipLocation, socketName, data);

		Pip2PipClient remotePip = null;

		try {
//			remotePip = _pipConnectionManager.obtain(new ThriftClientFactory().createPip2PipClient(pipLocation));
			remotePip = new ThriftClientFactory().createPip2PipClient(pipLocation);
			remotePip.connect();
		} catch (IOException e) {
			_logger.error("Unable to perform remote data transfer with [" + pipLocation + "]");
			return;
		}

		remotePip.initialRepresentation(socketName, data);

		remotePip.disconnect();

//		_pipConnectionManager.release(remotePip);
	}

	private void createPolicyKeyspace(XmlPolicy policy) {

		String keyspaceName = policy.getName().toLowerCase();

		try {
			_defaultSession.execute("CREATE KEYSPACE " + keyspaceName
					+ " WITH replication = {'class':'NetworkTopologyStrategy','" + IPLocation.localIpLocation.getHost() + "':1}");
		}
		catch (AlreadyExistsException e) {
			return;
		}

		final Session newSession = _cluster.connect(keyspaceName);

		ExecutorService es = Executors.newCachedThreadPool();

		// Create all tables
		for (final String tbl : _tables) {
				es.execute(new Runnable() {
					@Override
					public void run() {
						try {
							newSession.execute(tbl);
						} catch (AlreadyExistsException e) {}
					}
				});
		}
		try {
			es.awaitTermination(1, TimeUnit.MINUTES);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			newSession.execute("INSERT INTO " + TABLE_NAME_POLICY + " (policyName,policy) VALUES ('" + policy.getXml() + "'," + policy.getFirstTick() + ");");
		}
		catch (Exception e) {
			_logger.error("Error inserting policy {}: {}.", policy.getName(), e.getMessage());
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
	private boolean adjustPolicyKeyspace(String name, IPLocation location, boolean add) {

		name = name.toLowerCase();

		// (1) We retrieve the current information about the keyspace
		ResultSet rows = _defaultSession.execute("SELECT strategy_options FROM system.schema_keyspaces "
				+ "WHERE keyspace_name = '" + name + "'");

		// (2) We build the set of locations that are currently known within the keyspace
		Set<String> allLocations = new HashSet<>();
		for (Row row : rows) {
			String[] entries = row.getString("strategy_options").replaceAll("[{}\"]", "").split(",");
			for (String loc : entries) {
				allLocations.add(loc.split(":")[0]);
			}
		}

		// (3) According to parameter 'add', we add or remove the provided location from the set of locations
		if ((add && !allLocations.add(location.getHost())) || (!add && !allLocations.remove(location.getHost()))) {
			// (a) If we are supposed to add, but adding fails, then the location was already present and we are done
			// (b) If we are supposed to remove, but removing fails, then the location was not present and we are done
			return false;
		}


		// (3) We make a string out of all locations and create the query
		StringBuilder query = new StringBuilder();
		query.append("ALTER KEYSPACE " + name + " WITH replication ");
		query.append("= {'class':'NetworkTopologyStrategy',");
		for (String loc : allLocations) {
			query.append("'" + loc + "':1,");
		}
		query.deleteCharAt(query.length() - 1);
		query.append("}");

		// (4) We execute the query
		_defaultSession.execute(query.toString());

		return true;
	}

	@Override
	public void dataTransfer(RemoteDataFlowInfo dataflow) {
		_logger.info("dataTransfer: " + dataflow);

		for (Entry<SocketContainer, Map<SocketContainer, Set<IData>>> flow : dataflow.getFlows().entrySet()) {
			Map<SocketContainer, Set<IData>> dsts = flow.getValue();

			for (Entry<SocketContainer, Set<IData>> dst : dsts.entrySet()) {

				SocketContainer dstSocket = dst.getKey();
				Set<IData> data = dst.getValue();

				IPLocation pipLocation = new IPLocation(dstSocket.getResponsibleLocation().getHost(), Settings.getInstance().getPipListenerPort());
				IPLocation pmpLocation = new IPLocation(dstSocket.getResponsibleLocation().getHost(), Settings.getInstance().getPmpListenerPort());

				doStickyPolicyTransfer(getAllPolicies(data), pmpLocation);
				doCrossSystemDataTrackingCoarse(data, pipLocation);
				doCrossSystemDataTrackingFine(pipLocation, dstSocket.getSocketName(), data);
			}
		}
	}


	private Set<XmlPolicy> getAllPolicies(Set<IData> data) {
		if (data == null || data.size() == 0) {
			return Collections.emptySet();
		}

		Set<XmlPolicy> policies = new HashSet<>();

		for (IData d : data) {
			policies.addAll(_pmp.getPolicies(d));
		}

		return policies;
	}


	@Override
	public void update(Collection<IOperator> changedOperators, boolean endOfTimestep) {
		/*
		 * TODO
		 * (1) We can probably do this in a new thread. However -> correctness? What if it fails?
		 * (2) If this is done in a separate thread and potentially retried: use a fixed timestamp
		 *     instead of now().
		 */

		/*
		 * Each INSERT statement takes approx. 180 characters,
		 * plus some constant overhead of 50 characters.
		 */
		StringBuilder batchJob = new StringBuilder(180 * changedOperators.size() + 50);
		batchJob.append("BEGIN UNLOGGED BATCH ");

		for (IOperator op : changedOperators) {

			/*
			 * Calculate the time when this operator happened.
			 * If we are not at the end of a timestep, simply use now().
			 * Otherwise, create a UUID corresponding to lastTick.
			 */
			String time = "";

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
						 sdf.format(new Date(time.equals("now()") ? System.currentTimeMillis() : UUIDs.unixTimestamp(UUID.fromString(time)))),
						op.getFullId());

				batchJob.append("INSERT INTO " + op.getMechanism().getPolicyName() + "." + TABLE_NAME_OP_OBSERVED
						+ " (opid, location, time) VALUES ("
						+ "'" + op.getFullId() + "',"
						+ "'" + IPLocation.localIpLocation.getHost() + "',"
						+ time
						+ ") USING TTL " + op.getTTL() / 1000 + ";");
			}
		}

		batchJob.append(" APPLY BATCH;");

		_defaultSession.execute(batchJob.toString());
	}


	@Override
	public boolean wasTrueSince(AtomicOperator operator, long since) {
		_logger.debug("wasTrueSince({}, {})", operator, since);
		ResultSet rs = _defaultSession.execute("SELECT opid FROM " + operator.getMechanism().getPolicyName() + "." + TABLE_NAME_OP_OBSERVED
						+ " WHERE opid = '" + operator.getFullId() + "'"
						+ " AND time > maxTimeuuid('" + sdf.format(new Date(since)) + "')"
						+ " LIMIT 1;");
		return operator.getPositivity().is(true) != rs.isExhausted();
//		if (operator.getPositivity().is(true)) {
//			return !rs.isExhausted();
//		}
//		else {
//			return rs.isExhausted();
//		}
	}


	@Override
	public boolean wasTrueInBetween(AtomicOperator operator, long from, long to) {
		_logger.debug("wasTrueInBetween({}, {}, {})", operator, from, to);
		ResultSet rs = _defaultSession.execute("SELECT opid FROM " + operator.getMechanism().getPolicyName() + "." + TABLE_NAME_OP_OBSERVED
				+ " WHERE opid = '" + operator.getFullId() + "'"
				+ " AND time > maxTimeuuid('" + sdf.format(new Date(from)) + "')"
				+ " AND time < minTimeuuid('" + sdf.format(new Date(to)) + "')"
				+ " LIMIT 1;");
		return operator.getPositivity().is(true) != rs.isExhausted();
//		if (operator.getPositivity().is(true)) {
//			return !rs.isExhausted();
//		}
//		else {
//			return rs.isExhausted();
//		}
	}

	@Override
	public int howOftenTrueInBetween(AtomicOperator operator, long from, long to) {
		if (!operator.getPositivity().is(true)) {
			throw new IllegalArgumentException("This method is only available for positive operators.");
		}

		_logger.debug("wasTrueInBetween({}, {}, {})", operator, from, to);
		ResultSet rs = _defaultSession.execute("SELECT opid FROM " + operator.getMechanism().getPolicyName() + "." + TABLE_NAME_OP_OBSERVED
				+ " WHERE opid = '" + operator.getFullId() + "'"
				+ " AND time > maxTimeuuid('" + sdf.format(new Date(from)) + "')"
				+ " AND time < minTimeuuid('" + sdf.format(new Date(to)) + "');");

		return rs.all().size();
	}


	@Override
	public IPLocation getResponsibleLocation(String ip) {
		IPLocation result = _responsiblePdps.get(ip);

		if (result == null) {
			IPLocation loc = new IPLocation(ip, Settings.getInstance().getPepListenerPort());
			Pdp2PepClient pdp2pep = new ThriftClientFactory().createPdp2PepClient(loc);
			try {
				pdp2pep.connect();
				_logger.debug("Asking remotely for PdpLocation at {}.", loc);
				result = pdp2pep.getResponsiblePdpLocation();
				pdp2pep.disconnect();
			} catch (IOException e) {
				result = new IPLocation(ip);
				_logger.warn("Unable to connect to {}.", loc);
				_logger.warn("Assuming a responsible location of {}.", ip);
			}
		}

		_responsiblePdps.put(ip, result);

		return result;
	}

	@Override
	public long getFirstTick(String policyName, String mechanismName) {
		ResultSet rs =_defaultSession.execute("SELECT firstTick FROM " + policyName + "." + TABLE_NAME_POLICY
				+ " WHERE mechanismName = '" + mechanismName + "' LIMIT 1;");

		if (rs.isExhausted()) {
			return Long.MIN_VALUE;
		}

		return rs.iterator().next().getLong("firstTick");
	}
}
