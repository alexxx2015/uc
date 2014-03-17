package de.tum.in.i22.uc.cm.out;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Manages and synchronizes connections.
 *
 * This manager keeps a fixed amount of connections open, closing connections
 * that have been least-recently-used. Using this manager, {@link Connection}s
 * can safely be shared across threads.
 *
 * For this reason, this managers methods {@link ConnectionManager#obtain(IConnection)}
 * and {@link ConnectionManager#release(IConnection)} must be used.
 *
 *
 * @author Florian Kelbert
 *
 */
public class ConnectionManager {
	private static final int DEFAULT_MAX_ENTRIES = 20;

	private static final PoolMap connectionPool = new PoolMap(DEFAULT_MAX_ENTRIES);

	/**
	 * Connections that have been removed from the connection pool but that still need to be closed.
	 * Connections are added to this map if
	 *   (1) they must be deleted from the connection pool because it reaches its maximum size, and
	 *   (2) they have not yet been released.
	 * A map is used intentionally. Reason: From a set we cannot easily retrieve a value.
	 */
	private static final Map<PoolMapEntry, PoolMapEntry> toClose = new HashMap<>();

	/**
	 * Connects and reserves the specified connection for the caller.
	 *
	 * Note, that the returned connection might be different from the one that has been
	 * passed as an argument. In any case, the caller must use the _returned_ connection.
	 * The returned connection is connected and ready to be used by the caller.
	 *
	 * If the connection was not known to the {@link ConnectionManager} before,
	 * it gets (1) added to its internal pool of connections,
	 * (2) physically connected, (3) reserved for the caller.
	 *
	 * If the connection was already known, then:
	 * (a) if currently in use: this method blocks until the connection gets available,
	 * then reserves it for the caller.
	 * (b) if not currently in use: reserves the connection for the caller.
	 *
	 * Once no longer needed, the caller must "free" the reserved connection
	 * by calling method {@link ConnectionManager#release(IConnection)}.
	 *
	 * @param iconnection
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static <C extends IConnection> C obtain(C iconnection) throws IOException {
		if (iconnection == null) {
			throw new NullPointerException("No connection provided.");
		}

		if (!(iconnection instanceof Connection)) {
			throw new RuntimeException(iconnection + " is not a " + Connection.class);
		}

		Connection connection = (Connection) iconnection;

		PoolMapEntry entry = null;

		synchronized (connectionPool) {
			while (entry == null || entry.inuse != false) {
				entry = connectionPool.get(connection);

				if (entry == null) {
					// connection is not known
					connection.connect();
					entry = new PoolMapEntry(connection, false);
				}
				else if (entry.inuse == true) {
					try {
						connectionPool.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			entry.inuse = true;
			connectionPool.put(connection, entry);
		}

		return (C) entry.connection;
	}

	/**
	 * Returns the specified connection to the connection pool and
	 * makes it available to other threads.
	 *
	 * After returning the connection, the caller must no longer
	 * use the connection, as this would lead to unspecified results.
	 *
	 * @param iconnection
	 * @throws IOException
	 */
	public static void release(IConnection iconnection) throws IOException {
		if (iconnection == null) {
			throw new NullPointerException("No connection provided.");
		}

		if (!(iconnection instanceof Connection)) {
			throw new RuntimeException(iconnection + " is not a " + Connection.class);
		}

		Connection connection = (Connection) iconnection;

		synchronized (connectionPool) {
			if (connectionPool.get(connection) == null) {
				// It may be the case that the connection has been removed from the pool
				// while it was in use. In this case we get the connection from the 'toClose' map
				// and close it.
				PoolMapEntry pme;
				if ((pme = toClose.remove(connection)) != null) {
					pme.connection.disconnect();
				}
			}
			else {
				connectionPool.get(connection).inuse = false;
				connectionPool.notifyAll();
			}
		}
	}


	private static class PoolMap implements Map<Connection, PoolMapEntry> {
		private final int _maxEntries;

		private final Map<Connection, PoolMapEntry> _backMap;

		public PoolMap(int maxEntries) {
			_maxEntries = maxEntries;
			_backMap = Collections.synchronizedMap(new LinkedHashMap<Connection, PoolMapEntry>(maxEntries, 0.75f, true) {
				private static final long serialVersionUID = -3139938026277477159L;

				@Override
				protected boolean removeEldestEntry(Map.Entry<Connection,PoolMapEntry> eldest) {
					boolean result = size() > _maxEntries;

					if (result) {
						synchronized (connectionPool) {
							// When removing an entry, we need to disconnect the connection.
							// Yet, we only do this if the connection is currently not used.
							// If the connection is currently used, it will be disconnect as
							// soon as it is released, cf. release().
							PoolMapEntry pme = eldest.getValue();
							if (pme.inuse == false) {
								pme.connection.disconnect();
							}
							else {
								toClose.put(pme, pme);
							}
						}
					}
					return result;
				}
			});
		}


		@Override
		public int size() {
			return _backMap.size();
		}

		@Override
		public boolean isEmpty() {
			return _backMap.isEmpty();
		}

		@Override
		public boolean containsKey(Object key) {
			return _backMap.containsKey(key);
		}

		@Override
		public boolean containsValue(Object value) {
			return _backMap.containsValue(value);
		}

		@Override
		public PoolMapEntry get(Object key) {
			return _backMap.get(key);
		}

		@Override
		public PoolMapEntry put(Connection key, PoolMapEntry value) {
			return _backMap.put(key, value);
		}

		@Override
		public PoolMapEntry remove(Object key) {
			return _backMap.remove(key);
		}

		@Override
		public void putAll(Map<? extends Connection, ? extends PoolMapEntry> m) {
			_backMap.putAll(m);
		}

		@Override
		public void clear() {
			_backMap.clear();
		}

		@Override
		public Set<Connection> keySet() {
			return _backMap.keySet();
		}

		@Override
		public Collection<PoolMapEntry> values() {
			return _backMap.values();
		}

		@Override
		public Set<java.util.Map.Entry<Connection, PoolMapEntry>> entrySet() {
			return _backMap.entrySet();
		}
	}


	/**
	 * Groups a connection and its current state, i.e. whether it is currently in use.
	 */
	private static class PoolMapEntry {
		Connection connection;
		boolean inuse;

		public PoolMapEntry(Connection connection, boolean inuse) {
			this.connection = connection;
			this.inuse = inuse;
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(connection);
		}
	}
}
