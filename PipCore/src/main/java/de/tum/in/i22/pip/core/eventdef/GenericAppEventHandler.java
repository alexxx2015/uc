package de.tum.in.i22.pip.core.eventdef;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.log4j.Logger;

import de.tum.in.i22.pip.core.Scope;
import de.tum.in.i22.pip.core.Scope.scopeType;

public abstract class GenericAppEventHandler extends BaseEventHandler {
	private static final Logger _logger = Logger
			.getLogger(GenericAppEventHandler.class);
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
		
		try {
			delimiter = getParameterValue(_delimiterName);
			getFilename();
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return 0;
		}

		String HRscope = scopeName(delimiter);

		// create the new scope
		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("filename", _filename);

		
		try {
			direction= getParameterValue(_directionName);
			getFilename();
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return 0;
		}
		
		
		Scope.scopeType type=null;
		
		switch (direction){
		case _genericInDirection:
			type=scopeType.GENERIC_IN;
			break;
		case _genericOutDirection:
			type=scopeType.GENERIC_OUT;
			break;
		default:
			//default value.
			//no scope with this value should be opened
			type=scopeType.EMPTY;
		}
		
		if (delimiter.equals(_openDelimiter)) {
			Scope scope = new Scope(HRscope + _filename,
					type, attributes);
			// opening already handled at step 2 of executeEvent in BaseEventHandler
			//			openScope(scope);
			if (_scopesToBeOpened==null) _scopesToBeOpened=new HashSet<Scope>();
			_scopesToBeOpened.add(scope);
		} else if (delimiter.equals(_closeDelimiter)) {
			Scope scope = new Scope(HRscope + _filename,
					type, attributes);
			// closing already handled at step 4 of executeEvent in BaseEventHandler
			//			closeScope(scope);
			if (_scopesToBeClosed==null) _scopesToBeClosed=new HashSet<Scope>();
			_scopesToBeClosed.add(scope);
		}

		return 1;
	}

}
