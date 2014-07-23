package uctests;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import de.tum.in.i22.uc.cm.datatypes.basic.ContainerBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.PxpSpec;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IResponse;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.client.Any2PdpClient;
import de.tum.in.i22.uc.cm.distribution.client.Pmp2PmpClient;
import de.tum.in.i22.uc.cm.handlers.RequestHandler;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.thrift.server.IThriftServer;
import de.tum.in.i22.uc.thrift.server.ThriftServerFactory;

public class ThriftTest extends GenericTest {

	private static final String policyFile = "target" + File.separator
			+ "test-classes" + File.separator + "testTUM.xml";

	private static Logger _logger = LoggerFactory.getLogger(ThriftTest.class);

	@Test
	public void testPmpSpecifyPolicyFor() throws Exception {
		sayMyName(Thread.currentThread().getStackTrace()[1].getMethodName());
		
		IContainer c1 = new ContainerBasic("container1");
		IContainer c2 = new ContainerBasic("container2");
		IContainer c3 = new ContainerBasic("container3");
		
		Set<IContainer> representations = new HashSet<IContainer>();
		representations.add(c1);
		representations.add(c2);
		representations.add(c3);
		
		IStatus st= pmp.specifyPolicyFor(representations, "myDataClass");
		Assert.assertEquals(st.getEStatus(), EStatus.OKAY);
		
		st = pmp.specifyPolicyFor(representations, Settings.getInstance().getPolicySpecificationStarDataClass());
		Assert.assertEquals(st.getEStatus(), EStatus.OKAY);
		
	}
	
	@Test
	public void testDataContEventMatching() throws Exception {
		sayMyName(Thread.currentThread().getStackTrace()[1].getMethodName());

		/*
		 * Connect to the PDP server
		 */
		Any2PdpClient clientPdp = thriftClientFactory
				.createAny2PdpClient(new IPLocation("localhost",
						PDP_SERVER_PORT));
		boolean connected = false;
		int attempts = 0;
		while (!connected && attempts < 10) {
			try {
				clientPdp.connect();
				connected = true;
			} catch (IOException e) {
				_logger.debug("Connection failed. Trying again (attempts num. "
						+ attempts + "/10)");
				attempts++;
			}
		}
		if (attempts==10) Assert.fail("Impossible to connect clientPdp");
		
		IResponse response;
		Map<String, String> map = new HashMap<>();
		Map<String, String> map2 = new HashMap<>();
		map.put("target", "generic target");
		map2.put("target", "/tmp/datasrc");

		/*
		 * Deploy policy
		 */
		IStatus ret = clientPdp
				.deployPolicyURI("src/test/resources/testTUM.xml");
		_logger.info("deploy policy: " + ret);
		_logger.info("Deployed Mechanisms: " + clientPdp.listMechanisms());

		/*
		 * Signal sync event
		 */
		response = clientPdp.notifyEventSync(new EventBasic("importantEvent",
				map, false));
		Assert.assertEquals(EStatus.ALLOW, response.getAuthorizationAction()
				.getEStatus());

		_logger.info("\nNotifying important event returned: " + response + "\n");

		response = clientPdp.notifyEventSync(new EventBasic("importantEvent",
				map2, false));
		Assert.assertEquals(EStatus.INHIBIT, response.getAuthorizationAction()
				.getEStatus());

		_logger.info("\n Notify important event number 2 returned: " + response + "\n");

	}

