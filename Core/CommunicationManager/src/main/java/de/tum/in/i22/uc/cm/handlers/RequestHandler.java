package de.tum.in.i22.uc.cm.handlers;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cassandra.CassandraDistributionManager;
import de.tum.in.i22.uc.cm.datatypes.basic.ConflictResolutionFlagBasic.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.basic.PxpSpec;
import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IResponse;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.IDistributionManager;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.LocalLocation;
import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.distribution.client.Any2PdpClient;
import de.tum.in.i22.uc.cm.distribution.client.Any2PipClient;
import de.tum.in.i22.uc.cm.distribution.client.Any2PmpClient;
import de.tum.in.i22.uc.cm.factories.IClientFactory;
import de.tum.in.i22.uc.cm.processing.IForwarder;
import de.tum.in.i22.uc.cm.processing.IRequestHandler;
import de.tum.in.i22.uc.cm.processing.PdpProcessor;
import de.tum.in.i22.uc.cm.processing.PipProcessor;
import de.tum.in.i22.uc.cm.processing.PmpProcessor;
import de.tum.in.i22.uc.cm.processing.Request;
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
import de.tum.in.i22.uc.pip.requests.FlattenStructurePipRequest;
import de.tum.in.i22.uc.pip.requests.GetContainersForDataPipRequest;
import de.tum.in.i22.uc.pip.requests.GetDataInContainerPipRequest;
import de.tum.in.i22.uc.pip.requests.GetIfModelPipRequest;
import de.tum.in.i22.uc.pip.requests.GetStructureOfPipRequest;
import de.tum.in.i22.uc.pip.requests.HasAllContainersPipRequest;
import de.tum.in.i22.uc.pip.requests.HasAllDataPipRequest;
import de.tum.in.i22.uc.pip.requests.HasAnyContainerPipRequest;
import de.tum.in.i22.uc.pip.requests.HasAnyDataPipRequest;
import de.tum.in.i22.uc.pip.requests.InitialRepresentationPipRequest;
import de.tum.in.i22.uc.pip.requests.IsSimulatingPipRequest;
import de.tum.in.i22.uc.pip.requests.NewInitialRepresentationPipRequest;
import de.tum.in.i22.uc.pip.requests.NewStructuredDataPipRequest;
import de.tum.in.i22.uc.pip.requests.StartSimulationPipRequest;
import de.tum.in.i22.uc.pip.requests.StopSimulationPipRequest;
import de.tum.in.i22.uc.pip.requests.UpdateInformationFlowSemanticsPipRequest;
import de.tum.in.i22.uc.pip.requests.UpdatePipRequest;
import de.tum.in.i22.uc.pip.requests.WhoHasDataPipRequest;
import de.tum.in.i22.uc.pmp.PmpHandler;
import de.tum.in.i22.uc.pmp.requests.DeployPolicyRawXmlPmpRequest;
import de.tum.in.i22.uc.pmp.requests.DeployPolicyURIPmpPmpRequest;
import de.tum.in.i22.uc.pmp.requests.DeployPolicyXMLPmpPmpRequest;
import de.tum.in.i22.uc.pmp.requests.InformRemoteDataFlowPmpRequest;
import de.tum.in.i22.uc.pmp.requests.ListMechanismsPmpPmpRequest;
import de.tum.in.i22.uc.pmp.requests.RevokeMechanismPmpPmpRequest;
import de.tum.in.i22.uc.pmp.requests.RevokePolicyPmpPmpRequest;
import de.tum.in.i22.uc.pmp.requests.SpecifyPolicyForPmpRequest;
import de.tum.in.i22.uc.thrift.client.ThriftClientFactory;


public class RequestHandler implements IRequestHandler, IForwarder {

	private static Logger _logger = LoggerFactory.getLogger(RequestHandler.class);

	private RequestQueueManager _requestQueueManager;

	private Set<Integer> _portsUsed;
	private Settings _settings;
	private IClientFactory _clientFactory;

	private PdpProcessor _pdp;
	private PipProcessor _pip;
	private PmpProcessor _pmp;

	private IDistributionManager _distributionManager;

	/**
	 * Creates a new {@link RequestHandler} by invoking
	 * {@link RequestHandler#RequestHandler(Location, Location, Location)}
	 * with all parameters set to {@link LocalLocation}.
	 */
	public RequestHandler() {
		this(LocalLocation.getInstance(), LocalLocation.getInstance(), LocalLocation.getInstance());
	}


