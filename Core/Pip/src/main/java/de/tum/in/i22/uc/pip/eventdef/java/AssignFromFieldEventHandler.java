package de.tum.in.i22.uc.pip.eventdef.java;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.pip.eventdef.java.chopnode.ReferenceChopNodeLabel;

public class AssignFromFieldEventHandler extends JavaEventHandler {

	@Override
	protected IStatus update() {
		
		String threadId = null;
		String parentMethod = null;
		String parentObject = null;
		String parentClass = null;
		String fieldOwnerObject = null; // type@address
		String fieldOwnerClass = null;
		String field = null;
		ReferenceChopNodeLabel chopLabel = null;
		
		try {
			threadId = getParameterValue("threadId");
			parentMethod = getParameterValue("parentMethod");
			parentObject = getParameterValue("parentObject");
			parentClass = getParameterValue("parentClass");
			fieldOwnerObject = getParameterValue("fieldOwnerObject");
			fieldOwnerClass = getParameterValue("fieldOwnerClass");
			field = getParameterValue("fieldName");
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
		
		// Left side name
		String leftSideVar = chopLabel.getLeftSide();
		IName leftSideName = new NameBasic(threadId + DLM + parentObject + DLM + parentMethod + DLM + leftSideVar);
		
		// Field type (Class types contain ";")
		boolean fieldIsReferenceType = field.contains(";");
		boolean fieldIsStatic = fieldOwnerObject.equals("null");
		
		// Field owner container (create if necessary)
		IName fieldOwnerName;
		if (fieldIsStatic) {
			fieldOwnerName = new NameBasic(threadId + DLM + "class" + DLM + fieldOwnerClass);
		} else { // instance field
			fieldOwnerName = new NameBasic(threadId + DLM + fieldOwnerObject);
		}
		IContainer fieldOwnerContainer = addContainerIfNotExists(fieldOwnerName);
		
		// Field container (create if necessary)
		IName fieldName;
		if (fieldIsStatic) {
			fieldName = new NameBasic(threadId + DLM + "class" + DLM + fieldOwnerClass + DLM + field);
		} else { // instance field
			fieldName = new NameBasic(threadId + DLM + fieldOwnerObject + DLM + field);
		}
		IContainer fieldContainer = addContainerIfNotExists(fieldName, fieldOwnerContainer);
		
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
