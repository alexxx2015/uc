package de.tum.in.i22.uc.cm.distribution;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.AlreadyExistsException;
import com.datastax.driver.core.exceptions.InvalidQueryException;

import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
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
		_cluster = Cluster.builder().addContactPoint("localhost").build();
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

	private Session switchKeyspace(String keyspace) {
		if (keyspace != null && keyspace.equals(_currentKeyspace)) {
			return _currentSession;
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
			return switchKeyspace(keyspace);
		}

		return _currentSession;
	}

	@Override
	public void newPolicy(XmlPolicy policy) {
		switchKeyspace(policy.getName());

		//TODO initialization of distributed policy information
	}

	private void doStickyPolicyTransfer(Set<XmlPolicy> policies, IPLocation dstLocation) {
		_logger.debug("doStickyPolicyTransfer invoked: " + dstLocation + ": " + policies);

		for (XmlPolicy policy : policies) {
			/*
			 * For each policy, the protocol is as follows:
			 * (1) switch to the policy's keyspace
			 * (2) extend the keyspace with the given location
			 * (3) if the keyspace was in fact extended
			 *     (meaning that the provided location was not yet aware of the policy),
			 *     then the policy is sent to the remote PMP.
			 */

			switchKeyspace(policy.getName());

			if (extendPolicyKeyspace(policy.getName(), dstLocation)) {
				try {
					Pmp2PmpClient remotePmp = _pmpConnectionManager.obtain(new ThriftClientFactory().createPmp2PmpClient(dstLocation));
					remotePmp.deployPolicyRawXMLPmp(policy.getXml());

					/*
					 * TODO: If this goes wrong, then remove dstLocation from the keyspace
					 */

					_pmpConnectionManager.release(remotePmp);
				} catch (IOException e) {
					_logger.error("Unable to deploy XML policy remotely at [" + dstLocation + "]: " + e.getMessage());
				}
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
			for (XmlPolicy p : _pmp.getPolicies(d)) {
				switchKeyspace(p.getName());

				_currentSession.execute("INSERT INTO hasdata (data, location) " + "VALUES ('" + d.getId() + "','"
						+ dstLocation.getHost() + "')");
			}
		}
	}


	/**
	 * Perform cross-system data flow tracking on a per-container basis.
	 * The mapping between containers (more precisely: their names) and the
	 * set of data being transferred to them is specified by parameter flows.
	 *
	 * @param dstLocation the location to which the data flow occurred.
	 * @param flows maps the destination container name to set of data it is receiving
	 */
	private void doCrossSystemDataTrackingFine(IPLocation dstLocation, Map<IName, Set<IData>> flows) {
		Pip2PipClient remotePip = null;

		try {
			remotePip = _pipConnectionManager.obtain(new ThriftClientFactory().createPip2PipClient(dstLocation));
		} catch (IOException e) {
			_logger.error("Unable to perform remote data transfer with [" + dstLocation + "]");
			return;
		}

		for (IName name : flows.keySet()) {
			remotePip.initialRepresentation(name, flows.get(name));
		}

		_pipConnectionManager.release(remotePip);
	}

	private void createPolicyKeyspace(String policyName, IPLocation... locations) {
		if (locations == null || locations.length == 0) {
			_logger.info("Unable to create keyspace. No locations provided.");
			return;
		}

		policyName = policyName.toLowerCase();

		// check whether the keyspace exists already
		ResultSet rows = _currentSession.execute("SELECT strategy_options " + "FROM system.schema_keyspaces "
				+ "WHERE keyspace_name = '" + policyName + "'");
		if (rows.iterator().hasNext()) {
			// if the keyspace does exist, then we return
			return;
		}

		StringBuilder queryCreateKeyspace = new StringBuilder();
		queryCreateKeyspace.append("CREATE KEYSPACE " + policyName + " WITH replication ");
		queryCreateKeyspace.append("= {'class':'NetworkTopologyStrategy',");
		for (IPLocation loc : locations) {
			queryCreateKeyspace.append("'" + loc.getHost() + "':1,");
		}
		queryCreateKeyspace.deleteCharAt(queryCreateKeyspace.length() - 1);
		queryCreateKeyspace.append("}");

		try {
			_currentSession.execute(queryCreateKeyspace.toString());
			switchKeyspace(policyName);
			_currentSession.execute("CREATE TABLE hasdata (data text,location text,PRIMARY KEY (data))");
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
	 * @param name the keyspace to which to switch to
	 * @param locations the locations to add to the keyspace
	 * @return returns true if the keyspace was in fact extended,
	 * i.e. if the provided location was not yet part of the keyspace; false otherwise.
	 */
	private boolean extendPolicyKeyspace(String name, IPLocation location) {
		name = name.toLowerCase();

		// (1) We retrieve the current information about the keyspace
		ResultSet rows = _currentSession.execute("SELECT strategy_options " + "FROM system.schema_keyspaces "
				+ "WHERE keyspace_name = '" + name + "'");

		// (2) We build the set of locations that are already known within the
		// keyspace
		Set<String> allLocations = new HashSet<>();
		for (Row row : rows) {
			String[] entries = row.getString("strategy_options").replaceAll("[{}\"]", "").split(",");
			for (String loc : entries) {
				allLocations.add(loc.split(":")[0]);
			}
		}

		if (!allLocations.add(location.getHost())) {
			// provided location was already present. We are done.
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

				doStickyPolicyTransfer(getAllPolicies(data), pmpLocation);
//				doCrossSystemDataTrackingCoarse(data, (IPLocation) dstLocation);
//				doCrossSystemDataTrackingFine((IPLocation) dstLocation, flows.get(dstLocation));
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
