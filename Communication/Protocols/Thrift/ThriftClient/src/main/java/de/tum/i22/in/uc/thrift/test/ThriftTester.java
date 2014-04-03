package de.tum.i22.in.uc.thrift.test;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import de.tum.i22.in.uc.thrift.types.TContainer;
import de.tum.i22.in.uc.thrift.types.TData;
import de.tum.i22.in.uc.thrift.types.TEvent;
import de.tum.i22.in.uc.thrift.types.TName;
import de.tum.i22.in.uc.thrift.types.TPxpSpec;

import de.tum.i22.in.uc.thrift.types.TAny2Any;
import de.tum.i22.in.uc.thrift.types.TAny2Pip;
import de.tum.i22.in.uc.thrift.types.TAny2Pmp;
import de.tum.in.i22.uc.cm.basic.EventBasic;
import de.tum.in.i22.uc.cm.basic.MechanismBasic;
import de.tum.in.i22.uc.cm.basic.PxpSpec;
import de.tum.in.i22.uc.cm.client.PdpClientHandler;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.thrift.client.ThriftClientHandlerFactory;

public class ThriftTester {

	public static void main(String[] args) {

		ThriftClientHandlerFactory thriftClientFactory = new ThriftClientHandlerFactory();

		int x = 0;
		try {
			PdpClientHandler<?> clientPdp = thriftClientFactory.createPdpClientHandler(new IPLocation("localhost", Settings.getInstance().getPdpListenerPort()));
			try {
				clientPdp.connect();
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage(),e);
			}


			TTransport transportPip = new TSocket("localhost", Settings.getInstance().getPipListenerPort());
			transportPip.open();
			TProtocol protocolPip = new TBinaryProtocol(transportPip);
			TAny2Pip.Client clientPip = new TAny2Pip.Client(protocolPip);

			TTransport transportPmp = new TSocket("localhost", Settings.getInstance().getPmpListenerPort());
			transportPmp.open();
			TProtocol protocolPmp = new TBinaryProtocol(transportPmp);
			TAny2Pmp.Client clientPmp = new TAny2Pmp.Client(protocolPmp);

			TTransport transportAny = new TSocket("localhost", Settings.getInstance().getAnyListenerPort());
			transportAny.open();
			TProtocol protocolAny = new TBinaryProtocol(transportAny);
			TAny2Any.Client clientAny = new TAny2Any.Client(protocolAny);

			System.out.println("\n----------------");
			System.out.println("Testing TAny2Pdp");
			System.out.println("----------------");

			x = 0;

			Map<String,String> map = new HashMap<>();
			map.put("name1", "value1");

			System.out.println("Test " + (x++) + ": "
					+ clientPdp.notifyEventSync(new EventBasic("t", map, false)));

			System.out.println("Test " + (x++) + ": " + clientPdp.registerPxp(new PxpSpec(null, x, null, null)));

			System.out.println("Test " + (x++) + ": " + clientPdp.deployMechanism(new MechanismBasic()));

			System.out.println("Test " + (x++) + ": " + clientPdp.revokeMechanism("policy"));

			System.out.println("Test " + (x++) + ": " + clientPdp.revokeMechanism("policy", "mech"));

			System.out.println("Test " + (x++) + ": " + clientPdp.deployPolicy("policyPath"));

			System.out.println("Test " + (x++) + ": " + clientPdp.listMechanisms());

			System.out.println("\n----------------");
			System.out.println("Testing TAny2Pip");
			System.out.println("----------------");

			x = 0;

			System.out.println("Test " + (x++) + ": "
					+ clientPip.initialRepresentation(new TName("contid"), Collections.singleton(new TData("dataid"))));

			System.out.println("Test " + (x++) + ": " + clientPip.hasAllData(new HashSet<TData>()));

			System.out.println("Test " + (x++) + ": " + clientPip.hasAnyData(new HashSet<TData>()));

			System.out.println("Test " + (x++) + ": " + clientPip.hasAllContainers(new HashSet<TContainer>()));

			System.out.println("Test " + (x++) + ": " + clientPip.hasAnyContainer(new HashSet<TContainer>()));

			System.out.println("Test " + (x++) + ": "
					+ clientPip.notifyActualEvent(new TEvent("event", new HashMap<String, String>(), 0, true)));

			System.out.println("Test "
					+ (x++)
					+ ": "
					+ clientPip.evaluatePredicateSimulatingNextState(new TEvent("event", new HashMap<String, String>(),
							0, true), "predicate"));

			System.out.println("Test " + (x++) + ": " + clientPip.evaluatePredicatCurrentState("predicate"));

			System.out.println("Test " + (x++) + ": " + clientPip.getContainerForData(new TData("id")));

			System.out
					.println("Test " + (x++) + ": " + clientPip.getDataInContainer(new TContainer("classValue", "id")));

			System.out.println("Test " + (x++) + ": " + clientPip.startSimulation());

			System.out.println("Test " + (x++) + ": " + clientPip.stopSimulation());

			System.out.println("Test " + (x++) + ": " + clientPip.isSimulating());

			System.out.println("\n----------------");
			System.out.println("Testing TAny2Pmp");
			System.out.println("----------------");

			x = 0;

//			System.out.println("Test " + (x++) + ": " + clientPmp.deployMechanismPmp("mechanism"));
//
//			System.out.println("Test " + (x++) + ": " + clientPmp.revokeMechanism1Pmp("policy"));
//
//			System.out.println("Test " + (x++) + ": " + clientPmp.revokeMechanism2Pmp("policy", "mech"));
//
//			System.out.println("Test " + (x++) + ": " + clientPmp.deployPolicyPmp("policyPath"));
//
//			System.out.println("Test " + (x++) + ": " + clientPmp.listMechanismsPmp());

			System.out.println("\n----------------");
			System.out.println("Testing TAny2Any");
			System.out.println("----------------");

			x = 0;

			System.out.println("Test " + (x++) + ": "
					+ clientAny.notifyEventSync(new TEvent("event", new HashMap<String, String>(), 0, true)));

			System.out.println("Test " + (x++) + ": " + clientAny.registerPxp(new TPxpSpec()));

			System.out.println("Test " + (x++) + ": " + clientAny.deployMechanism("mechanism"));

			System.out.println("Test " + (x++) + ": " + clientAny.revokeMechanism1("policy"));

			System.out.println("Test " + (x++) + ": " + clientAny.revokeMechanism2("policy", "mech"));

			System.out.println("Test " + (x++) + ": " + clientAny.deployPolicy("policyPath"));

			System.out.println("Test " + (x++) + ": " + clientAny.listMechanisms());

			System.out.println("Test " + (x++) + ": "
					+ clientAny.initialRepresentation(new TContainer("classValue", "id"), new TData("id")));

			System.out.println("Test " + (x++) + ": " + clientAny.hasAllData(new HashSet<TData>()));

			System.out.println("Test " + (x++) + ": " + clientAny.hasAnyData(new HashSet<TData>()));

			System.out.println("Test " + (x++) + ": " + clientAny.hasAllContainers(new HashSet<TContainer>()));

			System.out.println("Test " + (x++) + ": " + clientAny.hasAnyContainer(new HashSet<TContainer>()));

			System.out.println("Test " + (x++) + ": "
					+ clientAny.notifyActualEvent(new TEvent("event", new HashMap<String, String>(), 0, true)));

			System.out.println("Test " + (x++) + ": "
					+ clientAny.notifyDataTransfer(new TName("name"), new HashSet<TData>()));

			System.out.println("Test "
					+ (x++)
					+ ": "
					+ clientAny.evaluatePredicateSimulatingNextState(new TEvent("event", new HashMap<String, String>(),
							0, true), "predicate"));

			System.out.println("Test " + (x++) + ": " + clientAny.evaluatePredicatCurrentState("predicate"));

			System.out.println("Test " + (x++) + ": " + clientAny.getContainerForData(new TData("id")));

			System.out
					.println("Test " + (x++) + ": " + clientAny.getDataInContainer(new TContainer("classValue", "id")));

			System.out.println("Test " + (x++) + ": " + clientAny.startSimulation());

			System.out.println("Test " + (x++) + ": " + clientAny.stopSimulation());

			System.out.println("Test " + (x++) + ": " + clientAny.isSimulating());

			System.out.println("Test " + (x++) + ": " + clientAny.deployMechanismPmp("mechanism"));

			System.out.println("Test " + (x++) + ": " + clientAny.revokeMechanism1Pmp("policy"));

			System.out.println("Test " + (x++) + ": " + clientAny.revokeMechanism2Pmp("policy", "mech"));

			System.out.println("Test " + (x++) + ": " + clientAny.deployPolicyPmp("policyPath"));

			System.out.println("Test " + (x++) + ": " + clientAny.listMechanismsPmp());

			transportAny.close();
		} catch (TTransportException e) {
			e.printStackTrace();
		} catch (TException ex) {
			ex.printStackTrace();
		}
	}

}
