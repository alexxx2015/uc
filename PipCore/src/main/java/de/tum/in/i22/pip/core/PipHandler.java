package de.tum.in.i22.pip.core;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import testutil.DummyMessageGen;
import de.tum.in.i22.pip.core.eventdef.DefaultEventHandler;
import de.tum.in.i22.pip.core.manager.EventHandlerManager;
import de.tum.in.i22.pip.core.manager.IEventHandlerCreator;
import de.tum.in.i22.pip.core.manager.IPipManager;
import de.tum.in.i22.pip.core.manager.PipManager;
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

	private IEventHandlerCreator _actionHandlerCreator;
	private IPipManager _pipManager;
	private InformationFlowModel _ifModel;
	private Map<IKey, String> _predicatesToEvaluate;

	private static PipHandler _instance = null;

	public static PipHandler getInstance() {
		if (_instance == null) {
			_instance = new PipHandler();
		}
		return _instance;
	}

	private PipHandler() {
		EventHandlerManager eventHandlerManager = new EventHandlerManager();
		PipManager pipManager = new PipManager(eventHandlerManager);
		pipManager.initialize();

		_actionHandlerCreator = eventHandlerManager;
		_pipManager = pipManager;
		_ifModel = InformationFlowModel.getInstance();
		_predicatesToEvaluate = new HashMap<IKey, String>();
	}


	/**
	 * Evaluate the predicate in the state obtained simulating the execution of event.
	 * @return the result of the formula 
	 */
	public Boolean evaluatePredicate(IEvent event, String predicate){
		_logger.info("Saving PIP current state");
		if (_ifModel.push()) {
			_logger.trace("Updating PIP semantics with current event ("
					+ (event == null ? "null" : event.getName()) + ")");
			notifyActualEvent(event);
			_logger.trace("Evaluate predicate in new updated state ("
					+ predicate + ")");
			Boolean res = evaluatePredicate(predicate);
			_logger.trace("Restoring PIP previous state...");
			_ifModel.pop();
			_logger.trace("done!");
			return res;
		}
		_logger.error("Failed! Stack not empty!");
		return null;
	}

	public Boolean evaluatePredicate(String predicate) {
		// TODO: add code to evaluate generic predicate
		// Note that the three parameters of the predicate (State-based formula,
		// parameter1, parameter2) should be separated by separator1, while list
		// of elements (containers or data) should be separated by separator2
		String separator1 = "\\|";
		String separator2 = ":";
		_logger.info("Evaluate Predicate "+predicate+ " in simulated environment");
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
			
			System.err.println("Evaluate Predicate "+formula+ " with parameters [" + par1 + "],[" + par2+"] and ["+par3+"]");
			
			switch (formula) {
			case "isNotIn":  //par1 is data, par2 is list of containers
				containers= par2.split(separator2);
				s= _ifModel.getContainersForData(par1);
				_logger.debug("size of s: "+s.size());
				for (String cont : containers){
					Name pname= new Name(cont);
					_logger.debug("..in loop("+cont+")..");
					if (s.contains(_ifModel.getContainerIdByNameRelaxed(pname))) return false;
				}
				_logger.trace("..no match found, returning true");
				return true;
			case "isOnlyIn":
				containers= par2.split(separator2);
				Set<String> limit = new HashSet<String>(Arrays.asList(containers));
				s= _ifModel.getContainersForData(par1);
				_logger.debug("size of s: "+s.size());
				for (String cont : s){
					Name pname= new Name(cont);
					_logger.debug("..in loop("+cont+")..");
					if (!(limit.contains(_ifModel.getContainerIdByNameRelaxed(pname)))) return false;
				}
				_logger.trace("..no match found, returning true");
				return true;

			case "isCombinedWith":
				Set<String> s1= _ifModel.getContainersForData(par1);
				Set<String> s2=_ifModel.getContainersForData(par2);
				for (String cont : s1){
					if (s2.contains(cont)) return true;
				}
				return false;
				
				
			default:
				return null;
			}

		} else
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
		String action = event.getName();
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

	@Override
	public ICacheUpdate update(IEvent e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus addPredicates(Map<IKey, String> predicates) {
		if (predicates == null)
			return DummyMessageGen
					.createErrorStatus("Empty list of predicates!");
		if (_predicatesToEvaluate == null) {
			_predicatesToEvaluate = predicates;
			return DummyMessageGen.createOkStatus();
		}
		for (IKey k : predicates.keySet()) {
			if (!_predicatesToEvaluate.containsKey(k)) {
				_predicatesToEvaluate.put(k, predicates.get(k));
			}
		}
		return DummyMessageGen.createOkStatus();
	}

	@Override
	public IStatus revokePredicates(List<IKey> keys) {
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
}
