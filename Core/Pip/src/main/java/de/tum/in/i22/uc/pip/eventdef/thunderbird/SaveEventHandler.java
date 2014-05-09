package de.tum.in.i22.uc.pip.eventdef.thunderbird;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import de.tum.in.i22.uc.cm.datatypes.basic.ScopeBasic;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.pip.interfaces.EScopeType;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.pip.eventdef.scope.AbstractScopeEventHandler;

public class SaveEventHandler extends AbstractScopeEventHandler {
	private String _delimiter = null;

	public SaveEventHandler() {
		super();
	}

	@Override
	protected int createScope() {
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


		ScopeBasic scope = new ScopeBasic("TB saving file " + filename + " SAVE",
				EScopeType.GENERIC_OUT, attributes);

		if (_delimiter.equals(_openDelimiter)) {
			if (_scopesToBeOpened==null) _scopesToBeOpened = new HashSet<ScopeBasic>();
			_scopesToBeOpened.add(scope);
			return 1;

		} else if (_delimiter.equals(_closeDelimiter)) {
			if (_scopesToBeClosed==null) _scopesToBeClosed = new HashSet<ScopeBasic>();
			_scopesToBeClosed.add(scope);
			return 1;
		}
		return -1;
	}

	@Override
	protected IStatus update() {
		return null;
	}

	// @Override
	// public IStatus execute() {
	// // TODO: implement the TB internal behavior of the LOAD function
	//
	// return _messageFactory.createStatus(EStatus.OKAY);
	// }

}
