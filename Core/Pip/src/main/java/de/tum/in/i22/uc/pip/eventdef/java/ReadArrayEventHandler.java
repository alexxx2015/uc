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
		String parentMethod = null;
		String parentObject = null;
		String array = null; // type@address
		String arrayAtIndex = null; // type@address or value
		String indexValue = null;
		ReferenceChopNodeLabel chopLabel = null;
		
		try {
			threadId = getParameterValue("threadId");
			pid = getParameterValue("processId");
			parentMethod = getParameterValue("parentMethod");
			parentObject = getParameterValue("parentObject");
			array = getParameterValue("array");
			indexValue = getParameterValue("index");
			arrayAtIndex = getParameterValue("arrayAtIndex");
			chopLabel = new ReferenceChopNodeLabel(getParameterValue("chopLabel"));
		} catch (ParameterNotFoundException | ClassCastException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}
		
		// No parent object means that the parent method is a class method
		if (parentObject.equals("null")) {
			parentObject = "class";
		}
		
		// derive left side type from array type (array@index)
		boolean leftSideIsReferenceType = array.contains("@");
		
		// Array container (create if necessary)
		String arrayVar = chopLabel.getArray();
		IName arrayName = new NameBasic(pid + DLM + threadId + DLM + parentObject + DLM + parentMethod + DLM + arrayVar);
		IContainer arrayContainer = addContainerIfNotExists(arrayName, array);
		
		// Array cell container (create if necessary)
		IName arrayCellName = new NameBasic(pid + DLM + array + DLM + indexValue);
		IContainer arrayCellContainer = addContainerIfNotExists(arrayCellName, arrayAtIndex);
		_informationFlowModel.addAlias(arrayCellContainer, arrayContainer);
		
		// Left side name
		String leftSideVar = chopLabel.getLeftSide();
		IName leftSideName = new NameBasic(pid + DLM + threadId + DLM + parentObject + DLM + parentMethod + DLM + leftSideVar);
		
		// 1. Put the array data into the left side container
		
		if (leftSideIsReferenceType) {
			// reference type -> assign left side name to cell container + copy data from array container (no other possibility)
			_informationFlowModel.addName(leftSideName, arrayCellContainer, false);
			_informationFlowModel.copyData(arrayContainer, arrayCellContainer);
		} else {
			// value type -> copy data from cell AND array container
			IContainer leftSideContainer = addContainerIfNotExists(leftSideName);
			_informationFlowModel.copyData(arrayCellContainer, leftSideContainer);
			_informationFlowModel.copyData(arrayContainer, leftSideContainer);
		}
		
		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
