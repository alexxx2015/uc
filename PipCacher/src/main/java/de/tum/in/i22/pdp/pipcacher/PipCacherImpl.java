package de.tum.in.i22.pdp.pipcacher;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import testutil.DummyMessageGen;

import de.tum.in.i22.pip.PipController;
import de.tum.in.i22.pip.core.IPipCacher2Pip;
import de.tum.in.i22.pip.core.PipHandler;
import de.tum.in.i22.pip.core.manager.IPipManager;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IKey;
import de.tum.in.i22.uc.cm.datatypes.IStatus;


public class PipCacherImpl implements IPdpCore2PipCacher,IPdpEngine2PipCacher {
	private static Logger _logger = Logger.getLogger(PipCacherImpl.class);
	
	private static PipCacherImpl _reference;
	private static IPipCacher2Pip _pip; 
	
	private static Map<IKey, String> _predicatesToEvaluate;
	
	private PipCacherImpl(){
		_pip=PipHandler.getInstance();
	}
	
	public static PipCacherImpl getReference(){
		if (_reference==null) _reference=new PipCacherImpl();
		return _reference;
	}
	
	@Override
	public IStatus refresh(IEvent desiredEvent) {
		_pip.refresh(desiredEvent);
		
		return null;
	}
	
	@Override
	public IStatus addPredicates(Map<IKey, String> predicates) {
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
		for (IKey k : predicates.keySet()) {
			if (!_predicatesToEvaluate.containsKey(k)) {
				_predicatesToEvaluate.put(k, predicates.get(k));
			}
			_logger.trace("Succesfully added list of predicates of length " + predicates.size() + ". New size is "+ _predicatesToEvaluate.size());

		}
		return DummyMessageGen.createOkStatus();
	}

	
	@Override
	public IStatus revokePredicates(List<IKey> keys) {
		//TODO: add logger output
		IStatus status = _pip.revokePredicates(keys);
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
	public boolean eval(IKey key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean eval(IKey key, IEvent event2Simulate) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getScopeId(IEvent event) {
		return ""; 
	}

	@Override
	public boolean isSimulating() {
		return _pip.isSimulating();
	}

	@Override
	public Boolean evaluatePredicate(IEvent event, String predicate) {
		return _pip.evaluatePredicate(event, predicate);
	}

	@Override
	public Boolean evaluatePredicate(String predicate) {
		return _pip.evaluatePredicate(predicate);
	}

	@Override
	public Set<IData> getDataInContainer(IContainer container) {
		return _pip.getDataInContainer(container);
	}

	@Override
	public Set<IContainer> getContainerForData(IData data) {
		return	_pip.getContainerForData(data);
	}

}
