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

public class CallInstanceMethodEventHandler extends CallMethodEventHandler {

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
		boolean callerObjectClassIsInstrumented = false;
		String[] methodArgs = null; // [class|address, value]
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
			callerObjectClassIsInstrumented = Boolean.parseBoolean(getParameterValue("callerObjectIsInstrumented"));
						
			JSONArray methodArgsJSON = (JSONArray) new JSONParser().parse(getParameterValue("methodArgs"));
			methodArgs = new String[methodArgsJSON.size()];
			methodArgsJSON.toArray(methodArgs);
			chopLabel = new CallChopNodeLabel(getParameterValue("chopLabel"));
		} catch (ParseException | ParameterNotFoundException | ClassCastException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}
		
		addAddressToNamesAndContainerIfNeeded(threadId, pid, parentClass, parentObjectAddress, parentMethod);
		
		boolean isConstructor = calledMethod.startsWith("<init>");
		if (isConstructor) {
			// add the threadId to the identifier to know which thread has called the constructor
			// enables to get the correct instance when the constructor has return
			callerObjectAddress = threadId + NOADDRESS;
		}
		
		String parent = getClassOrObject(parentClass, parentObjectAddress);
		String callerObject = callerObjectClass + DLM + callerObjectAddress;
		
		// if caller object class is a system class, get its container to add method parameters to it
		IContainer callerObjectContainer = null;
		IName callerObjectName = new NameBasic(pid + DLM + callerObject);
		callerObjectContainer = addContainerIfNotExists(callerObjectName, callerObject);
		
		IName callerObjectVarName = new NameBasic(pid + DLM + threadId + DLM + parent + DLM + parentMethod + DLM + chopLabel.getCaller());
		_informationFlowModel.addName(callerObjectVarName, callerObjectContainer, false);
		
		String outerArgNamePrefix = pid + DLM + threadId + DLM + parent + DLM + parentMethod;
		String innerArgNamePrefix = pid + DLM + threadId + DLM + callerObject + DLM + calledMethod;
				
		insertArguments(chopLabel.getArgs(), methodArgs, 
				outerArgNamePrefix, innerArgNamePrefix, callerObjectClassIsInstrumented ? null : callerObjectContainer);	
		
		return _messageFactory.createStatus(EStatus.OKAY);
	}
}
