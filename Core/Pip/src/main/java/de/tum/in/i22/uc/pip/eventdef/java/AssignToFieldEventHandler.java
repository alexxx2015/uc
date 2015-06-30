package de.tum.in.i22.uc.pip.eventdef.java;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.pip.eventdef.java.chopnode.ModifyChopNodeLabel;

public class AssignToFieldEventHandler extends JavaEventHandler {

	@Override
	protected IStatus update() {
		
		String threadId = null;
		String pid = null;
		String parentMethod = null;
		String parentObject = null;
		String fieldOwnerObject = null; // type@address
		String fieldOwnerClass = null;
		String field = null;
		String assignee = null;
		ModifyChopNodeLabel chopLabel = null;
		
		try {
			threadId = getParameterValue("threadId");
			pid = getParameterValue("processId");
			parentMethod = getParameterValue("parentMethod");
			parentObject = getParameterValue("parentObject");
			fieldOwnerObject = getParameterValue("fieldOwnerObject");
			fieldOwnerClass = getParameterValue("fieldOwnerClass");
			field = getParameterValue("fieldName");
			assignee = getParameterValue("assignee");
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
		
		boolean fieldIsReferenceType = assignee.contains("@");
		boolean fieldIsStatic = fieldOwnerObject.equals("null");
		
		// Right side container (create if necessary)
		String rightSideVar = chopLabel.getRightSide();
		IName rightSideName = new NameBasic(pid + DLM + threadId + DLM + parentObject + DLM + parentMethod + DLM + rightSideVar);
		IContainer rightSideContainer = addContainerIfNotExists(rightSideName, assignee, parentContainer);

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
		
		// reference type -> name assignee as the field
		// value type -> copy data from assignee into field
		if (fieldIsReferenceType) {
			_informationFlowModel.addName(fieldName, rightSideContainer, false);
			_informationFlowModel.addAlias(rightSideContainer, fieldOwnerContainer);
		} else {
			IContainer fieldContainer = addContainerIfNotExists(fieldName, fieldOwnerContainer);
			_informationFlowModel.copyData(rightSideContainer, fieldContainer);
		}
		
		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
