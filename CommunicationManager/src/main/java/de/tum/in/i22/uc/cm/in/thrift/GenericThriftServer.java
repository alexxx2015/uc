package de.tum.in.i22.uc.cm.in.thrift;

import org.apache.thrift.TProcessor;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericThriftServer<H> implements Runnable {
	protected static Logger _logger = LoggerFactory.getLogger(GenericThriftServer.class);

	private TServer _server = null;

	public GenericThriftServer(int port, H handler, TProcessor processor){	
		TServerTransport serverTransport;
		try {
			serverTransport = new TServerSocket(port);
		} catch (TTransportException e) {
			serverTransport = null;
			e.printStackTrace();
		}
		_server = new TSimpleServer(new Args(serverTransport).processor(processor));

		_logger.info("Server GenericThriftServer listening on port: " + port);
	}

	public void stop(){
		_server.stop();
	}

	@Override
	public void run() {
		_server.serve();
	}

}
