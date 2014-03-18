package de.tum.in.i22.pdp.pipcacher;

import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import testutil.DummyMessageGen;

import com.google.inject.Inject;

import de.tum.in.i22.pip.core.IPipCacher2Pip;
import de.tum.in.i22.uc.cm.datatypes.ICacheUpdate;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IKey;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.interfaces.IPdpCore2PipCacher;
import de.tum.in.i22.uc.cm.interfaces.IPdpEngine2PipCacher;


public class PipCacherImpl implements IPdpCore2PipCacher,IPdpEngine2PipCacher {
	private static Logger _logger = Logger.getLogger(PipCacherImpl.class);
	private static IPipCacher2Pip _pip; 
	
	private static Map<String, IKey> _predicatesToEvaluate;
	private static ICacheUpdate _cache;
	
	@Inject
	public PipCacherImpl(IPipCacher2Pip pipHandler){
		_pip = pipHandler;
	}
	
    /**
     * If @param event is a desired event, simulates the new state in the PIP, update the cache, and then revert.
     * If @param event is an actual event, does the same, but the PIP remains in the new state.
     * @param event
     * @return
     */
	@Override
	public IStatus refresh(IEvent desiredEvent) {
		if (desiredEvent!= null) _logger.debug("refresh ("+desiredEvent.getPrefixedName()+") invoked");
		else _logger.debug("refresh (null) invoked");

		ICacheUpdate newCache=_pip.refresh(desiredEvent);
		if (newCache!=null){
			_logger.debug("newCache != null");
			_cache=newCache;
			return DummyMessageGen.createOkStatus();
		} else {
			_logger.debug("newCache still == null");
		}
		return DummyMessageGen.createErrorStatus("PIP returned empty cache on update!");
	}
	
	@Override
	public IStatus addPredicates(Map<String, IKey> predicates) {
		_logger.debug("addPredicates invoked");
		IStatus status=_pip.addPredicates(predicates);
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
	public Boolean eval(IKey key) {
		_logger.debug("eval("+key+") invoked");
		if (key==null) {
			return null; 
		}
		if (_cache !=null) {
			Boolean res=_cache.getVal(key);
			_logger.debug("eval key ["+key+"] =" +res);
			return res; 
		} else{
			_logger.debug("cache is null!");
		}
		return null;
	}

	@Override
	public Boolean eval(IKey key, IEvent event2Simulate) {
		if (_pip.isSimulating()){
			_logger.error("Not possible to simulate event "+event2Simulate+" while PIP is already simulating. Return null.");
			return null;
		} else {
			_pip.startSimulation();
			IStatus status=_pip.notifyActualEvent(event2Simulate);
			Boolean result=null;
			if (status.isSameStatus(DummyMessageGen.createOkStatus())){
				_logger.trace("Simulating execution of event " + event2Simulate);
				result= eval(key);
				_logger.trace("Evaluation of predicate [" + key +"] after simulation of execution of event " + event2Simulate +" = "+result);
			} else {
				_logger.error ("Not possible to simulate event "+ event2Simulate+". Return null");
				return null;
			}
			_pip.stopSimulation();
			return result;
		}
	}

	@Override
	public String getScopeId() {
		return _cache.getScopeId(); 
	}

	@Override
	public boolean isSimulating() {
		return _pip.isSimulating();
	}

	@Override
	public Boolean evaluatePredicateSimulatingNextState(IEvent event, String predicate) {
		_logger.debug("Method evaluatePredicate(E,P) invoked. Bypassing PipCacher and invoking it directly on Pip...");
		return _pip.evaluatePredicateSimulatingNextState(event, predicate);
	}

	@Override
	public Boolean evaluatePredicateSimulatingNextState(IEvent event, IKey predicate) {
		_logger.debug("Method evaluatePredicate(E,P) invoked. Bypassing PipCacher and invoking it directly on Pip...");
		return _pip.evaluatePredicateSimulatingNextState(event, predicate);
	}

	@Override
	public Boolean evaluatePredicateCurrentState(String predicate) {
		_logger.debug("Method evaluatePredicate(P) invoked. Bypassing PipCacher and invoking it directly on Pip...");
		return _pip.evaluatePredicatCurrentState(predicate);
	}

	@Override
	public Set<IData> getDataInContainer(IContainer container) {
		_logger.debug("Method getDataInContainer invoked. Bypassing PipCacher and invoking it directly on Pip...");
		return _pip.getDataInContainer(container);
	}

	@Override
	public Set<IContainer> getContainerForData(IData data) {
		_logger.debug("Method getContainerForData invoked. Bypassing PipCacher and invoking it directly on Pip...");
		return	_pip.getContainerForData(data);
	}

	@Override
	public IStatus revokePredicates(Set<IKey> keys) {
		//no need to store the result of revoke _pip.revokePredicate, because it should behave exactly like this local function
		_pip.revokePredicates(keys);
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
	public String getCurrentPipModel() {
		// TODO Auto-generated method stub
		return this._pip.getCurrentPipModel();
	}

	@Override
	public void populatePip(String predicate) {
		// TODO Auto-generated method stub
		this._pip.populate(predicate);
	}

}
