package de.tum.in.i22.uc.thrift.client;

import java.util.Set;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pmp;
import de.tum.in.i22.uc.thrift.ThriftConverter;
import de.tum.in.i22.uc.thrift.types.TAny2Pmp;
import de.tum.in.i22.uc.thrift.types.TStatus;

class ThriftAny2PmpImpl implements IAny2Pmp {
	protected static final Logger _logger = LoggerFactory.getLogger(ThriftAny2PmpImpl.class);

	private final TAny2Pmp.Client _handle;

	public ThriftAny2PmpImpl(TAny2Pmp.Client handle) {
		_handle = handle;
	}


	@Override
	public IStatus receivePolicies(Set<String> policies) {
		try {
			TStatus status = _handle.remotePolicyTransfer(policies);
			return ThriftConverter.fromThrift(status);
		} catch (TException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public IStatus informRemoteDataFlow(Location srcLocation, Location dstLocation, Set<IData> data) {
		// TODO Auto-generated method stub
		return null;
	}

}
