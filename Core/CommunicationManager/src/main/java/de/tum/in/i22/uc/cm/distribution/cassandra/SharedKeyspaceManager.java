package de.tum.in.i22.uc.cm.distribution.cassandra;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IOperator;

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
		ISharedKeyspace keyspace;

		/*
		 * Check whether the keyspace already exists
		 */
		synchronized (_sharedKeyspaces) {
			keyspace = _sharedKeyspaces.get(policy.getName());
		}

		/*
		 * If the keyspace does not exist, create it.
		 */
		if (keyspace == null) {
			keyspace = new SharedKeyspace(policy, _cluster);
			synchronized (_sharedKeyspaces) {
				_sharedKeyspaces.put(policy.getName(), keyspace);
				_sharedKeyspaces.notifyAll();
			}
		}

		return keyspace;
	}


	ISharedKeyspace get(String policyName) {
		ISharedKeyspace k = null;

		/*
		 * Wait for the keyspace to get created.
		 */
		synchronized (_sharedKeyspaces) {
			while ((k = _sharedKeyspaces.get(policyName)) == null) {
				try {
					_sharedKeyspaces.wait();
				} catch (InterruptedException e) {}
			}
		}

		return k;
	}

	ISharedKeyspace get(IOperator op) {
		return get(op.getMechanism().getPolicyName());
	}
}
