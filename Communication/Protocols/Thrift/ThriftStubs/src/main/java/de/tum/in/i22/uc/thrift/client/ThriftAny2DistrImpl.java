package de.tum.in.i22.uc.thrift.client;

import java.util.Set;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.interfaces.IAny2Distr;
import de.tum.in.i22.uc.thrift.ThriftConverter;
import de.tum.in.i22.uc.thrift.types.TAny2Distr;

class ThriftAny2DistrImpl implements IAny2Distr {
	protected static final Logger _logger = LoggerFactory.getLogger(ThriftAny2DistrImpl.class);

	private final TAny2Distr.Client _handle;

	public ThriftAny2DistrImpl(TAny2Distr.Client handle) {
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

	
}
