package main;
import java.io.IOException;

import helper.IPIPCommunication;
import helper.PIPTCPServer;


public class PIPStarter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Starting PIP ...");
		
		IPIPCommunication pipHandler = new PIPHandler();
		pipHandler.initializePIP();
		
		PIPTCPServer server = new PIPTCPServer(pipHandler);
		
		
		try {
			server.startTCPServer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		//this should start the whole RMI Stuff.
		//If you don't want to use the same pipHandler as for the TCP Server just delete the Parameter, the RMIServer will create its own Handler.
		RMIServer.run(pipHandler);

		/* /Library/Java/JavaVirtualMachines/1.7.0.jdk/Contents/Home/bin/java -agentlib:jdwp=transport=dt_socket,suspend=y,address=localhost:54626 -Dfile.encoding=MacRoman -classpath /Users/ladmin/Documents/workspace/uc4cloud/PIP/bin main.PIPStarter*/
	}

}
