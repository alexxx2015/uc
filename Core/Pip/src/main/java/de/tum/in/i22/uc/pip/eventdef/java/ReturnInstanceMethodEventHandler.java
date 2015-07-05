package de.tum.in.i22.uc.pip.eventdef.java;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.pip.eventdef.java.chopnode.CallChopNodeLabel;

public class ReturnInstanceMethodEventHandler extends ReturnMethodEventHandler {
	
	@Override
	protected IStatus update() {
		
		String threadId = null;
		String pid = null;
		String parentMethod = null;
		String calledMethod = null;
		String parentObject = null;
		String callerObject = null;
		String returnValue = null;
		Boolean callerObjectClassIsInstrumented = false;
		int argsCount = 0;
		CallChopNodeLabel chopLabel = null;
		
		try {
			threadId = getParameterValue("threadId");
			pid = getParameterValue("processId");
			parentMethod = getParameterValue("parentMethod");
			calledMethod = getParameterValue("calledMethod");
			parentObject = getParameterValue("parentObject");
			callerObject = getParameterValue("callerObject");
			returnValue = getParameterValue("returnValue");
			callerObjectClassIsInstrumented = Boolean.parseBoolean(getParameterValue("callerObjectIsInstrumented"));
			argsCount = Integer.parseInt(getParameterValue("argsCount"));
			chopLabel = new CallChopNodeLabel(getParameterValue("chopLabel"));
		} catch (ParameterNotFoundException | ClassCastException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}
		
		// No parent object means that the parent method is a class method
		if (parentObject.equals("null")) {
			parentObject = "class";
		}
				
		// Get parent container
		IContainer parentContainer = addParentObjectContainerIfNotExists(parentObject, pid);
		

		IName callerObjectName = new NameBasic(pid + DLM + callerObject);
		IContainer callerObjectContainer = _informationFlowModel.getContainer(callerObjectName);
		
		boolean retValIsReferenceType = returnValue.contains("@");
		
		// Return value handling
		String leftSide = chopLabel.getLeftSide();
		if (leftSide != null) {
			IName leftSideName = new NameBasic(pid + DLM + threadId + DLM + parentObject + DLM + parentMethod + DLM + leftSide);			
			if (callerObjectClassIsInstrumented) {
				// For instrumented classes, assign the retContainer (if exists!) of the called method to the left side (or copy data if it is value type)
				IName retVarName = new NameBasic(pid + DLM + threadId + DLM + callerObject + DLM + calledMethod + DLM + RET);
				IContainer retContainer = _informationFlowModel.getContainer(retVarName);
				if (retContainer != null) {
					if (retValIsReferenceType) {
						// reference type
						_informationFlowModel.addName(leftSideName, retContainer, false);
						_informationFlowModel.addAlias(retContainer, parentContainer);
					} else {
						// value type
						IContainer leftSideContainer = addContainerIfNotExists(leftSideName, parentContainer);
						_informationFlowModel.copyData(retContainer, leftSideContainer);
					}
				} // no retContainer -> no information flow
			} else {
				// For not instrumented classes, copy all data (or create alias) from the caller object to the left side container
				IContainer leftSideContainer = addContainerIfNotExists(leftSideName, 
						retValIsReferenceType ? returnValue : null, parentContainer);
				if (retValIsReferenceType) {
					_informationFlowModel.addAlias(callerObjectContainer, leftSideContainer);
				} else {
					_informationFlowModel.copyData(callerObjectContainer, leftSideContainer);
				}
			}
		}
		
		// Clean up method parameters and local variables
		
		String methodVariablePrefix = pid + DLM + threadId + DLM + callerObject + DLM + calledMethod;
		
		cleanUpParamsAndLocals(methodVariablePrefix, callerObjectClassIsInstrumented, callerObjectContainer, argsCount);

		return _messageFactory.createStatus(EStatus.OKAY);
	}
}
