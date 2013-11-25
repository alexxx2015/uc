package de.tum.in.i22.pip.core.eventdef;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.log4j.Logger;

import de.tum.in.i22.pip.core.Scope;

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

		if (delimiter.equals(_openDelimiter)) {
			Scope scope = new Scope(HRscope + _filename,
					Scope.scopeType.GENERIC_IN, attributes);
			openScope(scope);
			_scopesToBeOpened=new HashSet<Scope>();
			_scopesToBeOpened.add(scope);
		} else if (delimiter.equals(_closeDelimiter)) {
			Scope scope = new Scope(HRscope + _filename,
					Scope.scopeType.GENERIC_OUT, attributes);
			closeScope(scope);
			_scopesToBeClosed=new HashSet<Scope>();
			_scopesToBeClosed.add(scope);
		}

		return 1;
	}

}
