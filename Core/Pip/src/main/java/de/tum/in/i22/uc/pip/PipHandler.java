package de.tum.in.i22.uc.pip;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.ConflictResolutionFlagBasic.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.basic.ContainerBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.DataBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.basic.exceptions.InvalidStateBasedFormulaException;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.LocalLocation;
import de.tum.in.i22.uc.cm.factories.MessageFactory;
import de.tum.in.i22.uc.cm.pip.ifm.IBasicInformationFlowModel;
import de.tum.in.i22.uc.cm.pip.interfaces.IEventHandler;
import de.tum.in.i22.uc.cm.pip.interfaces.IStateBasedPredicate;
import de.tum.in.i22.uc.cm.processing.PipProcessor;
import de.tum.in.i22.uc.cm.processing.dummy.DummyPdpProcessor;
import de.tum.in.i22.uc.cm.processing.dummy.DummyPmpProcessor;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pip.core.ifm.InformationFlowModelManager;
import de.tum.in.i22.uc.pip.core.manager.EventHandlerManager;
import de.tum.in.i22.uc.pip.core.manager.PipManager;
import de.tum.in.i22.uc.pip.core.statebased.StateBasedPredicateManager;
import de.tum.in.i22.uc.pip.distribution.DistributedPipStatus;
import de.tum.in.i22.uc.pip.eventdef.java.JavaPipStatus;
import de.tum.in.i22.uc.pip.extensions.javapip.JavaPipManager;

public class PipHandler extends PipProcessor {
	private static final Logger _logger = LoggerFactory.getLogger(PipHandler.class);

	private final IBasicInformationFlowModel _ifModel;

	private final InformationFlowModelManager _ifModelManager;

	private final PipManager _pipManager;

	private final StateBasedPredicateManager _stateBasedPredicateManager;

	/**
	 * Manager for remote Java Pip
	 */
	private final JavaPipManager _javaPipManager;

	public PipHandler() {
		this(new InformationFlowModelManager());
	}

	public PipHandler(InformationFlowModelManager ifmModelManager) {
		super(LocalLocation.getInstance());
		init(new DummyPdpProcessor(), new DummyPmpProcessor());

		_pipManager = new PipManager();
		_ifModelManager = ifmModelManager;
		_ifModel = _ifModelManager.getBasicInformationFlowModel();
		_stateBasedPredicateManager = new StateBasedPredicateManager();

		_javaPipManager = new JavaPipManager();
		Thread tjpip= new Thread(_javaPipManager);
		tjpip.start();

		// initialize data flow according to settings
		update(new EventBasic(Settings.getInstance().getPipInitializerEvent(),
				(Map<String,String>) null, true));
	}

	@Override
	public boolean evaluatePredicateCurrentState(String predicate) {
		IStateBasedPredicate pred;

		try {
			pred = _stateBasedPredicateManager.get(predicate, _ifModelManager);
		} catch (InvalidStateBasedFormulaException e) {
			_logger.warn(e.toString());
			return false;
		}
		if (!isSimulating() && Settings.getInstance().getPipPrintAfterUpdate()) {
			_logger.debug(this.toString());
		}

		return pred.evaluate();
	}

	@Override
	public Set<IContainer> getContainersForData(IData data) {
		return _ifModel.getContainers(data);
	}

	@Override
	public Set<IData> getDataInContainer(IName containerName) {
		return _ifModel.getData(containerName);
	}

	@Override
	public IStatus update(IEvent event) {

		IEventHandler eventHandler = null;
		IStatus status = null;

		/*
		 * Get the event handler and perform the information flow update
		 * according to its implemented semantics
		 */

		try {
			eventHandler = EventHandlerManager.createEventHandler(event);
		} catch (Exception e) {
			_logger.error("Could not instantiate event handler for " + event.getName() + ", " + e.getMessage());
			eventHandler = null;
		}

		if (eventHandler == null) {
			_logger.error("EventHandler is null. Unable to update PIP state,");
			status = new StatusBasic(EStatus.ERROR);
		}
		else {
			eventHandler.setEvent(event);
			eventHandler.setInformationFlowModel(_ifModelManager);
			eventHandler.setDistributionManager(_distributionManager);

			_logger.info("Executing PipHandler for " + event);
			status = eventHandler.performUpdate();

			/*
			 * The returned status will tell us whether we have to do some more
			 * work, namely remote data flow tracking and policy shipment
			 */
			if (!isSimulating() && status instanceof DistributedPipStatus) {
				_distributionManager.dataTransfer(((DistributedPipStatus) status).getDataflow());
			}

			if (Settings.getInstance().getJavaPipMonitor() && (status instanceof JavaPipStatus)) {
				/*
				 * TODO: Outsource this code.
				 */
				//	no need to check the PEP=java because that's the only way to get status instanceof JavaPipStatus
				_logger.debug("JAVAPIPMANGER NOTIFICATION");

				switch (event.getName()){
				case "Source":
				case "Sink":
					IName contName = ((JavaPipStatus) status).getContName();
					Set<IData> dataSet= ((JavaPipStatus) status).getDataSet();

					if (dataSet==null || contName==null) break;

					String dataSetString = "";
					for (IData d: dataSet){
						dataSetString = dataSetString + " ";
					}

					Map<String,String> map=new HashMap<String, String>();
					map.putAll(event.getParameters());
					map.put("JavaPipContName", contName.getName());
					map.put("JavaPipDataSet", dataSetString);
					IEvent e=new EventBasic(event.getName(), map, event.isActual(), event.getTimestamp());

					try {
						BlockingQueue<IEvent> q=_javaPipManager.getMasterQueue();
						_logger.debug("("+e+") queue size before= " +q.size());
						q.put(e);
						_logger.debug("("+e+") queue size after= " +q.size());
					} catch (InterruptedException ex) {
						// TODO Auto-generated catch block
						ex.printStackTrace();
					}
					break;
				default:
				}
			}

			if (!isSimulating() && Settings.getInstance().getPipPrintAfterUpdate()) {
				_logger.debug(this.toString());
			}
		}

		return status;
	}

