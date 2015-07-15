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
		String[] methodArgs = null; // [class@address, value]
		CallChopNodeLabel chopLabel = null;
		
		try {
			threadId = getParameterValue("threadId");
			pid = getParameterValue("processId");
			parentObjectAddress = getParameterValue("parentObjectAddress");
			parentClass = getParameterValue("parentClass");
			parentMethod = getParameterValue("parentMethod");
			callerClass = getParameterValue("callerClass");
			calledMethod = getParameterValue("calledMethod");
						
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
		
		String parent = getClassOrObject(parentClass, parentObjectAddress);
		
		String outerArgNamePrefix = pid + DLM + threadId + DLM + parent + DLM + parentMethod;
		String innerArgNamePrefix = pid + DLM + threadId + DLM + callerClass + DLM + calledMethod;
						
		insertArguments(chopLabel.getArgs(), methodArgs, outerArgNamePrefix, innerArgNamePrefix, null);
		
		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
