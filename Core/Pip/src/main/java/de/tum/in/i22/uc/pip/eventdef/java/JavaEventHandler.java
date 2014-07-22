package de.tum.in.i22.uc.pip.eventdef.java;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.Pair;
import de.tum.in.i22.uc.cm.datatypes.basic.ScopeBasic;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IScope;
import de.tum.in.i22.uc.cm.interfaces.informationFlowModel.IInformationFlowModel;
import de.tum.in.i22.uc.cm.pip.interfaces.EScopeState;
import de.tum.in.i22.uc.cm.pip.interfaces.EScopeType;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.pip.eventdef.scope.AbstractScopeEventHandler;

public abstract class JavaEventHandler extends AbstractScopeEventHandler {
protected static Map<String, String> contextToObject = new HashMap<String, String>();

	protected static Map<String, String[]> iFlow = new HashMap<String, String[]>();

	protected static Map<String, Set<IContainer>> containersByPid = new HashMap<String, Set<IContainer>>();

	protected final String _paramId = "id";
	protected final String _paramSignature = "signature";
	protected final String _paramLocation = "location";
	protected final String _paramParamPos = "parampos";
	protected final String _paramType = "type";
	protected final String _paramOffset = "offset";
	protected final String _paramObjectId = "objectId";
	protected final String _paramContextId = "context";
	protected final String _paramContextLocation = "contextLocation";
	protected final String _paramContextOffset = "contextOffset";
	protected final String _paramPID = "PID";
	protected final String _paramThreadId = "ThreadId";

	protected final String _javaIFDelim = ":";
	protected final String _otherDelim = Settings.getInstance()
			.getJoanaPidPoiSeparator();
	protected final String _srcPrefix = "source";
	protected final String _snkPrefix = "sink";

	
	public String scopeName(EScopeType type, String fileDescriptor, String pid) {
		return "Scope for generic "
				+ (type.equals(EScopeType.JBC_GENERIC_IN) ? "source" : "sink")
				+ " event with fileDescriptor + " + fileDescriptor + " (pid "
				+ pid + ")";
	}

	@Override
	public void reset() {
		super.reset();
		// other parameters don't need to be reset cause they are settings
		// values
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

		delimiter = delimiter.toLowerCase();
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
	protected IScope buildScope(EScopeType type) {
		String fileDescriptor;
		// String tid;
		String pid;
		try {
			fileDescriptor = getParameterValue("fileDescriptor");
			// tid = getParameterValue("ThreadId");
			pid = getParameterValue("PID");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return null;
		}

		String HRscope = scopeName(type, fileDescriptor, pid);

		// create the new scope
		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("fileDescriptor", fileDescriptor);
		// attributes.put("tid", tid);
		attributes.put("pid", pid);
		return new ScopeBasic(HRscope, type, attributes);
	}

	/**
	 * This method is invoked by the KillProcess Event Handler from Windows. The
	 * purpose of this method is to clean up the PIP removing all the
	 * Java-specific containers when the process dies.
	 */

	public static void killProcess(String pid,
			IInformationFlowModel _informationFlowModel) {
		if ((pid == null) || (pid.equals(""))) {
			_logger.error("Impossible to kill process with null PID");
			return;
		}

		Set<IContainer> set = containersByPid.get(pid);
		if (set!=null) for (IContainer c : set) _informationFlowModel.remove(c);
		
//		String _otherDelim = Settings.getInstance().getJoanaPidPoiSeparator();
//		for (String entry : iFlow.keySet()) {
//			String[] fields = entry.split(_otherDelim);
//			if (fields[0].equals(pid)) {
//				_informationFlowModel.removeName(new NameBasic(entry), true);
//			}
//		}
	}
}