	/**
	 * Creates a new RequestHandler. The parameters specify where the corresponding
	 * components are run. If a location is an instance of {@link LocalLocation}, a
	 * new local handler will be started. Otherwise, the {@link RequestHandler} will
	 * connect to the remote location and make use of that remote handler.
	 *
	 * @param pdpLocation
	 * @param pipLocation
	 * @param pmpLocation
	 */
	public RequestHandler(Location pdpLocation, Location pipLocation, Location pmpLocation) {
		init(pdpLocation, pipLocation, pmpLocation);
	}


	private void init(Location pdpLocation, Location pipLocation, Location pmpLocation) {
		_settings = Settings.getInstance();
		_portsUsed = portsInUse();
		_clientFactory = new ThriftClientFactory();

		/* Important: Creation of the handlers depends on properly initialized _portsUsed */
		_pdp = createPdpHandler(pdpLocation);
		_pip = createPipHandler(pipLocation);
		_pmp = createPmpHandler(pmpLocation);

		while (_pdp == null || _pip == null || _pmp == null) {
			try {
				int sleep = _settings.getConnectionAttemptInterval();
				_logger.info("One of the connections failed. Trying again in " + sleep + " milliseconds.");
				Thread.sleep(sleep);
			} catch (InterruptedException e) {	}

			if (_pdp == null) _pdp = createPdpHandler(pdpLocation);
			if (_pip == null) _pip = createPipHandler(pipLocation);
			if (_pmp == null) _pmp = createPmpHandler(pmpLocation);
		}

		_distributionManager = new CassandraDistributionManager();

		_pdp.init(_pip, _pmp, _distributionManager);
		_pip.init(_pdp, _pmp, _distributionManager);
		_pmp.init(_pip, _pdp, _distributionManager);

		_distributionManager.init(_pdp, _pip, _pmp);

		_requestQueueManager = new RequestQueueManager(_pdp, _pip, _pmp);
		new Thread(_requestQueueManager).start();
	}

