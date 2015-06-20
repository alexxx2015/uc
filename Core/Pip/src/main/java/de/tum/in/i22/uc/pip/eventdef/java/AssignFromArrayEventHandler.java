package de.tum.in.i22.uc.pip.eventdef.java;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.pip.eventdef.java.chopnode.ReferenceChopNodeLabel;

public class AssignFromArrayEventHandler extends JavaEventHandler {

	@Override
	protected IStatus update() {
		
		String threadId = null;
		String parentMethod = null;
		String parentObject = null;
		String parentClass = null;
		String array = null; // type@address
		String indexValue = null;
		ReferenceChopNodeLabel chopLabel = null;
		
		try {
			threadId = getParameterValue("threadId");
			parentMethod = getParameterValue("parentMethod");
			parentObject = getParameterValue("parentObject");
			parentClass = getParameterValue("parentClass");
			array = getParameterValue("array");
			indexValue = getParameterValue("index");
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
		
		// Parent container (create if necessary)
		
		IName parentName;
		if (parentObject.equals("class")) {
			parentName = new NameBasic(threadId + DLM + "class" + DLM + parentClass);
		} else {
			parentName = new NameBasic(threadId + DLM + parentObject);
		}
		IContainer parentContainer = addContainerIfNotExists(parentName);
		
		// Left side container (create if necessary)
		String leftSideVar = chopLabel.getLeftSide();
		IName leftSideName = new NameBasic(threadId + DLM + parentObject + DLM + parentMethod + DLM + leftSideVar);
		IContainer leftSideContainer = addContainerIfNotExists(leftSideName, parentContainer);
		
		// derive left side type from array type (Class types contain ";")
		boolean leftSideIsReferenceType = array.contains(";");
		
		// Array container (create if necessary)
		String arrayVar = chopLabel.getArray();
		IName arrayName = new NameBasic(threadId + DLM + parentObject + DLM + parentMethod + DLM + arrayVar);
		IContainer arrayContainer = addContainerIfNotExists(arrayName, array, parentContainer);
		
		// 1. Put the array data into the left side container
		
		if (leftSideIsReferenceType) {
			// reference type -> only alias
			_informationFlowModel.addAlias(arrayContainer, leftSideContainer);
		} else {
			// value type -> copy data
			_informationFlowModel.copyData(arrayContainer, leftSideContainer);
		}
		
		// 2. Put the index data into the left side container
		
		// Index container (create if necessary)
		String indexVar = chopLabel.getArrayIndex();
		if (!indexVar.startsWith("#")) {
			IName indexVarName = new NameBasic(threadId + DLM + parentObject + DLM + parentMethod + DLM + indexVar);
			IContainer indexVarContainer = addContainerIfNotExists(indexVarName, parentContainer);
			
			// reference and value type -> copy index (always value type) data into left side
			_informationFlowModel.copyData(indexVarContainer, leftSideContainer);		
		}
		
		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
