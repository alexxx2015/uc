package de.tum.in.i22.uc.cm.thrift;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.i22.in.uc.thrift.types.Event;
import de.tum.i22.in.uc.thrift.types.Pxp;
import de.tum.i22.in.uc.thrift.types.Response;
import de.tum.i22.in.uc.thrift.types.StatusType;
import de.tum.i22.in.uc.thrift.types.TAny2Pdp;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.handlers.RequestHandler;
import de.tum.in.i22.uc.pdp.requests.NotifyEventPdpRequest;
import de.tum.in.i22.uc.thrift.ThriftConverter;

public class TAny2PdpThriftProcessor extends ThriftServerHandler implements TAny2Pdp.Iface {
	protected static Logger _logger = LoggerFactory.getLogger(TAny2PdpThriftProcessor.class);

	private final RequestHandler _requestHandler;

	public TAny2PdpThriftProcessor() {
		_requestHandler = RequestHandler.getInstance();
	}


	@Override
	public Response notifyEventSync(Event e) throws TException {
		_logger.debug("TAny2Pdp: notifyEvent");

		IEvent ev = ThriftConverter.fromThrift(e);
		NotifyEventPdpRequest request = new NotifyEventPdpRequest(ev);

		_requestHandler.addRequest(request, this);
		waitForResponse(request);
		return ThriftConverter.toThrift(request.getResponse());
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

	@Override
	public void notifyEventAsync(Event e) throws TException {
		// TODO Auto-generated method stub
	}
}
