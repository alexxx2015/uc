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
    	long start = System.nanoTime();
	String threadId = null;
	String pid = null;
	String parentObjectAddress = null;
	String parentClass = null;
	String parentMethod = null;
	String callerObjectAddress = null;
	String callerObjectClass = null;
	String calledMethod = null;
	boolean calleeObjectClassIsInstrumented = false;
	String[] methodArgTypes = null;
	String[] methodArgAddresses = null;
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
	    calleeObjectClassIsInstrumented = Boolean.parseBoolean(getParameterValue("callerObjectIsInstrumented"));
	    JSONArray methodArgTypesJSON = (JSONArray) new JSONParser().parse(getParameterValue("methodArgTypes"));
	    methodArgTypes = new String[methodArgTypesJSON.size()];
	    methodArgTypesJSON.toArray(methodArgTypes);
	    JSONArray methodArgAddressesJSON = (JSONArray) new JSONParser().parse(getParameterValue("methodArgAddresses"));
	    methodArgAddresses = new String[methodArgAddressesJSON.size()];
	    methodArgAddressesJSON.toArray(methodArgAddresses);
	    chopLabel = new CallChopNodeLabel(getParameterValue("chopLabel"));
	} catch (ParseException | ParameterNotFoundException | ClassCastException e) {
	    _logger.error(e.getMessage());
	    return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
	}
	
//	long start = System.nanoTime();
	addAddressToNamesAndContainerIfNeeded(threadId, pid, parentClass, parentObjectAddress, parentMethod);
//	long stop = System.nanoTime()-start;
//	System.out.println("---CallInstanceMethod.addAddressToNamesAndContainer: "+stop);
	
	boolean isConstructor = calledMethod.startsWith("<init>");
	if (isConstructor) {
	    // add the threadId to the identifier to know which thread has
	    // called the constructor
	    // enables to get the correct instance when the constructor has
	    // returned
	    callerObjectAddress = threadId + NOADDRESS;
	}

	// if caller object class is a system class, get its container to add
	// method parameters to it
	IContainer callerObjectContainer = null;
	IName callerObjectName = new ObjectName(pid, callerObjectClass, callerObjectAddress);
//	start = System.nanoTime();
	callerObjectContainer = addContainerIfNotExists(callerObjectName, callerObjectClass, callerObjectAddress);
//	stop = System.nanoTime()-start;
//	System.out.println("---CallInstanceMethod.addContainerIfNotExists: "+stop);

	IName callerObjectVarName = JavaNameFactory.createLocalVarName(pid, threadId, parentClass, parentObjectAddress,
		parentMethod, chopLabel.getCallee());
	_informationFlowModel.addName(callerObjectVarName, callerObjectContainer, false);

//	start = System.nanoTime();
	insertArguments(chopLabel.getArgs(), methodArgTypes, methodArgAddresses, pid, threadId, parentClass, parentObjectAddress, parentMethod,
		callerObjectClass, callerObjectAddress, calledMethod, calleeObjectClassIsInstrumented ? null
			: callerObjectContainer);
//	stop = System.nanoTime()-start;
//	System.out.println("---CallInstanceMethod.insertArguments: "+stop);

	long stop = System.nanoTime() -start;
	System.out.println("--- CallInstanceMethod-total: "+calledMethod+"| "+stop);
	return _messageFactory.createStatus(EStatus.OKAY);
    }
}
