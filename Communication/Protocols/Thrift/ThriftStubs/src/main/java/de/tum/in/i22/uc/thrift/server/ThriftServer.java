package de.tum.in.i22.uc.thrift.server;

import org.apache.thrift.TProcessor;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.settings.Settings;

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

	private final TServer _server;

	private int _port;

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
		_port = port;
		TServerTransport serverTransport;

		if (Settings.getInstance().isSslEnabled()) {
			TSSLTransportParameters params = new TSSLTransportParameters();
			params.setKeyStore(Settings.getInstance().getSslKeystore(), Settings.getInstance().getSslKeystorePassword(), null, null);
			serverTransport = TSSLTransportFactory.getServerSocket(_port, 0, null, params);
		}
		else {
			serverTransport = new TServerSocket(_port);
		}

		_server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));
		_logger.info("ThriftServer [{}] listening on port {} with SSL {}.", processor.getClass().toString().substring(processor.getClass().toString().lastIndexOf('.') + 1), _port, Settings.getInstance().isSslEnabled() ? "enabled" : "disabled");
	}

	@Override
	public void stop() {
		_server.stop();
	}

	@Override
	public void run() {
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
		return _server.isServing();
	}
}
