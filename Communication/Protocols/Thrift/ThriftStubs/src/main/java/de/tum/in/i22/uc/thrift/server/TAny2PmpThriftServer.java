package de.tum.in.i22.uc.thrift.server;

import java.util.Set;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.server.IRequestHandler;
import de.tum.in.i22.uc.thrift.ThriftConverter;
import de.tum.in.i22.uc.thrift.types.TAny2Pmp;
import de.tum.in.i22.uc.thrift.types.TData;
import de.tum.in.i22.uc.thrift.types.TStatus;


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

	@Override
	public TStatus remotePolicyTransfer(Set<String> policies) throws TException {
		_logger.debug("TAny2Pmp: remotePolicyTransfer");
		IStatus result = _requestHandler.receivePolicies(policies);
		return ThriftConverter.toThrift(result);
	}

	@Override
	public TStatus informRemoteDataFlow(String srcAddress, int srcPort, String dstAddress, int dstPort, Set<TData> data) throws TException {
		Set<IData> d = ThriftConverter.fromThriftDataSet(data);
		IStatus status = _requestHandler.informRemoteDataFlow(new IPLocation(srcAddress, srcPort), new IPLocation(dstAddress, dstPort), d);
		return ThriftConverter.toThrift(status);
	}
}
