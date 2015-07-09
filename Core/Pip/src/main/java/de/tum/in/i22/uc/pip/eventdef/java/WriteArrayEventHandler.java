package de.tum.in.i22.uc.pip.eventdef.java;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.pip.eventdef.java.chopnode.ModifyChopNodeLabel;

public class WriteArrayEventHandler extends JavaEventHandler {

	@Override
	protected IStatus update() {
		String threadId = null;
		String pid = null;
		String parentObjectAddress = null;
		String parentClass = null;
		String parentMethod = null;
		String arrayClass = null;
		String arrayAddress = null;
		String valueClass = null;
		String valueAddress = null;
		String indexValue = null;
		ModifyChopNodeLabel chopLabel = null;
		
		try {
			threadId = getParameterValue("threadId");
			pid = getParameterValue("processId");
			parentObjectAddress = getParameterValue("parentObjectAddress");
			parentClass = getParameterValue("parentClass");
			parentMethod = getParameterValue("parentMethod");
			arrayClass = getParameterValue("arrayClass");
			arrayAddress = getParameterValue("arrayAddress");
			indexValue = getParameterValue("index");
			valueClass = getParameterValue("valueClass");
			valueAddress = getParameterValue("valueAddress");
			chopLabel = new ModifyChopNodeLabel(getParameterValue("chopLabel"));
		} catch (ParameterNotFoundException | ClassCastException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}
		
		String parent = getClassOrObject(parentClass, parentObjectAddress);
		String valueToInsert = valueClass + DLM + valueAddress;
		String array = arrayClass + DLM + arrayAddress;
		
		// Right side container (create if necessary)
		String rightSideVar = chopLabel.getRightSide();
		IName rightSideName = new NameBasic(pid + DLM + threadId + DLM + parent + DLM + parentMethod + DLM + rightSideVar);
		IContainer rightSideContainer = addContainerIfNotExists(rightSideName, valueToInsert);
		
		// Array container (create if necessary)
		String arrayVar = chopLabel.getArray();
		IName arrayName = new NameBasic(pid + DLM + threadId + DLM + parent + DLM + parentMethod + DLM + arrayVar);
		IContainer arrayContainer = addContainerIfNotExists(arrayName, array);
		
		IName elementName = new NameBasic(pid + DLM + array + DLM + indexValue);
		
		// Put the right side data into the array element container
		
		if (isReferenceType(valueToInsert)) {
			// reference type -> assign array element name to right side container
			// remove alias to the array of previous object at that index
			_informationFlowModel.removeAlias(_informationFlowModel.getContainer(elementName), arrayContainer);
			_informationFlowModel.addName(elementName, rightSideContainer, false);
			_informationFlowModel.addAlias(rightSideContainer, arrayContainer);
		} else {
			// value type -> copy data from right side to array element container (create if necessary)
			IContainer elementContainer = addContainerIfNotExists(elementName, array + DLM + indexValue);
			_informationFlowModel.addAlias(elementContainer, arrayContainer);
			_informationFlowModel.emptyContainer(elementContainer);
			_informationFlowModel.copyData(rightSideContainer, elementContainer);
		}
		
		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
