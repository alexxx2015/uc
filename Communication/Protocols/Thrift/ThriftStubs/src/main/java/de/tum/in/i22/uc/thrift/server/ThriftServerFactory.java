package de.tum.in.i22.uc.thrift.server;

import org.apache.thrift.TProcessor;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.interfaces.IAny2Dmp;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pdp;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pep;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pip;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pmp;
import de.tum.in.i22.uc.cm.interfaces.IAny2Prp;
import de.tum.in.i22.uc.thrift.types.TAny2Any;
import de.tum.in.i22.uc.thrift.types.TAny2Dmp;
import de.tum.in.i22.uc.thrift.types.TAny2Pdp;
import de.tum.in.i22.uc.thrift.types.TAny2Pep;
import de.tum.in.i22.uc.thrift.types.TAny2Pip;
import de.tum.in.i22.uc.thrift.types.TAny2Pmp;
import de.tum.in.i22.uc.thrift.types.TAny2Prp;
import de.tum.in.i22.uc.thrift.types.TAny2Pxp;
import de.tum.in.i22.uc.thrift.types.TPip2JPip;


/**
 * Factory for creating Thrift server instances.
 *
 * @author Florian Kelbert & Enrico Lovat
 *
 */
public class ThriftServerFactory {
	private static Logger _logger = LoggerFactory.getLogger(ThriftServerFactory.class);

	/**
	 * Creates a Pdp Thrift server listening on the specified port and redirecting
	 * requests to the specified {@link IAny2Pdp} handler.
	 *
	 * The server's run method will not yet be executed.
	 *
	 * @param port the port to listen on
	 * @param handler the {@link IAny2Pdp} to which requests are dispatched
	 * @return the server instance on success, null on failure
	 */
	public static IThriftServer createPdpThriftServer(int port, IAny2Pdp handler) {
		return createThriftServer(port,
				new TAny2Pdp.Processor<TAny2PdpThriftServer>(new TAny2PdpThriftServer(handler)));
	}

	/**
	 * Creates a Pip Thrift server listening on the specified port and redirecting
	 * requests to the specified {@link IAny2Pip} handler.
	 *
	 * The server's run method will not yet be executed.
	 *
	 * @param port the port to listen on
	 * @param handler the {@link IAny2Pip} handler to which requests are dispatched
	 * @return the server instance on success, null on failure
	 */
	public static IThriftServer createPipThriftServer(int port, IAny2Pip handler) {
		return createThriftServer(port,
				new TAny2Pip.Processor<TAny2PipThriftServer>(new TAny2PipThriftServer(handler)));
	}

	/**
	 * Creates a Pmp Thrift server listening on the specified port and redirecting
	 * requests to the specified {@link IAny2Pmp} handler.
	 *
	 * The server's run method will not yet be executed.
	 *
	 * @param port the port to listen on
	 * @param handler the {@link IAny2Pmp} handler to which requests are dispatched
	 * @return the server instance on success, null on failure
	 */
	public static IThriftServer createPmpThriftServer(int port, IAny2Pmp handler) {
		return createThriftServer(port,
				new TAny2Pmp.Processor<TAny2PmpThriftServer>(new TAny2PmpThriftServer(handler)));
	}
	
	public static IThriftServer createPmpArmeriaThriftServer(int port, IAny2Pmp handler){
//		TProcessor processor = new TAny2Pmp.Processor<TAny2PmpThriftServer>(new TAny2PmpThriftServer(handler));
		ThriftWebServer _return = new ThriftWebServer(port, new TAny2PmpThriftServer(handler));
		return _return;
	}


	/**
	 * Creates a Pep Thrift server listening on the specified port and redirecting
	 * requests to the specified {@link IAny2Pep} handler.
	 *
	 * The server's run method will not yet be executed.
	 *
	 * @param port the port to listen on
	 * @param handler the {@link IAny2Pep} handler to which requests are dispatched
	 * @return the server instance on success, null on failure
	 */
	public static IThriftServer createPepThriftServer(int port, IAny2Pep handler) {
		return createThriftServer(port,
				new TAny2Pep.Processor<TAny2PepThriftServer>(new TAny2PepThriftServer(handler)));
	}

	/**
	 * Creates a Any Thrift server listening on the specified port and redirecting
	 * requests to the specified Pdp/Pip/Pmp servers.
	 *
	 * The server's run method will not yet be executed.
	 *
	 * @param port the port to listen on
	 * @param pdpPort the local port to which Pdp requests will be redirected
	 * @param pipPort the local port to which Pip requests will be redirected
	 * @param pmpPort the local port to which Pmp requests will be redirected
	 * @return the server instance on success, null on failure
	 */
	public static IThriftServer createAnyThriftServer(int port, int pdpPort, int pipPort, int pmpPort) {
		return createThriftServer(port,
				new TAny2Any.Processor<TAny2AnyThriftServer>(new TAny2AnyThriftServer(pdpPort, pipPort, pmpPort)));
	}
	
	public static IThriftServer createDmpThriftServer(int port, IAny2Dmp handler) {
		return createThriftServer(port,
				new TAny2Dmp.Processor<TAny2DmpThriftServer>(new TAny2DmpThriftServer(handler)));
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

	public static <T extends TAny2Pxp.Iface> IThriftServer createPxpThriftServer(int port, T pxpProcessor) {
		return createThriftServer(port,
				new TAny2Pxp.Processor<T>(pxpProcessor));
	}
	

	
	public static IThriftServer createPrpThriftServer(int port, IAny2Prp handler){
		return createThriftServer(port, new TAny2Prp.Processor<TAny2PrpThriftServer>(new TAny2PrpThriftServer(handler)));
	}

	/**
	 * Creates a JPip Thrift server listening on the specified port.
	 *
	 * The server's run method will not yet be executed.
	 *
	 * @param port the port to listen on
	 * @return the server instance on success, null on failure
	 */
	public static IThriftServer createJPipThriftServer(int port) {
		return createThriftServer(port,
				new TPip2JPip.Processor<TAny2JPipThriftServer>(new TAny2JPipThriftServer()));
	}

	public static <T extends TPip2JPip.Iface> IThriftServer createJPipThriftServer(int port, T jPipProcessor) {
		return createThriftServer(port,
				new TPip2JPip.Processor<T>(jPipProcessor));
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
