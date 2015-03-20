package de.tum.in.i22.uc.thrift.client;

import java.util.Set;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.AtomicOperator;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IOperator;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.interfaces.IAny2Dmp;
import de.tum.in.i22.uc.cm.pip.RemoteDataFlowInfo;
import de.tum.in.i22.uc.thrift.ThriftConverter;
import de.tum.in.i22.uc.thrift.types.TAny2Dmp;

class ThriftAny2DmpImpl implements IAny2Dmp {
	protected static final Logger _logger = LoggerFactory.getLogger(ThriftAny2DmpImpl.class);

	private final TAny2Dmp.Client _handle;

	public ThriftAny2DmpImpl(TAny2Dmp.Client handle) {
		_handle = handle;
	}

	@Override
	public IStatus remoteTransfer(Set<XmlPolicy> policies, String fromHost, IName containerName, Set<IData> data) {
		try {
			return ThriftConverter.fromThrift(
					_handle.remoteTransfer(ThriftConverter.toThriftPoliciesSet(policies),
							fromHost,
							ThriftConverter.toThrift(containerName),
							ThriftConverter.toThriftDataSet(data)));
		} catch (TException e) {
			e.printStackTrace();
		}
		return new StatusBasic(EStatus.ERROR);
	}

	@Override
	public void notify(IOperator operator, boolean endOfTimestep) {
		throw new UnsupportedOperationException("This method must only be invoked locally.");
	}

	@Override
	public void setFirstTick(String policyName, String mechanismName, long firstTick) {
		throw new UnsupportedOperationException("This method must only be invoked locally.");
	}

	@Override
	public long getFirstTick(String policyName, String mechanismName) {
		throw new UnsupportedOperationException("This method must only be invoked locally.");
	}

	@Override
	public boolean wasNotifiedAtTimestep(AtomicOperator operator, long timestep) {
		throw new UnsupportedOperationException("This method must only be invoked locally.");
	}

	@Override
	public int howOftenNotifiedAtTimestep(AtomicOperator operator, long timestep) {
		throw new UnsupportedOperationException("This method must only be invoked locally.");
	}

	@Override
	public int howOftenNotifiedSinceTimestep(AtomicOperator operator, long timestep) {
		throw new UnsupportedOperationException("This method must only be invoked locally.");
	}

	@Override
	public void doDataTransfer(RemoteDataFlowInfo dataflow) {
		throw new UnsupportedOperationException("This method must only be invoked locally.");
	}

	@Override
	public IPLocation getResponsibleLocation(String ip) {
		throw new UnsupportedOperationException("This method must only be invoked locally.");
	}

	@Override
	public void register(XmlPolicy policy, String from) {
		throw new UnsupportedOperationException("This method must only be invoked locally.");
	}

	@Override
	public void deregister(String policyName, IPLocation location) {
		throw new UnsupportedOperationException("This method must only be invoked locally.");
	}	
}
