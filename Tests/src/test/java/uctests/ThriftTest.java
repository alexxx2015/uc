package uctests;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Maps;

import de.tum.in.i22.uc.cm.basic.EventBasic;
import de.tum.in.i22.uc.cm.basic.PxpSpec;
import de.tum.in.i22.uc.cm.client.Any2PdpClient;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.LocalLocation;
import de.tum.in.i22.uc.cm.handlers.RequestHandler;
import de.tum.in.i22.uc.thrift.client.ThriftClientFactory;
import de.tum.in.i22.uc.thrift.server.IThriftServer;
import de.tum.in.i22.uc.thrift.server.ThriftServerFactory;

public class ThriftTest {

	private static final String policyFile = "target" + File.separator
												+ "test-classes" + File.separator
												+ "testTUM.xml";

	private static int pdpPort = 60000;

	private static ThriftClientFactory thriftClientFactory = new ThriftClientFactory();

	@Test
	public void thriftTest() throws Exception {

		/*
		 * Start the PDP server
		 */
		IThriftServer pdpServer = ThriftServerFactory.createPdpThriftServer(pdpPort, new RequestHandler(
				new LocalLocation(),
				new LocalLocation(),
				new LocalLocation()));
		new Thread(pdpServer).start();

		/*
		 * Connect to the PDP server
		 */
		Any2PdpClient clientPdp = thriftClientFactory.createPdpClientHandler(new IPLocation("localhost", pdpPort));
		clientPdp.connect();

		int x = 0;

		IResponse response;
		IStatus status;
		Map<String, List<String>> mechanisms;

		Map<String,String> map = new HashMap<>();
		map.put("name1", "value1");

		System.out.println(x++);

		/*
		 * Signal sync event
		 */
		response = clientPdp.notifyEventSync(new EventBasic("t", map, false));
		Assert.assertEquals(EStatus.ALLOW, response.getAuthorizationAction().getEStatus());

		System.out.println(x++);

		/*
		 * list all mechanisms. Expected result is empty.
		 */
		mechanisms = clientPdp.listMechanisms();
		Assert.assertEquals(Maps.newHashMap(), mechanisms);
		Assert.assertEquals(0, mechanisms.size());

		System.out.println(x++);

		/*
		 * deploy a mechanism
		 */
		status = clientPdp.deployPolicyURI(policyFile);
		Assert.assertEquals(EStatus.OKAY, status.getEStatus());

		System.out.println(x++);

		/*
		 * list all mechanisms. Expected size of list is now 1.
		 */
		mechanisms = clientPdp.listMechanisms();
		Assert.assertEquals(1, mechanisms.size());

		System.out.println(x++);

		/*
		 * try to register a PXP.
		 */
		Assert.assertTrue(clientPdp.registerPxp(new PxpSpec(null, 0, null, null)));

		System.out.println(x++);




		/*
		 * TODO
		 * FK: I do not really know what the expected behavior for the remaining tests is and whether
		 * the corresponding implementations actually work.
		 */

		// Assert.assertEquals(EStatus.OKAY, clientPdp.revokeMechanism("testPolicy", "prev2").getEStatus());

//		System.out.println("Test " + (x++) + ": " + clientPdp.listMechanisms());
//
//		System.out.println("Test " + (x++) + ": " + clientPdp.revokePolicy("testPolicy"));
//
//		System.out.println("Test " + (x++) + ": " + clientPdp.listMechanisms());
//
//			System.out.println("\n----------------");
//			System.out.println("Testing TAny2Pip");
//			System.out.println("----------------");
//
//			x = 0;
//
//			System.out.println("Test " + (x++) + ": "
//					+ clientPip.initialRepresentation(new TName("contid"), Collections.singleton(new TData("dataid"))));
//
//			System.out.println("Test " + (x++) + ": " + clientPip.hasAllData(new HashSet<TData>()));
//
//			System.out.println("Test " + (x++) + ": " + clientPip.hasAnyData(new HashSet<TData>()));
//
//			System.out.println("Test " + (x++) + ": " + clientPip.hasAllContainers(new HashSet<TContainer>()));
//
//			System.out.println("Test " + (x++) + ": " + clientPip.hasAnyContainer(new HashSet<TContainer>()));
//
//			System.out.println("Test " + (x++) + ": "
//					+ clientPip.notifyActualEvent(new TEvent("event", new HashMap<String, String>(), 0, true)));
//
//			System.out.println("Test "
//					+ (x++)
//					+ ": "
//					+ clientPip.evaluatePredicateSimulatingNextState(new TEvent("event", new HashMap<String, String>(),
//							0, true), "predicate"));
//
//			System.out.println("Test " + (x++) + ": " + clientPip.evaluatePredicatCurrentState("predicate"));
//
//			System.out.println("Test " + (x++) + ": " + clientPip.getContainerForData(new TData("id")));
//
//			System.out
//					.println("Test " + (x++) + ": " + clientPip.getDataInContainer(new TContainer("classValue", "id")));
//
//			System.out.println("Test " + (x++) + ": " + clientPip.startSimulation());
//
//			System.out.println("Test " + (x++) + ": " + clientPip.stopSimulation());
//
//			System.out.println("Test " + (x++) + ": " + clientPip.isSimulating());
//
//			System.out.println("\n----------------");
//			System.out.println("Testing TAny2Pmp");
//			System.out.println("----------------");
//
//			x = 0;
//
////			System.out.println("Test " + (x++) + ": " + clientPmp.deployMechanismPmp("mechanism"));
////
////			System.out.println("Test " + (x++) + ": " + clientPmp.revokeMechanism1Pmp("policy"));
////
////			System.out.println("Test " + (x++) + ": " + clientPmp.revokeMechanism2Pmp("policy", "mech"));
////
////			System.out.println("Test " + (x++) + ": " + clientPmp.deployPolicyPmp("policyPath"));
////
////			System.out.println("Test " + (x++) + ": " + clientPmp.listMechanismsPmp());
//
//			System.out.println("\n----------------");
//			System.out.println("Testing TAny2Any");
//			System.out.println("----------------");
//
//			x = 0;
//
//			System.out.println("Test " + (x++) + ": "
//					+ clientAny.notifyEventSync(new TEvent("event", new HashMap<String, String>(), 0, true)));
//
//			System.out.println("Test " + (x++) + ": " + clientAny.registerPxp(new TPxpSpec()));
//
//			System.out.println("Test " + (x++) + ": " + clientAny.deployMechanism("mechanism"));
//
//			System.out.println("Test " + (x++) + ": " + clientAny.revokeMechanism1("policy"));
//
//			System.out.println("Test " + (x++) + ": " + clientAny.revokeMechanism2("policy", "mech"));
//
//			System.out.println("Test " + (x++) + ": " + clientAny.deployPolicy("policyPath"));
//
//			System.out.println("Test " + (x++) + ": " + clientAny.listMechanisms());
//
//			System.out.println("Test " + (x++) + ": "
//					+ clientAny.initialRepresentation(new TContainer("classValue", "id"), new TData("id")));
//
//			System.out.println("Test " + (x++) + ": " + clientAny.hasAllData(new HashSet<TData>()));
//
//			System.out.println("Test " + (x++) + ": " + clientAny.hasAnyData(new HashSet<TData>()));
//
//			System.out.println("Test " + (x++) + ": " + clientAny.hasAllContainers(new HashSet<TContainer>()));
//
//			System.out.println("Test " + (x++) + ": " + clientAny.hasAnyContainer(new HashSet<TContainer>()));
//
//			System.out.println("Test " + (x++) + ": "
//					+ clientAny.notifyActualEvent(new TEvent("event", new HashMap<String, String>(), 0, true)));
//
//			System.out.println("Test " + (x++) + ": "
//					+ clientAny.notifyDataTransfer(new TName("name"), new HashSet<TData>()));
//
//			System.out.println("Test "
//					+ (x++)
//					+ ": "
//					+ clientAny.evaluatePredicateSimulatingNextState(new TEvent("event", new HashMap<String, String>(),
//							0, true), "predicate"));
//
//			System.out.println("Test " + (x++) + ": " + clientAny.evaluatePredicatCurrentState("predicate"));
//
//			System.out.println("Test " + (x++) + ": " + clientAny.getContainerForData(new TData("id")));
//
//			System.out
//					.println("Test " + (x++) + ": " + clientAny.getDataInContainer(new TContainer("classValue", "id")));
//
//			System.out.println("Test " + (x++) + ": " + clientAny.startSimulation());
//
//			System.out.println("Test " + (x++) + ": " + clientAny.stopSimulation());
//
//			System.out.println("Test " + (x++) + ": " + clientAny.isSimulating());
//
//			System.out.println("Test " + (x++) + ": " + clientAny.deployMechanismPmp("mechanism"));
//
//			System.out.println("Test " + (x++) + ": " + clientAny.revokeMechanism1Pmp("policy"));
//
//			System.out.println("Test " + (x++) + ": " + clientAny.revokeMechanism2Pmp("policy", "mech"));
//
//			System.out.println("Test " + (x++) + ": " + clientAny.deployPolicyPmp("policyPath"));
//
//			System.out.println("Test " + (x++) + ": " + clientAny.listMechanismsPmp());
//
//			transportAny.close();
	}

}
