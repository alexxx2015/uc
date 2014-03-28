package de.tum.in.i22.uc.cm.in.thrift;

import java.util.ArrayList;
import java.util.HashMap;
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
import de.tum.in.i22.uc.cm.interfaces.IAny2Pdp;

public class TAny2PdpServerHandler implements TAny2Pdp.Iface {
	protected static Logger _logger = LoggerFactory.getLogger(TAny2PdpServerHandler.class);

	@Override
	public Response notifyEvent(Event e) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Pdp: notifyEvent");
		return new Response();
	}

	@Override
	public boolean registerPxp(Pxp pxp) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Pdp: registerPxp");
		return false;
	}

	@Override
	public StatusType deployMechanism(String mechanism) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Pdp: deploymech");
		return StatusType.ERROR;
	}

	@Override
	public StatusType revokeMechanism1(String policyName) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Pdp: revokemech1");
		return StatusType.ERROR;
	}

	@Override
	public StatusType revokeMechanism2(String policyName, String mechName)
			throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Pdp: revokemech2");
		return StatusType.ERROR;
	}

	@Override
	public StatusType deployPolicy(String policyFilePath) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Pdp: deployPolicy");
		return StatusType.ERROR;
	}

	@Override
	public Map<String, List<String>> listMechanisms() throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Pdp: listMech");
		HashMap<String, List<String>> m = new HashMap<String, List<String>>();
		List<String> l = new ArrayList<String>();
		l.add("test");
		m.put("mystring", l);
		return m;
	}
}
