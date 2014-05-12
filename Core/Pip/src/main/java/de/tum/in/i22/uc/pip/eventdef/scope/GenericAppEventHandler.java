package de.tum.in.i22.uc.pip.eventdef.scope;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.Pair;
import de.tum.in.i22.uc.cm.datatypes.basic.ScopeBasic;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IScope;
import de.tum.in.i22.uc.cm.pip.interfaces.EBehavior;
import de.tum.in.i22.uc.cm.pip.interfaces.EScopeState;
import de.tum.in.i22.uc.cm.pip.interfaces.EScopeType;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

abstract class GenericAppEventHandler extends AbstractScopeEventHandler {
	/*
	 * This function should be overridden by single events class
	 */
	public String scopeName(String delimiter, String filename) {
		return delimiter.toUpperCase() + " generic scope for file "+ filename;
	}

	private IScope buildScope(String delimiter) {
		String direction = null;
		String filename = null;

		try {
			direction = getParameterValue(_directionName);
			filename = getParameterValue("filename");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return null;
		}

		String HRscope = scopeName(delimiter,filename);

		// create the new scope
		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("filename", filename);


		EScopeType type = null;
		
		if (direction.equals(_genericInDirection))
			type = EScopeType.JBC_GENERIC_IN;
		else if (direction.equals(_genericOutDirection))
			type = EScopeType.JBC_GENERIC_OUT;
		else
			// default value.
			// no scope with this value should be opened
			type = EScopeType.EMPTY;
		return new ScopeBasic(HRscope + filename, type,
				attributes);
	}

	
	
	/*
	 * For this generic action the scope is only one and the "direction"
	 * (open/close) is given as a parameter
	 * 
	 * @see de.tum.in.i22.pip.core.eventdef.BaseEventHandler#createScope()
	 */
	@Override
	protected Set<Pair<EScopeState, IScope>> XDelim(IEvent event) {
		String delimiter = null;
		try {
			delimiter = getParameterValue(_delimiterName);
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return null;
		}

		IScope scope = buildScope(delimiter);
		
		if (scope==null){
			return new HashSet<Pair<EScopeState, IScope>>();
		}
		
		Set<Pair<EScopeState, IScope>> res = new HashSet<Pair<EScopeState, IScope>>();
		if (delimiter.equals(_openDelimiter)) {
			// opening already handled at step 2 of executeEvent in
			// BaseEventHandler
			// openScope(scope);
			res.add(new Pair<EScopeState, IScope>(EScopeState.OPEN, scope));
		} else if (delimiter.equals(_closeDelimiter)) {
			// closing already handled at step 4 of executeEvent in
			// BaseEventHandler
			// closeScope(scope);
			res.add(new Pair<EScopeState, IScope>(EScopeState.CLOSED, scope));
		}

		return res;
	}
	
	@Override
	protected Pair<EBehavior, IScope> XBehav(IEvent event) {
		String delimiter = null;
		try {
			delimiter = getParameterValue(_delimiterName);
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return null;
		}
		IScope scope = buildScope(delimiter);
		if (scope==null)return new Pair<EBehavior, IScope>(EBehavior.UNKNOWN, null);
		if (scope.getScopeType().equals(EScopeType.JBC_GENERIC_IN))return new Pair<EBehavior, IScope>(EBehavior.IN, scope);
		if (scope.getScopeType().equals(EScopeType.JBC_GENERIC_OUT))return new Pair<EBehavior, IScope>(EBehavior.OUT, scope);
		//this line should never be reached
		return new Pair<EBehavior, IScope>(EBehavior.UNKNOWN, null);
	}


}
