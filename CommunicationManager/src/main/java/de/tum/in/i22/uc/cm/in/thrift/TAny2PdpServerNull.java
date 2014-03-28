package de.tum.in.i22.uc.cm.in.thrift;

import java.util.List;
import java.util.Map;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.i22.in.uc.cm.thrift.Event;
import de.tum.i22.in.uc.cm.thrift.Pxp;
import de.tum.i22.in.uc.cm.thrift.Response;
import de.tum.i22.in.uc.cm.thrift.StatusType;
import de.tum.i22.in.uc.cm.thrift.TAny2Pdp;

public class TAny2PdpServerNull implements TAny2Pdp.Iface {
	protected static Logger _logger = LoggerFactory.getLogger(TAny2PdpServerNull.class);

	@Override
	public Response notifyEvent(Event e) throws TException {
		_logger.warn("No Pdp thrift server running.");
		return null;
	}

	@Override
	public boolean registerPxp(Pxp pxp) throws TException {
		_logger.warn("No Pdp thrift server running.");
		return false;
	}

	@Override
	public StatusType deployMechanism(String mechanism) throws TException {
		_logger.warn("No Pdp thrift server running.");
		return null;
	}

	@Override
	public StatusType revokeMechanism1(String policyName) throws TException {
		_logger.warn("No Pdp thrift server running.");
		return null;
	}

	@Override
	public StatusType revokeMechanism2(String policyName, String mechName)
			throws TException {
		_logger.warn("No Pdp thrift server running.");
		return null;
	}

	@Override
	public StatusType deployPolicy(String policyFilePath) throws TException {
		_logger.warn("No Pdp thrift server running.");
		return null;
	}

	@Override
	public Map<String, List<String>> listMechanisms() throws TException {
		_logger.warn("No Pdp thrift server running.");
		return null;
	}
}
