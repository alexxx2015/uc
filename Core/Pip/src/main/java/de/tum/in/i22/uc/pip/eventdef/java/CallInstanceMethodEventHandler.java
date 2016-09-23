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
import de.tum.in.i22.uc.cm.datatypes.java.names.ObjectName;
import de.tum.in.i22.uc.cm.factories.JavaNameFactory;
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
	String calleeObjectAddress = null;
	String calleeObjectClass = null;
	String calledMethod = null;
	boolean calleeObjectClassIsInstrumented = false;
	String[] methodArgTypes = null;
	String[] methodArgAddresses = null;
	CallChopNodeLabel chopLabel = null;
	
	String methodLabel = null;//SAP database security label

	try {
	    threadId = getParameterValue("threadId");
	    pid = getParameterValue("processId");
	    parentObjectAddress = getParameterValue("parentObjectAddress");
	    parentClass = getParameterValue("parentClass");
	    parentMethod = getParameterValue("parentMethod");
	    calleeObjectAddress = getParameterValue("calleeObjectAddress");
	    calleeObjectClass = getParameterValue("calleeObjectClass");
	    calledMethod = getParameterValue("calledMethod");
	    calleeObjectClassIsInstrumented = Boolean.parseBoolean(getParameterValue("calleeObjectIsInstrumented"));
	    JSONArray methodArgTypesJSON = (JSONArray) new JSONParser().parse(getParameterValue("methodArgTypes"));
	    methodArgTypes = new String[methodArgTypesJSON.size()];
	    methodArgTypesJSON.toArray(methodArgTypes);
	    JSONArray methodArgAddressesJSON = (JSONArray) new JSONParser().parse(getParameterValue("methodArgAddresses"));
	    methodArgAddresses = new String[methodArgAddressesJSON.size()];
	    methodArgAddressesJSON.toArray(methodArgAddresses);
	    chopLabel = new CallChopNodeLabel(getParameterValue("chopLabel"));
	    
//	    methodLabel = getParameterValue("methodLabel");//SAP database security label
//		System.out.println("CIEventHandler| ML: "+methodLabel+", chopLabel: "+chopLabel);
	} catch (ParseException | ParameterNotFoundException | ClassCastException e) {
	    _logger.error(e.getMessage());
	    return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
	}
	
	addAddressToNamesAndContainerIfNeeded(threadId, pid, parentClass, parentObjectAddress, parentMethod);

	boolean isConstructor = calledMethod.startsWith("<init>");
	if (isConstructor) {
	    // add the threadId to the identifier to know which thread has
	    // called the constructor
	    // enables to get the correct instance when the constructor has
	    // returned
	    calleeObjectAddress = threadId + NOADDRESS;
	}

	// if caller object class is a system class, get its container to add
	// method parameters to it
	IContainer calleeObjectContainer = null;
	IName calleeObjectName = new ObjectName(pid, calleeObjectClass, calleeObjectAddress);
	calleeObjectContainer = addContainerIfNotExists(calleeObjectName, calleeObjectClass, calleeObjectAddress);

	IName calleeObjectVarName = JavaNameFactory.createLocalVarName(pid, threadId, parentClass, parentObjectAddress,
		parentMethod, chopLabel.getCallee());
	_informationFlowModel.addName(calleeObjectVarName, calleeObjectContainer, false);

	insertArguments(chopLabel.getArgs(), methodArgTypes, methodArgAddresses, pid, threadId, parentClass, parentObjectAddress, parentMethod,
		calleeObjectClass, calleeObjectAddress, calledMethod, calleeObjectClassIsInstrumented ? null
			: calleeObjectContainer);

	return _messageFactory.createStatus(EStatus.OKAY);
    }
}
