package de.tum.in.i22.uc.cm.handlers;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.basic.EventBasic;
import de.tum.in.i22.uc.cm.basic.PxpSpec;
import de.tum.in.i22.uc.cm.client.PdpClientHandler;
import de.tum.in.i22.uc.cm.client.PipClientHandler;
import de.tum.in.i22.uc.cm.client.PmpClientHandler;
import de.tum.in.i22.uc.cm.datatypes.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.server.IForwarder;
import de.tum.in.i22.uc.cm.server.IRequestHandler;
import de.tum.in.i22.uc.cm.server.PdpProcessor;
import de.tum.in.i22.uc.cm.server.PipProcessor;
import de.tum.in.i22.uc.cm.server.PmpProcessor;
import de.tum.in.i22.uc.cm.server.Request;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pdp.PdpHandler;
import de.tum.in.i22.uc.pdp.requests.DeployPolicyURIPdpRequest;
import de.tum.in.i22.uc.pdp.requests.DeployPolicyXMLPdpRequest;
import de.tum.in.i22.uc.pdp.requests.ListMechanismsPdpRequest;
import de.tum.in.i22.uc.pdp.requests.NotifyEventPdpRequest;
import de.tum.in.i22.uc.pdp.requests.RegisterPxpPdpRequest;
import de.tum.in.i22.uc.pdp.requests.RevokeMechanismPdpRequest;
import de.tum.in.i22.uc.pdp.requests.RevokePolicyPdpRequest;
import de.tum.in.i22.uc.pip.PipHandler;
import de.tum.in.i22.uc.pip.requests.EvaluatePredicateCurrentStatePipRequest;
import de.tum.in.i22.uc.pip.requests.EvaluatePredicateSimulatingNextStatePipRequest;
import de.tum.in.i22.uc.pip.requests.GetContainersForDataPipRequest;
import de.tum.in.i22.uc.pip.requests.GetDataInContainerPipRequest;
import de.tum.in.i22.uc.pip.requests.HasAllContainersPipRequest;
import de.tum.in.i22.uc.pip.requests.HasAllDataPipRequest;
import de.tum.in.i22.uc.pip.requests.HasAnyContainerPipRequest;
import de.tum.in.i22.uc.pip.requests.HasAnyDataPipRequest;
import de.tum.in.i22.uc.pip.requests.InitialRepresentationPipRequest;
import de.tum.in.i22.uc.pip.requests.IsSimulatingPipRequest;
import de.tum.in.i22.uc.pip.requests.StartSimulationPipRequest;
import de.tum.in.i22.uc.pip.requests.StopSimulationPipRequest;
import de.tum.in.i22.uc.pip.requests.UpdatePipRequest;
import de.tum.in.i22.uc.pmp.PmpHandler;
import de.tum.in.i22.uc.pmp.requests.InformRemoteDataFlowPmpRequest;
import de.tum.in.i22.uc.pmp.requests.ReceivePoliciesPmpRequest;
import de.tum.in.i22.uc.thrift.client.ThriftClientHandlerFactory;

public class RequestHandler implements IRequestHandler, IForwarder {

	private static Logger _logger = LoggerFactory.getLogger(RequestHandler.class);

	private final RequestQueueManager _requestQueueManager;

	private final Set<Integer> _portsUsed;
	private final Settings _settings;
	private final ThriftClientHandlerFactory thriftClientFactory;

	public RequestHandler(Location pdpLocation, Location pipLocation, Location pmpLocation) {
		_settings = Settings.getInstance();
		_portsUsed = portsInUse();
		thriftClientFactory = new ThriftClientHandlerFactory();

		/* Important: Creation of the handlers depends on properly initialized _portsUsed */
		PdpProcessor pdp = createPdpHandler(pdpLocation);
		PipProcessor pip = createPipHandler(pipLocation);
		PmpProcessor pmp = createPmpHandler(pmpLocation);

		while (pdp == null || pip == null || pmp == null) {
			if (pdp == null) pdp = createPdpHandler(pdpLocation);
			if (pip == null) pip = createPipHandler(pipLocation);
			if (pmp == null) pmp = createPmpHandler(pmpLocation);

			try {
				int sleep = _settings.getConnectionAttemptInterval();
				_logger.info("One of the connections failed. Trying again in " + sleep + " milliseconds.");
				Thread.sleep(sleep);
			} catch (InterruptedException e) {	}
		}

		pdp.init(pip, pmp);
		pip.init(pdp, pmp);
		pmp.init(pip, pdp);

		_requestQueueManager = new RequestQueueManager(pdp, pip, pmp);
		new Thread(_requestQueueManager).start();
	}

