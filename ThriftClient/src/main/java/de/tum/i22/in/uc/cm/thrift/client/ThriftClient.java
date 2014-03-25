package de.tum.i22.in.uc.cm.thrift.client;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import de.tum.i22.in.uc.cm.thrift.Container;
import de.tum.i22.in.uc.cm.thrift.Data;
import de.tum.i22.in.uc.cm.thrift.Event;
import de.tum.i22.in.uc.cm.thrift.Name;
import de.tum.i22.in.uc.cm.thrift.Pxp;
import de.tum.i22.in.uc.cm.thrift.TAny2Any;
import de.tum.i22.in.uc.cm.thrift.TAny2Pdp;
import de.tum.i22.in.uc.cm.thrift.TAny2Pip;
import de.tum.i22.in.uc.cm.thrift.TAny2Pmp;

public class ThriftClient {

 public static void main(String[] args) {

		int x = 0;
  try {
   TTransport transportPdp;
   transportPdp = new TSocket("localhost", 60001);
   transportPdp.open();
   TProtocol protocolPdp = new TBinaryProtocol(transportPdp);
   TAny2Pdp.Client clientPdp = new TAny2Pdp.Client(protocolPdp);
   
   TTransport transportPip;
   transportPip = new TSocket("localhost", 60002);
   transportPip.open();
   TProtocol protocolPip = new TBinaryProtocol(transportPip);
   TAny2Pip.Client clientPip = new TAny2Pip.Client(protocolPip);

   TTransport transportPmp;
   transportPmp = new TSocket("localhost", 60003);
   transportPmp.open();
   TProtocol protocolPmp = new TBinaryProtocol(transportPmp);
   TAny2Pmp.Client clientPmp = new TAny2Pmp.Client(protocolPmp);

   TTransport transportAny;
   transportAny = new TSocket("localhost", 60004);
   transportAny.open();
   TProtocol protocolAny = new TBinaryProtocol(transportAny);
   TAny2Any.Client clientAny = new TAny2Any.Client(protocolAny);
   
   
   System.out.println("\n----------------");
   System.out.println("Testing TAny2Pdp");
   System.out.println("----------------");

   x=0;
  
   System.out.println("Test "+ (x++) +": "+ clientPdp.notifyEvent(new Event("event",new HashMap<String,String>(),0)));

   System.out.println("Test "+ (x++) +": "+ clientPdp.registerPxp(new Pxp()));

   System.out.println("Test "+ (x++) +": "+ clientPdp.deployMechanism("mechanism"));

   System.out.println("Test "+ (x++) +": "+ clientPdp.revokeMechanism1("policy"));

   System.out.println("Test "+ (x++) +": "+ clientPdp.revokeMechanism2("policy", "mech"));

   System.out.println("Test "+ (x++) +": "+ clientPdp.deployPolicy("policyPath"));

   System.out.println("Test "+ (x++) +": "+ clientPdp.listMechanisms());

   
   
   System.out.println("\n----------------");
   System.out.println("Testing TAny2Pip");
   System.out.println("----------------");

   x=0;
  
   System.out.println("Test "+ (x++) +": "+ clientPip.initialRepresentation(new Container("classValue","id"), new Data("id")));

   System.out.println("Test "+ (x++) +": "+ clientPip.hasAllData(new HashSet<Data>() ));

   System.out.println("Test "+ (x++) +": "+ clientPip.hasAnyData(new HashSet<Data>()));

   System.out.println("Test "+ (x++) +": "+ clientPip.hasAllContainers(new HashSet<Container>()));

   System.out.println("Test "+ (x++) +": "+ clientPip.hasAnyContainer(new HashSet<Container>()));

   System.out.println("Test "+ (x++) +": "+ clientPip.notifyActualEvent(new Event("event",new HashMap<String,String>(),0)));

   System.out.println("Test "+ (x++) +": "+ clientPip.notifyDataTransfer(new Name("name"), new HashSet<Data>()));

   System.out.println("Test "+ (x++) +": "+ clientPip.evaluatePredicateSimulatingNextState(new Event("event",new HashMap<String,String>(),0), "predicate"));

   System.out.println("Test "+ (x++) +": "+ clientPip.evaluatePredicatCurrentState("predicate"));

   System.out.println("Test "+ (x++) +": "+ clientPip.getContainerForData(new Data("id")));

   System.out.println("Test "+ (x++) +": "+ clientPip.getDataInContainer(new Container("classValue","id")));

   System.out.println("Test "+ (x++) +": "+ clientPip.startSimulation());

   System.out.println("Test "+ (x++) +": "+ clientPip.stopSimulation());

   System.out.println("Test "+ (x++) +": "+ clientPip.isSimulating());

   
   System.out.println("\n----------------");
   System.out.println("Testing TAny2Pmp");
   System.out.println("----------------");

   x=0;

   System.out.println("Test "+ (x++) +": "+ clientAny.deployMechanismPmp("mechanism"));

   System.out.println("Test "+ (x++) +": "+ clientAny.revokeMechanism1Pmp("policy"));

   System.out.println("Test "+ (x++) +": "+ clientAny.revokeMechanism2Pmp("policy", "mech"));

   System.out.println("Test "+ (x++) +": "+ clientAny.deployPolicyPmp("policyPath"));

   System.out.println("Test "+ (x++) +": "+ clientAny.listMechanismsPmp());

      
   
   
   System.out.println("\n----------------");
   System.out.println("Testing TAny2Any");
   System.out.println("----------------");

   x=0;
  
   System.out.println("Test "+ (x++) +": "+ clientAny.notifyEvent(new Event("event",new HashMap<String,String>(),0)));

   System.out.println("Test "+ (x++) +": "+ clientAny.registerPxp(new Pxp()));

   System.out.println("Test "+ (x++) +": "+ clientAny.deployMechanism("mechanism"));

   System.out.println("Test "+ (x++) +": "+ clientAny.revokeMechanism1("policy"));

   System.out.println("Test "+ (x++) +": "+ clientAny.revokeMechanism2("policy", "mech"));

   System.out.println("Test "+ (x++) +": "+ clientAny.deployPolicy("policyPath"));

   System.out.println("Test "+ (x++) +": "+ clientAny.listMechanisms());

   System.out.println("Test "+ (x++) +": "+ clientAny.initialRepresentation(new Container("classValue","id"), new Data("id")));

   System.out.println("Test "+ (x++) +": "+ clientAny.hasAllData(new HashSet<Data>() ));

   System.out.println("Test "+ (x++) +": "+ clientAny.hasAnyData(new HashSet<Data>()));

   System.out.println("Test "+ (x++) +": "+ clientAny.hasAllContainers(new HashSet<Container>()));

   System.out.println("Test "+ (x++) +": "+ clientAny.hasAnyContainer(new HashSet<Container>()));

   System.out.println("Test "+ (x++) +": "+ clientAny.notifyActualEvent(new Event("event",new HashMap<String,String>(),0)));

   System.out.println("Test "+ (x++) +": "+ clientAny.notifyDataTransfer(new Name("name"), new HashSet<Data>()));

   System.out.println("Test "+ (x++) +": "+ clientAny.evaluatePredicateSimulatingNextState(new Event("event",new HashMap<String,String>(),0), "predicate"));

   System.out.println("Test "+ (x++) +": "+ clientAny.evaluatePredicatCurrentState("predicate"));

   System.out.println("Test "+ (x++) +": "+ clientAny.getContainerForData(new Data("id")));

   System.out.println("Test "+ (x++) +": "+ clientAny.getDataInContainer(new Container("classValue","id")));

   System.out.println("Test "+ (x++) +": "+ clientAny.startSimulation());

   System.out.println("Test "+ (x++) +": "+ clientAny.stopSimulation());

   System.out.println("Test "+ (x++) +": "+ clientAny.isSimulating());

   System.out.println("Test "+ (x++) +": "+ clientAny.deployMechanismPmp("mechanism"));

   System.out.println("Test "+ (x++) +": "+ clientAny.revokeMechanism1Pmp("policy"));

   System.out.println("Test "+ (x++) +": "+ clientAny.revokeMechanism2Pmp("policy", "mech"));

   System.out.println("Test "+ (x++) +": "+ clientAny.deployPolicyPmp("policyPath"));

   System.out.println("Test "+ (x++) +": "+ clientAny.listMechanismsPmp());

 
   
   transportAny.close();
  } catch (TTransportException e) {
   e.printStackTrace();
  } catch (TException ex) {
   ex.printStackTrace();
  }
 }

}
