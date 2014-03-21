package de.tum.in.i22.uc.pip.core;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import testutil.DummyMessageGen;
import de.tum.in.i22.uc.cm.basic.DataBasic;
import de.tum.in.i22.uc.cm.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pdp;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pip;
import de.tum.in.i22.uc.cm.interfaces.IAny2Pmp;
import de.tum.in.i22.uc.cm.requests.PipRequest;
import de.tum.in.i22.uc.distribution.pip.EDistributedPipStrategy;
import de.tum.in.i22.uc.pip.core.distribution.DistributedPipManager;
import de.tum.in.i22.uc.pip.core.eventdef.DefaultEventHandler;
import de.tum.in.i22.uc.pip.core.manager.EventHandlerManager;
import de.tum.in.i22.uc.pip.core.manager.PipManager;

public class PipHandler implements IAny2Pip {

	private static final Logger _logger = LoggerFactory.getLogger(PipHandler.class);

	private final EventHandlerManager _actionHandlerCreator = new EventHandlerManager();

	private final InformationFlowModel _ifModel = InformationFlowModel.getInstance();

	private final PipManager _pipManager;

	/**
	 * Manages everything related to distributed data flow tracking
	 */
	private final DistributedPipManager _distributedPipManager;

	// this is to include classes within the jar file. DO NOT REMOVE.
	@SuppressWarnings("unused")
	private final boolean dummyIncludes = DummyIncludes.dummyInclude();

	private IAny2Pdp _pdp;
	private IAny2Pmp _pmp;

	public PipHandler(EDistributedPipStrategy distributedPipStrategy, int pipPort) {
		_pipManager = new PipManager(_actionHandlerCreator, pipPort);
		_distributedPipManager = DistributedPipManager.getInstance(distributedPipStrategy);
	}


	@Override
	public void init(IAny2Pdp pdp, IAny2Pmp pmp) {
		_pdp = pdp;
		_pmp = pmp;
	}


