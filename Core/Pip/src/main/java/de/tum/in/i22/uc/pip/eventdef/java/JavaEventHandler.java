package de.tum.in.i22.uc.pip.eventdef.java;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.Pair;
import de.tum.in.i22.uc.cm.datatypes.basic.ScopeBasic;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IScope;
import de.tum.in.i22.uc.cm.datatypes.java.ArrayName;
import de.tum.in.i22.uc.cm.datatypes.java.InstanceMethodVariableName;
import de.tum.in.i22.uc.cm.datatypes.java.ObjectName;
import de.tum.in.i22.uc.cm.interfaces.informationFlowModel.IInformationFlowModel;
import de.tum.in.i22.uc.cm.pip.interfaces.EScopeState;
import de.tum.in.i22.uc.cm.pip.interfaces.EScopeType;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.pip.eventdef.scope.AbstractScopeEventHandler;

public abstract class JavaEventHandler extends AbstractScopeEventHandler {

    protected static Map<String, String[]> iFlow = new HashMap<String, String[]>();

    protected static Map<String, Set<IContainer>> containersByPid = new HashMap<String, Set<IContainer>>();

    protected static Map<String, String> contextToObject = new HashMap<String, String>();

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
    protected final String _otherDelim = Settings.getInstance().getJoanaPidPoiSeparator();
    protected final String _srcPrefix = "source";
    protected final String _snkPrefix = "sink";

    protected final String DLM = "|";
    protected final String RET = "ret";
    protected final String OBJ = "obj";
    protected final String NOADDRESS = "implicit";

