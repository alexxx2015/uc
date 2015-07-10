package de.tum.in.i22.uc.pip.eventdef.java;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.simple.JSONArray;
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
		int argsCount = 0;
		Map<String, String> sourcesMap = null;
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
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}
		
		String parent = getClassOrObject(parentClass, parentObjectAddress);
		String callerObject = callerObjectClass + DLM + callerObjectAddress;
		String returnValue = returnValueClass + DLM + returnValueAddress;
		
		IName callerObjectName = new NameBasic(pid + DLM + callerObject);
		IContainer callerObjectContainer = _informationFlowModel.getContainer(callerObjectName);
		
		String methodVariablePrefix = pid + DLM + threadId + DLM + callerObject + DLM + calledMethod;
		
		// Put data items corresponding to sources into parameters
		for (Entry<String, String> sourceMapping : sourcesMap.entrySet()) {
			String param = sourceMapping.getKey();
			if (param.startsWith("p")) {
				IName paramName = new NameBasic(methodVariablePrefix + DLM + param);
				IContainer paramContainer = _informationFlowModel.getContainer(paramName);
				if (paramContainer != null && containerIsReference(paramContainer)) {
					IData sourceData = new DataBasic(sourceMapping.getValue());
					_informationFlowModel.addData(sourceData, paramContainer);
				}
			}
		}
		
		// add source data to whole object if available
		if (sourcesMap.containsKey(OBJ)) {
			IData sourceData = new DataBasic(sourcesMap.get(OBJ));
			_informationFlowModel.addData(sourceData, callerObjectContainer);
		}
		
		// Return value handling
		String leftSide = chopLabel.getLeftSide();
		if (leftSide != null) {
			IName leftSideName = new NameBasic(pid + DLM + threadId + DLM + parent + DLM + parentMethod + DLM + leftSide);			
			if (callerObjectClassIsInstrumented) {
				// For instrumented classes, assign the retContainer (if exists!) of the called method to the left side (or copy data if it is value type)
				IName retVarName = new NameBasic(methodVariablePrefix + DLM + RET);
				IContainer retContainer = _informationFlowModel.getContainer(retVarName);
				if (retContainer != null) {
					// add source data to return value if available
					if (sourcesMap.containsKey(RET)) {
						IData sourceData = new DataBasic(sourcesMap.get(RET));
						_informationFlowModel.addData(sourceData, retContainer);
					}
					if (isReferenceType(returnValue)) {
						// reference type
						_informationFlowModel.addName(leftSideName, retContainer, false);
					} else {
						// value type
						IContainer leftSideContainer = addContainerIfNotExists(leftSideName);
						_informationFlowModel.emptyContainer(leftSideContainer);
						_informationFlowModel.copyData(retContainer, leftSideContainer);
					}
				} // no retContainer -> no information flow
			} else {
				// For not instrumented classes, copy all (transitively) data from the caller object to the left side container
				IContainer leftSideContainer = addContainerIfNotExists(leftSideName, returnValue);
				if (isReferenceType(returnValue)) _informationFlowModel.emptyContainer(leftSideContainer);
				
				Set<IData> data = getDataTransitively(callerObjectContainer);
				_informationFlowModel.addData(data, leftSideContainer);
				
				// return value contains data
				if (sourcesMap.containsKey(RET)) {
					IData sourceData = new DataBasic(sourcesMap.get(RET));
					_informationFlowModel.addData(sourceData, leftSideContainer);
				}
			}
		}
		
		// Clean up method parameters and local variables
		cleanUpParamsAndLocals(methodVariablePrefix, callerObjectClassIsInstrumented, callerObjectContainer, argsCount);
		
		return _messageFactory.createStatus(EStatus.OKAY);
	}
}
