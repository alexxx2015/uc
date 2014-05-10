package de.tum.in.i22.uc.thrift.server;

import org.apache.thrift.TProcessor;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main Thrift server class.
 *
 * Use {@link ThriftServerFactory} to get instances.
 *
 * @author Florian Kelbert
 *
 */
class ThriftServer implements IThriftServer {
	protected static Logger _logger = LoggerFactory.getLogger(ThriftServer.class);

	private TServer _server = null;

	private boolean _started = false;

	/**
	 * Creates a new multithreaded {@link ThriftServer},
	 * listening on the specified port and implementing the specified processor.
	 *
	 * The server will not yet be started.
	 *
	 * @param port the port
	 * @param processor the processor
	 * @throws TTransportException
	 */
	ThriftServer(int port, TProcessor processor) throws TTransportException {
		TServerTransport serverTransport = new TServerSocket(port);

		_server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));

		_logger.info("ThriftServer listening on port: " + port);
	}	

	@Override
	public void stop() {
		_started=false;
		_server.stop();
	}

	@Override
	public void run() {
		_started = true;
		_server.serve();
	}

	/**
	 * Whether this server has been started, i.e. whether
	 * {@link ThriftServer#run()} has been executed.
	 *
	 * @return true if the server has been started
	 */
	@Override
	public boolean started() {
		return _started;
	}
}
