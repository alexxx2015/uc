package de.tum.in.i22.uc.thrift.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.i22.in.uc.thrift.types.TAny2Pmp;
import de.tum.in.i22.uc.cm.client.PmpClientHandler;
import de.tum.in.i22.uc.cm.server.PmpProcessor;


/**
 * The client side of a remote Thrift {@link PmpProcessor} server.
 *
 * Create a instance of this class, connect it
 * (using {@link PmpClientHandler#connect()}) and
 * do calls on a remote {@link PmpProcessor}.
 *
 * @author Florian  Kelbert
 *
 */
public class ThriftPmpClientHandler extends PmpClientHandler<TAny2Pmp.Client> {
	protected static final Logger _logger = LoggerFactory.getLogger(ThriftPmpClientHandler.class);

	/**
	 * Creates a {@link ThriftPmpClientHandler} that will be
	 * connected (upon calling {@link PmpClientHandler#connect()})
	 * the the specified thrift server on the specified address/port.
	 *
	 * @param address the address of the remote point
	 * @param port the port of the remote point
	 */
	public ThriftPmpClientHandler(String address, int port) {
		super(new ThriftConnector<>(address, port, TAny2Pmp.Client.class));
	}
}
