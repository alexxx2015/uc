package uctests;

import org.junit.Assert;
import org.junit.Test;

import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.LocalLocation;
import de.tum.in.i22.uc.cm.distribution.client.Any2PdpClient;
import de.tum.in.i22.uc.cm.distribution.client.ConnectionManager;
import de.tum.in.i22.uc.cm.handlers.RequestHandler;
import de.tum.in.i22.uc.cm.processing.IRequestHandler;
import de.tum.in.i22.uc.thrift.client.ThriftClientFactory;
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

		// create a new connection manager of size 2 and start the pdp server
		ConnectionManager<Any2PdpClient> manager = new ConnectionManager<>(2);
		IRequestHandler requestHandler = new RequestHandler(new LocalLocation(),
															new LocalLocation(),
															new LocalLocation());

		IThriftServer pdpServer = ThriftServerFactory.createPdpThriftServer(pdpPort, requestHandler);
		new Thread(pdpServer).start();

		// create 6 different
		Any2PdpClient pdpClient1 = new ThriftClientFactory().createAny2PdpClient(new IPLocation("localhost", pdpPort));
		Any2PdpClient pdpClient2 = new ThriftClientFactory().createAny2PdpClient(new IPLocation("localhost", pdpPort));
		Any2PdpClient pdpClient3 = new ThriftClientFactory().createAny2PdpClient(new IPLocation("localhost", pdpPort));
		Any2PdpClient pdpClientRef;

		/*
		 * Tests to make sure that we got indeed three different instances above.
		 */
		Assert.assertNotSame(pdpClient1, pdpClient2);
		Assert.assertNotSame(pdpClient2, pdpClient3);
		Assert.assertNotSame(pdpClient1, pdpClient3);

		/*
		 * First test, first connection.
		 * The Connection returned from obtain() should be
		 * exactly the one that we passed as an argument
		 */
		pdpClientRef = null;
		pdpClientRef = manager.obtain(pdpClient1);
		Assert.assertSame(pdpClientRef, pdpClient1);
		manager.release(pdpClient1);

		/*
		 * Second test: Different client instance, but same server
		 * The connection returned from obtain() should be
		 * exactly the same as the one from above, because the
		 * connection is suppossed to be reused.
		 */
		pdpClientRef = null;
		pdpClientRef = manager.obtain(pdpClient2);
		Assert.assertSame(pdpClientRef, pdpClient1);
		Assert.assertNotSame(pdpClientRef, pdpClient2);
		manager.release(pdpClient2);

		/*
		 * Third test: Different client instance, but same server.
		 * The connection returned from obtain() should be
		 * exactly the same as the one from the first invocation.
		 */
		pdpClientRef = null;
		pdpClientRef = manager.obtain(pdpClient3);
		Assert.assertSame(pdpClientRef, pdpClient1);
		Assert.assertNotSame(pdpClientRef, pdpClient3);
		manager.release(pdpClient3);
	}


	@Test
	public void testMaxSize() throws Exception {
		/*
		 * This test checks whether old connections are in fact removed
		 * from the ConnectionManager once its maximum size is reached.
		 */


		// create a new connection manager of size 2 and start the pdp server
		ConnectionManager<Any2PdpClient> manager = new ConnectionManager<>(2);
		IRequestHandler requestHandler = new RequestHandler(new LocalLocation(),
				new LocalLocation(), new LocalLocation());

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
		Any2PdpClient pdpClient1a = new ThriftClientFactory().createAny2PdpClient(new IPLocation("localhost", pdpPort + 1));
		Any2PdpClient pdpClient1b = new ThriftClientFactory().createAny2PdpClient(new IPLocation("localhost", pdpPort + 1));
		Any2PdpClient pdpClient2 = new ThriftClientFactory().createAny2PdpClient(new IPLocation("localhost", pdpPort + 2));
		Any2PdpClient pdpClient3 = new ThriftClientFactory().createAny2PdpClient(new IPLocation("localhost", pdpPort + 3));
		Any2PdpClient pdpClientRef;

		/*
		 * First connection. We expect the connection to be returned
		 * that has been given as a parameter
		 */
		pdpClientRef = null;
		pdpClientRef = manager.obtain(pdpClient1a);
		Assert.assertSame(pdpClient1a, pdpClientRef);
		manager.release(pdpClient1a);

		/*
		 * Second connection but to a different server.
		 * We expect the connection to be returned that
		 * has been given as a parameter.
		 */
		pdpClientRef = null;
		pdpClientRef = manager.obtain(pdpClient2);
		Assert.assertSame(pdpClient2, pdpClientRef);
		manager.release(pdpClient2);

		/*
		 * Third test, third server. Same as above.
		 * The point of this is to remove the first
		 * connection from above from the ConnectionManager
		 * because of the least-recently-used policy.
		 */
		pdpClientRef = null;
		pdpClientRef = manager.obtain(pdpClient3);
		Assert.assertSame(pdpClient3, pdpClientRef);
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
		Assert.assertSame(pdpClient1b, pdpClientRef);
		Assert.assertNotSame(pdpClient1a, pdpClientRef);
		manager.release(pdpClient1b);
	}
}
