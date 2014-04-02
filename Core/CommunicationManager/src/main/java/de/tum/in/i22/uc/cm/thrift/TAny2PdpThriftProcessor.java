package de.tum.in.i22.uc.cm.thrift;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.i22.in.uc.thrift.types.TEvent;
import de.tum.i22.in.uc.thrift.types.TPxpSpec;
import de.tum.i22.in.uc.thrift.types.TResponse;
import de.tum.i22.in.uc.thrift.types.TStatus;
import de.tum.i22.in.uc.thrift.types.TAny2Pdp;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.handlers.RequestHandler;
import de.tum.in.i22.uc.pdp.requests.NotifyEventPdpRequest;
import de.tum.in.i22.uc.pdp.requests.RegisterPxpPdpRequest;
import de.tum.in.i22.uc.thrift.ThriftConverter;
import de.tum.in.i22.uc.thrift.server.ThriftServerHandler;

public class TAny2PdpThriftProcessor extends ThriftServerHandler implements TAny2Pdp.Iface {
	protected static Logger _logger = LoggerFactory.getLogger(TAny2PdpThriftProcessor.class);

	private final RequestHandler _requestHandler;

	public TAny2PdpThriftProcessor() {
		_requestHandler = RequestHandler.getInstance();
	}


	@Override
	public TResponse notifyEventSync(TEvent e) throws TException {
		_logger.debug("TAny2Pdp: notifyEvent");

		IEvent ev = ThriftConverter.fromThrift(e);
		NotifyEventPdpRequest request = new NotifyEventPdpRequest(ev);

		_requestHandler.addRequest(request, this);
		waitForResponse(request);
		return ThriftConverter.toThrift(request.getResponse());
	}

	@Override
	public boolean registerPxp(TPxpSpec pxp) throws TException {
		_logger.debug("TAny2Pdp: registerPxp");
		RegisterPxpPdpRequest request = new RegisterPxpPdpRequest(ThriftConverter.fromThrift(pxp));
		_requestHandler.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public TStatus deployMechanism(String mechanism) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Pdp: deploymech");
		return TStatus.ERROR;
	}

	@Override
	public TStatus revokeMechanism1(String policyName) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Pdp: revokemech1");
		return TStatus.ERROR;
	}

	@Override
	public TStatus revokeMechanism2(String policyName, String mechName)
			throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Pdp: revokemech2");
		return TStatus.ERROR;
	}

	@Override
	public TStatus deployPolicy(String policyFilePath) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Pdp: deployPolicy");
		return TStatus.ERROR;
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
	public void notifyEventAsync(TEvent e) throws TException {
		// TODO Auto-generated method stub
	}
}
