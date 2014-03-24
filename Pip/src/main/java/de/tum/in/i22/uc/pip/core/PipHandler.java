package de.tum.in.i22.uc.pip.core;

import java.io.File;
import java.util.Collection;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import testutil.DummyMessageGen;
import de.tum.in.i22.uc.cm.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pdp;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pip;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pmp;
import de.tum.in.i22.uc.cm.requests.GenericHandler;
import de.tum.in.i22.uc.cm.requests.PipRequest;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pip.core.distribution.DistributedPipManager;
import de.tum.in.i22.uc.pip.core.ifm.InformationFlowModel;
import de.tum.in.i22.uc.pip.core.ifm.states.IsCombinedWith;
import de.tum.in.i22.uc.pip.core.ifm.states.IsNotIn;
import de.tum.in.i22.uc.pip.core.ifm.states.IsOnlyIn;
import de.tum.in.i22.uc.pip.core.ifm.states.StateBasedPredicate;
import de.tum.in.i22.uc.pip.core.manager.EventHandlerManager;
import de.tum.in.i22.uc.pip.core.manager.PipManager;
import de.tum.in.i22.uc.pip.interfaces.EStateBasedFormula;
import de.tum.in.i22.uc.pip.interfaces.IEventHandler;
import de.tum.in.i22.uc.pip.interfaces.IStateBasedPredicate;

public class PipHandler extends GenericHandler<PipRequest> implements IAny2Pip {

	private static final Logger _logger = LoggerFactory.getLogger(PipHandler.class);

	private final EventHandlerManager _actionHandlerCreator = new EventHandlerManager();

	private final InformationFlowModel _ifModel = InformationFlowModel.getInstance();

	private final PipManager _pipManager = new PipManager(_actionHandlerCreator, Settings.getInstance().getPipPortNum());

	private static final PipHandler _instance = new PipHandler();

	/**
	 * Manages everything related to distributed data flow tracking
	 */
	private final DistributedPipManager _distributedPipManager= DistributedPipManager.getInstance();

	// this is to include classes within the jar file. DO NOT REMOVE.
	@SuppressWarnings("unused")
	private final boolean dummyIncludes = DummyIncludes.dummyInclude();

	private IAny2Pdp _pdp;
	private IAny2Pmp _pmp;

	private boolean _initialized = false;

	private PipHandler() {
	}

	public static PipHandler getInstance(){
		return _instance;
	}

	@Override
	public void init(IAny2Pdp pdp, IAny2Pmp pmp) {
		_initialized = true;
		if (!_initialized) {
			_pdp = pdp;
			_pmp = pmp;
		}
	}


	@Override
	public Boolean evaluatePredicatCurrentState(String predicate) {
		_logger.info("Evaluate Predicate "+predicate+ " in simulated environment");

		IStateBasedPredicate spredicate = null;

		String[] st = predicate.split(StateBasedPredicate.separator1);
		if (st.length == 0) {
			return null;
		}

		switch (EStateBasedFormula.from(st[0])) {
			case IS_COMBINED_WITH:
				if (st.length < 3) { return null; }
				spredicate = new IsCombinedWith(predicate, st[1], st[2]);
				break;
			case IS_NOT_IN:
				if (st.length < 3) { return null; }
				spredicate = new IsNotIn(predicate, st[1], st[2]);
				break;
			case IS_ONLY_IN:
				if (st.length < 3) { return null; }
				spredicate = new IsOnlyIn(predicate, st[1], st[2]);
				break;
		}

		return spredicate != null ? spredicate.evaluate() : null;
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

		_logger.debug("Action name: " + action);

		try {
			actionHandler = _actionHandlerCreator.createEventHandler(event);
		} catch (IllegalAccessException | InstantiationException e) {
			return new StatusBasic(EStatus.ERROR, "Failed to create event handler for action " + action);
		} catch (ClassNotFoundException e) {
			return new StatusBasic(EStatus.ERROR, "Class not found for event handler " + action);
		}

		return actionHandler != null
			? actionHandler.setEvent(event).executeEvent()
			: new StatusBasic(EStatus.ERROR);
	}

	@Override
	public IStatus updateInformationFlowSemantics(IPipDeployer deployer,
			File jarFile, EConflictResolution flagForTheConflictResolution) {

		return _pipManager.updateInformationFlowSemantics(deployer, jarFile,
				flagForTheConflictResolution);
	}

	@Override
	public IStatus startSimulation() {
		return _ifModel.push()
			? DummyMessageGen.createOkStatus()
			: DummyMessageGen.createErrorStatus("Impossible to push current state.");
	}

	@Override
	public IStatus stopSimulation() {
		return _ifModel.pop()
			? DummyMessageGen.createOkStatus()
			: DummyMessageGen.createErrorStatus("Impossible to pop current state.");
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
		return _ifModel.isSimulating();
	}


	/**
	 * Evaluate the predicate in the state obtained simulating the execution of event.
	 * @return the result of the formula
	 */
	@Override
	public Boolean evaluatePredicateSimulatingNextState(IEvent event, String predicate){
		_logger.info("Saving PIP current state");

		Boolean res = null;

		if (_ifModel.push()) {
			_logger.trace("Updating PIP semantics with current event ("
					+ (event == null ? "null" : event.getPrefixedName()) + ")");
			notifyActualEvent(event);
			_logger.trace("Evaluate predicate in new updated state ("
					+ predicate + ")");
			res = evaluatePredicatCurrentState(predicate);
			_logger.trace("Restoring PIP previous state...");
			_ifModel.pop();
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object process(PipRequest request) {
		Object result = null;

		switch(request.getType()) {
			case EVALUATE_PREDICATE:
				break;
			case GET_CONTAINER_FOR_DATA:
				break;
			case GET_DATA_IN_CONTAINER:
				break;
			case HAS_ALL_CONTAINERS:
				break;
			case HAS_ALL_DATA:
				break;
			case HAS_ANY_CONTAINER:
				break;
			case HAS_ANY_DATA:
				break;
			case NOTIFY_ACTUAL_EVENT:
				result = notifyActualEvent(request.getEvent());
				break;
			case NOTIFY_DATA_TRANSFER:
				break;
			case UPDATE_INFORMATION_FLOW_SEMANTICS:
				break;
			default:
				throw new RuntimeException("Method " + request.getType() + " is not supported!");
		}

		return result;
	}
}
