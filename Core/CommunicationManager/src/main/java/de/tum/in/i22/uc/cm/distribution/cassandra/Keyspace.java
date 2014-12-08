package de.tum.in.i22.uc.cm.distribution.cassandra;

import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.AlreadyExistsException;

import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.Threading;
import de.tum.in.i22.uc.cm.settings.Settings;

abstract class Keyspace {
	protected final Session _session;
	protected final String _name;

	protected static final ConsistencyLevel writeConsistency = Settings.getInstance().getDistributionWriteConsistency();
	protected static final ConsistencyLevel readConsistency = Settings.getInstance().getDistributionReadConsistency();
	protected static final ConsistencyLevel defaultConsistency = Settings.getInstance().getDistributionDefaultConsistency();

	public Keyspace(String name, Cluster cluster) {
		_name = name;

		boolean created = createKeyspace(cluster);
		_session = cluster.connect(_name);

		if (created) {
			createTables();
		}

		prepareStatements();
	}

	abstract List<String> getTables();
	abstract IPreparedStatementId[] getPrepareStatements();

	private boolean createKeyspace(Cluster cluster) {
		try {
			cluster.connect().execute("CREATE KEYSPACE " + _name
					+ " WITH replication = {'class':'NetworkTopologyStrategy','"
					+ IPLocation.localIpLocation.getHost() + "':1}");
		}
		catch (AlreadyExistsException e) {
			return false;
		}

		return true;
	}

	private void createTables() {
		List<String> tables = getTables();

		CompletionService<?> cs = new ExecutorCompletionService<>(Threading.instance());
		tables.forEach(t -> cs.submit(() -> _session.execute(t), null));
		Threading.waitFor(tables.size(), cs);
	}

	private void prepareStatements() {
		for (IPreparedStatementId stmt : getPrepareStatements()) {
			stmt.prepare(_session);
		}
	}
}

