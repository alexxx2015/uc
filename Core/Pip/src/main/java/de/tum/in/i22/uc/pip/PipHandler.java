package de.tum.in.i22.uc.pip;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.interfaces.informationFlowModel.IBasicInformationFlowModel;
import de.tum.in.i22.uc.cm.pip.interfaces.IEventHandler;
import de.tum.in.i22.uc.cm.pip.interfaces.IStateBasedPredicate;
import de.tum.in.i22.uc.cm.processing.PipProcessor;
import de.tum.in.i22.uc.cm.processing.dummy.DummyPdpProcessor;
import de.tum.in.i22.uc.cm.processing.dummy.DummyPmpProcessor;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pip.core.ifm.InformationFlowModelManager;
import de.tum.in.i22.uc.pip.core.manager.EventHandlerManager;
import de.tum.in.i22.uc.pip.core.manager.PipManager;
import de.tum.in.i22.uc.pip.core.statebased.StateBasedPredicate;
import de.tum.in.i22.uc.pip.extensions.distribution.DistributedPipStatus;

public class PipHandler extends PipProcessor {
	private static final Logger _logger = LoggerFactory
			.getLogger(PipHandler.class);

	private final IBasicInformationFlowModel _ifModel;

	private final InformationFlowModelManager _ifModelManager;

	private final PipManager _pipManager;

	public PipHandler() {
		this(new InformationFlowModelManager());
	}

	public PipHandler(InformationFlowModelManager ifmModelManager) {
		super(LocalLocation.getInstance());
		init(new DummyPdpProcessor(), new DummyPmpProcessor());

		_pipManager = new PipManager();
		_ifModelManager = ifmModelManager;
		_ifModel = _ifModelManager.getBasicInformationFlowModel();

		// initialize data flow according to settings
		update(new EventBasic(Settings.getInstance().getPipInitializerEvent(),
				(Map<String,String>) null, true));
	}

	@Override
	public boolean evaluatePredicateCurrentState(String predicate) {
		IStateBasedPredicate pred;

		try {
			pred = StateBasedPredicate.create(predicate, _ifModelManager);
		} catch (InvalidStateBasedFormulaException e) {
			_logger.warn(e.toString());
			return false;
		}
		if (!isSimulating() && Settings.getInstance().getPipPrintAfterUpdate()) {
			_logger.debug(this.toString());
		}
		if (pred == null) {
			_logger.error("Predicate to be evaluated is null. returning predefined value false. This shouldn't happen, though.");
			return false;
		}
		try {
			return pred.evaluate();
		} catch (InvalidStateBasedFormulaException e) {
			e.printStackTrace();
			return false;

		}
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
		String eventName = event.getName();
		IEventHandler eventHandler = null;
		IStatus status;

		/*
		 * Get the event handler and perform the information flow update
		 * according to its implemented semantics
		 */

		try {
			eventHandler = EventHandlerManager.createEventHandler(event);
		} catch (Exception e) {
			return new StatusBasic(EStatus.ERROR,
					"Could not instantiate event handler for " + eventName + ", "
							+ e.getMessage());
		}

		if (eventHandler == null) {
			return new StatusBasic(EStatus.ERROR);
		}

		eventHandler.setEvent(event);
		eventHandler.setInformationFlowModel(_ifModelManager);

		_logger.info("Executing PipHandler for " + event);
		status = eventHandler.performUpdate();

		/*
		 * The returned status will tell us whether we have to do some more
		 * work, namely remote data flow tracking and policy shipment
		 */
		if (!isSimulating() && status instanceof DistributedPipStatus) {
			_distributionManager.dataTransfer(((DistributedPipStatus) status).getDataflow());
		}

		if (!isSimulating() && Settings.getInstance().getPipPrintAfterUpdate()) {
			_logger.debug(this.toString());
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
	public boolean hasAllData(Set<IData> data) {
		Set<IData> all = new HashSet<>();
		for (IContainer c : _ifModel.getAllContainers()) {
			all.addAll(_ifModel.getData(c));
		}
		return all.containsAll(data);
	}

	@Override
	public boolean hasAnyData(Set<IData> data) {
		for (IContainer c : _ifModel.getAllContainers()) {
			if (_ifModel.getData(c).contains(data)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean hasAllContainers(Set<IName> containers) {
		// TODO
		return false;
	}

	@Override
	public boolean hasAnyContainer(Set<IName> containers) {
		// TODO
		return false;
	}

	@Override
	public IStatus initialRepresentation(IName containerName, Set<IData> data) {
		_logger.debug("initialRepresentation(" + containerName + "," + data
				+ ")");

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
	public Set<Location> whoHasData(Set<IData> data, int recursionDepth) {
		//		return _distributedPipManager.whoHasData(data, recursionDepth);
		return Collections.emptySet();
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
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public IData getDataFromId(String id) {
		IData d = _ifModelManager.getDataFromId(id);
		return (d == null) ? new DataBasic("null") : d;
	}
}
