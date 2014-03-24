package de.tum.in.i22.uc.pip.core.eventdef.scope;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import de.tum.in.i22.uc.pip.core.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.pip.core.scope.Scope;
import de.tum.in.i22.uc.pip.interfaces.EScopeType;

public abstract class GenericAppEventHandler extends AbstractScopeEventHandler {
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
	public int createScope() {
		String delimiter = null;
		String direction = null;
		String filename = null;

		try {
			delimiter = getParameterValue(_delimiterName);
			direction= getParameterValue(_directionName);
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

		switch (direction) {
		case _genericInDirection:
			type = EScopeType.GENERIC_IN;
			break;
		case _genericOutDirection:
			type = EScopeType.GENERIC_OUT;
			break;
		default:
			// default value.
			// no scope with this value should be opened
			type = EScopeType.EMPTY;
		}

		if (delimiter.equals(_openDelimiter)) {
			Scope scope = new Scope(HRscope + filename,
					type, attributes);
			// opening already handled at step 2 of executeEvent in BaseEventHandler
			//			openScope(scope);
			if (_scopesToBeOpened==null) _scopesToBeOpened=new HashSet<Scope>();
			_scopesToBeOpened.add(scope);
		} else if (delimiter.equals(_closeDelimiter)) {
			Scope scope = new Scope(HRscope + filename,
					type, attributes);
			// closing already handled at step 4 of executeEvent in BaseEventHandler
			//			closeScope(scope);
			if (_scopesToBeClosed==null) _scopesToBeClosed=new HashSet<Scope>();
			_scopesToBeClosed.add(scope);
		}

		return 1;
	}

}
