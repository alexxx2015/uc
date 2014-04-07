package de.tum.in.i22.uc.pip;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import de.tum.in.i22.uc.cm.basic.ContainerBasic;
import de.tum.in.i22.uc.cm.basic.EventBasic;
import de.tum.in.i22.uc.cm.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.server.PipProcessor;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pip.core.ifm.BasicInformationFlowModel;
import de.tum.in.i22.uc.pip.core.ifm.InformationFlowModelManager;
import de.tum.in.i22.uc.pip.core.manager.EventHandlerManager;
import de.tum.in.i22.uc.pip.core.manager.PipManager;
import de.tum.in.i22.uc.pip.extensions.distribution.DistributedPipManager;
import de.tum.in.i22.uc.pip.extensions.distribution.DistributedPipStatus;
import de.tum.in.i22.uc.pip.extensions.statebased.InvalidStateBasedFormula;
import de.tum.in.i22.uc.pip.extensions.statebased.StateBasedPredicate;
import de.tum.in.i22.uc.pip.interfaces.IEventHandler;
import de.tum.in.i22.uc.pip.interfaces.IStateBasedPredicate;

public class PipHandler extends PipProcessor {
	private static final Logger _logger = LoggerFactory.getLogger(PipHandler.class);

	private final BasicInformationFlowModel _ifModel;

	private final InformationFlowModelManager _ifModelManager;

	private final PipManager _pipManager;

	/**
	 * Manages everything related to distributed data flow tracking
	 */
	private final DistributedPipManager _distributedPipManager;

	// this is to include classes within the jar file. DO NOT REMOVE.
	@SuppressWarnings("unused")
	private final boolean dummyIncludes = DummyIncludes.dummyInclude();

	public PipHandler() {
		_pipManager = new PipManager();
		_distributedPipManager = new DistributedPipManager();
		_ifModelManager = InformationFlowModelManager.getInstance();
		_ifModel = _ifModelManager.getBasicInformationFlowModel();

		// initialize data flow according to settings
		update(new EventBasic(Settings.getInstance().getPipInitializerEvent(), null, true));
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
		return _ifModel.getContainersForData(data);
	}

	@Override
	public Set<IData> getDataInContainer(IContainer container) {
		return _ifModel.getDataInContainer(container);
	}

	@Override
	public IStatus update(IEvent event) {
		String action = event.getPrefixedName();
		IEventHandler actionHandler = null;
		IStatus result;

		try {
			actionHandler = EventHandlerManager.createEventHandler(event);
		} catch (Exception e) {
			return new StatusBasic(EStatus.ERROR, "Could not instantiate event handler for " + action + ", " + e.getMessage());
		}

		if (actionHandler == null) {
			return new StatusBasic(EStatus.ERROR);
		}

		actionHandler.setEvent(event);

		_logger.info(System.lineSeparator() + "Executing PipHandler for " + event);
		result = actionHandler.performUpdate();

		// Potentially, we need to do some more work ...
		if (result.isStatus(EStatus.REMOTE_DATA_FLOW_HAPPENED) && result instanceof DistributedPipStatus) {
			// TODO: PIP communication and PMP communication
			// can be improved by either doing only one call
			// or by doing them in parallel

			// .... remote data flow tracking ....
			_distributedPipManager.remoteDataFlow(((DistributedPipStatus) result).getDataflow());

			// .... and remote policy transfer
			// TODO: notify PMP
		}


		_logger.info(System.lineSeparator() + _ifModelManager.niceString());

		return result;
	}

	@Override
	public IStatus updateInformationFlowSemantics(IPipDeployer deployer,
			File jarFile, EConflictResolution flagForTheConflictResolution) {

		return _pipManager.updateInformationFlowSemantics(deployer, jarFile, flagForTheConflictResolution);
	}

	@Override
	public IStatus startSimulation() {
		return _ifModelManager.startSimulation();
	}

	@Override
	public IStatus stopSimulation() {
		return _ifModelManager.stopSimulation();
	}

//    /**
//     * If @param event is a desired event, simulates the new state in the PIP, update the cache, and then revert.
//     * If @param event is an actual event, does the same, but the PIP remains in the new state.
//     * @param event
//     * @return
//     */
//	@Override
//	public ICacheUpdate refresh (IEvent e) {
//		if (e==null) {
//			_logger.error("null event received. returning null");
//			return null;
//		}
//		ICacheUpdate res = new CacheUpdateBasic();
//		Map<IKey,Boolean> map=new HashMap<IKey,Boolean>();
//
//
//		res.setMap(map);
//		res.setScopeId("<GET SCOPE ID STILL NOT IMPLEMENTED>");
//
//		int counter=0;
//		_logger.debug("refreshing cache with event "+e);
//
//		if (!e.isActual()){
//			_logger.debug("event " + e.getPrefixedName() + " is a desired event. Simulating new state.");
//			if (!isSimulating()){
//				startSimulation();
//			} else {
//				_logger.error("Pip is already simulating. returning null");
//				return null;
//			}
//		} else {
//			_logger.debug("event " + e.getPrefixedName() + " is an actual event");
//		}
//		_logger.debug("Updating PIP with event " + e.getPrefixedName() );
//		notifyActualEvent(e);
//		_logger.debug("Creating cache response");
//		for (String key : _predicatesToEvaluate.keySet()){
//			Boolean b = evaluatePredicatCurrentState(key);
//			_logger.debug("("+counter+") ["+key+"]="+b);
//			map.put(_predicatesToEvaluate.get(key), b);
//			counter++;
//		}
//
//		if (!e.isActual()){
//			_logger.debug("Reverting simulation");
//			if (isSimulating()){
//				stopSimulation();
//			} else {
//				_logger.error("Pip is not simulating. ERROR!!!! returning null");
//				return null;
//			}
//		} else {
//			_logger.debug("Done!");
//		}
//
//		return res;
//	}


	@Override
	public boolean isSimulating() {
		return _ifModelManager.isSimulating();
	}


	/**
	 * Evaluate the predicate in the state obtained simulating the execution of event.
	 * @return the result of the formula
	 */
	@Override
	public boolean evaluatePredicateSimulatingNextState(IEvent event, String predicate){
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
		}
		else {
			_logger.error("Failed! Stack not empty!");
		}

		return res;
	}

	@Override
	public boolean hasAllData(Set<IData> data) {
		Set<IData> all = new HashSet<>();
		for (IContainer c : _ifModel.getAllContainers()) {
			all.addAll(_ifModel.getDataInContainer(c));
		}
		return all.containsAll(data);
	}

	@Override
	public boolean hasAnyData(Set<IData> data) {
		for (IContainer c : _ifModel.getAllContainers()) {
			if (_ifModel.getDataInContainer(c).contains(data)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean hasAllContainers(Set<IContainer> containers) {
		return _ifModel.getAllContainers().containsAll(containers);
	}

	@Override
	public boolean hasAnyContainer(Set<IContainer> containers) {
		return Sets.intersection(containers, _ifModel.getAllContainers()).size() >= 1;
	}

	@Override
	public IStatus initialRepresentation(IName containerName, Set<IData> data) {
		_logger.debug("initialRepresentation(" + containerName + "," + data + ")");

		IContainer container;
		if ((container = _ifModel.getContainer(containerName)) == null) {
			container = new ContainerBasic();
			_ifModel.addName(containerName, container);
		}
		_ifModel.addDataToContainerAndAliases(data, container);
		return new StatusBasic(EStatus.OKAY);
	}

	@Override
	public String toString() {
        return _ifModelManager.niceString();
    }
}
