package de.tum.i22.monitor.connector;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;

public class ThriftServer {
	private static TServer server=null;
	
	public static void createListener(int thriftServerPort, int pepPort){
		ThriftServerHandler handler = new ThriftServerHandler(pepPort);
		PDPThriftConnector.Processor processor = new PDPThriftConnector.Processor(handler);
		TServerTransport serverTransport;
		try {
			serverTransport = new TServerSocket(thriftServerPort);
		} catch (TTransportException e) {
			serverTransport = null;
			e.printStackTrace();
		}
		server = new TSimpleServer(new Args(serverTransport).processor(processor));
			
		System.out.println("Server initialized on port "+thriftServerPort);
	}

	public static void startListener(){
		System.out.println("Starting the simple server...");
		server.serve();
	}
	
}
