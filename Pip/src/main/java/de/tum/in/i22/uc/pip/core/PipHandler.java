package de.tum.in.i22.uc.pip.core;

import java.io.File;
import java.util.Collection;
import java.util.Set;

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
import de.tum.in.i22.uc.cm.requests.GenericPipHandler;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pip.core.ifm.BasicInformationFlowModel;
import de.tum.in.i22.uc.pip.core.ifm.InformationFlowModelManager;
import de.tum.in.i22.uc.pip.core.manager.EventHandlerManager;
import de.tum.in.i22.uc.pip.core.manager.PipManager;
import de.tum.in.i22.uc.pip.extensions.distribution.DistributedPipManager;
import de.tum.in.i22.uc.pip.extensions.statebased.InvalidStateBasedFormula;
import de.tum.in.i22.uc.pip.extensions.statebased.StateBasedPredicate;
import de.tum.in.i22.uc.pip.interfaces.IEventHandler;
import de.tum.in.i22.uc.pip.interfaces.IStateBasedPredicate;

public class PipHandler extends GenericPipHandler {

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
		_pipManager = PipManager.getInstance();
		_distributedPipManager = DistributedPipManager.getInstance();
		_ifModelManager = InformationFlowModelManager.getInstance();
		_ifModel = _ifModelManager.getBasicInformationFlowModel();

		// initialize data flow according to settings
		notifyActualEvent(new EventBasic(Settings.getInstance().getPipInitializerEvent(), null, true));
	}

	@Override
	public Boolean evaluatePredicatCurrentState(String predicate) {
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
	public Set<IContainer> getContainerForData(IData data) {
		return _ifModel.getContainersForData(data);
	}

	@Override
	public Set<IData> getDataInContainer(IContainer container) {
		return _ifModel.getDataInContainer(container);
	}

	@Override
	public IStatus notifyActualEvent(IEvent event) {
		String action = event.getPrefixedName();
		IEventHandler actionHandler = null;

		try {
			actionHandler = EventHandlerManager.createEventHandler(event);
		} catch (IllegalAccessException | InstantiationException e) {
			return new StatusBasic(EStatus.ERROR, "Failed to create event handler for action " + action);
		} catch (ClassNotFoundException e) {
			return new StatusBasic(EStatus.ERROR, "Class not found for event handler " + action);
		}

		if (actionHandler == null) {
			return new StatusBasic(EStatus.ERROR);
		}


		_logger.info(System.lineSeparator() + event);
		IStatus result = actionHandler.setEvent(event).executeEvent();
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
//		//TODO: fix missing getScopeId. requires implementation of XBEHAV.
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
	public Boolean evaluatePredicateSimulatingNextState(IEvent event, String predicate){
		_logger.info("Saving PIP current state");

		Boolean res = null;

		if (_ifModelManager.startSimulation().getEStatus() == EStatus.OKAY) {
			_logger.trace("Updating PIP semantics with current event ("
					+ (event == null ? "null" : event.getPrefixedName()) + ")");
			notifyActualEvent(event);
			_logger.trace("Evaluate predicate in new updated state ("
					+ predicate + ")");
			res = evaluatePredicatCurrentState(predicate);
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
	public boolean hasAllData(Collection<IData> data) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasAnyData(Collection<IData> data) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasAllContainers(Collection<IContainer> container) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasAnyContainer(Collection<IContainer> container) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IStatus notifyDataTransfer(IName containerName,
			Collection<IData> data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus initialRepresentation(IContainer container, IData data) {
		_ifModel.addDataToContainer(data, container);
		return new StatusBasic(EStatus.OKAY);
	}
}