	private PdpProcessor createPdpHandler(Location loc) {
		switch (loc.getLocation()) {
			case IP:
				IPLocation iploc = (IPLocation) loc;
				if (isConnectionAllowed(iploc)) {
					PdpClientHandler pdp = thriftClientFactory.createPdpClientHandler(loc);
					try {
						pdp.connect();
					} catch (Exception e) {
						_logger.error("Unable to connect to remote Pdp.", e);
						return null;
					}
					return pdp;
				}
				break;
			case LOCAL:
			default:
				return new PdpHandler();
		}

		return null;
	}

	private PmpProcessor createPmpHandler(Location loc) {
		switch (loc.getLocation()) {
			case IP:
				IPLocation iploc = (IPLocation) loc;
				if (isConnectionAllowed(iploc)) {
					PmpClientHandler pmp = thriftClientFactory.createPmpClientHandler(loc);
					try {
						pmp.connect();
					} catch (Exception e) {
						_logger.error("Unable to connect to remote Pmp.", e);
						return null;
					}
					return pmp;
				}
				break;
			case LOCAL:
			default:
				return new PmpHandler();
		}

		return null;
	}

	private PipProcessor createPipHandler(Location loc) {
		switch (loc.getLocation()) {
			case IP:
				IPLocation iploc = (IPLocation) loc;
				if (isConnectionAllowed(iploc)) {
					PipClientHandler pip = thriftClientFactory.createPipClientHandler(loc);
					try {
						pip.connect();
					} catch (Exception e) {
						_logger.error("Unable to connect to remote Pip.", e);
						return null;
					}
					return pip;
				}
				break;
			case LOCAL:
			default:
				return new PipHandler();
		}

		return null;
	}

	private Set<Integer> portsInUse() {
		Set<Integer> result = new HashSet<>();

		if (_settings.isPdpListenerEnabled()) {
			result.add(_settings.getPdpListenerPort());
		}

		if (_settings.isPipListenerEnabled()) {
			result.add(_settings.getPipListenerPort());
		}

		if (_settings.isPmpListenerEnabled()) {
			result.add(_settings.getPmpListenerPort());
		}

		if (_settings.isAnyListenerEnabled()) {
			result.add(_settings.getAnyListenerPort());
		}

		return result;
	}

	private boolean isConnectionAllowed(IPLocation loc) {
		RuntimeException rte = new RuntimeException("Not allowed to forward PIP/PMP/PDP requests to " + loc + ". "
				+ "Rethink your setup/configuration.");

		InetAddress addr;

		try {
			addr = InetAddress.getByName(loc.getHost());
		} catch (UnknownHostException e) {
			throw rte;
		}

		if ((addr.isAnyLocalAddress() || addr.isLoopbackAddress()) && _portsUsed.contains(loc.getPort())) {
			throw rte;
		}

		return true;
	}

	@Override
	public void notifyEventAsync(IEvent event) {
		NotifyEventPdpRequest request = new NotifyEventPdpRequest(event);
		_requestQueueManager.addRequest(request, null);
	}