	@Override
	public IStatus updateInformationFlowSemantics(IPipDeployer deployer,
			File jarFile, EConflictResolution flagForTheConflictResolution) {
		return _pipManager.updateInformationFlowSemantics(deployer, jarFile,
				flagForTheConflictResolution);
	}

	@Override
	public IStatus startSimulation() {
		return _ifModelManager.startSimulation();
	}

	@Override
	public IStatus stopSimulation() {
		return _ifModelManager.stopSimulation();
	}

	@Override
	public boolean isSimulating() {
		return _ifModelManager.isSimulating();
	}

	/**
	 * Evaluate the predicate in the state obtained simulating the execution of
	 * event.
	 *
	 * @return the result of the formula
	 */
	@Override
	public boolean evaluatePredicateSimulatingNextState(IEvent event,
			String predicate) {
		_logger.info("Saving PIP current state");

		Boolean res = null;

		if (_ifModelManager.startSimulation().getEStatus() == EStatus.OKAY) {
			_logger.trace("Updating PIP semantics with current event ({})", event);
			update(event);
			_logger.trace("Evaluate predicate in new updated state ("+ predicate + ")");
			res = evaluatePredicateCurrentState(predicate);
			_logger.trace("Result of the evaluation is " + res);
			_logger.trace("Restoring PIP previous state...");
			_ifModelManager.stopSimulation();
		} else {
			_logger.error("Failed! Stack not empty!");
		}

		return res;
	}

	@Override
	public IStatus initialRepresentation(IName containerName, Set<IData> data) {
		_logger.debug("initialRepresentation(" + containerName + "," + data + ")");

		/*
		 * Convert to a 'more' proper IName object.
		 */
		containerName = MessageFactory.createName(containerName.getName());

		IContainer container;
		if ((container = _ifModel.getContainer(containerName)) == null) {
			_ifModel.addName(containerName, container = new ContainerBasic());
		}

		if (data == null || data.size() == 0) {
			newInitialRepresentation(containerName);
		}
		else {
			_ifModel.addDataTransitively(data, container);
		}

		return new StatusBasic(EStatus.OKAY);
	}

	@Override
	public IData newInitialRepresentation(IName containerName) {
		_logger.debug("newInitialRepresentation(" + containerName + ")");
		IContainer container;
		IData d = new DataBasic();

		if ((container = _ifModel.getContainer(containerName)) == null) {
			_ifModel.addName(containerName, container = new ContainerBasic());
		}

		_ifModel.addDataTransitively(Collections.singleton(d), container);
		return d;
	}

	@Override
	public String toString() {
		return _ifModelManager.niceString();
	}

	@Override
	public IData newStructuredData(Map<String, Set<IData>> structure) {
		return _ifModelManager.newStructuredData(structure);
	}

	@Override
	public Map<String, Set<IData>> getStructureOf(IData data) {
		return _ifModelManager.getStructureOf(data);
	}

	@Override
	public Set<IData> flattenStructure(IData data) {
		return _ifModelManager.flattenStructure(data);
	}

	@Override
	public IData getDataFromId(String id) {
		IData d = _ifModelManager.getDataFromId(id);
		return (d == null) ? new DataBasic("null") : d;
	}

	@Override
	public IStatus addListener(String ip, int port, String id, String filter) {
		//we use event objects to communicate with the updater thread manager
		Map<String,String> pars = new HashMap<String,String>();
		pars.put("ip", ip);
		pars.put("port", ""+port);
		pars.put("id", id);
		pars.put("filter", filter);

		IEvent ev= new EventBasic("AddListener",pars);

		try {
			BlockingQueue<IEvent> q=_javaPipManager.getMasterQueue();
			_logger.debug("("+ev+") queue size before= " +q.size());
			q.put(ev);
			_logger.debug("("+ev+")queue size after= " +q.size());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new StatusBasic(EStatus.OKAY);
	}

	@Override
	public IStatus setUpdateFrequency(int msec, String id) {
		//we use event objects to communicate with the updater thread manager
		Map<String,String> pars = new HashMap<String,String>();
		pars.put("msec", ""+msec);
		pars.put("id", id);

		IEvent ev= new EventBasic("SetUpdateFrequency",pars);

		try {
			BlockingQueue<IEvent> q=_javaPipManager.getMasterQueue();
			_logger.debug("("+ev+") queue size before= " +q.size());
			q.put(ev);
			_logger.debug("("+ev+")queue size after= " +q.size());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new StatusBasic(EStatus.OKAY);
	}
}
