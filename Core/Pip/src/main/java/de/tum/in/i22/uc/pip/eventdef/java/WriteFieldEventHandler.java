package de.tum.in.i22.uc.pip.eventdef.java;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.pip.eventdef.java.chopnode.ModifyChopNodeLabel;

public class WriteFieldEventHandler extends JavaEventHandler {

	@Override
	protected IStatus update() {
		
		String threadId = null;
		String pid = null;
		String parentMethod = null;
		String parentObject = null;
		String fieldOwnerObject = null; // type@address
		String fieldOwnerClass = null;
		boolean fieldOwnerClassIsInstrumented = false;
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
			fieldOwnerClassIsInstrumented = Boolean.parseBoolean(getParameterValue("fieldOwnerClassIsInstrumented"));
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
		
		boolean fieldIsReferenceType = assignee.contains("@");
		boolean fieldIsStatic = fieldOwnerObject.equals("null");
		
		// Right side container (create if necessary)
		String rightSideVar = chopLabel.getRightSide();
		IName rightSideName = new NameBasic(pid + DLM + threadId + DLM + parentObject + DLM + parentMethod + DLM + rightSideVar);
		IContainer rightSideContainer = addContainerIfNotExists(rightSideName, assignee);
		
		// Get field container (create if necessary)
		IName fieldName;
		IContainer fieldOwnerContainer = null;
		if (fieldIsStatic) {
			fieldName = new NameBasic(pid + DLM + "class" + DLM + fieldOwnerClass + DLM + field);
		} else { // instance field
			fieldName = new NameBasic(pid + DLM + fieldOwnerObject + DLM + field);
			fieldOwnerContainer = addContainerIfNotExists(new NameBasic(pid + DLM + fieldOwnerObject));
		}

		
		// reference type -> name assignee as the field
		// value type -> copy data from assignee into field
		if (fieldIsReferenceType) {
			// remove possible alias of previous object in that field
			_informationFlowModel.removeAlias(_informationFlowModel.getContainer(fieldName), fieldOwnerContainer);
			_informationFlowModel.addName(fieldName, rightSideContainer, false);
		} else {
			IContainer fieldContainer = addContainerIfNotExists(fieldName);
			_informationFlowModel.emptyContainer(fieldContainer);
			_informationFlowModel.copyData(rightSideContainer, fieldContainer);
		}
		
		// add alias to fieldOwnerContainer if class not instrumented
		// helps getting all data from that object because it is blackboxed
		if (!fieldOwnerClassIsInstrumented && !fieldIsStatic) {
			_informationFlowModel.addAlias(rightSideContainer, fieldOwnerContainer);
		}
		
		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
