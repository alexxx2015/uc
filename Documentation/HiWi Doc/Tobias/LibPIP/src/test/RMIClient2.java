package test;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

import main.RMICommunicationInterface;

//This Client prints the PIP. Output should match the representation in Client1.
public class RMIClient2 {


    private static Logger log = Logger.getLogger("RMI Server");

    // nothing to do here
    private RMIClient2() {
    }

    // should run on an other VM than the PIPStarter
    // you can pass the hostname as the first argument.
    public static void main(String[] args) {


	String host = (args.length < 1) ? null : args[0];
	try {
	    Registry registry = LocateRegistry.getRegistry(host);

	    @SuppressWarnings("unused")
	    RMICommunicationInterface rmiCommunicationInterface = (RMICommunicationInterface) registry.lookup("RMICommunicationInterface");

	    log.log(Level.INFO, "RMI Client Ready");
	    System.out.println("RMI Client Ready");

	    
	    System.out.println(rmiCommunicationInterface.printModel());

	} catch (Exception e) {
	    log.log(Level.SEVERE, "Client exception occured - type:" + e.getClass() + " = " + e.getStackTrace());
	    System.err.println("Client exception occured - type:" + e.getClass() + " = " + e.getStackTrace());
	}
    }
}