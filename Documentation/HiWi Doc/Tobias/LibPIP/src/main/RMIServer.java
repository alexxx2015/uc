package main;

import helper.IPIPCommunication;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

import structures.PDPEvent;
import structures.UniqueIntList;

//TODO RemoteException?
/* Remote method invocations can fail in many additional ways compared to local method invocations (such as 
 * network-related communication problems and server problems), and remote methods will report such failures by throwing a java.rmi.RemoteException. */

public class RMIServer implements RMICommunicationInterface {
    private static Logger log = Logger.getLogger("RMI Server");

    IPIPCommunication communication;

   //No one but the run method should create a server.
    private RMIServer() {
	communication = new PIPHandler();
    }

    private RMIServer(IPIPCommunication ipipCommunication) {
	this.communication = ipipCommunication;
    }
    
    
    //I have not yet decided which of this run Methods is really necessary, but this should cover all cases.

    //This is the most simple one: Just hit run and the server does everything needed.
    public static void main(String[] args) {
	RMIServer server = new RMIServer();
	run(server);
    }

    //Taking an IPIPCommunication as a parameter is the same behavior as the TCPServer
    public static void run(IPIPCommunication ipipCommunication) {
	RMIServer server = new RMIServer(ipipCommunication);
	run(server);
    }
    
    
    //TODO: Does it make sense to run the server twice? Should it be avoided?
    private static void run(RMIServer server) {
	try {
	    RMICommunicationInterface rmiCommunicationInterface = (RMICommunicationInterface) UnicastRemoteObject.exportObject(server, 0);

	    // Bind the remote object's stub in the registry
	    Registry registry = LocateRegistry.getRegistry();
	    registry.rebind("RMICommunicationInterface", rmiCommunicationInterface);

	    //Log
	    log.log(Level.INFO, "RMI Server ready");
	    System.out.println("RMI Server ready");
	    
	} catch (Exception e) {
	    log.log(Level.SEVERE, "RMI Server exception occured - type:" + e.getClass() + " = " + e.getStackTrace());
	    System.err.println("RMI Server exception occured - type:" + e.getClass() + " = " + e.getStackTrace());
	}
    }

    
    //delegating the calls:
    
    @Override
    public boolean initializePIP() {
	return communication.initializePIP();
    }

    @Override
    public int initialRepresentation(int PID, String rep) {
	return communication.initialRepresentation(PID, rep);
    }

    @Override
    public int updatePIP(PDPEvent newEvent) {
	return communication.updatePIP(newEvent);
    }

    @Override
    public int representationRefinesData(int PID, String rep, int dataID, boolean strict) {
	return communication.representationRefinesData(PID, rep, dataID, strict);
    }

    @Override
    public UniqueIntList getDataIDbyRepresentation(int PID, String rep, boolean strict) {
	return communication.getDataIDbyRepresentation(PID, rep, strict);
    }

    @Override
    public void resetPIP() {
	communication.resetPIP();
    }

    @Override
    public String printModel() {
	return communication.printModel();
    }
}