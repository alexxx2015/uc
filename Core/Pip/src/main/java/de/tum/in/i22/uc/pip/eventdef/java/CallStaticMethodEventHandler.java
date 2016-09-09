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

public class CallStaticMethodEventHandler extends CallMethodEventHandler {

    @Override
    protected IStatus update() {
	String threadId = null;
	String pid = null;
	String parentObjectAddress = null;
	String parentClass = null;
	String parentMethod = null;
	String callerClass = null;
	String calledMethod = null;
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
	    callerClass = getParameterValue("callerClass");
	    calledMethod = getParameterValue("calledMethod");

	    JSONArray methodArgTypesJSON = (JSONArray) new JSONParser().parse(getParameterValue("methodArgTypes"));
	    methodArgTypes = new String[methodArgTypesJSON.size()];
	    methodArgTypesJSON.toArray(methodArgTypes);
	    JSONArray methodArgAddressesJSON = (JSONArray) new JSONParser().parse(getParameterValue("methodArgAddresses"));
	    methodArgAddresses = new String[methodArgAddressesJSON.size()];
	    methodArgAddressesJSON.toArray(methodArgAddresses);
	    chopLabel = new CallChopNodeLabel(getParameterValue("chopLabel"));
	    
	    methodLabel = getParameterValue("methodLabel");//SAP database security label
		System.out.println("CSEventHandler| ML: "+methodLabel+", chopLabel: "+chopLabel);
	} catch (ParseException | ParameterNotFoundException | ClassCastException e) {
	    _logger.error(e.getMessage());
	    return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
	}

	addAddressToNamesAndContainerIfNeeded(threadId, pid, parentClass, parentObjectAddress, parentMethod);

	insertArguments(chopLabel.getArgs(), methodArgTypes, methodArgAddresses, pid, threadId, parentClass, parentObjectAddress, parentMethod,
		callerClass, null, calledMethod, null);

	return _messageFactory.createStatus(EStatus.OKAY);
    }

}
