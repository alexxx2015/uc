package de.tum.in.i22.uc.pip.eventdef.scope;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import de.tum.in.i22.uc.cm.datatypes.basic.ScopeBasic;
import de.tum.in.i22.uc.cm.pip.interfaces.EScopeType;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

abstract class GenericAppEventHandler extends AbstractScopeEventHandler {
	/*
	 * This function should be overridden by single events class
	 */
	public String scopeName(String delimiter) {
		return delimiter.toUpperCase() + " generic scope for file ";
	}

	/*
	 * For this generic action the scope is only one and the "direction"
	 * (open/close) is given as a parameter
	 * 
	 * @see de.tum.in.i22.pip.core.eventdef.BaseEventHandler#createScope()
	 */
	@Override
	protected int createScope() {
		String delimiter = null;
		String direction = null;
		String filename = null;

		try {
			delimiter = getParameterValue(_delimiterName);
			direction = getParameterValue(_directionName);
			filename = getParameterValue("filename");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return 0;
		}

		String HRscope = scopeName(delimiter);

		// create the new scope
		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("filename", filename);


		EScopeType type = null;
		
		if (direction.equals(_genericInDirection))
			type = EScopeType.GENERIC_IN;
		else if (direction.equals(_genericOutDirection))
			type = EScopeType.GENERIC_OUT;
		else
			// default value.
			// no scope with this value should be opened
			type = EScopeType.EMPTY;

		if (delimiter.equals(_openDelimiter)) {
			ScopeBasic scope = new ScopeBasic(HRscope + filename, type,
					attributes);
			// opening already handled at step 2 of executeEvent in
			// BaseEventHandler
			// openScope(scope);
			if (_scopesToBeOpened == null)
				_scopesToBeOpened = new HashSet<ScopeBasic>();
			_scopesToBeOpened.add(scope);
		} else if (delimiter.equals(_closeDelimiter)) {
			ScopeBasic scope = new ScopeBasic(HRscope + filename, type,
					attributes);
			// closing already handled at step 4 of executeEvent in
			// BaseEventHandler
			// closeScope(scope);
			if (_scopesToBeClosed == null)
				_scopesToBeClosed = new HashSet<ScopeBasic>();
			_scopesToBeClosed.add(scope);
		}

		return 1;
	}

}
