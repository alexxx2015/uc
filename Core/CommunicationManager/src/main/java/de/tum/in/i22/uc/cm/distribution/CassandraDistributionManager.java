package de.tum.in.i22.uc.cm.distribution;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
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
import com.datastax.driver.core.exceptions.InvalidQueryException;

import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.distribution.IDistributionManager;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.LocalLocation;
import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.distribution.client.ConnectionManager;
import de.tum.in.i22.uc.cm.distribution.client.Pip2PipClient;
import de.tum.in.i22.uc.cm.distribution.client.Pmp2PmpClient;
import de.tum.in.i22.uc.cm.pip.RemoteDataFlowInfo;
import de.tum.in.i22.uc.cm.processing.PdpProcessor;
import de.tum.in.i22.uc.cm.processing.PipProcessor;
import de.tum.in.i22.uc.cm.processing.PmpProcessor;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.thrift.client.ThriftClientFactory;

class CassandraDistributionManager implements IDistributionManager {
	protected static final Logger _logger = LoggerFactory.getLogger(CassandraDistributionManager.class);

	private static final String TABLE_NAME_DATA = "hasdata";
	private static final String TABLE_NAME_EVENTS = "issueevents";
	private static final String TABLE_NAME_STATES = "operatorstates";

	private static final String QUERY_CREATE_TABLE_DATA = "CREATE TABLE " + TABLE_NAME_DATA + " ("
			+ "data text,"
			+ "locations set<text>,"
			+ "PRIMARY KEY (data)"
			+ ");";

	private static final String QUERY_CREATE_TABLE_EVENTS = "CREATE TABLE " + TABLE_NAME_EVENTS + " ("
			+ "event text,"
			+ "locations set<text>,"
			+ "PRIMARY KEY (event)"
			+ ");";

	private static final String QUERY_CREATE_TABLE_STATES = "CREATE TABLE " + TABLE_NAME_STATES + " ("
			+ "id text,"
			+ "value boolean,"
			+ "immutable boolean,"
			+ "counter counter,"
			+ "subevertrue boolean,"
			+ "circArry list<text>,"
			+ "PRIMARY KEY (id)"
			+ ");";

	/*
	 * IMPORTANT:
	 * Cassandra 2.1.0-beta causes some trouble with upper/lowercase
	 * (Inserting a keyspace named 'testPolicy' resulted in keyspace named 'testpolicy',
	 *  which was afterwards not found by a lookup with name 'testPolicy')
	 * Therefore, make all tables, namespaces, column names, etc. lowercase.
	 *
	 */

	private final ConnectionManager<Pmp2PmpClient> _pmpConnectionManager;
	private final ConnectionManager<Pip2PipClient> _pipConnectionManager;

	private final Cluster _cluster;
	private Session _currentSession;
	private String _currentKeyspace;

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
		_currentSession = _cluster.connect();
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

	private void switchKeyspace(String keyspace) {
		if (keyspace != null && keyspace.equals(_currentKeyspace)) {
			return;
		}

		try {
			_currentSession = _cluster.connect(keyspace);
			_currentKeyspace = _currentSession.getLoggedKeyspace();
		} catch (InvalidQueryException e) {
			// this happens if the keyspace did not exist
			_currentKeyspace = null;
		}

		if (_currentKeyspace == null) {
			createPolicyKeyspace(keyspace, IPLocation.localIpLocation);
		}
	}

	@Override
	public void newPolicy(XmlPolicy policy) {
		switchKeyspace(policy.getName());
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
			 * (1) switch to the policy's keyspace
			 * (2) extend the keyspace with the given location
			 * (3) if the keyspace was in fact extended
			 *     (meaning that the provided location was not yet aware of the policy),
			 *     then the policy is sent to the remote PMP.
			 * (4) if remote deployment of the policy fails, then the given location
			 *     is removed from the policy's keyspace
			 */

			/**
			 * FIXME: There might be potential to parallelize policy transfer
			 */

			switchKeyspace(policy.getName());

			if (adjustPolicyKeyspace(policy.getName(), pmpLocation, true)) {
				boolean success = true;

				// if the location was not yet part of the keyspace, then we need to
				// deploy the policy at the remote location
				try {
					Pmp2PmpClient remotePmp = _pmpConnectionManager.obtain(new ThriftClientFactory().createPmp2PmpClient(pmpLocation));

					if (remotePmp.deployPolicyRawXMLPmp(policy.getXml()).getEStatus() != EStatus.OKAY) {
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
				switchKeyspace(p.getName());

				// Check whether there already exists an entry for this dataID ...
				if (_currentSession.execute("SELECT data from " + TABLE_NAME_DATA + " WHERE data = '" + dataID + "';").isExhausted()) {
					// ... if not, insert the corresponding row
					_currentSession.execute("INSERT INTO " + TABLE_NAME_DATA
							+ " (data, locations) VALUES ('" + dataID + "',{'"
							+ IPLocation.localIpLocation.getHost() + "','"
							+ dstLocation.getHost() + "'})");
				}
				else {
					// ... otherwise add the additional location to the existing row
					_currentSession.execute("UPDATE " + TABLE_NAME_DATA + " SET locations = locations + {'"
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

	private void createPolicyKeyspace(String policyName, IPLocation location) {
		if (location == null) {
			_logger.info("Unable to create keyspace. No location provided.");
			return;
		}

		policyName = policyName.toLowerCase();

		// Return if the keyspace exists already
		if (!_currentSession.execute("SELECT strategy_options FROM system.schema_keyspaces WHERE keyspace_name = '" + policyName + "'").isExhausted()) {
			return;
		}

		try {
			_currentSession.execute("CREATE KEYSPACE " + policyName
					+ " WITH replication = {'class':'NetworkTopologyStrategy','" + location.getHost() + "':1}");
			switchKeyspace(policyName);
			_currentSession.execute(QUERY_CREATE_TABLE_DATA);
			_currentSession.execute(QUERY_CREATE_TABLE_EVENTS);
			_currentSession.execute(QUERY_CREATE_TABLE_STATES);
		}
		catch (AlreadyExistsException e) {
			// don't worry.. about a thing.
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
		ResultSet rows = _currentSession.execute("SELECT strategy_options FROM system.schema_keyspaces "
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
		_currentSession.execute(query.toString());

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
}
