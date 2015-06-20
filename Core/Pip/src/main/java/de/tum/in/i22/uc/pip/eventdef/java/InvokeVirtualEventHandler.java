package de.tum.in.i22.uc.pip.eventdef.java;

import org.json.simple.parser.ParseException;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import de.tum.in.i22.uc.cm.datatypes.basic.ContainerBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.pip.eventdef.java.chopnode.CallChopNodeLabel;

public class InvokeVirtualEventHandler extends JavaEventHandler {

	@Override
	protected IStatus update() {
		String threadId = null;
		String parentMethod = null;
		String calledMethod = null;
		String parentObject = null;
		String parentClass = null;
		String callerObject = null;
		String[] methodArgs = null; // [class@address, value]
		CallChopNodeLabel chopLabel = null;
		
		try {
			threadId = getParameterValue("threadId");
			parentMethod = getParameterValue("parentMethod");
			calledMethod = getParameterValue("calledMethod");
			parentObject = getParameterValue("parentObject");
			callerObject = getParameterValue("callerObject");
			parentClass = getParameterValue("parentClass");
						
			JSONArray methodArgsJSON = (JSONArray) new JSONParser().parse(getParameterValue("methodArgs"));
			methodArgs = new String[methodArgsJSON.size()];
			methodArgsJSON.toArray(methodArgs);
			chopLabel = new CallChopNodeLabel(getParameterValue("chopLabel"));
		} catch (ParseException | ParameterNotFoundException | ClassCastException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}
		
		// No parent object means that the parent method is a class method
		if (parentObject.equals("null")) {
			parentObject = "class";
		}
		
		IName callerObjectName = new NameBasic(threadId + DLM + callerObject);
		IContainer callerObjectContainer = _informationFlowModel.getContainer(callerObjectName);
		
		if (callerObjectContainer == null) {
			callerObjectContainer = _messageFactory.createContainer(callerObject);
			_informationFlowModel.addName(callerObjectName, callerObjectContainer, false);
		}
		
		IName callerObjectVarName = new NameBasic(threadId + DLM + parentObject + DLM + parentMethod + DLM + chopLabel.getCaller());
		_informationFlowModel.addName(callerObjectVarName, callerObjectContainer, false);
				
		for (int i = 0; i < methodArgs.length; i++) {
			String argName = chopLabel.getArgs()[i];
			String argument = methodArgs[i];
			
			// constants as parameters -> no information flow, no outer arg container needed
			if (argName.startsWith("#")) continue;
			
			// 1. Add the arguments into the method 
			// reference type -> assign containers named by parent method var
			//		to name of called method's parameter
			// value type -> copy data from container named by parent method var
			// 		to the container named by called method's parameter (create container if necessary)
			
			IName outerArgName = new NameBasic(threadId + DLM + parentObject + DLM + parentMethod + DLM + argName);
			IName innerArgName = new NameBasic(threadId + DLM + callerObject + DLM + calledMethod + DLM + "p" + (i+1));
			
			IContainer innerArgContainer;
			
			if (argument.contains("@")) {
				// reference type
				IContainer outerArgContainer = _informationFlowModel.getContainer(outerArgName);
				if (outerArgContainer == null) {
					outerArgContainer = _messageFactory.createContainer(argument);
					_informationFlowModel.addName(outerArgName, outerArgContainer, false);
				}
				innerArgContainer = outerArgContainer;
				_informationFlowModel.addName(innerArgName, outerArgContainer, false);
				
			} else {
				// value type
				innerArgContainer = _informationFlowModel.getContainer(innerArgName);
				if (innerArgContainer == null) {
					innerArgContainer = _messageFactory.createContainer();
					_informationFlowModel.addName(innerArgName, innerArgContainer, false);
				}
				
				IContainer outerArgContainer = _informationFlowModel.getContainer(outerArgName);
				if (outerArgContainer == null) {
					outerArgContainer = _messageFactory.createContainer();
					_informationFlowModel.addName(outerArgName, outerArgContainer, false);
				}
				_informationFlowModel.copyData(outerArgContainer, innerArgContainer);
				
			}
			
			// 2. Put the var containers into the caller object container (named by var in parent method)
			_informationFlowModel.addAlias(innerArgContainer, callerObjectContainer);
		}		
		
		// 3. Put the caller object container into the left side container
		String leftSide = chopLabel.getLeftSide();
		if (leftSide != null) {
			IName leftSideName = new NameBasic(threadId + DLM + parentObject + DLM + parentMethod + DLM + leftSide);
			IContainer leftSideContainer = _informationFlowModel.getContainer(leftSideName);
			if (leftSideContainer == null) {
				leftSideContainer = _messageFactory.createContainer();
				_informationFlowModel.addName(leftSideName, leftSideContainer, false);
			}
			_informationFlowModel.addAlias(callerObjectVarName, leftSideName);
			
			// 4. Put the left side container into the parent object (or class, if parent method is static) container
			IName parentName;
			if (parentObject.equals("class")) {
				parentName = new NameBasic(threadId + DLM + "class" + DLM + parentClass);
			} else {
				parentName = new NameBasic(threadId + DLM + parentObject);
			}
			IContainer parentContainer = _informationFlowModel.getContainer(parentName);
			if (parentContainer == null) {
				parentContainer = _messageFactory.createContainer();
				_informationFlowModel.addName(parentName, parentContainer, false);
			}
			_informationFlowModel.addAlias(leftSideName, parentName);
		}
		
		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
