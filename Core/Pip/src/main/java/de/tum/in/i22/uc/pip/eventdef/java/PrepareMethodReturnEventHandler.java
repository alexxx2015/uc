package de.tum.in.i22.uc.pip.eventdef.java;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.factories.JavaNameFactory;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.pip.eventdef.java.chopnode.CompoundChopNodeLabel;

public class PrepareMethodReturnEventHandler extends JavaEventHandler {

    @Override
    protected IStatus update() {

	String threadId = null;
	String pid = null;
	String parentObjectAddress = null;
	String parentClass = null;
	String parentMethod = null;
	String returnValueClass = null;
	String returnValueAddress = null;
	CompoundChopNodeLabel chopLabel = null;

	try {
	    threadId = getParameterValue("threadId");
	    pid = getParameterValue("processId");
	    parentObjectAddress = getParameterValue("parentObjectAddress");
	    parentClass = getParameterValue("parentClass");
	    parentMethod = getParameterValue("parentMethod");
	    returnValueClass = getParameterValue("returnValueClass");
	    returnValueAddress = getParameterValue("returnValueAddress");
	    chopLabel = new CompoundChopNodeLabel(getParameterValue("chopLabel"));
	} catch (ParameterNotFoundException | ClassCastException e) {
	    _logger.error(e.getMessage());
	    return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
	}

	// Variable to return
	String var = chopLabel.getArgument();
	IName varName = JavaNameFactory.createLocalVarName(pid, threadId, parentClass, parentObjectAddress, parentMethod, var);
	IContainer varContainer = addContainerIfNotExists(varName, returnValueClass, returnValueAddress);
	
	IName varNameLoc = JavaNameFactory.createLocalVarName(pid, threadId, parentClass, returnValueAddress, parentMethod, var);
	_informationFlowModel.addName(varNameLoc, varContainer);

	// ret var name of the method
	IName retVarName = JavaNameFactory.createLocalVarName(pid, threadId, parentClass, parentObjectAddress, parentMethod, RET);

	// reference type -> add ret name to varContainer
	// value type -> create ret container and copy data from varContainer
	if (isValidAddress(returnValueAddress)) {
	    _informationFlowModel.addName(retVarName, varContainer, false);
	} else {
	    IContainer retVarContainer = addContainerIfNotExists(retVarName, null, null);
	    _informationFlowModel.copyData(varContainer, retVarContainer);
	}

	return _messageFactory.createStatus(EStatus.OKAY);
    }

}