	@Override
	public Boolean evaluatePredicatCurrentState(String predicate) {
		// TODO: evaluatePredicateCurrentState
		// TODO: add code to evaluate generic predicate
		// Note that the three parameters of the predicate (State-based formula,
		// parameter1, parameter2) should be separated by separator1, while list
		// of elements (containers or data) should be separated by separator2
		final String separator1 = "\\|";
		final String separator2 = "#";
		_logger.info("Evaluate Predicate "+predicate+ " in simulated environment");


		///BEGINNING OF TEST BLOCK
//		String contId = _ifModel.getContainerIdByName(new Name(
//				"TEST_C"));
//
//		if (contId!=null){
//			_logger.debug("number of data elements in container TEST_C = "+_ifModel.getDataInContainer(contId).size());
//		} else {
//			_logger.debug("TEST_C contains no data or no container TEST_C found");
//		}
		///END OF TEST BLOCK



		//System.err.println(_ifModel.printModel());
		String[] st = predicate.split(separator1);
		_logger.debug("st.length="+st.length);

		if (st.length == 4) {
			String formula = st[0];
			String par1 = st[1];
			String par2 = st[2];
			int par3 = Integer.parseInt(st[3]);  //to be used for quantitative formulae

			String[] containers;
			Set<IContainer> s;

			String out="Evaluate Predicate "+formula+ " with parameters [" + par1 + "],[" + par2+"] and ["+par3+"]";

			/*
			 * TODO
			 *
			 * Whoever wrote this: Factor this code out.
			 * Proposal: Create an abstract class or interface and
			 * have one subclass per state based formula.
			 * Then just get the correct instance and invoke the function
			 * for evaluation. done.
			 * -FK-
			 */

			switch (formula) {
			case "isNotIn":  //par1 is data, par2 is list of containers
//				containers= par2.split(separator2);
//				s= _ifModel.getContainersForData(new DataBasic(par1));
//				//_logger.debug("size of s: "+s.size());
//				if(s.size() > 0){
//					for (String cont : containers){
//						NameBasic pname= new NameBasic(cont);
//						//_logger.debug("..in loop("+cont+")..");
//						if (s.contains(_ifModel.getContainerRelaxed(pname))) {
//							_logger.trace(out+"=false");
//							return false;
//						}
//					}
//					//_logger.trace("..no match found, returning true");
//					_logger.trace(out+"=true");
//					return true;
//				} else{
//					return false;
//				}
				IContainer par1Container = _ifModel.getContainerRelaxed(new NameBasic(par1));
				Set<IData> par1DataSet = _ifModel.getDataInContainer(par1Container);
				containers= par2.split(separator2);
				for(IData par1Data : par1DataSet){
					s= _ifModel.getContainersForData(par1Data);
					//_logger.debug("size of s: "+s.size());
					if(s.size() > 0){
						for (String cont : containers){
							NameBasic pname= new NameBasic(cont);
							//_logger.debug("..in loop("+cont+")..");
							if (s.contains(_ifModel.getContainerRelaxed(pname))) {
								_logger.trace(out+"=false");
								return false;
							}
						}
						//_logger.trace("..no match found, returning true");
						_logger.trace(out+"=true");
						return true;
					}
				}
				if(par1DataSet.size() == 0){
					return false;
				}
			case "isOnlyIn":
				containers= par2.split(separator2);
				Set<String> limit = new HashSet<String>(Arrays.asList(containers));
				s= _ifModel.getContainersForData(new DataBasic(par1));
				//_logger.debug("size of s: "+s.size());
				for (IContainer cont : s){
					NameBasic pname= new NameBasic(cont.getId());//TODO: not sure if it is correct
					//_logger.debug("..in loop("+cont+")..");
					if (!(limit.contains(_ifModel.getContainerRelaxed(pname)))) {
						_logger.trace(out+"=false");
						return false;
					}
				}
				//_logger.trace("..no match found, returning true");
				_logger.trace(out+"=false");
				return true;

			case "isCombinedWith":
				Set<IContainer> s1= _ifModel.getContainersForData(new DataBasic(par1));
				Set<IContainer> s2=_ifModel.getContainersForData(new DataBasic(par2));
				for (IContainer cont : s1){
					if (s2.contains(cont)) {
						_logger.trace(out+"=true");
						return true;
					}
				}
				_logger.trace(out+"=false");
				return false;


			default:
				_logger.trace(out+"=null");
				return null;
			}

		} else
			_logger.trace("returning null");
			return null;
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
		_logger.debug("Action name: " + action);
		IEventHandler actionHandler = null;
		try {
			_logger.trace("Create event handler");
			actionHandler = _actionHandlerCreator.createEventHandler(event);
		} catch (IllegalAccessException | InstantiationException e) {
			_logger.error(
					"Failed to create event handler for action " + action, e);
			// FIXME create error status with description
		} catch (ClassNotFoundException e) {
			_logger.error("Class not found for event handler " + action, e);
			// FIXME create error status with description
		}

		if (actionHandler == null) {
			_logger.trace("Create default event handler");
			actionHandler = new DefaultEventHandler();
		}

		actionHandler.setEvent(event);

		IStatus status = actionHandler.executeEvent();
		_logger.trace("Status to return: " + status);

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
		if (_ifModel.push())
			return DummyMessageGen.createOkStatus();
		return DummyMessageGen
				.createErrorStatus("Impossible to push current state.");
	}

	@Override
	public IStatus stopSimulation() {
		if (_ifModel.pop())
			return DummyMessageGen.createOkStatus();
		return DummyMessageGen
				.createErrorStatus("Impossible to pop current state.");
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
		if (_ifModel.push()) {
			_logger.trace("Updating PIP semantics with current event ("
					+ (event == null ? "null" : event.getPrefixedName()) + ")");
			notifyActualEvent(event);
			_logger.trace("Evaluate predicate in new updated state ("
					+ predicate + ")");
			Boolean res = evaluatePredicatCurrentState(predicate);
			_logger.trace("Restoring PIP previous state...");
			_ifModel.pop();
			_logger.trace("done!");
			return res;
		}
		_logger.error("Failed! Stack not empty!");
		return null;
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
	public IStatus updateInformationFlowSemantics(IPipDeployer deployer,
			byte[] jarFileBytes,
			EConflictResolution flagForTheConflictResolution) {
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
