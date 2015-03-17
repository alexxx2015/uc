package de.tum.in.i22.uc.thrift.server;

import java.util.Set;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.thrift.ThriftConverter;
import de.tum.in.i22.uc.thrift.types.TAny2Distr;
import de.tum.in.i22.uc.thrift.types.TData;
import de.tum.in.i22.uc.thrift.types.TName;
import de.tum.in.i22.uc.thrift.types.TStatus;
import de.tum.in.i22.uc.thrift.types.TXmlPolicy;
import de.tum.in.i22.uc.cm.distribution.IDistributionManagerPublic;
import de.tum.in.i22.uc.cm.processing.IRequestHandler;


/**
 * Use {@link ThriftServerFactory} to create an instance.
 *
 * @author Florian Kelbert
 *
 */
class TAny2DistrThriftServer extends ThriftServerHandler implements TAny2Distr.Iface {
	private static Logger _logger = LoggerFactory.getLogger(TAny2DistrThriftServer.class);

	private final IDistributionManagerPublic _requestHandler;

	TAny2DistrThriftServer(IDistributionManagerPublic requestHandler) {
		_requestHandler = requestHandler;
	}

	@Override
	public TStatus remoteTransfer(Set<TXmlPolicy> policies, String fromHost,
			TName containerName, Set<TData> data) throws TException {
		return ThriftConverter.toThrift(_requestHandler.remoteTransfer(
							ThriftConverter.fromThriftPolicySet(policies), 
							fromHost, 
							ThriftConverter.fromThrift(containerName),
							ThriftConverter.fromThriftDataSet(data)));
	}


	
}
