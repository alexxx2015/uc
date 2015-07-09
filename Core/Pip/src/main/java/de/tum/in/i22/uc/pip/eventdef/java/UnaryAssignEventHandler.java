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
import de.tum.in.i22.uc.pip.eventdef.java.chopnode.AssignChopNodeLabel;

public class UnaryAssignEventHandler extends JavaEventHandler {

	@Override
	protected IStatus update() {
		
		String threadId = null;
		String pid = null;
		String parentObjectAddress = null;
		String parentClass = null;
		String parentMethod = null;
		String argumentClass = null;
		String argumentAddress = null;
		AssignChopNodeLabel chopLabel = null;
		
		try {
			threadId = getParameterValue("threadId");
			pid = getParameterValue("processId");
			parentMethod = getParameterValue("parentMethod");
			parentObjectAddress = getParameterValue("parentObjectAddress");
			parentClass = getParameterValue("parentClass");
			argumentClass = getParameterValue("argumentClass");
			argumentAddress = getParameterValue("argumentAddress");
			chopLabel = new AssignChopNodeLabel(getParameterValue("chopLabel"));
		} catch (ParameterNotFoundException | ClassCastException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}
		
		String parent = getClassOrObject(parentClass, parentObjectAddress);
		String argument = argumentClass + DLM + argumentAddress;
		
		// Left side name
		String leftSideVar = chopLabel.getLeftSide();
		IName leftSideName = new NameBasic(pid + DLM + threadId + DLM + parent + DLM + parentMethod + DLM + leftSideVar);
		
		// Argument container (create if necessary)
		// no data flows if the operand is a constant, so cancel event right here
		String argumentVar = chopLabel.getOperands()[0];
		if (argumentVar.startsWith("#")) {
			return _messageFactory.createStatus(EStatus.OKAY);
		}
		IName argumentName = new NameBasic(pid + DLM + threadId + DLM + parent + DLM + parentMethod + DLM + argumentVar);
		IContainer argumentContainer = addContainerIfNotExists(argumentName, argument);
		
		if (isReferenceType(argument)) {
			// reference type -> assign left side name to argument container
			_informationFlowModel.addName(argumentName, leftSideName);			
		} else {
			// value type -> copy contents of argument container into left side container (create if necessary)
			IContainer leftSideContainer = addContainerIfNotExists(leftSideName);
			_informationFlowModel.copyData(argumentContainer, leftSideContainer);
		}
		
		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
