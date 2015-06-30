package de.tum.in.i22.uc.pip.eventdef.java;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.pip.eventdef.java.chopnode.AssignChopNodeLabel;

public class BinaryAssignEventHandler extends JavaEventHandler {

	@Override
	protected IStatus update() {
		// IMPORTANT: This event handler assumes that arguments are only of value type
		String threadId = null;
		String pid = null;
		String parentMethod = null;
		String parentObject = null;
		String argument1 = null; // value
		String argument2 = null; // value
		AssignChopNodeLabel chopLabel = null;
		
		try {
			threadId = getParameterValue("threadId");
			pid = getParameterValue("processId");
			parentMethod = getParameterValue("parentMethod");
			parentObject = getParameterValue("parentObject");
			argument1 = getParameterValue("argument1");
			argument2 = getParameterValue("argument2");
			chopLabel = new AssignChopNodeLabel(getParameterValue("chopLabel"));
		} catch (ParameterNotFoundException | ClassCastException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}
		
		// No parent object means that the parent method is a class method
		if (parentObject.equals("null")) {
			parentObject = "class";
		}
		
		// Parent container (create if necessary)
		IContainer parentContainer = addParentObjectContainerIfNotExists(parentObject, pid);
		
		// Left side name
		String leftSideVar = chopLabel.getLeftSide();
		IName leftSideName = new NameBasic(pid + DLM + threadId + DLM + parentObject + DLM + parentMethod + DLM + leftSideVar);
		
		for (int i = 0; i < 2; i++) {
			String argumentVar = chopLabel.getOperands()[i];
			String argument = i == 0 ? argument1 : argument2;
			
			// no data flows if the operand is a constant, so skip to next argument
			if (argumentVar.startsWith("#")) {
				continue;
			}
			
			// Argument container (create if necessary)
			IName argumentName = new NameBasic(pid + DLM + threadId + DLM + parentObject + DLM + parentMethod + DLM + argumentVar);
			IContainer argumentContainer = addContainerIfNotExists(argumentName, argument, parentContainer);
			
			// value type -> copy contents of argument container into left side container (create if necessary)
			IContainer leftSideContainer = addContainerIfNotExists(leftSideName, parentContainer);
			_informationFlowModel.copyData(argumentContainer, leftSideContainer);
		}
		
		
		
		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
