package de.tum.in.i22.uc.cassandra;

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

import de.tum.in.i22.uc.cm.datatypes.basic.DataBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.distribution.IDistributionManager;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.LocalLocation;
import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.pip.RemoteDataFlowInfo;
import de.tum.in.i22.uc.cm.processing.PdpProcessor;
import de.tum.in.i22.uc.cm.processing.PipProcessor;
import de.tum.in.i22.uc.cm.processing.PmpProcessor;

public class CassandraDistributionManager implements IDistributionManager {
	protected static final Logger _logger = LoggerFactory.getLogger(CassandraDistributionManager.class);

	/*
	 * IMPORTANT:
	 * Cassandra 2.1.0-beta causes some trouble with upper/lowercase
	 * (Inserting a keyspace named 'testPolicy' resulted in keyspace named 'testpolicy',
	 *  which was afterwards not found by a lookup with name 'testPolicy')
	 * Therefore, make all tables, namespaces, column names, etc. lowercase.
	 * 
	 */

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
		//		play();
	}

	private void play() {
		// policyTransfer(new IPLocation("127.0.0.2"), new
		// IPLocation("127.0.0.3"), "policy1");
		// policyTransfer(new IPLocation("127.0.0.3"), new
		// IPLocation("127.0.0.5"), new XmlPolicy("policy1", "<xml>"));
		_pmp.deployPolicyURIPmp("/home/florian/GIT/pdp/Tests/src/test/resources/testTUM.xml");
		dataTransfer(Collections.singleton((IData) new DataBasic()), new IPLocation("127.0.0.5"));
	}

	private Session changeKeyspace(String keyspace) {
		if (keyspace != null && keyspace.equals(_currentKeyspace)) {
			return _currentSession;
		}

		try {
			_currentSession = _cluster.connect(keyspace);
			_currentKeyspace = _currentSession.getLoggedKeyspace();
		} catch (InvalidQueryException e) {
			// This exception is thrown if the provided keyspace does not exist.
			// Do nothing.
		}

		return _currentSession;
	}

	@Override
	public void newPolicy(XmlPolicy xmlPolicy) {
		createPolicyKeyspace(xmlPolicy.getName(), IPLocation.localIpLocation);

		changeKeyspace(xmlPolicy.getName());

		try {
			_currentSession.execute("CREATE TABLE hasdata (data text,location text,PRIMARY KEY (data))");
		} catch (AlreadyExistsException e) {
			// If the table already exists: just be happy.
		}
	}

	private void policyTransfer(String policyName, IPLocation dstLocation) {
		changeKeyspace(policyName);

		if (_currentKeyspace == null) {
			// If we end up here, then there was no keyspace for our policy.
			// We create it and are done.
			createPolicyKeyspace(policyName, dstLocation);
		} else {
			// The keyspace did already exist. Therefore, we update it
			// to also replicate the policy to the new destination
			extendPolicyKeyspace(policyName, dstLocation);
		}
	}

	private void policyTransfer(Set<XmlPolicy> policies, IPLocation dstLocation) {
		for (XmlPolicy p : policies) {
			policyTransfer(p.getName(), dstLocation);
		}
	}

	private void dataTransfer(Set<IData> data, IPLocation dstLocation) {
		for (IData d : data) {
			for (XmlPolicy p : _pmp.getPolicies(d)) {
				changeKeyspace(p.getName());
				_currentSession.execute("INSERT INTO hasdata (data, location) " + "VALUES ('" + d.getId() + "','"
						+ dstLocation.getHost() + "')");
			}
		}

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

		StringBuilder query = new StringBuilder();
		query.append("CREATE KEYSPACE " + policyName + " WITH replication ");
		query.append("= {'class':'NetworkTopologyStrategy',");
		for (IPLocation loc : locations) {
			query.append("'" + loc.getHost() + "':1,");
		}
		query.deleteCharAt(query.length() - 1);
		query.append("}");

		try {
			_currentSession.execute(query.toString());
		}
		catch (AlreadyExistsException e) {
			// don't worry.. about a thing.
		}

	}

	private void extendPolicyKeyspace(String policyName, IPLocation... locations) {
		policyName = policyName.toLowerCase();

		// (1) We retrieve the current information about the keyspace
		ResultSet rows = _currentSession.execute("SELECT strategy_options " + "FROM system.schema_keyspaces "
				+ "WHERE keyspace_name = '" + policyName + "'");

		// (2) We build the set of locations that are already known within the
		// keyspace
		Set<String> oldLocations = new HashSet<>();
		for (Row row : rows) {
			String[] entries = row.getString("strategy_options").replaceAll("[{}\"]", "").split(",");
			for (String loc : entries) {
				oldLocations.add(loc.split(":")[0]);
			}
		}

		// (3) We add the new destination locations to the keyspace
		for (IPLocation loc : locations) {
			oldLocations.add(loc.getHost());
		}

		// (4) We make a string out of all locations and create the query
		StringBuilder query = new StringBuilder();
		query.append("ALTER KEYSPACE " + policyName + " WITH replication ");
		query.append("= {'class':'NetworkTopologyStrategy',");
		for (String loc : oldLocations) {
			query.append("'" + loc + "':1,");
		}
		query.deleteCharAt(query.length() - 1);
		query.append("}");

		// (5) We execute the query
		_currentSession.execute(query.toString());
	}

	@Override
	public void dataTransfer(RemoteDataFlowInfo dataflow) {
		Map<Location, Map<IName, Set<IData>>> df = dataflow.getFlows();

		Location srcLocation = dataflow.getSrcLocation();

		if (srcLocation instanceof LocalLocation) {
			srcLocation = IPLocation.localIpLocation;
		}
		else if (!(srcLocation instanceof IPLocation)) {
			_logger.warn("Unsupported kind of Location. Not performing remote data flow.");
			return;
		}

		for (Location dstLocation : df.keySet()) {
			if (dstLocation instanceof IPLocation) {
				Set<IData> data = new HashSet<>();
				for (Set<IData> d : df.get(dstLocation).values()) {
					data.addAll(d);
				}

				policyTransfer(getAllPolicies(data), (IPLocation) dstLocation);
				dataTransfer(data, (IPLocation) dstLocation);
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
