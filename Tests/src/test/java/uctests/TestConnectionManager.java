package uctests;

import org.junit.Assert;
import org.junit.Test;

import de.tum.in.i22.uc.cm.client.ConnectionManager;
import de.tum.in.i22.uc.cm.client.PdpClientHandler;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.handlers.RequestHandler;
import de.tum.in.i22.uc.cm.server.IRequestHandler;
import de.tum.in.i22.uc.thrift.client.ThriftClientHandlerFactory;
import de.tum.in.i22.uc.thrift.server.IThriftServer;
import de.tum.in.i22.uc.thrift.server.ThriftServerFactory;

public class TestConnectionManager {

	private static int pdpPort = 60000;


	@Test
	public void testObtainSameConnection() throws Exception {
		/*
		 * This is a simple test to see whether a second invocation
		 * of ConnectionManager.obtain() with a different but equal object
		 * returns the correct result (i.e. the connection obtained earlier).
		 */

		// create a new connection manager of size 5 and start the pdp server
		ConnectionManager<PdpClientHandler> manager = new ConnectionManager<>(2);
		IRequestHandler requestHandler = RequestHandler.getInstance();

		IThriftServer pdpServer = ThriftServerFactory.createPdpThriftServer(pdpPort, requestHandler);
		new Thread(pdpServer).start();

		// create 6 different
		PdpClientHandler pdpClient1 = new ThriftClientHandlerFactory().createPdpClientHandler(new IPLocation("localhost", pdpPort));
		PdpClientHandler pdpClient2 = new ThriftClientHandlerFactory().createPdpClientHandler(new IPLocation("localhost", pdpPort));
		PdpClientHandler pdpClient3 = new ThriftClientHandlerFactory().createPdpClientHandler(new IPLocation("localhost", pdpPort));
		PdpClientHandler pdpClientRef;

		/*
		 * Tests to make sure that we got indeed three different instances above.
		 */
		Assert.assertTrue(pdpClient1 != pdpClient2);
		Assert.assertTrue(pdpClient2 != pdpClient3);
		Assert.assertTrue(pdpClient1 != pdpClient3);

		/*
		 * First test, first connection.
		 * The Connection returned from obtain() should be
		 * exactly the one that we passed as an argument
		 */
		pdpClientRef = null;
		pdpClientRef = manager.obtain(pdpClient1);
		Assert.assertTrue(pdpClientRef == pdpClient1);
		manager.release(pdpClient1);

		/*
		 * Second test: Different client instance, but same server
		 * The connection returned from obtain() should be
		 * exactly the same as the one from above, because the
		 * connection is suppossed to be reused.
		 */
		pdpClientRef = null;
		pdpClientRef = manager.obtain(pdpClient2);
		Assert.assertTrue(pdpClientRef == pdpClient1);
		Assert.assertTrue(pdpClientRef != pdpClient2);
		manager.release(pdpClient2);

		/*
		 * Third test: Different client instance, but same server.
		 * The connection returned from obtain() should be
		 * exactly the same as the one from the first invocation.
		 */
		pdpClientRef = null;
		pdpClientRef = manager.obtain(pdpClient3);
		Assert.assertTrue(pdpClientRef == pdpClient1);
		Assert.assertTrue(pdpClientRef != pdpClient3);
		manager.release(pdpClient3);
	}


	@Test
	public void testMaxSize() throws Exception {
		/*
		 * This test checks whether old connections are in fact removed
		 * from the ConnectionManager once its maximum size is reached.
		 */


		// create a new connection manager of size 5 and start the pdp server
		ConnectionManager<PdpClientHandler> manager = new ConnectionManager<>(2);
		IRequestHandler requestHandler = RequestHandler.getInstance();

		/*
		 * Start three servers
		 */
		IThriftServer pdpServer1 = ThriftServerFactory.createPdpThriftServer(pdpPort + 1, requestHandler);
		IThriftServer pdpServer2 = ThriftServerFactory.createPdpThriftServer(pdpPort + 2, requestHandler);
		IThriftServer pdpServer3 = ThriftServerFactory.createPdpThriftServer(pdpPort + 3, requestHandler);
		new Thread(pdpServer1).start();
		new Thread(pdpServer2).start();
		new Thread(pdpServer3).start();

		/*
		 * Create three clients
		 */
		PdpClientHandler pdpClient1a = new ThriftClientHandlerFactory().createPdpClientHandler(new IPLocation("localhost", pdpPort + 1));
		PdpClientHandler pdpClient1b = new ThriftClientHandlerFactory().createPdpClientHandler(new IPLocation("localhost", pdpPort + 1));
		PdpClientHandler pdpClient2 = new ThriftClientHandlerFactory().createPdpClientHandler(new IPLocation("localhost", pdpPort + 2));
		PdpClientHandler pdpClient3 = new ThriftClientHandlerFactory().createPdpClientHandler(new IPLocation("localhost", pdpPort + 3));
		PdpClientHandler pdpClientRef;

		/*
		 * First connection. We expect the connection to be returned
		 * that has been given as a parameter
		 */
		pdpClientRef = null;
		pdpClientRef = manager.obtain(pdpClient1a);
		Assert.assertTrue(pdpClient1a == pdpClientRef);
		manager.release(pdpClient1a);

		/*
		 * Second connection but to a different server.
		 * We expect the connection to be returned that
		 * has been given as a parameter.
		 */
		pdpClientRef = null;
		pdpClientRef = manager.obtain(pdpClient2);
		Assert.assertTrue(pdpClient2 == pdpClientRef);
		manager.release(pdpClient2);

		/*
		 * Third test, third server. Same as above.
		 * The point of this is to remove the first
		 * connection from above from the ConnectionManager
		 * because of the least-recently-used policy.
		 */
		pdpClientRef = null;
		pdpClientRef = manager.obtain(pdpClient3);
		Assert.assertTrue(pdpClient3 == pdpClientRef);
		manager.release(pdpClient3);

		/*
		 * Now, once we do again the connection to the first
		 * server but using a different client handle, we
		 * expect _not_ to reference from above to be returned
		 * but the one we have just passed as an argument due to
		 * the size of the manager and its LRU policy.
		 */
		pdpClientRef = null;
		pdpClientRef = manager.obtain(pdpClient1b);
		Assert.assertTrue(pdpClient1b == pdpClientRef);
		Assert.assertTrue(pdpClient1a != pdpClientRef);
		manager.release(pdpClient1b);
	}
}
