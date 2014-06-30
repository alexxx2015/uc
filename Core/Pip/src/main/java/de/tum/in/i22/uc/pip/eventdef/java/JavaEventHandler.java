package de.tum.in.i22.uc.pip.eventdef.java;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.Pair;
import de.tum.in.i22.uc.cm.datatypes.basic.ScopeBasic;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IScope;
import de.tum.in.i22.uc.cm.pip.interfaces.EScopeState;
import de.tum.in.i22.uc.cm.pip.interfaces.EScopeType;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.pip.eventdef.scope.AbstractScopeEventHandler;

public abstract class JavaEventHandler extends AbstractScopeEventHandler {

	protected static Map<String, String> iFlow = new HashMap<String, String>();
	protected final String _paramId = "id";
	protected final String _paramSignature = "signature";
	protected final String _paramLocation = "location";
	protected final String _paramParamPos = "parampos";
	protected final String _paramType = "type";
	protected final String _paramOffset = "offset";

	protected final String _javaIFDelim = ":";
	protected final String _srcPrefix = "src_";
	protected final String _snkPrefix = "snk_";

	public String scopeName(String delimiter, String fileDescriptor, String pid, String threadId) {
		return delimiter.toUpperCase() + " generic scope for fileDescriptor " + fileDescriptor + " (pid="+pid+",tid="+threadId+")";
	}

	/*
	 * For this generic action the scope is only one and the "delimiter"
	 * (start/end) is given as a parameter
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

		delimiter=delimiter.toLowerCase();
		IScope scope = buildScope(delimiter);

		if (scope == null) {
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
			res.add(new Pair<EScopeState, IScope>(EScopeState.CLOSE, scope));
		}

		return res;
	}

	protected IScope buildScope(String delimiter) {
		// TO BE OVERRIDDEN BY SUBCLASSES SOURCE AND SINK
		return null;
	}

	/*
	 * Auxiliary function to support the creation of a scope for sources and
	 * sinks
	 */
	protected IScope buildScope(String delimiter, EScopeType type) {
		String fileDescriptor;
		String tid;
		String pid;
		try {
			fileDescriptor = getParameterValue("fileDescriptor");
			tid = getParameterValue("ThreadId");
			pid = getParameterValue("Pid");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			 return null;
		}

		String HRscope = scopeName(delimiter, fileDescriptor, pid, tid);

		// create the new scope
		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("fileDescriptor", fileDescriptor);
		attributes.put("tid", tid);
		attributes.put("pid", pid);
		return new ScopeBasic(HRscope, type, attributes);
	}

}
