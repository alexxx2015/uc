package de.tum.in.i22.uc.pip.eventdef.java;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.pip.eventdef.java.chopnode.CallChopNodeLabel;

public class InvokeStaticEventHandler extends JavaEventHandler {

	@Override
	protected IStatus update() {
		String threadId = null;
		String parentMethod = null;
		String calledMethod = null;
		String callerClass = null;
		String parentObject = null;
		String parentClass = null;
		String[] methodArgs = null; // [class@address, value]
		CallChopNodeLabel chopLabel = null;
		
		try {
			threadId = getParameterValue("threadId");
			parentMethod = getParameterValue("parentMethod");
			calledMethod = getParameterValue("calledMethod");
			callerClass = getParameterValue("callerClass");
			parentObject = getParameterValue("parentObject");
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
		
		// Get caller class container
		IName callerClassName = new NameBasic(threadId + DLM + "class" +  DLM + callerClass);
		IContainer callerClassContainer = addContainerIfNotExists(callerClassName);
				
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
			IName innerArgName = new NameBasic(threadId + DLM + "class" + DLM + calledMethod + DLM + "p" + (i+1));
			
			IContainer innerArgContainer;
			
			if (argument.contains("@")) {
				// reference type
				IContainer outerArgContainer = addContainerIfNotExists(outerArgName, argument);
				innerArgContainer = outerArgContainer;
				_informationFlowModel.addName(innerArgName, outerArgContainer, false);
			} else {
				// value type
				innerArgContainer = addContainerIfNotExists(innerArgName);
				IContainer outerArgContainer = addContainerIfNotExists(outerArgName);
				_informationFlowModel.copyData(outerArgContainer, innerArgContainer);
				
			}
			
			// 2. Put the var containers into the caller class container
			_informationFlowModel.addAlias(innerArgContainer, callerClassContainer);
		
		}	
		
		// 3. Put the caller class container into the left side container
		
		String leftSide = chopLabel.getLeftSide();
		if (leftSide != null) {
			IName leftSideName = new NameBasic(threadId + DLM + parentObject + DLM + parentMethod + DLM + leftSide);
			IContainer leftSideContainer = addContainerIfNotExists(leftSideName);
			_informationFlowModel.addAlias(callerClassContainer, leftSideContainer);
			
			// 4. Put the left side container into the parent object (or class, if parent method is static) container
			
			IName parentName;
			if (parentObject.equals("class")) {
				parentName = new NameBasic(threadId + DLM + "class" + DLM + parentClass);
			} else {
				parentName = new NameBasic(threadId + DLM + parentObject);
			}
			IContainer parentContainer = addContainerIfNotExists(parentName);
			_informationFlowModel.addAlias(leftSideName, parentName);
		}
		
		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