	private PdpProcessor createPdpHandler(Location loc) {
		switch (loc.getLocation()) {
			case IP:
				IPLocation iploc = (IPLocation) loc;
				if (isConnectionAllowed(iploc)) {
					Any2PdpClient pdp = _clientFactory.createAny2PdpClient(loc);
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
				return new PdpHandler();
		}

		return null;
	}

	private PmpProcessor createPmpHandler(Location loc) {
		switch (loc.getLocation()) {
			case IP:
				IPLocation iploc = (IPLocation) loc;
				if (isConnectionAllowed(iploc)) {
					Any2PmpClient pmp = _clientFactory.createAny2PmpClient(loc);
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
				return new PmpHandler();
		}

		return null;
	}

	private PipProcessor createPipHandler(Location loc) {
		switch (loc.getLocation()) {
			case IP:
				IPLocation iploc = (IPLocation) loc;
				if (isConnectionAllowed(iploc)) {
					Any2PipClient pip = _clientFactory.createAny2PipClient(loc);
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
				return new PipHandler();
		}

		return null;
	}

	private Set<Integer> portsInUse() {
		Set<Integer> result = new HashSet<>();

		if (_settings.isPdpListenerEnabled()) result.add(_settings.getPdpListenerPort());
		if (_settings.isPipListenerEnabled()) result.add(_settings.getPipListenerPort());
		if (_settings.isPmpListenerEnabled()) result.add(_settings.getPmpListenerPort());
		if (_settings.isAnyListenerEnabled()) result.add(_settings.getAnyListenerPort());

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


	@Override
	public void reset() {
		_requestQueueManager.stop();
		init(_pdp.getLocation(),_pip.getLocation(),_pmp.getLocation());
	}
	
	@Override
	public void stop() {
		_requestQueueManager.stop();
		this._pdp.stop();
		this._pip.stop();
		this._pmp.stop();		
	}


	@Override
	public void notifyEventAsync(IEvent event) {
		NotifyEventPdpRequest request = new NotifyEventPdpRequest(event);
		_requestQueueManager.addRequest(request, null);
	}

	@Override
	public IResponse notifyEventSync(IEvent event) {
		NotifyEventPdpRequest request = new NotifyEventPdpRequest(event,true);
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
	public IStatus deployPolicyXML(XmlPolicy XMLPolicy) {
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
	public IMechanism exportMechanismPmp(String par) {
		// TODO Not yet implemented
		return null;
	}

	@Override
	public IStatus revokePolicyPmp(String policyName) {
		RevokePolicyPmpPmpRequest request = new RevokePolicyPmpPmpRequest(policyName);
		_requestQueueManager.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public IStatus revokeMechanismPmp(String policyName, String mechName) {
		RevokeMechanismPmpPmpRequest request = new RevokeMechanismPmpPmpRequest(policyName, mechName);
		_requestQueueManager.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public IStatus deployPolicyURIPmp(String policyFilePath) {
		DeployPolicyURIPmpPmpRequest request = new DeployPolicyURIPmpPmpRequest(policyFilePath);
		_requestQueueManager.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public IStatus deployPolicyXMLPmp(XmlPolicy XMLPolicy) {
		DeployPolicyXMLPmpPmpRequest request = new DeployPolicyXMLPmpPmpRequest(XMLPolicy);
		_requestQueueManager.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public Map<String, List<String>> listMechanismsPmp() {
		ListMechanismsPmpPmpRequest request = new ListMechanismsPmpPmpRequest();
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
	public IStatus updateInformationFlowSemantics(IPipDeployer deployer, File jarFile, EConflictResolution conflictResolutionFlag) {
		UpdateInformationFlowSemanticsPipRequest request = new UpdateInformationFlowSemanticsPipRequest(deployer, jarFile, conflictResolutionFlag);
		_requestQueueManager.addRequest(request, this);
		return waitForResponse(request);
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
	public Set<IData> getDataInContainer(IName containerName) {
		GetDataInContainerPipRequest request = new GetDataInContainerPipRequest(containerName);
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
	public boolean hasAllContainers(Set<IName> names) {
		HasAllContainersPipRequest request = new HasAllContainersPipRequest(names);
		_requestQueueManager.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public boolean hasAnyContainer(Set<IName> names) {
		HasAnyContainerPipRequest request = new HasAnyContainerPipRequest(names);
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
	public IData newInitialRepresentation(IName containerName) {
		NewInitialRepresentationPipRequest request = new NewInitialRepresentationPipRequest(containerName);
		_requestQueueManager.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public IStatus informRemoteDataFlow(Location srcLocation, Location dstLocation, Set<IData> dataflow) {
		InformRemoteDataFlowPmpRequest request = new InformRemoteDataFlowPmpRequest(srcLocation, dstLocation, dataflow);
		_requestQueueManager.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public Set<Location> whoHasData(Set<IData> data, int recursionDepth) {
		WhoHasDataPipRequest request = new WhoHasDataPipRequest(data, recursionDepth);
		_requestQueueManager.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public String getIfModel() {
		GetIfModelPipRequest request = new GetIfModelPipRequest();
		_requestQueueManager.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public IData newStructuredData(Map<String, Set<IData>> structure) {
		NewStructuredDataPipRequest request = new NewStructuredDataPipRequest(structure);
		_requestQueueManager.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public Map<String, Set<IData>> getStructureOf(IData data) {
		GetStructureOfPipRequest request = new GetStructureOfPipRequest(data);
		_requestQueueManager.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public Set<IData> flattenStructure(IData data) {
		FlattenStructurePipRequest request = new FlattenStructurePipRequest(data);
		_requestQueueManager.addRequest(request, this);
		return waitForResponse(request);
	}


	@Override
	public IStatus deployPolicyRawXMLPmp(String xml) {
		DeployPolicyRawXmlPmpRequest request = new DeployPolicyRawXmlPmpRequest(xml);
		_requestQueueManager.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public Set<XmlPolicy> getPolicies(IData data) {
		GetPoliciesPmpRequest request = new GetPoliciesPmpRequest(data);
		_requestQueueManager.addRequest(request, this);
		return waitForResponse(request);
	}

	@Override
	public void processEventAsync(IEvent pepEvent) {
		this.notifyEventAsync(pepEvent);
	}

	@Override
	public IResponse processEventSync(IEvent pepEvent) {
		return this.notifyEventSync(pepEvent);
	}


	@Override
	public IStatus specifyPolicyFor(Set<IContainer> representations,
			String dataClass) {
		SpecifyPolicyForPmpRequest request = new SpecifyPolicyForPmpRequest(representations,dataClass);
		_requestQueueManager.addRequest(request, this);
		return waitForResponse(request);
	}

}
