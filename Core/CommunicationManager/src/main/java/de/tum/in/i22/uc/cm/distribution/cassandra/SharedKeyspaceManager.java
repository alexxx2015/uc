package de.tum.in.i22.uc.cm.distribution.cassandra;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.AtomicOperator;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IOperator;
import de.tum.in.i22.uc.cm.distribution.IPLocation;

public class SharedKeyspaceManager {
	protected static final Logger _logger = LoggerFactory.getLogger(SharedKeyspaceManager.class);

	private final Map<String,ISharedKeyspace> _sharedKeyspaces;

	private final Cluster _cluster;

	SharedKeyspaceManager(Cluster cluster) {
		_sharedKeyspaces = new HashMap<>();
		_cluster = cluster;
	}

	/**
	 * This will create a new {@link ISharedKeyspace} in
	 * correspondence with the specified policy.
	 *
	 * @param policy
	 * @return
	 */
	ISharedKeyspace create(XmlPolicy policy) {
		ISharedKeyspace keyspace = get(policy.getName());

		if (keyspace == DummyKeyspace.instance) {
			keyspace = SharedKeyspace.create(policy, _cluster);
			_sharedKeyspaces.put(policy.getName(), keyspace);
		}

		return keyspace;
	}

	ISharedKeyspace get(String policyName) {
		ISharedKeyspace k = _sharedKeyspaces.get(policyName);
		return k != null ? k : DummyKeyspace.instance;
	}

	ISharedKeyspace get(IOperator op) {
		return get(op.getMechanism().getPolicyName());
	}

	static final class DummyKeyspace implements ISharedKeyspace {

		static final DummyKeyspace instance = new DummyKeyspace();

		private DummyKeyspace() {
		}

		@Override
		public int howOftenNotifiedSinceTimestep(AtomicOperator operator, long timestep) {
			_logger.error("DummyKeyspace::howOftenNotifiedSinceTimestep");
			return 0;
		}

		@Override
		public int howOftenNotifiedInBetween(AtomicOperator operator, long from, long to) {
			_logger.error("DummyKeyspace::howOftenNotifiedInBetween");
			return 0;
		}

		@Override
		public int howOftenNotifiedAtTimestep(AtomicOperator operator, long timestep) {
			_logger.error("DummyKeyspace::howOftenNotifiedAtTimestep");
			return 0;
		}

		@Override
		public boolean wasNotifiedInBetween(AtomicOperator operator, long from, long to) {
			_logger.error("DummyKeyspace::wasNotifiedInBetween");
			return false;
		}

		@Override
		public boolean wasNotifiedAtTimestep(AtomicOperator operator, long timestep) {
			_logger.error("DummyKeyspace::wasNotifiedAtTimestep");
			return false;
		}

		@Override
		public long getFirstTick(String mechanismName) {
			_logger.error("DummyKeyspace::getFirstTick");
			return 0;
		}

		@Override
		public void notify(IOperator op, boolean endOfTimestep) {
			_logger.error("DummyKeyspace::update");
		}

		@Override
		public void addData(IData d, IPLocation dstLocation) {
			_logger.error("DummyKeyspace::addData");
		}

		@Override
		public boolean adjust(String name, IPLocation pmpLocation, boolean b) {
			_logger.error("DummyKeyspace::adjust");
			return false;
		}

		@Override
		public void setFirstTick(String mechanismName, long firstTick) {
			_logger.error("DummyKeyspace::setFirstTick");
		}
	}
}
