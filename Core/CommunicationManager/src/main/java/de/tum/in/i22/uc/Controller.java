package de.tum.in.i22.uc;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;

import de.tum.in.i22.uc.cm.commandLineOptions.CommandLineOptions;
import de.tum.in.i22.uc.cm.datatypes.basic.ConflictResolutionFlagBasic.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.basic.PxpSpec;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IMechanism;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IPtpResponse;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IResponse;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.handlers.RequestHandler;
import de.tum.in.i22.uc.cm.processing.IRequestHandler;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.thrift.server.IThriftServer;
import de.tum.in.i22.uc.thrift.server.ThriftServerFactory;

public class Controller implements IRequestHandler  {
	private static Logger _logger = LoggerFactory.getLogger(Controller.class);

	private IThriftServer _pdpServer;
	private IThriftServer _pipServer;
	private IThriftServer _pmpServer;
	private IThriftServer _anyServer;

	private String[] _args = null;
	protected IRequestHandler _requestHandler;

	public Controller() {
		this(new String[0]);
	}

	public Controller(String[] args) {
		_args = args;
		loadProperties(_args);
	}

	public static void main(String[] args) {
		Controller c = new Controller(args);
		if (c.start()) {
			// lock forever
			lock();
		} else {
			_logger.error("Unable to start UC infrastructure. Exiting.");
			System.exit(1);
		}
	}

	public boolean start() {
		// If ports are available...
		if (arePortsAvailable()) {
			// ..start UC infrastructure
			startUC();
			return true;
		}

		// ..otherwise return false
		return false;
	}

	private void startUC() {

		_requestHandler = new RequestHandler(Settings.getInstance().getPdpLocation(),
				Settings.getInstance().getPipLocation(), Settings.getInstance().getPmpLocation());

		_logger.info("Deploying initial policies ...");
		deployInitialPolicies();
		_logger.info("done.");

		_logger.info("Starting up thrift servers");
		startListeners(_requestHandler);
		do {
			try {
				_logger.info("... waiting ...");
				Thread.sleep(100);
			} catch (InterruptedException e) {
				_logger.info(e.getMessage());
			}
		} while (!isStarted());
		_logger.info("Done. Thrift servers started.");

	}

	public boolean isStarted() {
		System.out.println("Calling isStarted: " + _pdpServer + "," + _pipServer+ "," +  _pmpServer+ "," +  _anyServer+ "," +  _pdpServer.started() + "," + _pipServer.started() + "," +  _pmpServer.started() + "," +  _anyServer.started());
		return (!Settings.getInstance().isPdpListenerEnabled() || _pdpServer != null && _pdpServer.started())
				&& (!Settings.getInstance().isPipListenerEnabled() || _pipServer != null && _pipServer.started())
				&& (!Settings.getInstance().isPmpListenerEnabled() || _pmpServer != null && _pmpServer.started())
				&& (!Settings.getInstance().isAnyListenerEnabled() || _anyServer != null && _anyServer.started());
	}

	/**
	 * Deploys the initial policies specified in the settings.
	 */
	private void deployInitialPolicies() {
		for (String uri : Settings.getInstance().getPmpInitialPolicies()) {
			IStatus status;

			/*
			 * Just calling deployPolicyURIPmp(uri)
			 * is _not_ OK, since this might be a remote
			 * method invocation and the URI would be resolved remotely.
			 * Instead, read the file locally and deploy as follows: -FK-
			 */
			try {
				status = deployPolicyRawXMLPmp(Files.toString(new File(uri), Charset.defaultCharset()));
			} catch (Exception e) {
				status = new StatusBasic(EStatus.ERROR, e.getMessage());
			}

			_logger.info(status.isStatus(EStatus.OKAY) ? "Deployed policy " + uri + " successfully." : "Error deploying policy " + uri + ": " + status.getErrorMessage() + ".");
		}
	}

	@Override
	public void stop() {
		if (_pdpServer != null)
			_pdpServer.stop();
		if (_pipServer != null)
			_pipServer.stop();
		if (_pmpServer != null)
			_pmpServer.stop();
		if (_anyServer != null)
			_anyServer.stop();
		if(_requestHandler != null)
			_requestHandler.stop();
	}

	private void startListeners(IRequestHandler requestHandler) {
		if (Settings.getInstance().isPdpListenerEnabled()) {
			_pdpServer = ThriftServerFactory.createPdpThriftServer(
					Settings.getInstance().getPdpListenerPort(), requestHandler);

			if (_pdpServer != null) {
				new Thread(_pdpServer).start();
			}
		}

		if (Settings.getInstance().isPipListenerEnabled()) {
			_pipServer = ThriftServerFactory.createPipThriftServer(
					Settings.getInstance().getPipListenerPort(), requestHandler);

			if (_pipServer != null) {
				new Thread(_pipServer).start();
			}
		}

		if (Settings.getInstance().isPmpListenerEnabled()) {
			_pmpServer = ThriftServerFactory.createPmpThriftServer(
					Settings.getInstance().getPmpListenerPort(), requestHandler);

			if (_pmpServer != null) {
				new Thread(_pmpServer).start();
			}
		}

		if (Settings.getInstance().isAnyListenerEnabled()) {
			_anyServer = ThriftServerFactory.createAnyThriftServer(
					Settings.getInstance().getAnyListenerPort(),
					Settings.getInstance().getPdpListenerPort(),
					Settings.getInstance().getPipListenerPort(),
					Settings.getInstance().getPmpListenerPort());

			if (_anyServer != null) {
				new Thread(_anyServer).start();
			}
		}
	}

