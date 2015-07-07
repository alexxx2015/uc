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
		String parentMethod = null;
		String parentObject = null;
		String array = null; // type@address
		String indexValue = null;
		String valueToInsert = null; // type@address or value
		ModifyChopNodeLabel chopLabel = null;
		
		try {
			threadId = getParameterValue("threadId");
			pid = getParameterValue("processId");
			parentMethod = getParameterValue("parentMethod");
			parentObject = getParameterValue("parentObject");
			array = getParameterValue("array");
			indexValue = getParameterValue("index");
			valueToInsert = getParameterValue("value");
			chopLabel = new ModifyChopNodeLabel(getParameterValue("chopLabel"));
		} catch (ParameterNotFoundException | ClassCastException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}
		
		// No parent object means that the parent method is a class method
		if (parentObject.equals("null")) {
			parentObject = "class";
		}
		
		boolean arrayIsReferenceType = valueToInsert.contains("@");
		
		// Right side container (create if necessary)
		String rightSideVar = chopLabel.getRightSide();
		IName rightSideName = new NameBasic(pid + DLM + threadId + DLM + parentObject + DLM + parentMethod + DLM + rightSideVar);
		IContainer rightSideContainer = addContainerIfNotExists(rightSideName, valueToInsert);
		
		// Array container (create if necessary)
		String arrayVar = chopLabel.getArray();
		IName arrayName = new NameBasic(pid + DLM + threadId + DLM + parentObject + DLM + parentMethod + DLM + arrayVar);
		IContainer arrayContainer = addContainerIfNotExists(arrayName, array);
		
		IName arrayCellName = new NameBasic(pid + DLM + array + DLM + indexValue);
		
		// Put the right side data into the array cell container
		
		if (arrayIsReferenceType) {
			// reference type -> assign array cell name to right side container
			// remove alias to the array of previous object at that index
			_informationFlowModel.removeAlias(_informationFlowModel.getContainer(arrayCellName), arrayContainer);
			_informationFlowModel.addName(arrayCellName, rightSideContainer, false);
			_informationFlowModel.addAlias(rightSideContainer, arrayContainer);
		} else {
			// value type -> copy data from right side to array cell container (create if necessary)
			IContainer arrayCellContainer = addContainerIfNotExists(arrayCellName, array + DLM + indexValue);
			_informationFlowModel.addAlias(arrayCellContainer, arrayContainer);
			_informationFlowModel.emptyContainer(arrayCellContainer);
			_informationFlowModel.copyData(rightSideContainer, arrayCellContainer);
		}
		
		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
