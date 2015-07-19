package de.tum.in.i22.uc.pip.eventdef.java;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.datatypes.java.ArrayElementName;
import de.tum.in.i22.uc.cm.datatypes.java.JavaNameFactory;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.pip.eventdef.java.chopnode.ReferenceChopNodeLabel;

public class ReadArrayEventHandler extends JavaEventHandler {

    @Override
    protected IStatus update() {

	String threadId = null;
	String pid = null;
	String parentObjectAddress = null;
	String parentClass = null;
	String parentMethod = null;
	String arrayClass = null;
	String arrayAddress = null;
	String elementClass = null;
	String elementAddress = null;
	int index = -1;
	ReferenceChopNodeLabel chopLabel = null;

	try {
	    threadId = getParameterValue("threadId");
	    pid = getParameterValue("processId");
	    parentObjectAddress = getParameterValue("parentObjectAddress");
	    parentClass = getParameterValue("parentClass");
	    parentMethod = getParameterValue("parentMethod");
	    arrayClass = getParameterValue("arrayClass");
	    arrayAddress = getParameterValue("arrayAddress");
	    index = Integer.valueOf(getParameterValue("index"));
	    elementClass = getParameterValue("elementClass");
	    elementAddress = getParameterValue("elementAddress");
	    chopLabel = new ReferenceChopNodeLabel(getParameterValue("chopLabel"));
	} catch (ParameterNotFoundException | ClassCastException e) {
	    _logger.error(e.getMessage());
	    return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
	}

	addAddressToNamesAndContainerIfNeeded(threadId, pid, parentClass, parentObjectAddress, parentMethod);

	String element = elementClass + DLM + elementAddress;

	// Array container (create if necessary)
	String arrayVar = chopLabel.getArray();
	IName arrayName = JavaNameFactory.createLocalVarName(pid, threadId, parentClass, parentObjectAddress,
		parentMethod, arrayVar);
	IContainer arrayContainer = addContainerIfNotExists(arrayName, arrayClass + DLM + arrayAddress);

	// Array element container (create if necessary)
	IName elementName = new ArrayElementName(pid, arrayClass, arrayAddress, index);
	IContainer elementContainer = addContainerIfNotExists(elementName, isReferenceType(element) ? element
		: arrayClass + DLM + arrayAddress + DLM + index);
	_informationFlowModel.addAlias(elementContainer, arrayContainer);

	// Left side name
	String leftSideVar = chopLabel.getLeftSide();
	IName leftSideName = JavaNameFactory.createLocalVarName(pid, threadId, parentClass, parentObjectAddress,
		parentMethod, leftSideVar);

	if (isReferenceType(element)) {
	    // reference type -> assign left side name to element container +
	    // copy data from array container (no other possibility)
	    _informationFlowModel.addName(leftSideName, elementContainer, false);
	    _informationFlowModel.copyData(arrayContainer, elementContainer);
	} else {
	    // value type -> copy data from element AND array container
	    IContainer leftSideContainer = addContainerIfNotExists(leftSideName);
	    _informationFlowModel.emptyContainer(leftSideContainer);
	    _informationFlowModel.copyData(elementContainer, leftSideContainer);
	    _informationFlowModel.copyData(arrayContainer, leftSideContainer);
	}

	return _messageFactory.createStatus(EStatus.OKAY);
    }

}
