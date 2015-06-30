package de.tum.in.i22.uc.pip.eventdef.java;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.pip.eventdef.java.chopnode.CompoundChopNodeLabel;

public class PrepareReturnEventHandler extends JavaEventHandler {

	@Override
	protected IStatus update() {
		
		String threadId = null;
		String pid = null;
		String parentMethod = null;
		String parentObject = null;
		String returnValue = null; // class@address or value
		CompoundChopNodeLabel chopLabel = null;
		
		try {
			threadId = getParameterValue("threadId");
			pid = getParameterValue("processId");
			parentMethod = getParameterValue("parentMethod");
			parentObject = getParameterValue("parentObject");
			returnValue = getParameterValue("returnValue");
			chopLabel = new CompoundChopNodeLabel(getParameterValue("chopLabel"));
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
		
		boolean returnValueIsReferenceType = returnValue.contains(";");
		
		// Variable to return
		String var = chopLabel.getArgument();
		IName varName = new NameBasic(pid + DLM + threadId + DLM + parentObject + DLM + parentMethod + DLM + var);
		IContainer varContainer = addContainerIfNotExists(varName, returnValue, parentContainer);
		
		// ret var name of the method
		IName retVarName = new NameBasic(pid + DLM + threadId + DLM + parentObject + DLM + parentMethod + DLM + RET);
		
		// reference type -> add ret name to varContainer
		// value type -> create ret container and copy data from varContainer
		if (returnValueIsReferenceType) {
			_informationFlowModel.addName(retVarName, varContainer, false);
		} else {
			IContainer retVarContainer = addContainerIfNotExists(retVarName, parentContainer);
			_informationFlowModel.copyData(varContainer, retVarContainer);
		}
		
		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
