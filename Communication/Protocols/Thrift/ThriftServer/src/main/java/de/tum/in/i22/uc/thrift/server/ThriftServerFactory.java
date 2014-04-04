package de.tum.in.i22.uc.thrift.server;

import org.apache.thrift.TProcessor;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.server.IRequestHandler;
import de.tum.in.i22.uc.thrift.types.TAny2Any;
import de.tum.in.i22.uc.thrift.types.TAny2Pdp;
import de.tum.in.i22.uc.thrift.types.TAny2Pip;
import de.tum.in.i22.uc.thrift.types.TAny2Pmp;
import de.tum.in.i22.uc.thrift.types.TAny2Pxp;


/**
 * Factory for creating Thrift server instances.
 *
 * @author Florian Kelbert
 *
 */
public class ThriftServerFactory {
	private static Logger _logger = LoggerFactory.getLogger(ThriftServerFactory.class);

	/**
	 * Creates a Pdp Thrift server listening on the specified port and redirecting
	 * requests to the specified {@link IRequestHandler}.
	 *
	 * The server's run method will not yet be executed.
	 *
	 * @param port the port to listen on
	 * @param requestHandler the {@link IRequestHandler} to which requests are dispatched
	 * @return the server instance on success, null on failure
	 */
	public static IThriftServer createPdpThriftServer(int port, IRequestHandler requestHandler) {
		return createThriftServer(port,
				new TAny2Pdp.Processor<TAny2PdpThriftServer>(new TAny2PdpThriftServer(requestHandler)));
	}

	/**
	 * Creates a Pip Thrift server listening on the specified port and redirecting
	 * requests to the specified {@link IRequestHandler}.
	 *
	 * The server's run method will not yet be executed.
	 *
	 * @param port the port to listen on
	 * @param requestHandler the {@link IRequestHandler} to which requests are dispatched
	 * @return the server instance on success, null on failure
	 */
	public static IThriftServer createPipThriftServer(int port, IRequestHandler requestHandler) {
		return createThriftServer(port,
				new TAny2Pip.Processor<TAny2PipThriftServer>(new TAny2PipThriftServer(requestHandler)));
	}

	/**
	 * Creates a Pmp Thrift server listening on the specified port and redirecting
	 * requests to the specified {@link IRequestHandler}.
	 *
	 * The server's run method will not yet be executed.
	 *
	 * @param port the port to listen on
	 * @param requestHandler the {@link IRequestHandler} to which requests are dispatched
	 * @return the server instance on success, null on failure
	 */
	public static IThriftServer createPmpThriftServer(int port, IRequestHandler requestHandler) {
		return createThriftServer(port,
				new TAny2Pmp.Processor<TAny2PmpThriftServer>(new TAny2PmpThriftServer(requestHandler)));
	}

	/**
	 * Creates a Any Thrift server listening on the specified port and redirecting
	 * requests to the specified {@link IRequestHandler}.
	 *
	 * The server's run method will not yet be executed.
	 *
	 * @param port the port to listen on
	 * @param requestHandler the {@link IRequestHandler} to which requests are dispatched
	 * @return the server instance on success, null on failure
	 */
	public static IThriftServer createAnyThriftServer(int port, IRequestHandler requestHandler) {
		return createThriftServer(port,
				new TAny2Any.Processor<TAny2AnyThriftServer>(new TAny2AnyThriftServer(requestHandler)));
	}

	/**
	 * Creates a Pxp Thrift server listening on the specified port.
	 *
	 * The server's run method will not yet be executed.
	 *
	 * @param port the port to listen on
	 * @return the server instance on success, null on failure
	 */
	public static IThriftServer createPxpThriftServer(int port) {
		return createThriftServer(port,
				new TAny2Pxp.Processor<TAny2PxpThriftServer>(new TAny2PxpThriftServer()));
	}

	private static IThriftServer createThriftServer(int port, TProcessor processor) {
		ThriftServer server;

		try {
			server = new ThriftServer(port, processor);
		}
		catch (TTransportException e) {
			_logger.warn("Unable to start server: " + e);
			server = null;
		}

		return server;
	}
}
