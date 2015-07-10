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
		String parentObjectAddress = null;
		String parentClass = null;
		String parentMethod = null;
		String fieldOwnerClass = null;
		String fieldOwnerAddress = null;
		String field = null;
		String assigneeClass = null;
		String assigneeAddress = null;
		boolean fieldOwnerClassIsInstrumented = false;
		ModifyChopNodeLabel chopLabel = null;
		
		try {
			threadId = getParameterValue("threadId");
			pid = getParameterValue("processId");
			parentObjectAddress = getParameterValue("parentObjectAddress");
			parentClass = getParameterValue("parentClass");
			parentMethod = getParameterValue("parentMethod");
			fieldOwnerAddress = getParameterValue("fieldOwnerAddress");
			fieldOwnerClass = getParameterValue("fieldOwnerClass");
			field = getParameterValue("fieldName");
			assigneeClass = getParameterValue("assigneeClass");
			assigneeAddress = getParameterValue("assigneeAddress");
			fieldOwnerClassIsInstrumented = Boolean.parseBoolean(getParameterValue("fieldOwnerClassIsInstrumented"));
			chopLabel = new ModifyChopNodeLabel(getParameterValue("chopLabel"));
		} catch (ParameterNotFoundException | ClassCastException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}
		
		String parent = getClassOrObject(parentClass, parentObjectAddress);
		String assignee = assigneeClass + DLM + assigneeAddress;
		String fieldOwner = getClassOrObject(fieldOwnerClass, fieldOwnerAddress);
				
		// Right side container (create if necessary)
		String rightSideVar = chopLabel.getRightSide();
		IName rightSideName = new NameBasic(pid + DLM + threadId + DLM + parent + DLM + parentMethod + DLM + rightSideVar);
		IContainer rightSideContainer = addContainerIfNotExists(rightSideName, assignee);
		
		// Get field container (create if necessary)
		IName fieldName = new NameBasic(pid + DLM + fieldOwner + DLM + field);
		IContainer fieldOwnerContainer = null;
		if (!fieldOwner.equals(fieldOwnerClass)) { // instance field
			fieldOwnerContainer = addContainerIfNotExists(new NameBasic(pid + DLM + fieldOwner), fieldOwner);
		}

		
		// reference type -> name assignee as the field
		// value type -> copy data from assignee into field
		if (isReferenceType(assignee)) {
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
		if (!fieldOwnerClassIsInstrumented) {
			_informationFlowModel.addAlias(rightSideContainer, fieldOwnerContainer);
		}
		
		return _messageFactory.createStatus(EStatus.OKAY);
	}

}