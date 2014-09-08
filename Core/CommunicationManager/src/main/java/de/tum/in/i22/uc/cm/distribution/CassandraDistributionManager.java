package de.tum.in.i22.uc.cm.distribution;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.QueryOptions;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.AlreadyExistsException;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IResponse;
import de.tum.in.i22.uc.cm.datatypes.interfaces.LiteralOperator;
import de.tum.in.i22.uc.cm.distribution.client.ConnectionManager;
import de.tum.in.i22.uc.cm.distribution.client.Pip2PipClient;
import de.tum.in.i22.uc.cm.distribution.client.Pmp2PmpClient;
import de.tum.in.i22.uc.cm.pip.RemoteDataFlowInfo;
import de.tum.in.i22.uc.cm.processing.PdpProcessor;
import de.tum.in.i22.uc.cm.processing.PipProcessor;
import de.tum.in.i22.uc.cm.processing.PmpProcessor;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pdp.core.operators.EventMatchOperator;
import de.tum.in.i22.uc.pdp.core.operators.StateBasedOperator;
import de.tum.in.i22.uc.pdp.distribution.DistributedPdpResponse;
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
						+ "location text,"
						+ "time timeuuid,"
						+ "PRIMARY KEY (opid,time)) "
						+ "WITH CLUSTERING ORDER BY (time DESC);");
		_tables.add(
				"CREATE TABLE " + TABLE_NAME_POLICY + " ("
						+ "policy text,"
						+ "PRIMARY KEY (policy));");
	};


	/*
	 * IMPORTANT:
	 * Cassandra 2.1.0-rc6 causes some trouble with upper/lowercase
	 * (Inserting a keyspace named 'testPolicy' resulted in keyspace named 'testpolicy',
	 *  which was afterwards not found by a lookup with name 'testPolicy')
	 * Therefore, make all tables, namespaces, column names, etc. lowercase.
	 *
	 */

	private final ConnectionManager<Pmp2PmpClient> _pmpConnectionManager;
	private final ConnectionManager<Pip2PipClient> _pipConnectionManager;

	private final Session _defaultSession;

	private final Cluster _cluster;

	private PdpProcessor _pdp;
	private PipProcessor _pip;
	private PmpProcessor _pmp;

	private boolean _initialized = false;

	public CassandraDistributionManager() {
		/*
		 * FIXME make ConsistencyLevel configurable from Settings.
		 */
		QueryOptions options = new QueryOptions().setConsistencyLevel(ConsistencyLevel.ALL);

		_cluster = Cluster.builder().withQueryOptions(options).addContactPoint("localhost").build();
		_defaultSession = _cluster.connect();
		_pmpConnectionManager = new ConnectionManager<>(5);
		_pipConnectionManager = new ConnectionManager<>(5);
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

			/**
			 * FIXME: There might be potential to parallelize policy transfer
			 */

			if (adjustPolicyKeyspace(policy.getName(), pmpLocation, true)) {
				boolean success = true;

				// if the location was not yet part of the keyspace, then we need to
				// deploy the policy at the remote location
				try {
					Pmp2PmpClient remotePmp = _pmpConnectionManager.obtain(new ThriftClientFactory().createPmp2PmpClient(pmpLocation));

					if (!remotePmp.deployPolicyRawXMLPmp(policy.getXml()).isStatus(EStatus.OKAY)) {
						success = false;
					}

					_pmpConnectionManager.release(remotePmp);
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
	private void doCrossSystemDataTrackingFine(IPLocation pipLocation, Map<IName, Set<IData>> flows) {
		Pip2PipClient remotePip = null;

		try {
			remotePip = _pipConnectionManager.obtain(new ThriftClientFactory().createPip2PipClient(pipLocation));
		} catch (IOException e) {
			_logger.error("Unable to perform remote data transfer with [" + pipLocation + "]");
			return;
		}

		for (IName name : flows.keySet()) {
			remotePip.initialRepresentation(name, flows.get(name));
		}

		_pipConnectionManager.release(remotePip);
	}

	private void createPolicyKeyspace(XmlPolicy policy) {
		try {
			_defaultSession.execute("CREATE KEYSPACE " + policy.getName()
					+ " WITH replication = {'class':'NetworkTopologyStrategy','" + IPLocation.localIpLocation.getHost() + "':1}");
		}
		catch (AlreadyExistsException e) {
			return;
		}

		Session newSession = _cluster.connect(policy.getName());

		// Create all tables
		for (String tbl : _tables) {
			try {
				newSession.execute(tbl);
			} catch (AlreadyExistsException e) {}
		}

		newSession.execute("INSERT INTO " + TABLE_NAME_POLICY + " (policy) VALUES ('" + policy.getXml() + "');");
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

		Map<Location, Map<IName, Set<IData>>> flows = dataflow.getFlows();

		Location srcLocation = dataflow.getSrcLocation();

		if (srcLocation instanceof LocalLocation) {
			srcLocation = IPLocation.localIpLocation;
		}
		else if (!(srcLocation instanceof IPLocation)) {
			_logger.warn("Source location [" + srcLocation + "] is not an IPLocation. Not performing remote data flow.");
			return;
		}

		for (Location dstLocation : flows.keySet()) {
			if (dstLocation instanceof IPLocation) {
				Set<IData> data = new HashSet<>();
				for (Set<IData> d : flows.get(dstLocation).values()) {
					data.addAll(d);
				}

				IPLocation pipLocation = new IPLocation(((IPLocation) dstLocation).getHost(), Settings.getInstance().getPipListenerPort());
				IPLocation pmpLocation = new IPLocation(((IPLocation) dstLocation).getHost(), Settings.getInstance().getPmpListenerPort());

				/*
				 * TODO: These three tasks can be parallelized(?!?)
				 */
				doStickyPolicyTransfer(getAllPolicies(data), pmpLocation);
				doCrossSystemDataTrackingCoarse(data, pipLocation);
				doCrossSystemDataTrackingFine(pipLocation, flows.get(dstLocation));

			}
			else {
				_logger.warn("Destination location [" + dstLocation + "] is not an IPLocation.");
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
	public void update(IResponse response) {
		if (!(response instanceof DistributedPdpResponse)) {
			return;
		}

		DistributedPdpResponse res = (DistributedPdpResponse) response;

		StringBuilder batchJob = new StringBuilder(512);
		batchJob.append("BEGIN UNLOGGED BATCH ");

		for (EventMatchOperator event : res.getEventMatches()) {
			_logger.info("UPDATING CASSANDRA STATE: event happened: {}", event.getFullId());

			batchJob.append("INSERT INTO " + event.getMechanism().getPolicyName() + "." + TABLE_NAME_OP_OBSERVED
					+ " (opid, location, time) VALUES ("
					+ "'" + event.getFullId() + "',"
					+ "'" + IPLocation.localIpLocation.getHost() + "',"
					+ "now()"
					+ ") USING TTL " + event.getTTL() / 1000 + ";");
		}


		for (StateBasedOperator sbo : res.getStateBasedOperatorTrue()) {
			_logger.info("UPDATING CASSANDRA STATE: state based operator signaled: {}", sbo.getFullId());

			batchJob.append("INSERT INTO " + sbo.getMechanism().getPolicyName() + "." + TABLE_NAME_OP_OBSERVED
					+ " (opid, location, time) VALUES ("
					+ "'" + sbo.getFullId() + "',"
					+ "'" + IPLocation.localIpLocation.getHost() + "',"
					+ "now()"
					+ ") USING TTL " + sbo.getTTL() / 1000 + ";");
		}

		batchJob.append(" APPLY BATCH;");

		_defaultSession.execute(batchJob.toString());
	}


	@Override
	public boolean wasTrueSince(LiteralOperator operator, long since) {
		_logger.debug("wasTrueSince({}, {})", operator, since);
		ResultSet rs = _defaultSession.execute("SELECT opid FROM " + operator.getMechanism().getPolicyName() + "." + TABLE_NAME_OP_OBSERVED
						+ " WHERE opid = '" + operator.getFullId() + "'"
						+ " AND time > maxTimeuuid('" + sdf.format(new Date(since)) + "')"
						+ " LIMIT 1;");
		if (operator.isPositive()) {
			return !rs.isExhausted();
		}
		else {
			return rs.isExhausted();
		}
	}


	@Override
	public boolean wasTrueInBetween(LiteralOperator operator, long from, long to) {
		_logger.debug("wasTrueInBetween({}, {}, {})", operator, from, to);
		ResultSet rs = _defaultSession.execute("SELECT opid FROM " + operator.getMechanism().getPolicyName() + "." + TABLE_NAME_OP_OBSERVED
				+ " WHERE opid = '" + operator.getFullId() + "'"
				+ " AND time > maxTimeuuid('" + sdf.format(new Date(from)) + "')"
				+ " AND time < minTimeuuid('" + sdf.format(new Date(to)) + "')"
				+ " LIMIT 1;");
		if (operator.isPositive()) {
			return !rs.isExhausted();
		}
		else {
			return rs.isExhausted();
		}
	}
}
