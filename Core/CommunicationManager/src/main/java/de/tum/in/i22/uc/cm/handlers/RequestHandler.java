package de.tum.in.i22.uc.cm.handlers;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.cm.basic.PxpSpec;
import de.tum.in.i22.uc.cm.datatypes.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.server.IForwarder;
import de.tum.in.i22.uc.cm.server.IRequestHandler;
import de.tum.in.i22.uc.cm.server.Request;
import de.tum.in.i22.uc.pdp.requests.DeployPolicyURIPdpRequest;
import de.tum.in.i22.uc.pdp.requests.DeployPolicyXMLPdpRequest;
import de.tum.in.i22.uc.pdp.requests.ListMechanismsPdpRequest;
import de.tum.in.i22.uc.pdp.requests.NotifyEventPdpRequest;
import de.tum.in.i22.uc.pdp.requests.RegisterPxpPdpRequest;
import de.tum.in.i22.uc.pdp.requests.RevokeMechanismPdpRequest;
import de.tum.in.i22.uc.pdp.requests.RevokePolicyPdpRequest;
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

public class RequestHandler implements IRequestHandler, IForwarder {
	private final RequestQueueManager _requestQueue;

	public RequestHandler() {
		_requestQueue = new RequestQueueManager();
		new Thread(_requestQueue).start();
	}

	@Override
	public void notifyEventAsync(IEvent event) {
		NotifyEventPdpRequest request = new NotifyEventPdpRequest(event);
		_requestQueue.addRequest(request, null);
	}

	@Override
	public IResponse notifyEventSync(IEvent event) {
		NotifyEventPdpRequest request = new NotifyEventPdpRequest(event);
		_requestQueue.addRequest(request, this);
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
		_requestQueue.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public IStatus revokeMechanism(String policyName, String mechName) {
		RevokeMechanismPdpRequest request = new RevokeMechanismPdpRequest(policyName, mechName);
		_requestQueue.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public IStatus deployPolicyURI(String policyFilePath) {
		DeployPolicyURIPdpRequest request = new DeployPolicyURIPdpRequest(policyFilePath);
		_requestQueue.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public IStatus deployPolicyXML(String XMLPolicy) {
		DeployPolicyXMLPdpRequest request = new DeployPolicyXMLPdpRequest(XMLPolicy);
		_requestQueue.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public Map<String, List<String>> listMechanisms() {
		ListMechanismsPdpRequest request = new ListMechanismsPdpRequest();
		_requestQueue.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public boolean registerPxp(PxpSpec pxp) {
		RegisterPxpPdpRequest request = new RegisterPxpPdpRequest(pxp);
		_requestQueue.addRequest(request, this);
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
		_requestQueue.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public boolean evaluatePredicateCurrentState(String predicate) {
		EvaluatePredicateCurrentStatePipRequest request = new EvaluatePredicateCurrentStatePipRequest(predicate);
		_requestQueue.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public Set<IContainer> getContainersForData(IData data) {
		GetContainersForDataPipRequest request = new GetContainersForDataPipRequest(data);
		_requestQueue.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public Set<IData> getDataInContainer(IContainer container) {
		GetDataInContainerPipRequest request = new GetDataInContainerPipRequest(container);
		_requestQueue.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public IStatus startSimulation() {
		StartSimulationPipRequest request = new StartSimulationPipRequest();
		_requestQueue.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public IStatus stopSimulation() {
		StopSimulationPipRequest request = new StopSimulationPipRequest();
		_requestQueue.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public boolean isSimulating() {
		IsSimulatingPipRequest request = new IsSimulatingPipRequest();
		_requestQueue.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public boolean hasAllData(Set<IData> data) {
		HasAllDataPipRequest request = new HasAllDataPipRequest(data);
		_requestQueue.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public boolean hasAnyData(Set<IData> data) {
		HasAnyDataPipRequest request = new HasAnyDataPipRequest(data);
		_requestQueue.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public boolean hasAllContainers(Set<IContainer> container) {
		HasAllContainersPipRequest request = new HasAllContainersPipRequest(container);
		_requestQueue.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public boolean hasAnyContainer(Set<IContainer> container) {
		HasAnyContainerPipRequest request = new HasAnyContainerPipRequest(container);
		_requestQueue.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public IStatus update(IEvent event) {
		UpdatePipRequest request = new UpdatePipRequest(event);
		_requestQueue.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public IStatus initialRepresentation(IName containerName, Set<IData> data) {
		InitialRepresentationPipRequest request = new InitialRepresentationPipRequest(containerName, data);
		_requestQueue.addRequest(request, this);
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
}