	@Override
	public IResponse notifyEventSync(IEvent event) {
		NotifyEventPdpRequest request = new NotifyEventPdpRequest(event);
		_requestQueueManager.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public IMechanism exportMechanism(String par) {
		// TODO Not yet implemented
		return null;
	}

	@Override
	public IStatus revokePolicy(String policyName) {
		RevokePolicyPdpRequest request = new RevokePolicyPdpRequest(policyName);
		_requestQueueManager.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public IStatus revokeMechanism(String policyName, String mechName) {
		RevokeMechanismPdpRequest request = new RevokeMechanismPdpRequest(policyName, mechName);
		_requestQueueManager.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public IStatus deployPolicyURI(String policyFilePath) {
		DeployPolicyURIPdpRequest request = new DeployPolicyURIPdpRequest(policyFilePath);
		_requestQueueManager.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public IStatus deployPolicyXML(String XMLPolicy) {
		DeployPolicyXMLPdpRequest request = new DeployPolicyXMLPdpRequest(XMLPolicy);
		_requestQueueManager.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public Map<String, List<String>> listMechanisms() {
		ListMechanismsPdpRequest request = new ListMechanismsPdpRequest();
		_requestQueueManager.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public boolean registerPxp(PxpSpec pxp) {
		RegisterPxpPdpRequest request = new RegisterPxpPdpRequest(pxp);
		_requestQueueManager.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public IStatus updateInformationFlowSemantics(IPipDeployer deployer, File jarFile, EConflictResolution flagForTheConflictResolution) {
		// TODO not yet implemented
		return null;
	}

	@Override
	public boolean evaluatePredicateSimulatingNextState(IEvent event, String predicate) {
		EvaluatePredicateSimulatingNextStatePipRequest request = new EvaluatePredicateSimulatingNextStatePipRequest(event, predicate);
		_requestQueueManager.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public boolean evaluatePredicateCurrentState(String predicate) {
		EvaluatePredicateCurrentStatePipRequest request = new EvaluatePredicateCurrentStatePipRequest(predicate);
		_requestQueueManager.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public Set<IContainer> getContainersForData(IData data) {
		GetContainersForDataPipRequest request = new GetContainersForDataPipRequest(data);
		_requestQueueManager.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public Set<IData> getDataInContainer(IContainer container) {
		GetDataInContainerPipRequest request = new GetDataInContainerPipRequest(container);
		_requestQueueManager.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public IStatus startSimulation() {
		StartSimulationPipRequest request = new StartSimulationPipRequest();
		_requestQueueManager.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public IStatus stopSimulation() {
		StopSimulationPipRequest request = new StopSimulationPipRequest();
		_requestQueueManager.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public boolean isSimulating() {
		IsSimulatingPipRequest request = new IsSimulatingPipRequest();
		_requestQueueManager.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public boolean hasAllData(Set<IData> data) {
		HasAllDataPipRequest request = new HasAllDataPipRequest(data);
		_requestQueueManager.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public boolean hasAnyData(Set<IData> data) {
		HasAnyDataPipRequest request = new HasAnyDataPipRequest(data);
		_requestQueueManager.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public boolean hasAllContainers(Set<IContainer> container) {
		HasAllContainersPipRequest request = new HasAllContainersPipRequest(container);
		_requestQueueManager.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public boolean hasAnyContainer(Set<IContainer> container) {
		HasAnyContainerPipRequest request = new HasAnyContainerPipRequest(container);
		_requestQueueManager.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public IStatus update(IEvent event) {
		UpdatePipRequest request = new UpdatePipRequest(event);
		_requestQueueManager.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public IStatus initialRepresentation(IName containerName, Set<IData> data) {
		InitialRepresentationPipRequest request = new InitialRepresentationPipRequest(containerName, data);
		_requestQueueManager.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public IStatus receivePolicies(Set<String> policies) {
		ReceivePoliciesPmpRequest request = new ReceivePoliciesPmpRequest(policies);
		_requestQueueManager.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public IStatus informRemoteDataFlow(Location location, Set<IData> dataflow) {
		InformRemoteDataFlowPmpRequest request = new InformRemoteDataFlowPmpRequest(location, dataflow);
		_requestQueueManager.addRequest(request, this);
		return waitForResponse(request);
	}


	/**
	 * Waits for the specified request to be processed.
	 * Once the corresponding response is ready, execution
	 * continues and the request's response is returned.
	 *
	 * @param request the request for whose processing/response is waited for.
	 * @return the response corresponding to the request.
	 */
	private <T> T waitForResponse(Request<T,?> request) {
		T result = null;

		synchronized (this) {
			while (!request.responseReady()) {
				try {
					wait();
				} catch (InterruptedException e) {	}
			}
			result = request.getResponse();
		}

		return result;
	}

	@Override
	public void forwardResponse(Request<?,?> request, Object response) {
		synchronized (this) {
			request.setResponse(response);
			notifyAll();
		}
	}

	public Object notifyEvent(String name, String[] paramKeys, String[] paramValues, boolean isActual) throws InterruptedException {
		IEvent event = assembleEvent(name, paramKeys, paramValues, isActual);
		Object response = null;

		if (event != null) {
			if (isActual) {
				notifyEventAsync(event);
			}
			else {
				notifyEventSync(event);
			}
		}

		return response;
	}

	private static IEvent assembleEvent(String name, String[] paramKeys, String[] paramValues, boolean isActual) {
		if (name == null || paramKeys == null || paramValues == null
				|| paramKeys.length != paramValues.length || name.isEmpty()) {
			return null;
		}

		Map<String,String> params = new HashMap<String,String>();
		for (int i = 0; i < paramKeys.length; i++) {
			params.put(paramKeys[i], paramValues[i]);
		}

		return new EventBasic(name, params, isActual);
	}
}
