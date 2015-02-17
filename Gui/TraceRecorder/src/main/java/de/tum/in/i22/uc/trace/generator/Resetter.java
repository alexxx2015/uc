package de.tum.in.i22.uc.trace.generator;

import java.io.IOException;

import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.distribution.client.Any2PdpClient;
import de.tum.in.i22.uc.thrift.client.ThriftClientFactory;


public class Resetter {

	static int port = 21003;
	static ThriftClientFactory tf = new ThriftClientFactory();
	static Location location = new IPLocation("localhost", port);
	static Any2PdpClient client = tf.createAny2PdpClient(location);
	
	public static void main(String[] args) {
		
		if (args.length>0){
			System.out.print("Reading port value from command line...");
			port=Integer.valueOf(args[1]);
			System.out.println("done!");
			System.out.println("New value for port: "+port);
		}
		
		try {
			System.out.print("Connecting to UC Framework ("+port+")...");
			client.connect();
			System.out.println("done!");

			System.out.print("Resetting UC Framework...");
			client.reset();
			System.out.println("done!");

		} catch (IOException e) {
			e.printStackTrace();
		}
		client.disconnect();
	}
}
