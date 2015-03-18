package de.tum.in.i22.uc.trace.generator;

import java.io.IOException;
import java.util.Collections;

import de.tum.in.i22.uc.cm.datatypes.basic.DataBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.distribution.client.Any2PdpClient;
import de.tum.in.i22.uc.cm.distribution.client.Any2PipClient;
import de.tum.in.i22.uc.thrift.client.ThriftClientFactory;

public class Resetter {

	static int numFiles = 10;
	static int port = 21004;
	static String prefix = "FILE_distr1.C:\\home\\florian\\struct\\file0";
	static String host = "localhost";
	static ThriftClientFactory tf = new ThriftClientFactory();
	static Location location;
	static Any2PdpClient client;
	static Any2PipClient pip;

	public static void main(String[] args) {

		if (args.length > 0) {
			System.out.print("Reading host value from command line...");
			host = args[0];
			System.out.println("done!");
			System.out.println("New value for host: " + host);
		}
		if (args.length > 1) {
			System.out.print("Reading port value from command line...");
			port = Integer.valueOf(args[1]);
			System.out.println("done!");
			System.out.println("New value for port: " + port);
		}

		if (args.length > 3) {
			System.out.print("Reading number of initial files from command line...");
			numFiles = Integer.valueOf(args[2]);
			prefix = args[3];
			System.out.println("done!");
			System.out.println("New value for numFiles: " + numFiles);
			System.out.println("New value for prefix: " + prefix);
		}

		location = new IPLocation(host, port);
		client = tf.createAny2PdpClient(location);
		location = new IPLocation(host, port);
		pip = tf.createAny2PipClient(location);
		
		try {
			System.out.print("Connecting to UC Framework (" + host + ":" + port + ")...");
			client.connect();
			pip.connect();
			System.out.println("done!");

			System.out.print("Resetting UC Framework...");
			client.reset();
			System.out.println("done!");

			System.out.println("Initializing data elements...");

			String pad = "%0" + Math.round(Math.ceil(Math.log10(numFiles))) + "d";

			for (int i = 0; i < numFiles; i++) {
				String d = String.format("D" + pad, i);
				String c = String.format(prefix + pad + ".txt", i);
				System.out.print("Adding mapping "+c+" --> "+d+" ...");
				pip.initialRepresentation(new NameBasic(c), Collections.singleton(new DataBasic(d)));
				System.out.println("done!");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		pip.disconnect();
		client.disconnect();
	}
}