	private boolean arePortsAvailable() {
		boolean isPdpPortAvailable = !Settings.getInstance().isPdpListenerEnabled()
				|| isPortAvailable(Settings.getInstance().getPdpListenerPort());
		boolean isPipPortAvailable = !Settings.getInstance().isPipListenerEnabled()
				|| isPortAvailable(Settings.getInstance().getPipListenerPort());
		boolean isPmpPortAvailable = !Settings.getInstance().isPmpListenerEnabled()
				|| isPortAvailable(Settings.getInstance().getPmpListenerPort());
		boolean isAnyPortAvailable = !Settings.getInstance().isAnyListenerEnabled()
				|| isPortAvailable(Settings.getInstance().getAnyListenerPort());

		if (!isPdpPortAvailable || !isPipPortAvailable || !isPmpPortAvailable
				|| !isAnyPortAvailable) {
			_logger.error("One of the ports is not available.");
			_logger.error("\nAre you sure you are not running another instance on the same ports?");
			return false;
		}
		return true;
	}

	private boolean isPortAvailable(int port) {
		Socket s = null;
		try {
			s = new Socket("localhost", port);
			// If the code makes it this far without an exception it means
			// that the port is available
			_logger.debug("Port " + port + " is not available");
			return false;
		} catch (IOException e) {
			_logger.debug("Port " + port + " is available");
			return true;
		} finally {
			if (s != null) {
				try {
					s.close();
				} catch (IOException e) {
					throw new RuntimeException("You should handle this error.",
							e);
				}
			}
		}
	}

	static void loadProperties(String[] args) {
		CommandLine cl = CommandLineOptions.init(args);
		if (cl != null && cl.hasOption(CommandLineOptions.OPTION_PROPFILE)) {
			Settings.setPropertiesFile(cl
					.getOptionValue(CommandLineOptions.OPTION_PROPFILE));
		}

		if (cl != null && cl.hasOption(CommandLineOptions.OPTION_LOCAL_PDP_LISTENER_PORT)) {
			Settings.getInstance()
			.loadSetting(
					CommandLineOptions.OPTION_LOCAL_PDP_LISTENER_PORT_LONG,
					Integer.valueOf(cl.getOptionValue(CommandLineOptions.OPTION_LOCAL_PDP_LISTENER_PORT)));
		}
		if (cl != null && cl.hasOption(CommandLineOptions.OPTION_LOCAL_PIP_LISTENER_PORT)) {
			Settings.getInstance()
			.loadSetting(
					CommandLineOptions.OPTION_LOCAL_PIP_LISTENER_PORT_LONG,
					Integer.valueOf(cl.getOptionValue(CommandLineOptions.OPTION_LOCAL_PIP_LISTENER_PORT)));
		}
		if (cl != null && cl.hasOption(CommandLineOptions.OPTION_LOCAL_PMP_LISTENER_PORT)) {
			Settings.getInstance()
			.loadSetting(
					CommandLineOptions.OPTION_LOCAL_PMP_LISTENER_PORT_LONG,
					Integer.valueOf(cl.getOptionValue(CommandLineOptions.OPTION_LOCAL_PMP_LISTENER_PORT)));
		}
		if (cl != null && cl.hasOption(CommandLineOptions.OPTION_LOCAL_ANY_LISTENER_PORT)) {
			Settings.getInstance()
			.loadSetting(
					CommandLineOptions.OPTION_LOCAL_ANY_LISTENER_PORT_LONG,
					Integer.valueOf(cl.getOptionValue(CommandLineOptions.OPTION_LOCAL_ANY_LISTENER_PORT)));
		}
	}

