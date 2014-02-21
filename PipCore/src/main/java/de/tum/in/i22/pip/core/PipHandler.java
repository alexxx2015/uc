package de.tum.in.i22.pip.core;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import testutil.DummyMessageGen;
import de.tum.in.i22.pip.core.Scope.scopeType;
import de.tum.in.i22.pip.core.eventdef.DefaultEventHandler;
import de.tum.in.i22.pip.core.manager.EventHandlerManager;
import de.tum.in.i22.pip.core.manager.IEventHandlerCreator;
import de.tum.in.i22.pip.core.manager.IPipManager;
import de.tum.in.i22.pip.core.manager.PipManager;
import de.tum.in.i22.uc.cm.basic.CacheUpdateBasic;
import de.tum.in.i22.uc.cm.datatypes.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.ICacheUpdate;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IKey;
import de.tum.in.i22.uc.cm.datatypes.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class PipHandler implements IPdp2Pip, IPipCacher2Pip {

	private static final Logger _logger = Logger.getLogger(PipHandler.class);

	private static IEventHandlerCreator _actionHandlerCreator;
	private static  IPipManager _pipManager;
	private static InformationFlowModel _ifModel;
	
	//info for PipCacher
	private Map<String, IKey> _predicatesToEvaluate;

	public PipHandler() {
		this(0);
	}
	
	public PipHandler(int pipPersistenceID) {
		EventHandlerManager eventHandlerManager = new EventHandlerManager();
		PipManager pipManager = new PipManager(eventHandlerManager);
		pipManager.initialize(pipPersistenceID);

		_actionHandlerCreator = eventHandlerManager;
		_pipManager = pipManager;
		_ifModel = InformationFlowModel.getInstance();
		_predicatesToEvaluate = new HashMap<String,IKey>();
	}




	public Boolean evaluatePredicatCurrentState(String predicate) {
		// TODO: add code to evaluate generic predicate
		// Note that the three parameters of the predicate (State-based formula,
		// parameter1, parameter2) should be separated by separator1, while list
		// of elements (containers or data) should be separated by separator2
		final String separator1 = "\\|";
		final String separator2 = "#";
		_logger.info("Evaluate Predicate "+predicate+ " in simulated environment");
		
		
		///BEGINNING OF TEST BLOCK
		String contId = _ifModel.getContainerIdByName(new Name(
				"TEST_C"));
		
		if (contId!=null){
			_logger.debug("number of data elements in container TEST_C = "+_ifModel.getDataInContainer(contId).size());
		} else {
			_logger.debug("TEST_C contains no data or no container TEST_C found");
		}
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
			Set<String> s;
			
			String out="Evaluate Predicate "+formula+ " with parameters [" + par1 + "],[" + par2+"] and ["+par3+"]";
			
			switch (formula) {
			case "isNotIn":  //par1 is data, par2 is list of containers
				containers= par2.split(separator2);
				s= _ifModel.getContainersForData(par1);
				//_logger.debug("size of s: "+s.size());
				for (String cont : containers){
					Name pname= new Name(cont);
					//_logger.debug("..in loop("+cont+")..");
					if (s.contains(_ifModel.getContainerIdByNameRelaxed(pname))) {
						_logger.trace(out+"=false");
						return false;
					}
				}
				//_logger.trace("..no match found, returning true");
				_logger.trace(out+"=true");
				return true;
			case "isOnlyIn":
				containers= par2.split(separator2);
				Set<String> limit = new HashSet<String>(Arrays.asList(containers));
				s= _ifModel.getContainersForData(par1);
				//_logger.debug("size of s: "+s.size());
				for (String cont : s){
					Name pname= new Name(cont);
					//_logger.debug("..in loop("+cont+")..");
					if (!(limit.contains(_ifModel.getContainerIdByNameRelaxed(pname)))) {	
						_logger.trace(out+"=false");
						return false;
					}
				}
				//_logger.trace("..no match found, returning true");
				_logger.trace(out+"=false");
				return true;

			case "isCombinedWith":
				Set<String> s1= _ifModel.getContainersForData(par1);
				Set<String> s2=_ifModel.getContainersForData(par2);
				for (String cont : s1){
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

	public Set<IContainer> getContainerForData(IData arg0) {
		if (arg0==null) return null;
		Set<String> contIds= _ifModel.getContainersForData(arg0.getId());
		Set<IContainer> result = new HashSet<IContainer>();
		for (String s: contIds){
			IContainer c = _ifModel.getContainerById(s);
			if (s!=null) result.add(c);
		}
		return result;
	}

	public Set<IData> getDataInContainer(IContainer container) {
		if (container==null) return null;
		Set<String> sd= _ifModel.getDataInContainer(container.getId());
		Set<IData> result = new HashSet<IData>();
		for (String s : sd){
			IData d = _ifModel.getDataById(s);
			if (d!=null) result.add(d);
		}
		return result;
	}

	@Override
	public IStatus notifyActualEvent(IEvent event) {
		String action = event.getPrefixedName();
		_logger.debug("Action name: " + action);
		IEventHandler actionHandler = null;
		try {
			_logger.trace("Create event handler");
			actionHandler = _actionHandlerCreator.createEventHandler(action);
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

    /**
     * If @param event is a desired event, simulates the new state in the PIP, update the cache, and then revert.
     * If @param event is an actual event, does the same, but the PIP remains in the new state.
     * @param event
     * @return
     */
	@Override
	public ICacheUpdate refresh (IEvent e) {
		if (e==null) {
			_logger.error("null event received. returning null");
			return null;
		}
		ICacheUpdate res = new CacheUpdateBasic();
		Map<IKey,Boolean> map=new HashMap<IKey,Boolean>();

		//TODO: fix missing getScopeId. requires implementation of XBEHAV.
			
		res.setMap(map);
		res.setScopeId("<GET SCOPE ID STILL NOT IMPLEMENTED>");
		
		int counter=0;
		_logger.debug("refreshing cache with event "+e);

		if (!e.isActual()){
			_logger.debug("event " + e.getPrefixedName() + " is a desired event. Simulating new state.");
			if (!isSimulating()){
				startSimulation();
			} else {
				_logger.error("Pip is already simulating. returning null");
				return null;
			}
		} else {
			_logger.debug("event " + e.getPrefixedName() + " is an actual event");
		}
		_logger.debug("Updating PIP with event " + e.getPrefixedName() );
		notifyActualEvent(e);			
		_logger.debug("Creating cache response");
		for (String key : _predicatesToEvaluate.keySet()){
			Boolean b = evaluatePredicatCurrentState(key);
			_logger.debug("("+counter+") ["+key+"]="+b);
			map.put(_predicatesToEvaluate.get(key), b);
			counter++;
		}

		if (!e.isActual()){
			_logger.debug("Reverting simulation");
			if (isSimulating()){
				stopSimulation();
			} else {
				_logger.error("Pip is not simulating. ERROR!!!! returning null");
				return null;
			}
		} else {
			_logger.debug("Done!");
		}

		return res;
	}

	@Override
	public IStatus addPredicates(Map<String, IKey> predicates) {
		if (predicates == null){
			_logger.warn("Empty list of predicates!");
			return DummyMessageGen
					.createErrorStatus("Empty list of predicates!");
		}
		if (_predicatesToEvaluate == null) {
			_predicatesToEvaluate = predicates;
			_logger.trace("Succesfully replaced empty list of predicates with one of size " + predicates.size());
			return DummyMessageGen.createOkStatus();
		}
		for (String s : predicates.keySet()) {
			if (!_predicatesToEvaluate.containsKey(s)) {
				_predicatesToEvaluate.put(s, predicates.get(s));
			}
			_logger.trace("Succesfully added list of predicates of length " + predicates.size() + ". New size is "+ _predicatesToEvaluate.size());

		}
		return DummyMessageGen.createOkStatus();
	}

	@Override
	public IStatus revokePredicates(Set<IKey> keys) {
		if (keys == null)
			return DummyMessageGen.createErrorStatus("Empty list of keys!");
		if (_predicatesToEvaluate == null) {
			return DummyMessageGen.createOkStatus();
		}
		for (IKey k : keys) {
			if (_predicatesToEvaluate.containsKey(k)) {
				_predicatesToEvaluate.remove(k);
			}
		}
		return DummyMessageGen.createOkStatus();
	}

	@Override
	public boolean isSimulating() {
		return _ifModel.isSimulating();
	}


	/**
	 * Evaluate the predicate in the state obtained simulating the execution of event.
	 * @return the result of the formula 
	 */
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
	public Boolean evaluatePredicateSimulatingNextState(IEvent event,
			IKey predicate) {
		_logger.info("retrieveing predicate with key "+ predicate);
		
		if (_predicatesToEvaluate.containsValue(predicate)){
			String pred=null;
			for (String s : _predicatesToEvaluate.keySet()){
				if (_predicatesToEvaluate.get(s).equals(predicate)){
					pred=s;
				}
			}
			if (pred==null) {
				_logger.error("impossible to evaluate key "+ predicate +" because it doesn't exists in the PIP. returning null.");
				return null;
			}
			
			return evaluatePredicateSimulatingNextState(event, pred);
		}
		_logger.error("impossible to evaluate key "+ predicate +" because it doesn't exists in the PIP. returning null.");
		return null;
	}

	@Override
	public String getCurrentPipModel() {
		// TODO Auto-generated method stub
		return this._ifModel.toString();
	}
	
}
