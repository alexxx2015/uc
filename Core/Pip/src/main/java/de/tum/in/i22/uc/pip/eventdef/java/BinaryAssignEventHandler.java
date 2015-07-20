package de.tum.in.i22.uc.pip.eventdef.java;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.factories.JavaNameFactory;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.pip.eventdef.java.chopnode.AssignChopNodeLabel;

public class BinaryAssignEventHandler extends JavaEventHandler {

    @Override
    protected IStatus update() {
	// IMPORTANT: This event handler assumes that arguments are only of
	// value type
	String threadId = null;
	String pid = null;
	String parentObjectAddress = null;
	String parentClass = null;
	String parentMethod = null;
	String argument1 = null; // value
	String argument2 = null; // value
	AssignChopNodeLabel chopLabel = null;

	try {
	    threadId = getParameterValue("threadId");
	    pid = getParameterValue("processId");
	    parentMethod = getParameterValue("parentMethod");
	    parentObjectAddress = getParameterValue("parentObjectAddress");
	    parentClass = getParameterValue("parentClass");
	    argument1 = getParameterValue("argument1");
	    argument2 = getParameterValue("argument2");
	    chopLabel = new AssignChopNodeLabel(getParameterValue("chopLabel"));
	} catch (ParameterNotFoundException | ClassCastException e) {
	    _logger.error(e.getMessage());
	    return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
	}

	addAddressToNamesAndContainerIfNeeded(threadId, pid, parentClass, parentObjectAddress, parentMethod);

	// Left side name
	String leftSideVar = chopLabel.getLeftSide();
	IName leftSideName = JavaNameFactory.createLocalVarName(pid, threadId, parentClass, parentObjectAddress, parentMethod, leftSideVar);
	IContainer leftSideContainer = addContainerIfNotExists(leftSideName);
	_informationFlowModel.emptyContainer(leftSideContainer);

	for (int i = 0; i < 2; i++) {
	    String argumentVar = chopLabel.getOperands()[i];
	    String argument = i == 0 ? argument1 : argument2;

	    // no data flows if the operand is a constant, so skip to next
	    // argument
	    if (argumentVar.startsWith("#")) {
		continue;
	    }

	    // Argument container (create if necessary)
	    IName argumentName = JavaNameFactory.createLocalVarName(pid, threadId, parentClass, parentObjectAddress, parentMethod, argumentVar);
	    IContainer argumentContainer = addContainerIfNotExists(argumentName);

	    // value type -> copy contents of argument container into left side
	    // container (create if necessary)
	    _informationFlowModel.copyData(argumentContainer, leftSideContainer);
	}

	return _messageFactory.createStatus(EStatus.OKAY);
    }

}
