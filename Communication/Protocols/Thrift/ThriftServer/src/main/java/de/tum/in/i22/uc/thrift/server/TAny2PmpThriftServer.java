package de.tum.in.i22.uc.thrift.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.server.IRequestHandler;
import de.tum.in.i22.uc.thrift.types.TAny2Pmp;


/**
 * Use {@link ThriftServerFactory} to create an instance.
 *
 * @author Florian Kelbert
 *
 */
class TAny2PmpThriftServer extends ThriftServerHandler implements TAny2Pmp.Iface {
	private static Logger _logger = LoggerFactory.getLogger(TAny2PmpThriftServer.class);

	private final IRequestHandler _requestHandler;

	TAny2PmpThriftServer(IRequestHandler requestHandler) {
		_requestHandler = requestHandler;
	}
}
