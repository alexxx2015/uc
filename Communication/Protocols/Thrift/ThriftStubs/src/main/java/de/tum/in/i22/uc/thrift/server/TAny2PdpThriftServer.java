package de.tum.in.i22.uc.thrift.server;

import java.util.List;
import java.util.Map;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.requests.pdp.DeployPolicyURIPdpRequest;
import de.tum.in.i22.uc.cm.requests.pdp.DeployPolicyXMLPdpRequest;
import de.tum.in.i22.uc.cm.requests.pdp.ListMechanismsPdpRequest;
import de.tum.in.i22.uc.cm.requests.pdp.NotifyEventPdpRequest;
import de.tum.in.i22.uc.cm.requests.pdp.RegisterPxpPdpRequest;
import de.tum.in.i22.uc.cm.requests.pdp.RevokeMechanismPdpRequest;
import de.tum.in.i22.uc.cm.requests.pdp.RevokePolicyPdpRequest;
import de.tum.in.i22.uc.cm.server.IRequestHandler;
import de.tum.in.i22.uc.thrift.ThriftConverter;
import de.tum.in.i22.uc.thrift.types.TAny2Pdp;
import de.tum.in.i22.uc.thrift.types.TEvent;
import de.tum.in.i22.uc.thrift.types.TPxpSpec;
import de.tum.in.i22.uc.thrift.types.TResponse;
import de.tum.in.i22.uc.thrift.types.TStatus;


/**
 * Use {@link ThriftServerFactory} to create an instance.
 *
 * @author Enrico Lovat & Florian Kelbert
 *
 */
class TAny2PdpThriftServer extends ThriftServerHandler implements TAny2Pdp.Iface {
	private static Logger _logger = LoggerFactory.getLogger(TAny2PdpThriftServer.class);

	private final IRequestHandler _requestHandler;

	TAny2PdpThriftServer(IRequestHandler requestHandler) {
		_requestHandler = requestHandler;
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