	@Test
	public void thriftTest() throws Exception {

		/*
		 * Start the PDP server
		 */
		IThriftServer pdpServer = ThriftServerFactory.createPdpThriftServer(
				PDP_SERVER_PORT + 100, new RequestHandler());
		new Thread(pdpServer).start();

		//Thread.sleep(1000);
		/*
		 * Connect to the PDP server
		 */
		Any2PdpClient clientPdp = thriftClientFactory
				.createAny2PdpClient(new IPLocation("localhost",
						PDP_SERVER_PORT + 100));
		clientPdp.connect();

		int x = 0;
		IResponse response;
		IStatus status;
		Map<String, List<String>> mechanisms;

		Map<String, String> map = new HashMap<>();
		map.put("name1", "value1");

		_logger.info(""+x++);

		/*
		 * Signal sync event
		 */
		response = clientPdp.notifyEventSync(new EventBasic("t", map, false));
		Assert.assertEquals(EStatus.ALLOW, response.getAuthorizationAction()
				.getEStatus());

		_logger.info(""+x++);

		/*
		 * Signal TobiasSync event
		 */
		response = clientPdp.processEventSync(new EventBasic("t", map, false));
		Assert.assertEquals(EStatus.ALLOW, response.getAuthorizationAction()
				.getEStatus());

		_logger.info(""+x++);
		
		/*
		 * Signal TobiasAsync event
		 */
		clientPdp.processEventAsync(new EventBasic("t", map, false));
		_logger.info(""+x++);

		
		/*
		 * list all mechanisms. Expected result is empty.
		 */
		mechanisms = clientPdp.listMechanisms();
		Assert.assertEquals(Maps.newHashMap(), mechanisms);
		Assert.assertEquals(0, mechanisms.size());

		_logger.info(""+x++);

		/*
		 * deploy a mechanism
		 */
		status = clientPdp.deployPolicyURI(policyFile);
		Assert.assertEquals(EStatus.OKAY, status.getEStatus());

		_logger.info(""+x++);

		/*
		 * list all mechanisms. Expected size of list is now 1.
		 */
		mechanisms = clientPdp.listMechanisms();
		Assert.assertEquals(1, mechanisms.size());

		_logger.info(""+x++);

		/*
		 * try to register a PXP.
		 */
		Assert.assertTrue(clientPdp
				.registerPxp(new PxpSpec(null, 0, null, null)));

		_logger.info(""+x++);

		pdpServer.stop();

		/*
		 * TODO FK: I do not really know what the expected behavior for the
		 * remaining tests is and whether the corresponding implementations
		 * actually work.
		 */

		// Assert.assertEquals(EStatus.OKAY,
		// clientPdp.revokeMechanism("testPolicy", "prev2").getEStatus());

		// _logger.info("Test " + (x++) + ": " +
		// clientPdp.listMechanisms());
		//
		// _logger.info("Test " + (x++) + ": " +
		// clientPdp.revokePolicy("testPolicy"));
		//
		// _logger.info("Test " + (x++) + ": " +
		// clientPdp.listMechanisms());
		//
		// _logger.info("\n----------------");
		// _logger.info("Testing TAny2Pip");
		// _logger.info("----------------");
		//
		// x = 0;
		//
		// _logger.info("Test " + (x++) + ": "
		// + clientPip.initialRepresentation(new TName("contid"),
		// Collections.singleton(new TData("dataid"))));
		//
		// _logger.info("Test " + (x++) + ": " + clientPip.hasAllData(new
		// HashSet<TData>()));
		//
		// _logger.info("Test " + (x++) + ": " + clientPip.hasAnyData(new
		// HashSet<TData>()));
		//
		// _logger.info("Test " + (x++) + ": " +
		// clientPip.hasAllContainers(new HashSet<TContainer>()));
		//
		// _logger.info("Test " + (x++) + ": " +
		// clientPip.hasAnyContainer(new HashSet<TContainer>()));
		//
		// _logger.info("Test " + (x++) + ": "
		// + clientPip.notifyActualEvent(new TEvent("event", new HashMap<String,
		// String>(), 0, true)));
		//
		// _logger.info("Test "
		// + (x++)
		// + ": "
		// + clientPip.evaluatePredicateSimulatingNextState(new TEvent("event",
		// new HashMap<String, String>(),
		// 0, true), "predicate"));
		//
		// _logger.info("Test " + (x++) + ": " +
		// clientPip.evaluatePredicatCurrentState("predicate"));
		//
		// _logger.info("Test " + (x++) + ": " +
		// clientPip.getContainerForData(new TData("id")));
		//
		// System.out
		// .println("Test " + (x++) + ": " + clientPip.getDataInContainer(new
		// TContainer("classValue", "id")));
		//
		// _logger.info("Test " + (x++) + ": " +
		// clientPip.startSimulation());
		//
		// _logger.info("Test " + (x++) + ": " +
		// clientPip.stopSimulation());
		//
		// _logger.info("Test " + (x++) + ": " +
		// clientPip.isSimulating());
		//
		// _logger.info("\n----------------");
		// _logger.info("Testing TAny2Pmp");
		// _logger.info("----------------");
		//
		// x = 0;
		//
		// // _logger.info("Test " + (x++) + ": " +
		// clientPmp.deployMechanismPmp("mechanism"));
		// //
		// // _logger.info("Test " + (x++) + ": " +
		// clientPmp.revokeMechanism1Pmp("policy"));
		// //
		// // _logger.info("Test " + (x++) + ": " +
		// clientPmp.revokeMechanism2Pmp("policy", "mech"));
		// //
		// // _logger.info("Test " + (x++) + ": " +
		// clientPmp.deployPolicyPmp("policyPath"));
		// //
		// // _logger.info("Test " + (x++) + ": " +
		// clientPmp.listMechanismsPmp());
		//
		// _logger.info("\n----------------");
		// _logger.info("Testing TAny2Any");
		// _logger.info("----------------");
		//
		// x = 0;
		//
		// _logger.info("Test " + (x++) + ": "
		// + clientAny.notifyEventSync(new TEvent("event", new HashMap<String,
		// String>(), 0, true)));
		//
		// _logger.info("Test " + (x++) + ": " + clientAny.registerPxp(new
		// TPxpSpec()));
		//
		// _logger.info("Test " + (x++) + ": " +
		// clientAny.deployMechanism("mechanism"));
		//
		// _logger.info("Test " + (x++) + ": " +
		// clientAny.revokeMechanism1("policy"));
		//
		// _logger.info("Test " + (x++) + ": " +
		// clientAny.revokeMechanism2("policy", "mech"));
		//
		// _logger.info("Test " + (x++) + ": " +
		// clientAny.deployPolicy("policyPath"));
		//
		// _logger.info("Test " + (x++) + ": " +
		// clientAny.listMechanisms());
		//
		// _logger.info("Test " + (x++) + ": "
		// + clientAny.initialRepresentation(new TContainer("classValue", "id"),
		// new TData("id")));
		//
		// _logger.info("Test " + (x++) + ": " + clientAny.hasAllData(new
		// HashSet<TData>()));
		//
		// _logger.info("Test " + (x++) + ": " + clientAny.hasAnyData(new
		// HashSet<TData>()));
		//
		// _logger.info("Test " + (x++) + ": " +
		// clientAny.hasAllContainers(new HashSet<TContainer>()));
		//
		// _logger.info("Test " + (x++) + ": " +
		// clientAny.hasAnyContainer(new HashSet<TContainer>()));
		//
		// _logger.info("Test " + (x++) + ": "
		// + clientAny.notifyActualEvent(new TEvent("event", new HashMap<String,
		// String>(), 0, true)));
		//
		// _logger.info("Test " + (x++) + ": "
		// + clientAny.notifyDataTransfer(new TName("name"), new
		// HashSet<TData>()));
		//
		// _logger.info("Test "
		// + (x++)
		// + ": "
		// + clientAny.evaluatePredicateSimulatingNextState(new TEvent("event",
		// new HashMap<String, String>(),
		// 0, true), "predicate"));
		//
		// _logger.info("Test " + (x++) + ": " +
		// clientAny.evaluatePredicatCurrentState("predicate"));
		//
		// _logger.info("Test " + (x++) + ": " +
		// clientAny.getContainerForData(new TData("id")));
		//
		// System.out
		// .println("Test " + (x++) + ": " + clientAny.getDataInContainer(new
		// TContainer("classValue", "id")));
		//
		// _logger.info("Test " + (x++) + ": " +
		// clientAny.startSimulation());
		//
		// _logger.info("Test " + (x++) + ": " +
		// clientAny.stopSimulation());
		//
		// _logger.info("Test " + (x++) + ": " +
		// clientAny.isSimulating());
		//
		// _logger.info("Test " + (x++) + ": " +
		// clientAny.deployMechanismPmp("mechanism"));
		//
		// _logger.info("Test " + (x++) + ": " +
		// clientAny.revokeMechanism1Pmp("policy"));
		//
		// _logger.info("Test " + (x++) + ": " +
		// clientAny.revokeMechanism2Pmp("policy", "mech"));
		//
		// _logger.info("Test " + (x++) + ": " +
		// clientAny.deployPolicyPmp("policyPath"));
		//
		// _logger.info("Test " + (x++) + ": " +
		// clientAny.listMechanismsPmp());
		//
		// transportAny.close();
	}

}
