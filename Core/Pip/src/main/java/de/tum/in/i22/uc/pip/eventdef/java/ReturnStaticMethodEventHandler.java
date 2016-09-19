package de.tum.in.i22.uc.pip.eventdef.java;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.tum.in.i22.uc.cm.datatypes.basic.DataBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.datatypes.java.containers.ReferenceContainer;
import de.tum.in.i22.uc.cm.datatypes.java.data.SourceData;
import de.tum.in.i22.uc.cm.factories.JavaNameFactory;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.pip.eventdef.java.chopnode.CallChopNodeLabel;

public class ReturnStaticMethodEventHandler extends ReturnMethodEventHandler {

    @SuppressWarnings("unchecked")
    @Override
    protected IStatus update() {

	String threadId = null;
	String pid = null;
	String parentObjectAddress = null;
	String parentClass = null;
	String parentMethod = null;
	String calleeClass = null;
	String calledMethod = null;
	String returnValueClass = null;
	String returnValueAddress = null;
	Boolean calleeClassIsInstrumented = false;
	int argsCount = -1;
	Map<String, Map<String, String>> sourcesMap = null;
	CallChopNodeLabel chopLabel = null;

	try {
	    threadId = getParameterValue("threadId");
	    pid = getParameterValue("processId");
	    parentObjectAddress = getParameterValue("parentObjectAddress");
	    parentClass = getParameterValue("parentClass");
	    parentMethod = getParameterValue("parentMethod");
	    calleeClass = getParameterValue("calleeClass");
	    calledMethod = getParameterValue("calledMethod");
	    returnValueClass = getParameterValue("returnValueClass");
	    returnValueAddress = getParameterValue("returnValueAddress");
	    calleeClassIsInstrumented = Boolean.parseBoolean(getParameterValue("calleeClassIsInstrumented"));
	    argsCount = Integer.parseInt(getParameterValue("argsCount"));
	    sourcesMap = (JSONObject) new JSONParser().parse(getParameterValue("sourcesMap"));
	    chopLabel = new CallChopNodeLabel(getParameterValue("chopLabel"));
	} catch (ParameterNotFoundException | ClassCastException | ParseException e) {
	    _logger.error(e.getMessage());
	    return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
	}

	// Put data items corresponding to sources into parameters
	for (Entry<String, Map<String, String>> sourceMapping : sourcesMap.entrySet()) {
	    String param = sourceMapping.getKey();
	    if (param.startsWith("p")) {
		IName paramName = JavaNameFactory.createLocalVarName(pid, threadId, calleeClass, null, calledMethod, param);
		IContainer paramContainer = _informationFlowModel.getContainer(paramName);
		if (paramContainer != null && paramContainer instanceof ReferenceContainer) {
		    IData sourceData = new SourceData(sourceMapping.getValue().get("sourceId"),
			    Long.valueOf(sourceMapping.getValue().get("timeStamp")));
		    _informationFlowModel.addData(sourceData, paramContainer);
		}
	    }
	}

	// Return value handling
	String leftSide = chopLabel.getLeftSide();
	if (leftSide != null) {
	    IName leftSideName = JavaNameFactory.createLocalVarName(pid, threadId, parentClass, parentObjectAddress,
		    parentMethod, leftSide);
	    if (calleeClassIsInstrumented) {
		// For instrumented classes, assign the retContainer (if
		// exists!) of the called method to the left side (or copy data
		// if it is value type)
		IName retVarName = JavaNameFactory.createLocalVarName(pid, threadId, calleeClass,
			null, calledMethod, RET);
		IContainer retContainer = _informationFlowModel.getContainer(retVarName);
		if (retContainer != null) {
		    // add source data to return value if available
		    if (sourcesMap.containsKey(RET)) {
			IData sourceData = new SourceData(sourcesMap.get(RET).get("sourceId"), Long.valueOf(sourcesMap.get(
				    RET).get("timeStamp")));
			_informationFlowModel.addData(sourceData, retContainer);
		    }
		    if (isValidAddress(returnValueAddress)) {
			// reference type
			_informationFlowModel.addName(leftSideName, retContainer, false);
		    } else {
			// value type
			IContainer leftSideContainer = addContainerIfNotExists(leftSideName, null, null);
			_informationFlowModel.emptyContainer(leftSideContainer);
			_informationFlowModel.copyData(retContainer, leftSideContainer);
		    }
		} // no retContainer -> no information flow
	    } else {
		// For not instrumented classes, copy all data (or create alias)
		// from the method parameters to the left side container
		IContainer leftSideContainer = addContainerIfNotExists(leftSideName, returnValueClass, returnValueAddress);
		if (!isValidAddress(returnValueAddress))
		    _informationFlowModel.emptyContainer(leftSideContainer);

		// return value contains data
		if (sourcesMap.containsKey(RET)) {
		    IData sourceData = new SourceData(sourcesMap.get(RET).get("sourceId"), Long.valueOf(sourcesMap.get(
			    RET).get("timeStamp")));
		    _informationFlowModel.addData(sourceData, leftSideContainer);
		}

		// Lookup each parameter container
		// argContainer is reference type -> copy data transitively
		// argContainer is value type -> only copy data
		for (int i = 0; i < argsCount; i++) {
		    IName argName = JavaNameFactory.createLocalVarName(pid, threadId, calleeClass,
				null, calledMethod, "p" + (i + 1));
		    IContainer argContainer = _informationFlowModel.getContainer(argName);
		    if (argContainer != null) {
			if (argContainer instanceof ReferenceContainer) {
			    Set<IData> data = getDataTransitively(argContainer);
			    _informationFlowModel.addData(data, leftSideContainer);
			} else {
			    _informationFlowModel.copyData(argContainer, leftSideContainer);
			}
		    }
		}
	    }
	}

	// Clean up method parameters and local variables
	cleanUpParamsAndLocals(pid, threadId, calleeClass, null, calledMethod,
		calleeClassIsInstrumented, null, argsCount);

	return _messageFactory.createStatus(EStatus.OKAY);
    }
}
