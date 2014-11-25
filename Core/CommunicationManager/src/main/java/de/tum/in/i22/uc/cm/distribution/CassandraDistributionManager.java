package de.tum.in.i22.uc.cm.distribution;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.AtomicMonotonicTimestampGenerator;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.QueryOptions;
import com.datastax.driver.core.ResultSet;
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

	private static final String TABLE_SHARED_DATA = "hasdata";
	private static final String TABLE_SHARED_OP_NOTIFIED = "opnotified";
	private static final String TABLE_SHARED_MECHANISMS = "mechanisms";
	private static final String TABLE_SHARED_POLICIES = "policies";

	private static final String TABLE_PRIVATE_POLICIES = "policies";

	private static final List<String> _tablesShared;

	private static final List<String> _tablesPrivate;

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

		_tablesPrivate = new LinkedList<>();
		_tablesPrivate.add(
				"CREATE TABLE " + TABLE_PRIVATE_POLICIES + " ("
						+ "policyName text,"
						+ "policy text,"
						+ "PRIMARY KEY (policyName));");
	};

	private final Session _defaultSession;

	private final Cluster _cluster;

	private PdpProcessor _pdp;
	private PipProcessor _pip;
	private PmpProcessor _pmp;

	private boolean _initialized = false;

	private String _hostname;

	private final Map<IOperator,Long> _lastInsert;

	private final CompletionService<?> _completionService;

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

		_responsiblePdps = Collections.synchronizedMap(new HashMap<>());
		try {
			_hostname = InetAddress.getLocalHost().getHostName().replaceAll("[^a-zA-Z0-9]","");
		} catch (UnknownHostException e) {
			_hostname = "";
			_logger.warn("Unable to retrieve hostname.");
		}

		_completionService = new ExecutorCompletionService<>(Executors.newCachedThreadPool());

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

		initPrivateKeyspace();

		_initialized = true;
	}

	@Override
	public void registerPolicy(XmlPolicy policy) {
		createSharedPolicyKeyspace(policy);

		/*
		 * TODO: Encrypt what we are writing, so that others don't know which policies we are enforcing
		 */
		ResultSet rs = _defaultSession.execute("SELECT policyName FROM " + _hostname + "." + TABLE_PRIVATE_POLICIES + " WHERE policyName = '" + policy.getName() + "';");
		if (rs.isExhausted()) {
			_defaultSession.execute("INSERT INTO " + _hostname + "." + TABLE_PRIVATE_POLICIES + " (policyName,policy) VALUES ('" + policy.getName() + "','" + toBase64(policy.getXml()) + "');");
		}
	}

	@Override
	public void unregisterPolicy(String policyName, IPLocation location) {
		adjustPolicyKeyspace(policyName, location, false);

		_defaultSession.execute("DELETE FROM " + _hostname + "." + TABLE_PRIVATE_POLICIES + " WHERE policyName = '" + policyName + "';");
	}

	@Override
	public void setFirstTick(String policyName, String mechanismName, long firstTick) {
		if (_defaultSession.execute("SELECT mechanismName FROM " + policyName + "." + TABLE_SHARED_MECHANISMS
				+ " WHERE mechanismName = '" + mechanismName + "' LIMIT 1;").isExhausted()) {

			_defaultSession.execute("INSERT INTO " + policyName + "." + TABLE_SHARED_MECHANISMS
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
					Pmp2PmpClient remotePmp = new ThriftClientFactory().createPmp2PmpClient(pmpLocation);
					remotePmp.connect();

					if (!remotePmp.deployPolicyRawXMLPmp(policy.getXml()).isStatus(EStatus.OKAY)) {
						success = false;
					}

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
						if (_defaultSession.execute("SELECT data from " + p.getName() + "." + TABLE_SHARED_DATA + " WHERE data = '" + dataID + "';").isExhausted()) {
							// ... if not, insert the corresponding row
							_defaultSession.execute("INSERT INTO " + p.getName() + "." + TABLE_SHARED_DATA
									+ " (data, locations) VALUES ('" + dataID + "',{'"
									+ IPLocation.localIpLocation.getHost() + "','"
									+ dstLocation.getHost() + "'})");
						}
						else {
							// ... otherwise add the additional location to the existing row
							_defaultSession.execute("UPDATE " + p.getName() + "." + TABLE_SHARED_DATA + " SET locations = locations + {'"
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
			remotePip = new ThriftClientFactory().createPip2PipClient(pipLocation);
			remotePip.connect();
		} catch (IOException e) {
			_logger.error("Unable to perform remote data transfer with [" + pipLocation + "]");
			return;
		}

		remotePip.initialRepresentation(socketName, data);

		remotePip.disconnect();
	}

	private void initPrivateKeyspace() {
		boolean existed = false;

		try {
			_defaultSession.execute("CREATE KEYSPACE " + _hostname
					+ " WITH replication = {'class':'NetworkTopologyStrategy','" + IPLocation.localIpLocation.getHost() + "':1}");
		}
		catch (AlreadyExistsException e) {
			existed = true;
		}

		if (existed) {
			ResultSet rs = _defaultSession.execute("SELECT policy FROM " + _hostname + "." + TABLE_PRIVATE_POLICIES);

			/*
			 * For some reason, this won't work if parallelized
			 */
			rs.forEach(r -> _pmp.deployPolicyRawXMLPmp(fromBase64(r.getString("policy"))));
		}
		else {
			Session newSession = _cluster.connect(_hostname);

			_tablesPrivate.forEach(t -> _completionService.submit(() -> newSession.execute(t), null));
			_tablesPrivate.forEach(t -> Threading.take(_completionService));
		}
	}

	private void createSharedPolicyKeyspace(XmlPolicy policy) {

		String keyspaceName = policy.getName().toLowerCase();

		try {
			_defaultSession.execute("CREATE KEYSPACE " + keyspaceName
					+ " WITH replication = {'class':'NetworkTopologyStrategy','" + IPLocation.localIpLocation.getHost() + "':1}");
		}
		catch (AlreadyExistsException e) {
			return;
		}

		Session newSession = _cluster.connect(keyspaceName);

		_tablesShared.forEach(t -> _completionService.submit(() -> newSession.execute(t), null));
		_tablesShared.forEach(t -> Threading.take(_completionService));

		try {
			newSession.execute("INSERT INTO " + TABLE_SHARED_POLICIES + " (policyName,policy) VALUES ('" + policy.getName() + "','" + policy.getXml() + "');");
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
		StringBuilder query = new StringBuilder();
		query.append("ALTER KEYSPACE " + name + " WITH replication ");
		query.append("= {'class':'NetworkTopologyStrategy',");

		allLocations.forEach(l ->
			query.append("'" + l + "':1,")
		);

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

		data.forEach(d ->
			policies.addAll(_pmp.getPolicies(d))
		);

		return policies;
	}


	@Override
	public void update(IOperator op, boolean endOfTimestep) {
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
					 sdf.format(new Date(time.equals("now()") ? System.currentTimeMillis() : UUIDs.unixTimestamp(UUID.fromString(time)))),
					op.getFullId());

			batchJob.append("INSERT INTO " + op.getMechanism().getPolicyName() + "." + TABLE_SHARED_OP_NOTIFIED
					+ " (opid, location, timestep, time) VALUES ("
					+ "'" + op.getFullId() + "',"
					+ "'" + IPLocation.localIpLocation.getHost() + "',"
					+ timestep + ","
					+ time
					+ ") USING TTL " + op.getTTL() / 1000 + ";");
		}

//		batchJob.append(" APPLY BATCH;");

		_defaultSession.execute(batchJob.toString());
	}


//	@Override
//	public boolean wasNotifiedSince(AtomicOperator operator, long since) {
//		_logger.debug("wasNotifiedSince({}, {})", operator, since);
//		ResultSet rs = _defaultSession.execute("SELECT opid FROM " + operator.getMechanism().getPolicyName() + "." + TABLE_NAME_OP_NOTIFIED
//						+ " WHERE opid = '" + operator.getFullId() + "'"
//						+ " AND time > maxTimeuuid('" + sdf.format(new Date(since)) + "')"
//						+ " LIMIT 1;");
//		return operator.getPositivity().value() != rs.isExhausted();
//	}


	@Override
	public boolean wasNotifiedInBetween(AtomicOperator operator, long from, long to) {
		_logger.debug("wasNotifiedInBetween({}, {}, {})", operator, from, to);
		ResultSet rs = _defaultSession.execute("SELECT opid FROM " + operator.getMechanism().getPolicyName() + "." + TABLE_SHARED_OP_NOTIFIED
				+ " WHERE opid = '" + operator.getFullId() + "'"
				+ " AND time > maxTimeuuid('" + sdf.format(new Date(from)) + "')"
				+ " AND time < minTimeuuid('" + sdf.format(new Date(to)) + "')"
				+ " LIMIT 1;");
		return operator.getPositivity().value() != rs.isExhausted();
	}

	@Override
	public int howOftenNotifiedInBetween(AtomicOperator operator, long from, long to) {
		_logger.debug("howOftenNotifiedInBetween({}, {}, {})", operator, from, to);
		ResultSet rs = _defaultSession.execute("SELECT opid FROM " + operator.getMechanism().getPolicyName() + "." + TABLE_SHARED_OP_NOTIFIED
				+ " WHERE opid = '" + operator.getFullId() + "'"
				+ " AND time > maxTimeuuid('" + sdf.format(new Date(from)) + "')"
				+ " AND time < minTimeuuid('" + sdf.format(new Date(to)) + "');");

		return rs.all().size();
	}

	@Override
	public boolean wasNotifiedAtTimestep(AtomicOperator operator, long timestep) {
		_logger.debug("wasNotifiedAtTimestep({}, {})", operator, timestep);
		ResultSet rs = _defaultSession.execute("SELECT opid FROM " + operator.getMechanism().getPolicyName() + "." + TABLE_SHARED_OP_NOTIFIED
				+ " WHERE opid = '" + operator.getFullId() + "'"
				+ " AND timestep = " + timestep
				+ " LIMIT 1;");
		return operator.getPositivity().value() != rs.isExhausted();
	}

	@Override
	public int howOftenNotifiedAtTimestep(AtomicOperator operator, long timestep) {
		_logger.debug("howOftenNotifiedAtTimestep({}, {})", operator, timestep);
		ResultSet rs = _defaultSession.execute("SELECT opid FROM " + operator.getMechanism().getPolicyName() + "." + TABLE_SHARED_OP_NOTIFIED
				+ " WHERE opid = '" + operator.getFullId() + "'"
				+ " AND timestep = " + timestep + ";");

		return rs.all().size();
	}

	@Override
	public int howOftenNotifiedSinceTimestep(AtomicOperator operator, long timestep) {
		_logger.debug("howOftenNotifiedAtTimestep({}, {})", operator, timestep);
		ResultSet rs = _defaultSession.execute("SELECT opid FROM " + operator.getMechanism().getPolicyName() + "." + TABLE_SHARED_OP_NOTIFIED
				+ " WHERE opid = '" + operator.getFullId() + "'"
				+ " AND timestep >= " + timestep + ";");

		return rs.all().size();
	}

	@Override
	public IPLocation getResponsibleLocation(String ip) {
		IPLocation result = _responsiblePdps.get(ip);

		if (result == null) {
			IPLocation loc = new IPLocation(ip, Settings.getInstance().getPepListenerPort());
			Pdp2PepClient pdp2pep = new ThriftClientFactory().createPdp2PepClient(loc);
			try {
				_logger.debug("Asking remotely for PdpLocation at {}.", loc);

				Future<?> futureCon = Threading.instance().submit(() -> {
					try {
						pdp2pep.connect();
					} catch (Exception e) {}
				}, null);

				futureCon.get(200, TimeUnit.MILLISECONDS);

				result = pdp2pep.getResponsiblePdpLocation();
				pdp2pep.disconnect();
			} catch (Exception e) {
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
		ResultSet rs =_defaultSession.execute("SELECT firstTick FROM " + policyName + "." + TABLE_SHARED_MECHANISMS
				+ " WHERE mechanismName = '" + mechanismName + "' LIMIT 1;");

		if (rs.isExhausted()) {
			return Long.MIN_VALUE;
		}

		return rs.iterator().next().getLong("firstTick");
	}

	private String toBase64(String s) {
		return new String(Base64.getEncoder().encode(s.getBytes(Charset.defaultCharset())), Charset.defaultCharset());
	}

	private String fromBase64(String s) {
		return new String(Base64.getDecoder().decode(s.getBytes(Charset.defaultCharset())), Charset.defaultCharset());
	}
}
