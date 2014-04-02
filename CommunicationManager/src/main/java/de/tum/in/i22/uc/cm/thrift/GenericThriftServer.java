package de.tum.in.i22.uc.cm.thrift;

import org.apache.thrift.TProcessor;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericThriftServer implements Runnable {
	protected static Logger _logger = LoggerFactory.getLogger(GenericThriftServer.class);

	private TServer _server = null;

	private boolean _started = false;

	public GenericThriftServer(int port, TProcessor processor){
		TServerTransport serverTransport;

		try {
			serverTransport = new TServerSocket(port);
		} catch (TTransportException e) {
			serverTransport = null;
			e.printStackTrace();
		}

		_server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));

		_logger.info("ThriftServer listening on port: " + port);
		_started = true;
	}

	public void stop(){
		_server.stop();
	}

	@Override
	public void run() {
		_server.serve();
	}

	public boolean started() {
		return _started;
	}
}
