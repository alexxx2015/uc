package de.tum.in.i22.uc.cm.thrift;

import java.util.List;
import java.util.Map;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.i22.in.uc.thrift.types.TAny2Pdp;
import de.tum.i22.in.uc.thrift.types.TEvent;
import de.tum.i22.in.uc.thrift.types.TPxpSpec;
import de.tum.i22.in.uc.thrift.types.TResponse;
import de.tum.i22.in.uc.thrift.types.TStatus;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.handlers.RequestHandler;
import de.tum.in.i22.uc.pdp.requests.DeployPolicyURIPdpRequest;
import de.tum.in.i22.uc.pdp.requests.DeployPolicyXMLPdpRequest;
import de.tum.in.i22.uc.pdp.requests.ListMechanismsPdpRequest;
import de.tum.in.i22.uc.pdp.requests.NotifyEventPdpRequest;
import de.tum.in.i22.uc.pdp.requests.RegisterPxpPdpRequest;
import de.tum.in.i22.uc.pdp.requests.RevokeMechanismPdpRequest;
import de.tum.in.i22.uc.pdp.requests.RevokePolicyPdpRequest;
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
		_logger.debug("TAny2Pdp: notifyEventSync");

		IEvent ev = ThriftConverter.fromThrift(e);
		NotifyEventPdpRequest request = new NotifyEventPdpRequest(ev);

		_requestHandler.addRequest(request, this);
		waitForResponse(request);
		return ThriftConverter.toThrift(request.getResponse());
	}

	@Override
	public void notifyEventAsync(TEvent e) throws TException {

		//identical to sync version, but discards the response
		
		_logger.debug("TAny2Pdp: notifyEventAsync");

		IEvent ev = ThriftConverter.fromThrift(e);
		NotifyEventPdpRequest request = new NotifyEventPdpRequest(ev);

		_requestHandler.addRequest(request, this);
		
		//do we need this in the async?
		waitForResponse(request);
		
	}


	@Override
	public boolean registerPxp(TPxpSpec pxp) throws TException {
		_logger.debug("TAny2Pdp: registerPxp");
		RegisterPxpPdpRequest request = new RegisterPxpPdpRequest(ThriftConverter.fromThrift(pxp));
		_requestHandler.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public TStatus revokePolicy(String policyName) throws TException {
		_logger.debug("TAny2Pdp: revokePolicy");
		RevokePolicyPdpRequest request = new RevokePolicyPdpRequest(policyName);
		_requestHandler.addRequest(request, this);
		return ThriftConverter.toThrift(waitForResponse(request));
	}

	@Override
	public TStatus revokeMechanism(String policyName, String mechName)
			throws TException {
		_logger.debug("TAny2Pdp: revokeMechanism");
		RevokeMechanismPdpRequest request = new RevokeMechanismPdpRequest(policyName, mechName);
		_requestHandler.addRequest(request, this);
		return ThriftConverter.toThrift(waitForResponse(request));
	}

	@Override
	public TStatus deployPolicyURI(String policyFilePath) throws TException {
		_logger.debug("TAny2Pdp: deployPolicy");
		DeployPolicyURIPdpRequest request = new DeployPolicyURIPdpRequest(policyFilePath);
		_requestHandler.addRequest(request, this);
		return ThriftConverter.toThrift(waitForResponse(request));
	}

	@Override
	public TStatus deployPolicyXML(String XMLPolicy) throws TException {
		_logger.debug("TAny2Pdp: deployPolicy");
		DeployPolicyXMLPdpRequest request = new DeployPolicyXMLPdpRequest(XMLPolicy);
		_requestHandler.addRequest(request, this);
		return ThriftConverter.toThrift(waitForResponse(request));
	}
	
	@Override
	public Map<String, List<String>> listMechanisms() throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Pdp: listMech");
		ListMechanismsPdpRequest request = new ListMechanismsPdpRequest();
		_requestHandler.addRequest(request, this);
		return waitForResponse(request);
	}

}
