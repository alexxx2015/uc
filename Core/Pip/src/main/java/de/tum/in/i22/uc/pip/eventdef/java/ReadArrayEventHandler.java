package de.tum.in.i22.uc.pip.eventdef.java;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
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
		String indexValue = null;
		ReferenceChopNodeLabel chopLabel = null;
		
		try {
			threadId = getParameterValue("threadId");
			pid = getParameterValue("processId");
			parentObjectAddress = getParameterValue("parentObjectAddress");
			parentClass = getParameterValue("parentClass");
			parentMethod = getParameterValue("parentMethod");
			arrayClass = getParameterValue("arrayClass");
			arrayAddress = getParameterValue("arrayAddress");
			indexValue = getParameterValue("index");
			elementClass = getParameterValue("elementClass");
			elementAddress = getParameterValue("elementAddress");
			chopLabel = new ReferenceChopNodeLabel(getParameterValue("chopLabel"));
		} catch (ParameterNotFoundException | ClassCastException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}
		
		String parent = getClassOrObject(parentClass, parentObjectAddress);
		String element = elementClass + DLM + elementAddress;
		String array = arrayClass + DLM + arrayAddress;
		
		// Array container (create if necessary)
		String arrayVar = chopLabel.getArray();
		IName arrayName = new NameBasic(pid + DLM + threadId + DLM + parent + DLM + parentMethod + DLM + arrayVar);
		IContainer arrayContainer = addContainerIfNotExists(arrayName, array);
		
		// Array element container (create if necessary)
		IName elementName = new NameBasic(pid + DLM + array + DLM + indexValue);
		IContainer elementContainer = addContainerIfNotExists(elementName, isReferenceType(element) ? element : array + DLM + indexValue);
		_informationFlowModel.addAlias(elementContainer, arrayContainer);
		
		// Left side name
		String leftSideVar = chopLabel.getLeftSide();
		IName leftSideName = new NameBasic(pid + DLM + threadId + DLM + parent + DLM + parentMethod + DLM + leftSideVar);
		
		
		if (isReferenceType(element)) {
			// reference type -> assign left side name to element container + copy data from array container (no other possibility)
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
