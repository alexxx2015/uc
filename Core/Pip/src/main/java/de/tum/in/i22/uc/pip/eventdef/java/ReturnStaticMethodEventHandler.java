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
		String callerClass = null;
		String calledMethod = null;
		String returnValueClass = null;
		String returnValueAddress = null;
		Boolean callerClassIsInstrumented = false;
		int argsCount = 0;
		Map<String, String> sourcesMap = null;
		CallChopNodeLabel chopLabel = null;
		
		try {
			threadId = getParameterValue("threadId");
			pid = getParameterValue("processId");
			parentObjectAddress = getParameterValue("parentObjectAddress");
			parentClass = getParameterValue("parentClass");
			parentMethod = getParameterValue("parentMethod");
			callerClass = getParameterValue("callerClass");
			calledMethod = getParameterValue("calledMethod");
			returnValueClass = getParameterValue("returnValueClass");
			returnValueAddress = getParameterValue("returnValueAddress");
			callerClassIsInstrumented = Boolean.parseBoolean(getParameterValue("callerClassIsInstrumented"));
			argsCount = Integer.parseInt(getParameterValue("argsCount"));
			sourcesMap = (JSONObject) new JSONParser().parse(getParameterValue("sourcesMap"));
			chopLabel = new CallChopNodeLabel(getParameterValue("chopLabel"));
		} catch (ParameterNotFoundException | ClassCastException | ParseException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}
		
		String parent = getClassOrObject(parentClass, parentObjectAddress);
		String returnValue = returnValueClass + DLM + returnValueAddress;
		
		String methodVariablePrefix = pid + DLM + threadId + DLM + callerClass + DLM + calledMethod;
		
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
		
		// Return value handling
		String leftSide = chopLabel.getLeftSide();
		if (leftSide != null) {
			IName leftSideName = new NameBasic(pid + DLM + threadId + DLM + parent + DLM + parentMethod + DLM + leftSide);			
			if (callerClassIsInstrumented) {
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
				// For not instrumented classes, copy all data (or create alias) from the method parameters to the left side container
				IContainer leftSideContainer = addContainerIfNotExists(leftSideName, returnValue);
				if (!isReferenceType(returnValue)) _informationFlowModel.emptyContainer(leftSideContainer);
				
				// return value contains data
				if (sourcesMap.containsKey(RET)) {
					IData sourceData = new DataBasic(sourcesMap.get(RET));
					_informationFlowModel.addData(sourceData, leftSideContainer);
				}
				
				// Lookup each parameter container
				// argContainer is reference type -> copy data transitively
				// argContainer is value type -> only copy data
				for (int i = 0; i < argsCount; i++) {
					IName argName = new NameBasic(methodVariablePrefix + DLM + "p" + (i+1));
					IContainer argContainer = _informationFlowModel.getContainer(argName);
					if (argContainer != null) {
						if (containerIsReference(argContainer)) {
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
		cleanUpParamsAndLocals(methodVariablePrefix, callerClassIsInstrumented, null, argsCount);
		
		return _messageFactory.createStatus(EStatus.OKAY);
	}
}
