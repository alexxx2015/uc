package de.tum.in.i22.uc.pip.eventdef.java;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.factories.JavaNameFactory;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.pip.eventdef.java.chopnode.ReferenceChopNodeLabel;

public class ReadFieldEventHandler extends JavaEventHandler {

    @Override
    protected IStatus update() {

	String threadId = null;
	String pid = null;
	String parentObjectAddress = null;
	String parentClass = null;
	String parentMethod = null;
	String fieldOwnerClass = null;
	String fieldOwnerAddress = null;
	String field = null;
	String fieldValueClass = null;
	String fieldValueAddress = null;
	ReferenceChopNodeLabel chopLabel = null;

	try {
	    threadId = getParameterValue("threadId");
	    pid = getParameterValue("processId");
	    parentObjectAddress = getParameterValue("parentObjectAddress");
	    parentClass = getParameterValue("parentClass");
	    parentMethod = getParameterValue("parentMethod");
	    fieldOwnerAddress = getParameterValue("fieldOwnerAddress");
	    fieldOwnerClass = getParameterValue("fieldOwnerClass");
	    field = getParameterValue("fieldName");
	    fieldValueClass = getParameterValue("fieldValueClass");
	    fieldValueAddress = getParameterValue("fieldValueAddress");
	    chopLabel = new ReferenceChopNodeLabel(getParameterValue("chopLabel"));
	} catch (ParameterNotFoundException | ClassCastException e) {
	    _logger.error(e.getMessage());
	    return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
	}

	addAddressToNamesAndContainerIfNeeded(threadId, pid, parentClass, parentObjectAddress, parentMethod);

	String fieldValue = fieldValueClass + DLM + fieldValueAddress;

	// Left side name
	String leftSideVar = chopLabel.getLeftSide();
	IName leftSideName = JavaNameFactory.createLocalVarName(pid, threadId, parentClass, parentObjectAddress,
		parentMethod, leftSideVar);

	// Get field container (create if necessary)
	IName fieldName = JavaNameFactory.createFieldName(pid, fieldOwnerClass, fieldOwnerAddress, field);
	IContainer fieldContainer = addContainerIfNotExists(fieldName, fieldValue);

	// Reference type -> make left side name also point to field container
	// Value type -> just copy the data from field container to left side
	// container (create it first)
	if (isReferenceType(fieldValue)) {
	    _informationFlowModel.addName(leftSideName, fieldContainer, false);
	} else {
	    IContainer leftSideContainer = addContainerIfNotExists(leftSideName);
	    _informationFlowModel.emptyContainer(leftSideContainer);
	    _informationFlowModel.copyData(fieldContainer, leftSideContainer);
	}

	return _messageFactory.createStatus(EStatus.OKAY);
    }

}
