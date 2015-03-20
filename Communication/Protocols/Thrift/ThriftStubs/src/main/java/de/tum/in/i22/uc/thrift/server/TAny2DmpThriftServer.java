package de.tum.in.i22.uc.thrift.server;

import java.util.Set;

import org.apache.thrift.TException;

import de.tum.in.i22.uc.cm.interfaces.IAny2Dmp;
import de.tum.in.i22.uc.thrift.ThriftConverter;
import de.tum.in.i22.uc.thrift.types.TData;
import de.tum.in.i22.uc.thrift.types.TName;
import de.tum.in.i22.uc.thrift.types.TStatus;
import de.tum.in.i22.uc.thrift.types.TXmlPolicy;
import de.tum.in.i22.uc.thrift.types.TAny2Dmp;


/**
 * Use {@link ThriftServerFactory} to create an instance.
 *
 * @author Florian Kelbert
 *
 */
class TAny2DmpThriftServer extends ThriftServerHandler implements TAny2Dmp.Iface {

	private final IAny2Dmp _requestHandler;

	TAny2DmpThriftServer(IAny2Dmp requestHandler) {
		_requestHandler = requestHandler;
	}

	@Override
	public TStatus remoteTransfer(Set<TXmlPolicy> policies, String fromHost, TName containerName, Set<TData> data) throws TException {
		return ThriftConverter.toThrift(_requestHandler.remoteTransfer(
							ThriftConverter.fromThriftPolicySet(policies), 
							fromHost, 
							ThriftConverter.fromThrift(containerName),
							ThriftConverter.fromThriftDataSet(data)));
	}


	
}