	protected static void lock() {
		Object lock = new Object();
		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void resetOnlyRequestHandler() {
		synchronized (this) {
			_requestHandler.reset();
		}
	}

	@Override
	public void reset() {
		synchronized (this) {
			stop();
			resetOnlyRequestHandler();
			start();
		}
	}

	@Override
	public void notifyEventAsync(IEvent pepEvent) {
		_requestHandler.notifyEventAsync(pepEvent);
	}

	@Override
	public IResponse notifyEventSync(IEvent pepEvent) {
		return _requestHandler.notifyEventSync(pepEvent);
	}

	@Override
	public boolean registerPxp(PxpSpec pxp) {
		return _requestHandler.registerPxp(pxp);
	}

	@Override
	public IMechanism exportMechanism(String par) {
		return _requestHandler.exportMechanism(par);
	}

	@Override
	public IStatus revokePolicy(String policyName) {
		return _requestHandler.revokePolicy(policyName);
	}

	@Override
	public IStatus revokeMechanism(String policyName, String mechName) {
		return _requestHandler.revokeMechanism(policyName, mechName);
	}

	@Override
	public IStatus deployPolicyXML(XmlPolicy XMLPolicy) {
		return _requestHandler.deployPolicyXML(XMLPolicy);
	}

	@Override
	public Map<String, Set<String>> listMechanisms() {
		return _requestHandler.listMechanisms();
	}

	@Override
	public boolean evaluatePredicateSimulatingNextState(IEvent eventToSimulate,
			String predicate) {
		return _requestHandler.evaluatePredicateSimulatingNextState(eventToSimulate, predicate);
	}

	@Override
	public boolean evaluatePredicateCurrentState(String predicate) {
		return _requestHandler.evaluatePredicateCurrentState(predicate);
	}

	@Override
	public Set<IContainer> getContainersForData(IData data) {
		return _requestHandler.getContainersForData(data);
	}

	@Override
	public Set<IData> getDataInContainer(IName containerName) {
		return _requestHandler.getDataInContainer(containerName);
	}

	@Override
	public IStatus startSimulation() {
		return _requestHandler.startSimulation();
	}

	@Override
	public IStatus stopSimulation() {
		return _requestHandler.stopSimulation();
	}

	@Override
	public boolean isSimulating() {
		return _requestHandler.isSimulating();
	}

	@Override
	public IStatus update(IEvent updateEvent) {
		return _requestHandler.update(updateEvent);
	}

	@Override
	public IStatus updateInformationFlowSemantics(IPipDeployer deployer,
			File jarFile, EConflictResolution conflictResolutionFlag) {
		return _requestHandler.updateInformationFlowSemantics(deployer, jarFile, conflictResolutionFlag);
	}

	@Override
	public IStatus initialRepresentation(IName containerName, Set<IData> data) {
		return _requestHandler.initialRepresentation(containerName, data);
	}

	@Override
	public IData newInitialRepresentation(IName containerName) {
		return _requestHandler.newInitialRepresentation(containerName);
	}

	@Override
	public IMechanism exportMechanismPmp(String par) {
		return _requestHandler.exportMechanismPmp(par);
	}

	@Override
	public IStatus revokePolicyPmp(String policyName) {
		return _requestHandler.revokePolicyPmp(policyName);
	}

	@Override
	public IStatus revokeMechanismPmp(String policyName, String mechName) {
		return _requestHandler.revokeMechanismPmp(policyName, mechName);
	}

	@Override
	public IStatus deployPolicyURIPmp(String policyFilePath) {
		return _requestHandler.deployPolicyURIPmp(policyFilePath);
	}

	@Override
	public IStatus deployPolicyXMLPmp(XmlPolicy XMLPolicy) {
		return _requestHandler.deployPolicyXMLPmp(XMLPolicy);
	}

	@Override
	public Map<String, Set<String>> listMechanismsPmp() {
		return _requestHandler.listMechanismsPmp();
	}

	@Override
	public String getIfModel() {
		return _requestHandler.getIfModel();
	}

	@Override
	public IData newStructuredData(Map<String, Set<IData>> structure) {
		return _requestHandler.newStructuredData(structure);
	}

	@Override
	public Map<String, Set<IData>> getStructureOf(IData data) {
		return _requestHandler.getStructureOf(data);
	}

	@Override
	public Set<IData> flattenStructure(IData data) {
		return _requestHandler.flattenStructure(data);
	}

	@Override
	public IStatus deployPolicyRawXMLPmp(String xml) {
		return _requestHandler.deployPolicyRawXMLPmp(xml);
	}

	@Override
	public Set<XmlPolicy> getPolicies(IData data) {
		return _requestHandler.getPolicies(data);
	}

	@Override
	public void processEventAsync(IEvent pepEvent) {
		_requestHandler.processEventAsync(pepEvent);
	}

	@Override
	public IResponse processEventSync(IEvent pepEvent) {
		return _requestHandler.processEventSync(pepEvent);
	}

	@Override
	public IData getDataFromId(String id) {
		return _requestHandler.getDataFromId(id);
	}

	@Override
	public IPtpResponse translatePolicy(String requestId, Map<String, String> parameters, XmlPolicy xmlPolicy) {
		return _requestHandler.translatePolicy(requestId, parameters, xmlPolicy);
	}

	@Override
	public IPtpResponse updateDomainModel(String requestId,	Map<String, String> parameters, XmlPolicy xmlDomainModel) {
		return _requestHandler.updateDomainModel(requestId, parameters, xmlDomainModel);
	}

	@Override
	public Set<XmlPolicy> listPoliciesPmp() {
		return _requestHandler.listPoliciesPmp();
	}

	@Override
	public IStatus addListener(String ip, int port, String id, String filter) {
		return _requestHandler.addListener(ip, port, id, filter);
	}

	@Override
	public IStatus setUpdateFrequency(int msec, String id) {
		return _requestHandler.setUpdateFrequency(msec, id);
	}

	@Override
	public IStatus remotePolicyTransfer(String xml, String from) {
		return _requestHandler.remotePolicyTransfer(xml, from);
	}

}
