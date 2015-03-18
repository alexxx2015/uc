package de.tum.in.i22.uc.gui;

import java.io.IOException;
import java.util.HashMap;

import de.tum.in.i22.uc.cm.datatypes.basic.EventBasic;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.client.Any2PdpClient;
import de.tum.in.i22.uc.thrift.client.ThriftClientFactory;

public class TraceTimerStop {
public static void main(String[] args) {
	
	if ((args.length != 2)) {
		System.out.println("Usage: TraceTimerStop <host> <port>");
		System.exit(-1);
	}
	
	String host=args[0];
	int port=Integer.valueOf(args[1]);
	ThriftClientFactory tf= new ThriftClientFactory();
	
	Any2PdpClient pdp=tf.createAny2PdpClient(new IPLocation(host, port));
	
	try {
		pdp.connect();
	} catch (IOException e) {
		e.printStackTrace();
		System.exit(-1);
	}
	
	System.out.println("Sending StopRecording event to "+host+":"+port);
	pdp.notifyEventSync(new EventBasic("StopRecording",new HashMap<String,String>()));
	
	}
}
