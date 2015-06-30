package de.tum.in.i22.uc.pip.eventdef.java;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.pip.eventdef.java.chopnode.ModifyChopNodeLabel;

public class AssignToArrayEventHandler extends JavaEventHandler {

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
		
		// Parent container (create if necessary)
		IContainer parentContainer = addParentObjectContainerIfNotExists(parentObject, pid);
		
		boolean arrayIsReferenceType = valueToInsert.contains(";");
		
		// Right side container (create if necessary)
		String rightSideVar = chopLabel.getRightSide();
		IName rightSideName = new NameBasic(pid + DLM + threadId + DLM + parentObject + DLM + parentMethod + DLM + rightSideVar);
		IContainer rightSideContainer = addContainerIfNotExists(rightSideName, valueToInsert, parentContainer);
		
		// Array container (create if necessary)
		String arrayVar = chopLabel.getArray();
		IName arrayName = new NameBasic(pid + DLM + threadId + DLM + parentObject + DLM + parentMethod + DLM + arrayVar);
		IContainer arrayContainer = addContainerIfNotExists(arrayName, array, parentContainer);
		
		// TODO: decide on data propagation from whole array to array cells
		// Array cell container (create if necessary)
		// Not named by local variable, because it does not belong there, can be found globally
		IName arrayCellName = new NameBasic(pid + DLM + array + DLM + indexValue);
		
		// Put the right side data into the array cell container
		
		if (arrayIsReferenceType) {
			// reference type -> assign array cell name to right side container
			_informationFlowModel.addName(arrayCellName, rightSideContainer, false);
			_informationFlowModel.addAlias(rightSideContainer, arrayContainer);
		} else {
			// value type -> copy data from right side to array cell container (create if necessary)
			IContainer arrayCellContainer = addContainerIfNotExists(arrayCellName, array + DLM + indexValue, arrayContainer);
			_informationFlowModel.copyData(rightSideContainer, arrayCellContainer);
		}
		
		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
