package de.tum.in.i22.pdp.cm.in.pep.thrift;

import org.apache.log4j.Logger;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;

public class ThriftServer {
	protected static Logger _logger = Logger.getLogger(ThriftServer.class);

	private static TServer server = null;

	public static void createListener(int thriftServerPort, int pepPort){
		ThriftServerHandler handler = new ThriftServerHandler(pepPort);
		ExtendedThriftConnector.Processor<ThriftServerHandler> processor =
				new ExtendedThriftConnector.Processor<ThriftServerHandler>(handler);
		TServerTransport serverTransport;
		try {
			serverTransport = new TServerSocket(thriftServerPort);
		} catch (TTransportException e) {
			serverTransport = null;
			e.printStackTrace();
		}
		server = new TSimpleServer(new Args(serverTransport).processor(processor));

		_logger.info("Server ThriftServer listening on port: "+thriftServerPort);
	}

	public static void start(){
		server.serve();
	}

	public static void stop(){
		server.stop();
	}

}
