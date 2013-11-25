package de.tum.in.i22.pip.core.eventdef;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.log4j.Logger;

import de.tum.in.i22.pip.core.Scope;

public class LoadActionHandler extends BaseEventHandler {
	private static final Logger _logger = Logger
			.getLogger(KillProcessActionHandler.class);

	private String _delimiter = null;

	public LoadActionHandler() {
		super();
	}

	@Override
	public int createScope() {
		_logger.info("Load action handler execute");

		try {
			_delimiter = getParameterValue(_delimiterName);
			getFilename();

		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return -1;
		}

		// check whether a scope with this loading have been already started
		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("app", "Thunderbird");
		attributes.put("filename", _filename);

		if (_delimiter.equals(_openDelimiter)) {
			Scope scope = new Scope("TB loading file " + _filename + " OPEN",
					Scope.scopeType.GENERIC_IN, attributes);
			openScope(scope);
			_scopesToBeOpened = new HashSet<Scope>();
			_scopesToBeOpened.add(scope);
			return 1;

		} else if (_delimiter.equals(_closeDelimiter)) {
			Scope scope = new Scope("TB loading file " + _filename + " CLOSE",
					Scope.scopeType.GENERIC_OUT, attributes);
			closeScope(scope);
			_scopesToBeClosed = new HashSet<Scope>();
			_scopesToBeClosed.add(scope);
			return 1;
		}
		return -1;
	}

//	@Override
//	public IStatus execute() {
//		// TODO: implement the TB internal behavior of the LOAD function
//		
//		return _messageFactory.createStatus(EStatus.OKAY);
//	}

}
