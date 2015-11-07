package de.tum.in.i22.uc.pip.eventdef.java;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.datatypes.java.names.ObjectName;
import de.tum.in.i22.uc.cm.factories.JavaNameFactory;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.pip.eventdef.java.chopnode.CallChopNodeLabel;

public class SourceEventHandler extends JavaEventHandler {

	@Override
	protected IStatus update() {
		String threadId = null;
		String pid = null;
		String parentObjectAddress = null;
		String parentClass = null;
		String parentMethod = null;
		String calledObjectAddress = null;
		String calledObjectClass = null;
		String calledMethod = null;
		String returnObjectAddress = null;
		String fileName = null;
		String[] methodArgTypes = null;
		String[] methodArgAddresses = null;		
		CallChopNodeLabel chopLabel = null;

		try {
		    threadId = getParameterValue("threadId");
		    pid = getParameterValue("processId");
		    parentObjectAddress = getParameterValue("parentObjectAddress");
		    parentClass = getParameterValue("parentClass");
		    parentMethod = getParameterValue("parentMethod");
		    calledObjectAddress = getParameterValue("calledObjectAddress");
		    calledObjectClass = getParameterValue("calledObjectClass");
		    calledMethod = getParameterValue("calledMethod");
		    returnObjectAddress = getParameterValue("returnObjectAddress");
		    fileName = getParameterValue("fileName");		    
		    chopLabel = new CallChopNodeLabel(getParameterValue("chopLabel"));
//		    JSONArray methodArgTypesJSON = (JSONArray) new JSONParser().parse(getParameterValue("methodArgTypes"));
//		    methodArgTypes = new String[methodArgTypesJSON.size()];
//		    methodArgTypesJSON.toArray(methodArgTypes);
//		    JSONArray methodArgAddressesJSON = (JSONArray) new JSONParser().parse(getParameterValue("methodArgAddresses"));
//		    methodArgAddresses = new String[methodArgAddressesJSON.size()];
//		    methodArgAddressesJSON.toArray(methodArgAddresses);
		} catch (ParameterNotFoundException | ClassCastException e) {
		    _logger.error(e.getMessage());
		    return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}
		
		// if caller object class is a system class, get its container to add
		// method parameters to it
		
		IContainer callerObjectContainer = null;
		IName callerObjectName = new ObjectName(pid, calledObjectClass, calledObjectAddress);
		callerObjectContainer = addContainerIfNotExists(callerObjectName, calledObjectClass, calledObjectAddress);
//
		IName callerObjectVarName = JavaNameFactory.createLocalVarName(pid, threadId, parentClass, parentObjectAddress,
			parentMethod, chopLabel.getCallee());
		_informationFlowModel.addName(callerObjectVarName, callerObjectContainer, false);
//
//		insertArguments(chopLabel.getArgs(), methodArgTypes, methodArgAddresses, pid, threadId, parentClass, parentObjectAddress, parentMethod,
//			callerObjectClass, callerObjectAddress, calledMethod, callerObjectClassIsInstrumented ? null
//				: callerObjectContainer);

		return _messageFactory.createStatus(EStatus.OKAY);
//		return new JavaPipStatus(EStatus.OKAY, srcName, scopeData);
	}

}
