package de.tum.in.i22.uc.distribution;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Florian Kelbert
 *
 */
public class ConnectionPool {
	private static final int DEFAULT_MAX_ENTRIES = 20;

	private static final ConnectionPool _instance = new ConnectionPool(DEFAULT_MAX_ENTRIES);

	private final PoolMap pool;

	private ConnectionPool() {
		this(DEFAULT_MAX_ENTRIES);
	}

	private ConnectionPool(int maxEntries) {
		pool = (PoolMap) Collections.synchronizedMap(new PoolMap(maxEntries));
	}

	public static ConnectionPool getInstance() {
		return _instance;
	}


	public Connection obtainConnection(Connection connection) throws IOException {
		if (connection == null) {
			throw new NullPointerException("No connection provided.");
		}

		synchronized (pool) {
			InUse inuse = null;

			while (inuse != InUse.NO) {
				inuse = pool.get(connection);

				if (inuse == null) {
					// connection is not known
					pool.put(connection, InUse.NO);
					connection.connect();
					inuse = InUse.NO;
				}
				else if (inuse == InUse.YES) {
					try {
						pool.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			pool.put(connection, InUse.YES);
		}

		return connection;
	}

	public void releaseConnection(Connection connection) throws IOException {
		synchronized (pool) {
			if (pool.get(connection) == null) {
				// It may be the case that the connection has been removed from the pool
				// while it was in use. In this case we do not add the new state but disconnect() silently.
				connection.disconnect();
			}
			else {
				pool.put(connection, InUse.NO);
				pool.notifyAll();
			}
		}
	}


	enum InUse {
		YES,
		NO;
	}


	private class PoolMap extends LinkedHashMap<Connection, InUse> {
		private static final long serialVersionUID = -6171984904678970780L;

		private final int _maxEntries;

		public PoolMap(int maxEntries) {
			super(maxEntries, 0.75f, true);
			_maxEntries = maxEntries;
		}

		@Override
		protected boolean removeEldestEntry(Map.Entry<Connection,InUse> eldest) {
			synchronized (pool) {
				// When removing an entry, we need to disconnect the connection.
				// Yet, we only do this if the connection is currently not used.
				// If the connection is currently used, it will be disconnect as
				// soon as it is released, cf. releaseConnection().
				if (eldest.getValue() == InUse.NO) {
					try {
						eldest.getKey().disconnect();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return size() > _maxEntries;
		}
	}
}
