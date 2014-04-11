package de.tum.in.i22.uc.cm.client;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages and synchronizes connections.
 *
 * Any manager instance keeps a fixed amount of connections open, closing connections
 * that have been least-recently-used. Using this manager, {@link IConnectable}s
 * can safely be shared across threads.
 *
 * {@link IConnectable}s used with this manager must properly implement
 * {@link IConnectable#hashCode()} and {@link IConnectable#equals(Object)}.
 *
 * For this reason, this manager's methods {@link ConnectionManager#obtain(IConnectable)}
 * and {@link ConnectionManager#release(IConnectable)} must be used (see their documentation).
 *
 *
 * @author Florian Kelbert
 *
 */
public class ConnectionManager<C extends IConnectable> {

	private final PoolMap connectionPool;;

	/**
	 * Connections that have been removed from the connection pool but that still need to be closed.
	 * Connections are added to this map if
	 *   (1) they must be deleted from the connection pool because it reaches its maximum size, and
	 *   (2) they have not yet been released.
	 * A map is used intentionally. Reason: From a set we cannot easily retrieve a value.
	 */
	private final Map<PoolMapEntry, PoolMapEntry> toClose;


	/**
	 * Creates a new {@link ConnectionManager} of the specified size.
	 * @param size the number of entries (i.e. connections) to keep alive
	 */
	public ConnectionManager(int size) {
		connectionPool = new PoolMap(size);
		toClose = new ConcurrentHashMap<>();
	}


	/**
	 * Connects and reserves the specified connection for the caller.
	 *
	 *
	 * {@link IConnectable}s used with this manager must properly implement
	 * {@link IConnectable#hashCode()} and {@link IConnectable#equals(Object)}.
	 *
	 * Note, that the returned {@link IConnectable} might be a different instance than the one that has been
	 * passed as an argument. The reason is, that another ("similar") instance might have been
	 * opened before, is still available, and thus returned.
	 * In any case, the caller must use the _returned_ connection.
	 * The returned {@link IConnectable} object is connected and ready to be used by the caller.
	 *
	 * If the connection was not known to the {@link ConnectionManager} before,
	 * it gets
	 *   (1) added to its internal pool of connections,
	 *   (2) physically connected,
	 *   (3) reserved for the caller.
	 *
	 * If the connection was already known, then:
	 *   (a) if currently in use: this method blocks until the connection gets available,
	 *     then reserves it for the caller.
	 *   (b) if not currently in use: reserves the connection for the caller.
	 *
	 * Once no longer needed, the caller must "free" the reserved connection
	 * by calling method {@link ConnectionManager#release(IConnectable)}.
	 *
	 * @param connectable
	 * @return
	 * @throws IOException
	 */
	public C obtain(C connectable) throws IOException {
		if (connectable == null) {
			throw new NullPointerException("No connection provided.");
		}

		PoolMapEntry entry = null;

		synchronized (connectionPool) {
			while (entry == null || entry.inuse != false) {
				entry = connectionPool.get(connectable);

				if (entry == null) {
					// connection is not known
					try {
						connectable.connect();
					} catch (Exception e) {
						throw new IOException("Unable to connect.", e);
					}
					entry = new PoolMapEntry(connectable, false);
					connectionPool.put(connectable, entry);
				}
				else if (entry.inuse == true) {
					try {
						connectionPool.wait();
					} catch (InterruptedException e) {
						throw new IOException("InterruptedException.", e);
					}
				}
			}

			entry.inuse = true;
		}

		return entry.connectable;
	}


	/**
	 * Returns the specified {@link IConnectable} to the connection pool and
	 * makes it available to other threads. The connection is *not*
	 * closed (at least not immediately; it might get closed after
	 * some time, though).
	 *
	 * After returning the {@link IConnectable}, the caller must no longer
	 * use the connection, as this would lead to unspecified results.
	 *
	 * Method {@link ConnectionManager#close(IConnectable)} might be
	 * used to force closing of a connection.
	 *
	 * @param connectable
	 * @throws IOException
	 */
	public void release(C connectable) {
		release(connectable, false);
	}


	/**
	 * Returns and closes the specified {@link IConnectable}, irrespective of whether
	 * it might still be in use by other threads.
	 *
	 * After returning the connection, the caller must no longer
	 * use the connection, as this would lead to unspecified results.
	 *
	 * Also consider calling {@link ConnectionManager#release(IConnectable)}
	 * instead, which will also return the connection to the connection
	 * manager but allow other threads to reuse the same connection.
	 *
	 * @param connectable
	 * @throws IOException
	 */
	public void close(C connectable) throws IOException {
		release(connectable, true);
	}

	public void closeAll() {
		synchronized (connectionPool) {
			for (PoolMapEntry pme : connectionPool.values()) {
				pme.connectable.disconnect();
				connectionPool.remove(pme.connectable);
			}


			for (PoolMapEntry pme : toClose.keySet()) {
				pme.connectable.disconnect();
				toClose.remove(pme.connectable);
			}

			connectionPool.notifyAll();
		}
	}

	/**
	 * Returns the specified {@link IConnectable} to the connection pool.
	 * If forceClose is true, then the connection is forced to be
	 * closed, irrespective of whether it might still be in use by
	 * other threads.
	 *
	 * @param connectable
	 * @param forceClose
	 * @throws IOException
	 */
	private void release(C connectable, boolean forceClose) {
		if (connectable == null) {
			throw new NullPointerException("No connection provided.");
		}

		synchronized (connectionPool) {
			PoolMapEntry pme = connectionPool.get(connectable);

			if (pme == null) {
				// It may be the case that the connection has been removed from the pool
				// while it was in use. In this case we get the connection from the 'toClose' map
				// and close it.
				if ((pme = toClose.remove(connectable)) != null) {
					pme.connectable.disconnect();
				}
			}
			else {
				if (forceClose) {
					// force the connection to be closed
					pme.connectable.disconnect();
					connectionPool.remove(pme.connectable);
				}
				else {
					// set the connection to be reusable
					pme.inuse = false;
				}
				connectionPool.notifyAll();
			}
		}
	}



	private class PoolMap implements Map<C, PoolMapEntry> {
		private final int _maxEntries;

		private final Map<C, PoolMapEntry> _backMap;

		public PoolMap(int maxEntries) {
			_maxEntries = maxEntries;
			_backMap = Collections.synchronizedMap(new LinkedHashMap<C, PoolMapEntry>(maxEntries, 0.75f, true) {
				private static final long serialVersionUID = -3139938026277477159L;

				@Override
				protected boolean removeEldestEntry(Map.Entry<C,PoolMapEntry> eldest) {
					boolean result = size() > _maxEntries;

					if (result) {
						synchronized (connectionPool) {
							// When removing an entry, we need to disconnect the connection.
							// Yet, we only do this if the connection is currently not used.
							// If the connection is currently used, it will be disconnect as
							// soon as it is released, cf. release().
							PoolMapEntry pme = eldest.getValue();
							if (pme.inuse == false) {
								pme.connectable.disconnect();
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
		public PoolMapEntry put(C key, PoolMapEntry value) {
			return _backMap.put(key, value);
		}

		@Override
		public PoolMapEntry remove(Object key) {
			return _backMap.remove(key);
		}

		@Override
		public void putAll(Map<? extends C, ? extends PoolMapEntry> m) {
			_backMap.putAll(m);
		}

		@Override
		public void clear() {
			_backMap.clear();
		}

		@Override
		public Set<C> keySet() {
			return _backMap.keySet();
		}

		@Override
		public Collection<PoolMapEntry> values() {
			return _backMap.values();
		}

		@Override
		public Set<java.util.Map.Entry<C, PoolMapEntry>> entrySet() {
			return _backMap.entrySet();
		}
	}


	/**
	 * Groups a connection and its current state, i.e. whether it is currently in use.
	 */
	private class PoolMapEntry {
		C connectable;
		boolean inuse;

		public PoolMapEntry(C connection, boolean inuse) {
			this.connectable = connection;
			this.inuse = inuse;
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(connectable);
		}
	}
}
