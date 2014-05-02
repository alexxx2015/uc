package de.tum.in.i22.uc.thrift.client;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
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
	public IStatus deployPolicy(String policy) {
		try {
			TStatus status = _handle.deployPolicy(policy);
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


	@Override
	public IMechanism exportMechanismPmp(String par) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IStatus revokePolicyPmp(String policyName) {
		try {
			return ThriftConverter.fromThrift(_handle.revokePolicyPmp(policyName));
		} catch (TException e) {
			e.printStackTrace();
			return null;
		}

	}


	@Override
	public IStatus revokeMechanismPmp(String policyName, String mechName) {
		try {
			return ThriftConverter.fromThrift(_handle.revokeMechanismPmp(policyName, mechName));
		} catch (TException e) {
			e.printStackTrace();
			return null;
		}
	}


	@Override
	public IStatus deployPolicyURIPmp(String policyFilePath) {
		try {
			return ThriftConverter.fromThrift(_handle.deployPolicyURIPmp(policyFilePath));
		} catch (TException e) {
			e.printStackTrace();
			return null;
		}
	}


	@Override
	public IStatus deployPolicyXMLPmp(String XMLPolicy) {
		try {
			return ThriftConverter.fromThrift(_handle.deployPolicyXMLPmp(XMLPolicy));
		} catch (TException e) {
			e.printStackTrace();
			return null;
		}
	}


	@Override
	public Map<String, List<String>> listMechanismsPmp() {
		try {
			return _handle.listMechanismsPmp();
		} catch (TException e) {
			e.printStackTrace();
			return null;
		}
	}

}
