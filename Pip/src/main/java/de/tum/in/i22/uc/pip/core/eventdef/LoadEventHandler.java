package de.tum.in.i22.uc.pip.core.eventdef;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.pip.core.Scope;

public class LoadEventHandler extends BaseEventHandler {
	private String _delimiter = null;

	public LoadEventHandler() {
		super();
	}

	@Override
	public int createScope() {
		String filename;

		try {
			_delimiter = getParameterValue(_delimiterName);
			filename = getParameterValue("filename");

		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return -1;
		}

		// check whether a scope with this loading have been already started
		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("app", "Thunderbird");
		attributes.put("filename", filename);


		Scope scope = new Scope("TB loading file " + filename + " OPEN",
				Scope.scopeType.GENERIC_IN, attributes);

		if (_delimiter.equals(_openDelimiter)) {
			if (_scopesToBeOpened==null) _scopesToBeOpened = new HashSet<Scope>();
			_scopesToBeOpened.add(scope);
			return 1;

		} else if (_delimiter.equals(_closeDelimiter)) {
			if (_scopesToBeClosed==null) _scopesToBeClosed = new HashSet<Scope>();
			_scopesToBeClosed.add(scope);
			return 1;
		}
		return -1;
	}

	@Override
	public IStatus execute() {
		return null;
	}

	// @Override
	// public IStatus execute() {
	// // TODO: implement the TB internal behavior of the LOAD function
	//
	// return _messageFactory.createStatus(EStatus.OKAY);
	// }

}
