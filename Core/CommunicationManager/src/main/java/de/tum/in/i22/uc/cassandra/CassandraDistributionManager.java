package de.tum.in.i22.uc.cassandra;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.InvalidQueryException;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.distribution.IPLocation;


public class CassandraDistributionManager implements IDistributionManager {
	protected static final Logger _logger = LoggerFactory.getLogger(CassandraDistributionManager.class);

	private final Cluster _cluster;
	private Session _currentSession;
	private String _currentKeyspace;

	public CassandraDistributionManager() {
		_cluster = Cluster.builder().addContactPoint("localhost").build();
		_currentSession = _cluster.connect();
	}

	@Override
	public void playWithMe() {
		//		policyTransfer(new IPLocation("127.0.0.2"), new IPLocation("127.0.0.3"), "policy1");
		policyTransfer(new IPLocation("127.0.0.3"), new IPLocation("127.0.0.5"), "policy1");
	}

	public void policyTransfer(IPLocation srcLocation, IPLocation dstLocation, String policyName) {
		if (_currentKeyspace == null) {
			try {
				_currentSession = _cluster.connect(policyName);
				_currentKeyspace = _currentSession.getLoggedKeyspace();
			}
			catch (InvalidQueryException e) {
				// This exception is thrown if the provided keyspace does not exist.
				// Do nothing.
			}
		}

		if (_currentKeyspace == null) {
			// If we end up here, then there was no keyspace for our policy.
			// We create it and are done.
			createPolicyKeyspace(policyName, srcLocation.getHost(), dstLocation.getHost());
			_currentSession = _cluster.connect(policyName);
			_currentKeyspace = _currentSession.getLoggedKeyspace();
		}
		else {
			// The keyspace did already exist. Therefore, we update it
			// to also replicate the policy to the new destination


			// (1) We retrieve the current information about the keyspace
			ResultSet rows = _currentSession.execute(
					"SELECT strategy_options "
							+ "FROM system.schema_keyspaces "
							+ "WHERE keyspace_name = '" + policyName + "'");


			// (2) We build the set of locations that are already known within the keyspace
			Set<String> locations = new HashSet<>();
			for (Row row : rows) {
				String[] entries = row.getString("strategy_options").replaceAll("[{}\"]", "").split(",");
				for (String loc : entries) {
					locations.add(loc.split(":")[0]);
				}
			}

			// (3) We add the new destination location to the keyspace
			locations.add(dstLocation.getHost());

			// (4) We make a string out of all locations and create the query
			StringBuilder query = new StringBuilder();
			query.append("ALTER KEYSPACE " + policyName + " WITH replication ");
			query.append("= {'class':'NetworkTopologyStrategy',");
			for (String loc : locations) {
				query.append("'" + loc + "':1,");
			}
			query.deleteCharAt(query.length() - 1);
			query.append("}");

			// (5) We execute the query
			_currentSession.execute(query.toString());
		}
	}


	public void dataTransfer(IPLocation srcLocation, IPLocation dstLocation, Set<IData> data) {

	}


	private void createPolicyKeyspace(String policyName, String ... locations) {
		if (locations == null || locations.length == 0) {
			_logger.info("Unable to create keyspace. No locations provided.");
			return;
		}

		StringBuilder query = new StringBuilder();
		query.append("CREATE KEYSPACE " + policyName + " WITH replication ");
		query.append("= {'class':'NetworkTopologyStrategy',");
		for (String loc : locations) {
			query.append("'" + loc + "':1,");
		}
		query.deleteCharAt(query.length() - 1);
		query.append("}");

		_currentSession.execute(query.toString());

	}

}
