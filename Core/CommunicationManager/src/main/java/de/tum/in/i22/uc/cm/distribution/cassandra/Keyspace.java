package de.tum.in.i22.uc.cm.distribution.cassandra;

import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.exceptions.AlreadyExistsException;
import com.datastax.driver.core.exceptions.QueryTimeoutException;

import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.Threading;
import de.tum.in.i22.uc.cm.settings.Settings;


/**
 * Representation of a physical Cassandra keyspace.
 *
 * @author Florian Kelbert
 *
 */
abstract class Keyspace {
	protected static final Logger _logger = LoggerFactory.getLogger(Keyspace.class);

	/**
	 * The session that is used to query the tables
	 * within the keyspace.
	 */
	private final Session _session;

	/**
	 * The name of this keyspace
	 */
	protected final String _name;

	protected static final ConsistencyLevel writeConsistency = Settings.getInstance().getDistributionWriteConsistency();
	protected static final ConsistencyLevel readConsistency = Settings.getInstance().getDistributionReadConsistency();
	protected static final ConsistencyLevel defaultConsistency = Settings.getInstance().getDistributionDefaultConsistency();

	/**
	 * Creates a Keyspace instance that represents the physical
	 * keyspace with the specified name within the specified cluster.
	 * If a physical keyspace with the specified name does not yet
	 * exist, it will be created. In this case, also all associated tables
	 * i.e. the table descriptions returned by {@link Keyspace#getTables()},
	 * will be created. Therefore, {@link Keyspace#getTables()} must be
	 * implemented by any instantiable subclass. Furthermore, all
	 * Prepared Statements returned by the subclasses
	 * {@link Keyspace#getPrepareStatements()} will be prepared.
	 *
	 * @param name the name of this keyspace
	 * @param cluster the cluster in which the keyspace is in
	 */
	Keyspace(String name, Cluster cluster) {
		_name = toValidKeyspaceName(name);

		boolean created = createKeyspace(cluster);
		_session = cluster.connect(_name);

		createTables();
		prepareStatements();
	}

	/**
	 * Returns all tables to be created within this keyspace.
	 * @return all tables to be created within this keyspace.
	 */
	abstract List<String> getTables();

	/**
	 * Returns all prepared statements to be prepared within this keyspace.
	 * @return all prepared statements to be prepared within this keyspace.
	 */
	abstract IPreparedStatementId[] getPrepareStatements();

	/**
	 * Try to physically create the keyspace which is represented by
	 * this object. If the keyspace did already exist, false is returned.
	 * If the keyspace was actually created by this call, true is returned.
	 *
	 * @param cluster the cluster in which the keyspace is to be created.
	 * @return true, if the keyspace was physically created by this call. False otherwise.
	 */
	private boolean createKeyspace(Cluster cluster) {
		boolean created = true;

		try {
			/*
			 * Do _not_ add an
			 * IF NOT EXISTS
			 * clause to the CREATE query, because
			 * we won't know whether we actually
			 * created the keyspace with this call.
			 */
			cluster.connect().execute("CREATE KEYSPACE " + _name
					+ " WITH replication = {'class':'NetworkTopologyStrategy','"
					+ IPLocation.localIpLocation.getHost() + "':1}");
		}
		catch (AlreadyExistsException e) {
			created = false;
		}

		return created;
	}

	/**
	 * Creates the tables within this keyspace.
	 * All tables returned by {@link Keyspace#getTables()}
	 * will be created.
	 */
	private void createTables() {
		List<String> tables = getTables();

		CompletionService<?> cs = new ExecutorCompletionService<>(Threading.instance());
		tables.forEach(t -> cs.submit(() -> _session.execute(t), null));
		Threading.waitFor(tables.size(), cs);
	}

	static String toValidKeyspaceName(String s) {
		return s.toLowerCase().replaceAll("-", "_");
	}

	/**
	 * Prepares all prepared statements within this keyspace.
	 * All prepared statements returned by
	 * {@link Keyspace#getPrepareStatements()} will be prepared.
	 */
	private void prepareStatements() {
		for (IPreparedStatementId stmt : getPrepareStatements()) {
			stmt.prepare(_session);
		}
	}

	interface IPreparedStatementId {
		void prepare(Session session);
		PreparedStatement get();
	}


	protected ResultSet execute(Statement stmt) {
		boolean success = false;

		while (!success) {
			try {
				success = true;
				return _session.execute(stmt);
			}
			catch (QueryTimeoutException e) {
				success = false;
				_logger.info("QueryTimeoutException: {}. Query: {}. Retrying in {}.", e.getMessage(), stmt.toString(), Settings.getInstance().getDistributionRetryInterval());
			}

			if (!success) {
				try {
					Thread.sleep(Settings.getInstance().getDistributionRetryInterval());
				} catch (InterruptedException e1) {}
			}
		}

		return null;
	}
}

