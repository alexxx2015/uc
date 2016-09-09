package de.tum.in.i22.uc.pip.eventdef.java;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.datatypes.java.containers.ReferenceContainer;
import de.tum.in.i22.uc.cm.datatypes.java.data.SourceData;
import de.tum.in.i22.uc.cm.datatypes.java.names.ObjectName;
import de.tum.in.i22.uc.cm.factories.JavaNameFactory;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.pip.eventdef.java.chopnode.CallChopNodeLabel;

public class ReturnInstanceMethodEventHandler extends ReturnMethodEventHandler {

    @SuppressWarnings("unchecked")
    @Override
    protected IStatus update() {

	String threadId = null;
	String pid = null;
	String parentObjectAddress = null;
	String parentClass = null;
	String parentMethod = null;
	String callerObjectAddress = null;
	String callerObjectClass = null;
	String calledMethod = null;
	String returnValueClass = null;
	String returnValueAddress = null;
	Boolean callerObjectClassIsInstrumented = false;
	int argsCount = -1;
	Map<String, Map<String, String>> sourcesMap = null;
	CallChopNodeLabel chopLabel = null;

	try {
	    threadId = getParameterValue("threadId");
	    pid = getParameterValue("processId");
	    parentObjectAddress = getParameterValue("parentObjectAddress");
	    parentClass = getParameterValue("parentClass");
	    parentMethod = getParameterValue("parentMethod");
	    callerObjectAddress = getParameterValue("callerObjectAddress");
	    callerObjectClass = getParameterValue("callerObjectClass");
	    calledMethod = getParameterValue("calledMethod");
	    returnValueClass = getParameterValue("returnValueClass");
	    returnValueAddress = getParameterValue("returnValueAddress");
	    callerObjectClassIsInstrumented = Boolean.parseBoolean(getParameterValue("callerObjectIsInstrumented"));
	    argsCount = Integer.parseInt(getParameterValue("argsCount"));
	    sourcesMap = (JSONObject) new JSONParser().parse(getParameterValue("sourcesMap"));
	    chopLabel = new CallChopNodeLabel(getParameterValue("chopLabel"));
	} catch (ParameterNotFoundException | ClassCastException | ParseException e) {
	    _logger.error(e.getMessage());
	    return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
	}

	addAddressToNamesAndContainerIfNeeded(threadId, pid, callerObjectClass, callerObjectAddress, calledMethod);

	IName callerObjectName = new ObjectName(pid, callerObjectClass, callerObjectAddress);
	IContainer callerObjectContainer = _informationFlowModel.getContainer(callerObjectName);

	// Put data items corresponding to sources into parameters
	for (Entry<String, Map<String, String>> sourceMapping : sourcesMap.entrySet()) {
	    String param = sourceMapping.getKey();
	    if (param.startsWith("p")) {
		IName paramName = JavaNameFactory.createLocalVarName(pid, threadId, callerObjectClass,
			callerObjectAddress, calledMethod, param);
		IContainer paramContainer = _informationFlowModel.getContainer(paramName);
		if (paramContainer != null && paramContainer instanceof ReferenceContainer) {
		    IData sourceData = new SourceData(sourceMapping.getValue().get("sourceId"),
			    Long.valueOf(sourceMapping.getValue().get("timeStamp")));
		    _informationFlowModel.addData(sourceData, paramContainer);
		}
	    }
	}

	// add source data to whole object if available
	if (sourcesMap.containsKey(OBJ)) {
	    IData sourceData = new SourceData(sourcesMap.get(OBJ).get("sourceId"), Long.valueOf(sourcesMap.get(OBJ)
		    .get("timeStamp")));
	    _informationFlowModel.addData(sourceData, callerObjectContainer);
	}

	// Return value handling
	String leftSide = chopLabel.getLeftSide();
	if (leftSide != null) {
	    IName leftSideName = JavaNameFactory.createLocalVarName(pid, threadId, parentClass, parentObjectAddress,
		    parentMethod, leftSide);
	    if (callerObjectClassIsInstrumented) {
		// For instrumented classes, assign the retContainer (if
		// exists!) of the called method to the left side (or copy data
		// if it is value type)
		IName retVarName = JavaNameFactory.createLocalVarName(pid, threadId, callerObjectClass,
			callerObjectAddress, calledMethod, RET);
		IContainer retContainer = _informationFlowModel.getContainer(retVarName);
		if (retContainer != null) {
		    // add source data to return value if available
		    if (sourcesMap.containsKey(RET)) {
			IData sourceData = new SourceData(sourcesMap.get(RET).get("sourceId"), Long.valueOf(sourcesMap
				.get(RET).get("timeStamp")));
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
		// For not instrumented classes, copy all (transitively) data
		// from the caller object to the left side container
		IContainer leftSideContainer = addContainerIfNotExists(leftSideName, returnValueClass, returnValueAddress);
		if (isValidAddress(returnValueAddress))
		    _informationFlowModel.emptyContainer(leftSideContainer);

		Set<IData> data = getDataTransitively(callerObjectContainer);
		_informationFlowModel.addData(data, leftSideContainer);

		// return value contains data
		if (sourcesMap.containsKey(RET)) {
		    IData sourceData = new SourceData(sourcesMap.get(RET).get("sourceId"), Long.valueOf(sourcesMap.get(
			    RET).get("timeStamp")));
		    _informationFlowModel.addData(sourceData, leftSideContainer);
		}
	    }
	}

	// Clean up method parameters and local variables
	cleanUpParamsAndLocals(pid, threadId, callerObjectClass, callerObjectAddress, calledMethod,
		callerObjectClassIsInstrumented, callerObjectContainer, argsCount);

	return _messageFactory.createStatus(EStatus.OKAY);
    }
}
