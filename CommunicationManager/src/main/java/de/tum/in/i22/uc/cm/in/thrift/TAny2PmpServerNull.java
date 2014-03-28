package de.tum.in.i22.uc.cm.in.thrift;

import java.util.List;
import java.util.Map;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.i22.in.uc.cm.thrift.StatusType;
import de.tum.i22.in.uc.cm.thrift.TAny2Pmp;


public class TAny2PmpServerNull implements TAny2Pmp.Iface {
	protected static Logger _logger = LoggerFactory.getLogger(TAny2PmpServerNull.class);

	@Override
	public StatusType deployMechanismPmp(String mechanism) throws TException {
		_logger.warn("No Pmp thrift server running.");
		return null;
	}

	@Override
	public StatusType revokeMechanism1Pmp(String policyName) throws TException {
		_logger.warn("No Pmp thrift server running.");
		return null;
	}

	@Override
	public StatusType revokeMechanism2Pmp(String policyName, String mechName)
			throws TException {
		_logger.warn("No Pmp thrift server running.");
		return null;
	}

	@Override
	public StatusType deployPolicyPmp(String policyFilePath) throws TException {
		_logger.warn("No Pmp thrift server running.");
		return null;
	}

	@Override
	public Map<String, List<String>> listMechanismsPmp() throws TException {
		_logger.warn("No Pmp thrift server running.");
		return null;
	}

}
