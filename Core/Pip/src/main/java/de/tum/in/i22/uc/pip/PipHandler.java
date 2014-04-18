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
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.processing.PipProcessor;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pip.core.ifm.BasicInformationFlowModel;
import de.tum.in.i22.uc.pip.core.ifm.InformationFlowModelManager;
import de.tum.in.i22.uc.pip.core.manager.EventHandlerManager;
import de.tum.in.i22.uc.pip.core.manager.PipManager;
import de.tum.in.i22.uc.pip.extensions.distribution.DistributedPipStatus;
import de.tum.in.i22.uc.pip.extensions.distribution.PipDistributionManager;
import de.tum.in.i22.uc.pip.extensions.distribution.RemoteDataFlowInfo;
import de.tum.in.i22.uc.pip.extensions.statebased.InvalidStateBasedFormula;
import de.tum.in.i22.uc.pip.extensions.statebased.StateBasedPredicate;
import de.tum.in.i22.uc.pip.interfaces.IEventHandler;
import de.tum.in.i22.uc.pip.interfaces.IStateBasedPredicate;

public class PipHandler extends PipProcessor {
	private static final Logger _logger = LoggerFactory
			.getLogger(PipHandler.class);

	private final BasicInformationFlowModel _ifModel;

	private final InformationFlowModelManager _ifModelManager;

	private final PipManager _pipManager;

	/**
	 * Manages everything related to distributed data flow tracking
	 */
	private final PipDistributionManager _distributedPipManager;

	// this is to include classes within the jar file. DO NOT REMOVE.
	@SuppressWarnings("unused")
	private final boolean dummyIncludes = DummyIncludes.dummyInclude();

	public PipHandler() {
		_pipManager = new PipManager();
		_distributedPipManager = new PipDistributionManager();
		_ifModelManager = InformationFlowModelManager.getInstance();
		_ifModel = _ifModelManager.getBasicInformationFlowModel();

		// initialize data flow according to settings
		update(new EventBasic(Settings.getInstance().getPipInitializerEvent(),
				null, true));
	}

	@Override
	public boolean evaluatePredicateCurrentState(String predicate) {
		IStateBasedPredicate pred;

		try {
			pred = StateBasedPredicate.create(predicate);
		} catch (InvalidStateBasedFormula e) {
			_logger.warn(e.toString());
			return false;
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
		String action = event.getPrefixedName();
		IEventHandler actionHandler = null;
		IStatus status;

		/*
		 * Get the event handler and perform the information flow update
		 * according to its implemented semantics
		 */

		try {
			actionHandler = EventHandlerManager.createEventHandler(event);
		} catch (Exception e) {
			return new StatusBasic(EStatus.ERROR,
					"Could not instantiate event handler for " + action + ", "
							+ e.getMessage());
		}

		if (actionHandler == null) {
			return new StatusBasic(EStatus.ERROR);
		}

		actionHandler.setEvent(event);

		_logger.info(System.lineSeparator() + "Executing PipHandler for "
				+ event);
		status = actionHandler.performUpdate();

		/*
		 * The returned status will tell us whether we have to do some more
		 * work, namely remote data flow tracking and policy shipment
		 */

		if (status.isStatus(EStatus.REMOTE_DATA_FLOW_HAPPENED)
				&& (status instanceof DistributedPipStatus)) {

			// TODO: PIP communication and PMP communication
			// can be improved by either doing only one call
			// or by doing them in parallel

			/*
			 * Get the information about the remote data flow from the returned
			 * status and inform both the distributed Pip manager and the Pmp.
			 */

			RemoteDataFlowInfo df = ((DistributedPipStatus) status)
					.getDataflow();
			Map<Location, Map<IName, Set<IData>>> dataflow = df.getFlows();

			Location srcLocation = df.getSrcLocation();

			for (Location dstlocation : dataflow.keySet()) {
				// .... remote data flow tracking ....
				_distributedPipManager.remoteDataFlow(srcLocation, dstlocation,
						dataflow.get(dstlocation));

				// .... and remote policy transfer
				Set<IData> data = new HashSet<>();
				for (Set<IData> d : dataflow.get(dstlocation).values()) {
					data.addAll(d);
				}
				getPmp().informRemoteDataFlow(srcLocation, dstlocation, data);
			}

		}

		_logger.info(System.lineSeparator() + _ifModelManager.niceString());

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
			_logger.trace("Updating PIP semantics with current event ("
					+ (event == null ? "null" : event.getPrefixedName()) + ")");
			update(event);
			_logger.trace("Evaluate predicate in new updated state ("
					+ predicate + ")");
			res = evaluatePredicateCurrentState(predicate);
			_logger.trace("Restoring PIP previous state...");
			_ifModelManager.stopSimulation();
			_logger.trace("done!");
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
		if ((data == null) || (data == Collections.EMPTY_SET)) {
			newInitialRepresentation(containerName);
		} else
			_ifModel.addDataTransitively(data, container);
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
		return _distributedPipManager.whoHasData(data, recursionDepth);
	}

	@Override
	public String toString() {
		return _ifModelManager.niceString();
	}
}
