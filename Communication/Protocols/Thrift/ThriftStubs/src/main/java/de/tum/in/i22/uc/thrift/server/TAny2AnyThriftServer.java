package de.tum.in.i22.uc.thrift.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.requests.pdp.DeployPolicyURIPdpRequest;
import de.tum.in.i22.uc.cm.requests.pdp.DeployPolicyXMLPdpRequest;
import de.tum.in.i22.uc.cm.requests.pdp.NotifyEventPdpRequest;
import de.tum.in.i22.uc.cm.requests.pdp.RegisterPxpPdpRequest;
import de.tum.in.i22.uc.cm.requests.pdp.RevokeMechanismPdpRequest;
import de.tum.in.i22.uc.cm.requests.pdp.RevokePolicyPdpRequest;
import de.tum.in.i22.uc.cm.server.IRequestHandler;
import de.tum.in.i22.uc.thrift.ThriftConverter;
import de.tum.in.i22.uc.thrift.types.TAny2Any;
import de.tum.in.i22.uc.thrift.types.TContainer;
import de.tum.in.i22.uc.thrift.types.TData;
import de.tum.in.i22.uc.thrift.types.TEvent;
import de.tum.in.i22.uc.thrift.types.TName;
import de.tum.in.i22.uc.thrift.types.TPxpSpec;
import de.tum.in.i22.uc.thrift.types.TResponse;
import de.tum.in.i22.uc.thrift.types.TStatus;


/**
 * Use {@link ThriftServerFactory} to create an instance.
 *
 * @author Enrico Lovat & Florian Kelbert
 *
 */
class TAny2AnyThriftServer extends ThriftServerHandler implements TAny2Any.Iface {
	private static Logger _logger = LoggerFactory.getLogger(TAny2AnyThriftServer.class);

	private final IRequestHandler _requestHandler;

	TAny2AnyThriftServer(IRequestHandler requestHandler) {
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

		_requestHandler.addRequest(request, null);
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
		HashMap<String, List<String>> m = new HashMap<String, List<String>>();
		List<String> l = new ArrayList<String>();
		l.add("test");
		m.put("mystring", l);
		return m;
	}

	@Override
	public TStatus initialRepresentation(TContainer container, TData data)
			throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: initrep");
		return TStatus.ERROR;
	}

	@Override
	public boolean hasAllData(Set<TData> data) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: hasAllData");
		return false;
	}

	@Override
	public boolean hasAnyData(Set<TData> data) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: hasAnyData");
		return false;
	}

	@Override
	public boolean hasAllContainers(Set<TContainer> container) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: hasAllContainers");
		return false;
	}

	@Override
	public boolean hasAnyContainer(Set<TContainer> container) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: hasAnycontainers");
		return false;
	}

	@Override
	public TStatus update(TEvent event) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: notifyActualEvent");
		return TStatus.ERROR;
	}

	@Override
	public TStatus notifyDataTransfer(TName containerName, Set<TData> data)
			throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: notifyDatatransfer");
		return TStatus.ERROR;
	}

	@Override
	public boolean evaluatePredicateSimulatingNextState(TEvent event,
			String predicate) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: evaluatePredicateSimulatingNextState");
		return false;
	}

	@Override
	public boolean evaluatePredicatCurrentState(String predicate)
			throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: evaluatePredicateCurrentState");
		return false;
	}

	@Override
	public Set<TContainer> getContainerForData(TData data) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: getContainerforData");
		return new HashSet<TContainer>();
	}

	@Override
	public Set<TData> getDataInContainer(TContainer container) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: getDataInContainer");
		return new HashSet<TData>();
	}

	@Override
	public TStatus startSimulation() throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: startsimulation");
		return TStatus.ERROR;
	}

	@Override
	public TStatus stopSimulation() throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: stopSimulation");
		return TStatus.ERROR;
	}

	@Override
	public boolean isSimulating() throws TException {
		// TODO Auto-generated method stub
		_logger.debug("TAny2Any: isSimulating");
		return false;
	}


}
