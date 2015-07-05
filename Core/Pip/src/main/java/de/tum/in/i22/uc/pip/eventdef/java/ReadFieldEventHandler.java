package de.tum.in.i22.uc.pip.eventdef.java;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.pip.eventdef.java.chopnode.ReferenceChopNodeLabel;

public class ReadFieldEventHandler extends JavaEventHandler {

	@Override
	protected IStatus update() {
		
		String threadId = null;
		String pid = null;
		String parentMethod = null;
		String parentObject = null;
		String fieldOwnerObject = null; // type@address
		String fieldOwnerClass = null;
		String field = null;
		String fieldValue = null;
		ReferenceChopNodeLabel chopLabel = null;
		
		try {
			threadId = getParameterValue("threadId");
			pid = getParameterValue("processId");
			parentMethod = getParameterValue("parentMethod");
			parentObject = getParameterValue("parentObject");
			fieldOwnerObject = getParameterValue("fieldOwnerObject");
			fieldOwnerClass = getParameterValue("fieldOwnerClass");
			field = getParameterValue("fieldName");
			fieldValue = getParameterValue("fieldValue");
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
		IContainer parentContainer = addParentObjectContainerIfNotExists(parentObject, pid);
		
		// Left side name
		String leftSideVar = chopLabel.getLeftSide();
		IName leftSideName = new NameBasic(pid + DLM + threadId + DLM + parentObject + DLM + parentMethod + DLM + leftSideVar);
		
		boolean fieldIsReferenceType = fieldValue.contains("@");
		boolean fieldIsStatic = fieldOwnerObject.equals("null");
		
		// Get field owner container (only if instance field) (create if necessary)
		IContainer fieldOwnerContainer = null;
		if (!fieldIsStatic) { // instance field
			IName fieldOwnerName = new NameBasic(pid + DLM + fieldOwnerObject);
			fieldOwnerContainer = addContainerIfNotExists(fieldOwnerName);
		}
		
		// Get field container (create if necessary)
		IName fieldName;
		if (fieldIsStatic) {
			fieldName = new NameBasic(pid + DLM + "class" + DLM + fieldOwnerClass + DLM + field);
		} else { // instance field
			fieldName = new NameBasic(pid + DLM + fieldOwnerObject + DLM + field);
		}
		IContainer fieldContainer = addContainerIfNotExists(fieldName, fieldValue, fieldOwnerContainer);
		
		// Reference type -> make left side name also point to field container
		// Value type -> just copy the data from field container to left side container (create it first)
		if (fieldIsReferenceType) {
			_informationFlowModel.addName(leftSideName, fieldContainer, false);
			_informationFlowModel.addAlias(fieldContainer, parentContainer);
		} else {
			IContainer leftSideContainer = addContainerIfNotExists(leftSideName, parentContainer);
			_informationFlowModel.copyData(fieldContainer, leftSideContainer);
		}
		
		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
