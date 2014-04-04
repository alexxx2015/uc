package de.tum.in.i22.uc.thrift.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.server.IRequestHandler;
import de.tum.in.i22.uc.thrift.types.TAny2Pmp;


/**
 * Use {@link ThriftProcessorFactory} to create an instance.
 *
 * @author Florian Kelbert
 *
 */
class TAny2PmpThriftProcessor extends ThriftServerHandler implements TAny2Pmp.Iface {
	private static Logger _logger = LoggerFactory.getLogger(TAny2PmpThriftProcessor.class);

	private final IRequestHandler _requestHandler;

	TAny2PmpThriftProcessor(IRequestHandler requestHandler) {
		_requestHandler = requestHandler;
	}
}
