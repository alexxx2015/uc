package de.tum.in.i22.uc.pip.eventdef.thunderbird;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.Pair;
import de.tum.in.i22.uc.cm.datatypes.basic.ScopeBasic;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IScope;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.pip.interfaces.EBehavior;
import de.tum.in.i22.uc.cm.pip.interfaces.EScopeState;
import de.tum.in.i22.uc.cm.pip.interfaces.EScopeType;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.pip.eventdef.scope.AbstractScopeEventHandler;

public class LoadEventHandler extends AbstractScopeEventHandler {
	private String _delimiter = null;

	public LoadEventHandler() {
		super();
	}

	private IScope buildScope() {
		String filename;
		try {
			filename = getParameterValue("filename");

		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return null;
		}

		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("app", "Thunderbird");
		attributes.put("filename", filename);

		return new ScopeBasic("TB loading file " + filename,
				EScopeType.LOAD_FILE, attributes);
	}

	@Override
	protected Set<Pair<EScopeState, IScope>> XDelim(IEvent event) {
		try {
			_delimiter = getParameterValue(_delimiterName);

		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return new HashSet<Pair<EScopeState, IScope>>();
		}
		IScope scope = buildScope();
		if (scope==null){
			return new HashSet<Pair<EScopeState, IScope>>();
		}
		Set<Pair<EScopeState, IScope>> res = new HashSet<Pair<EScopeState, IScope>>();
		if (_delimiter.equals(_openDelimiter)) {
			res.add(new Pair<EScopeState, IScope>(EScopeState.OPEN, scope));
			return res;
		} else if (_delimiter.equals(_closeDelimiter)) {
			res.add(new Pair<EScopeState, IScope>(EScopeState.CLOSED, scope));
			return res;
		}
		return res;
	}

	@Override
	protected Pair<EBehavior, IScope> XBehav(IEvent event) {
		IScope scope = buildScope();
		if (scope==null)return new Pair<EBehavior, IScope>(EBehavior.UNKNOWN, null);
		return new Pair<EBehavior, IScope>(EBehavior.OUT, scope);
	}

	@Override
	protected IStatus update() {
		return null;
	}

}