    public String scopeName(EScopeType type, String fileDescriptor, String pid) {
	return "Scope for generic " + (type.equals(EScopeType.JBC_GENERIC_LOAD) ? "source" : "sink")
		+ " event with fileDescriptor + " + fileDescriptor + " (pid " + pid + ")";
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

    public static void killProcess(String pid, IInformationFlowModel _informationFlowModel) {
	if ((pid == null) || (pid.equals(""))) {
	    _logger.error("Impossible to kill process with null PID");
	    return;
	}

	Set<IContainer> set = containersByPid.get(pid);
	if (set != null)
	    for (IContainer c : set)
		_informationFlowModel.remove(c);

	// String _otherDelim =
	// Settings.getInstance().getJoanaPidPoiSeparator();
	// for (String entry : iFlow.keySet()) {
	// String[] fields = entry.split(_otherDelim);
	// if (fields[0].equals(pid)) {
	// _informationFlowModel.removeName(new NameBasic(entry), true);
	// }
	// }
    }

    private String getPID() {
	try {
	    return getParameterValue("processId");
	} catch (ParameterNotFoundException e) {
	    // TODO Auto-generated catch block
	    _logger.error(e.getMessage());
	}
	return null;
    }

    /**
     * class|address => true class|null => false everything else => false
     * 
     * @param objectAtAddress
     * @return
     */
    protected boolean isReferenceType(String objectAtAddress) {
	String[] comps = objectAtAddress.split("\\" + DLM);
	return comps.length == 2 && !comps[1].equals("null");
    }
    
    protected boolean isArray(String objectAtAddress) {
	String[] comps = objectAtAddress.split("\\" + DLM);
	return comps.length == 2 && !comps[1].equals("null") && comps[0].contains("[");
    }

    private static boolean isInteger(String s) {
	try {
	    Integer.parseInt(s);
	} catch (NumberFormatException e) {
	    return false;
	}
	return true;
    }

    private boolean isArrayElement(String objectAtAddress) {
	String[] comps = objectAtAddress.split("\\" + DLM);
	return comps.length == 3 && !comps[1].equals("null") && isInteger(comps[2]);
    }

    /**
     * Replaces all occurences of an uninitialized object identifier in names
     * (and containers) for an object whose constructor was called but has not
     * returned yet. This method is called in every event handler and ensures
     * that the model objects and names containing the object reference added by
     * a constructor call get the correct address. Long scan of the names is
     * only done once (ensured by checking if the name of the object itself has
     * been fixed).
     * 
     * @param threadId
     * @param pid
     * @param className
     * @param objectAddress
     * @param methodName
     */
    protected void addAddressToNamesAndContainerIfNeeded(String threadId, String pid, String className,
	    String objectAddress, String methodName) {
	if (objectAddress.equals("null"))
	    return;
	if (methodName.startsWith("<init>")) {
	    String addressReplacement = threadId + NOADDRESS;

	    // object
	    IName oldObjectName = new ObjectName(pid, className, addressReplacement);
	    IContainer oldObjectContainer = _informationFlowModel.getContainer(oldObjectName);
	    if (oldObjectContainer != null) {
		IName newObjectName = new ObjectName(pid, className, objectAddress);
		IContainer newObjectContainer = addContainerIfNotExists(newObjectName, className + DLM + objectAddress,
			null);
		_informationFlowModel.addData(_informationFlowModel.getData(oldObjectContainer), newObjectContainer);
		for (IContainer aliasFrom : _informationFlowModel.getAliasesFrom(oldObjectContainer)) {
		    _informationFlowModel.addAlias(aliasFrom, newObjectContainer);
		}
		for (IContainer aliasTo : _informationFlowModel.getAliasesTo(oldObjectContainer)) {
		    _informationFlowModel.addAlias(newObjectContainer, aliasTo);
		}
		for (IName oldName : _informationFlowModel.getAllNames(oldObjectContainer)) {
		    _informationFlowModel.addName(oldName, newObjectContainer, false);
		}
		_informationFlowModel.removeName(oldObjectName, true);
	    } else
		return; // if there is no uninitialized object, then the
			// replacement was already done

	    // method locals
	    Set<InstanceMethodVariableName> namesToReplace = new HashSet<>();
	    for (IName name : _informationFlowModel.getAllNames()) {
		if (name instanceof InstanceMethodVariableName) {
		    InstanceMethodVariableName iVarName = (InstanceMethodVariableName) name;
		    if (iVarName.getPid().equals(pid) && iVarName.getThreadId().equals(threadId)
			    && iVarName.getClassName().equals(className)
			    && iVarName.getObjectAddress().equals(addressReplacement)) {
			namesToReplace.add(iVarName);
		    }
		}
	    }
	    for (InstanceMethodVariableName oldName : namesToReplace) {
		IName fixedName = new InstanceMethodVariableName(oldName.getPid(), oldName.getThreadId(),
			oldName.getClassName(), objectAddress, oldName.getMethodName(),
			oldName.getVarName());
		_informationFlowModel.addName(oldName, fixedName);
		_informationFlowModel.removeName(oldName);
	    }
	}
    }

    protected boolean containerIsArray(IContainer container) {
	return container != null && container.getId().contains(DLM) && container.getId().contains("[");
    }

    protected boolean containerIsReference(IContainer container) {
	return container != null && container.getId().contains(DLM);
    }

    protected IContainer addContainerIfNotExists(IName name, String identifier) {
	return addContainerIfNotExists(name, identifier, null);
    }

    protected IContainer addContainerIfNotExists(IName name) {
	return addContainerIfNotExists(name, null, null);
    }

    // if identifier is class@address, then create additional name to identify
    // container globally
    protected IContainer addContainerIfNotExists(IName name, String globalIdentifier, IContainer aliasToContainer) {
	IContainer container = _informationFlowModel.getContainer(name);
	if (container == null) {
	    if (globalIdentifier != null && (isReferenceType(globalIdentifier) || isArrayElement(globalIdentifier))) {
		// store reference type containers process-wide
		container = _messageFactory.createContainer(globalIdentifier);
		IName globalObjectName;
		if (isArray(globalIdentifier)) {
		    globalObjectName = new ArrayName(getPID() + DLM + globalIdentifier);
		} else {
		    globalObjectName = new ObjectName(getPID() + DLM + globalIdentifier);
		}
		_informationFlowModel.addName(globalObjectName, container, false);
	    } else {
		container = _messageFactory.createContainer();
	    }
	    _informationFlowModel.addName(name, container, false);
	}
	_informationFlowModel.addAlias(container, aliasToContainer);
	return container;
    }

    /**
     * Traverses the alias graph and returns all data being "visible" in
     * {@code container} in respect to the alias function.
     * 
     * @param container
     * @return
     */
    protected Set<IData> getDataTransitively(IContainer container) {
	Set<IData> result = new HashSet<>();
	if (container == null) {
	    return result;
	}

	result.addAll(_informationFlowModel.getData(container));

	for (IContainer c : _informationFlowModel.getAliasesTo(container)) {
	    result.addAll(getDataTransitively(c));
	}

	return result;
    }
}
