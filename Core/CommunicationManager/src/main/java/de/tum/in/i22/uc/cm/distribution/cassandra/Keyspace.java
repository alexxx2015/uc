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

public abstract class Keyspace {
	protected final Session _session;
	protected final Cluster _cluster;
	protected final String _name;

	protected static final ConsistencyLevel writeConsistency = Settings.getInstance().getDistributionWriteConsistency();
	protected static final ConsistencyLevel readConsistency = Settings.getInstance().getDistributionReadConsistency();

	public Keyspace(String name, Cluster cluster) {
		_name = name;
		_cluster = cluster;

		boolean created = createKeyspace();
		_session = cluster.connect(_name);

		if (created) {
			createTables();
		}

		prepareStatements();
	}

	abstract List<String> getTables();
	abstract void prepareStatements();

	private boolean createKeyspace() {
		try {
			_cluster.connect().execute("CREATE KEYSPACE " + _name
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
}
