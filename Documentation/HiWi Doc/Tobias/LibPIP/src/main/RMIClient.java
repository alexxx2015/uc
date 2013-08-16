package main;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Logger;

//this is just an example stub.
public class RMIClient {


    private static Logger log = Logger.getLogger("RMI Server");

    // nothing to do here
    private RMIClient() {
    }

    // should run on an other VM than the PIPStarter
    // you can pass the hostname as the first argument.
    public static void main(String[] args) {

	String host = (args.length < 1) ? null : args[0];
		/*try {
		    Registry registry = LocateRegistry.getRegistry(host);
	
		    @SuppressWarnings("unused")
		    RMICommunicationInterface rmiCI = (RMICommunicationInterface) registry.lookup("RMICommunicationInterface");
		    rmiCI.initializePIP();
		    log.log(Level.INFO, "RMI Client Ready");
		    System.out.println("RMI Client Ready");
	
		    // Now you got the RMICommunicationInterface on your Client. Do what
		    // you want here.
		    PDPEvent pdpEv = new PDPEvent();
		    pdpEv.action = "ReadField";
		    
		    rmiCI.updatePIP(pdpEv);
	
		} catch (Exception e) {
		    log.log(Level.SEVERE, "Client exception occured - type:" + e.getClass() + " = " + e.getStackTrace());
		    System.err.println("Client exception occured - type:" + e.getClass() + " = " + e.getStackTrace());
		}*/
		
		try {
			Socket clientSocket = new Socket("localhost", 10001);
			PrintStream out = new PrintStream(clientSocket.getOutputStream());
			out.print("ReadField");
			clientSocket.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }
}