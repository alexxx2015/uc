//package de.tum.in.i22.uc.cassandra;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public class EmbeddedCassandraServer {
//	protected static final Logger _logger = LoggerFactory.getLogger(EmbeddedCassandraServer.class);
//
//	private CassandraDaemon _cassandraDaemon;
//
//	private Thread _cassandraThread;
//
//
//	public void start() throws Exception {
//		try {
//			_cassandraDaemon = new CassandraDaemon();
//			_cassandraDaemon.init(null);
//
//			_cassandraThread = new Thread(new Runnable() {
//				@Override
//				public void run() {
//					try {
//						_cassandraDaemon.start();
//					} catch (Exception e) {
//						_logger.error("Embedded casandra server run failed", e);
//					}
//				}
//			});
//
//			_cassandraThread.setDaemon(true);
//			_cassandraThread.start();
//		} catch (Exception e) {
//			_logger.error("Embedded casandra server start failed", e);
//
//			// cleanup
//			stop();
//		}
//	}
//
//	/**
//	 * Stops embedded Cassandra server.
//	 *
//	 * @throws Exception
//	 * if an error occurs
//	 */
//	public void stop() throws Exception {
//		if (_cassandraThread != null) {
//			_cassandraDaemon.stop();
//			_cassandraDaemon.destroy();
//			_cassandraThread.interrupt();
//			_cassandraThread = null;
//		}
//	}
//}
