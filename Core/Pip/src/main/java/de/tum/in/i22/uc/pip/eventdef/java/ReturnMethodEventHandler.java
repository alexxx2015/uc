package de.tum.in.i22.uc.pip.eventdef.java;

import java.util.HashSet;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;

public abstract class ReturnMethodEventHandler extends JavaEventHandler {

	@Override
	protected IStatus update() {
		
		/*
		Todo:
		1. Get the method argument containers
		2. Put the method argument containers into the return object
		3. Put the return object into the left side (if there is one), 
			if class not instrumented, put the whole called object inside
		4. Clean up: Remove method argument containers
			clean all aliases from arguments to called object
			remove names (p1, p2 ...) and remove unnamed containers


		// Get left side container
		String leftSide = chopLabel.getLeftSide();
		IContainer leftSideContainer = null;
		if (leftSide != null) {
			IName leftSideName = new NameBasic(threadId + DLM + parentObject + DLM + parentMethod + DLM + leftSide);
			leftSideContainer = addContainerIfNotExists(leftSideName);
						
			// Put the left side container into the parent object container (if its not a class)
			if (!parentObject.equals("class")) {
				IName parentName = new NameBasic(threadId + DLM + parentObject);
				IContainer parentContainer = addContainerIfNotExists(parentName);
				_informationFlowModel.addAlias(leftSideContainer, parentContainer);
			}
		}
		
		*/
		return _messageFactory.createStatus(EStatus.OKAY);
	}
	
	protected void cleanUpParamsAndLocals(String methodVariablePrefix, boolean classIsInstrumented,
			IContainer callerObjectContainer, int argsCount) {
		if (classIsInstrumented) {
			Set<IName> namesToCleanUp = new HashSet<IName>();
			for (IName name : _informationFlowModel.getAllNames()) {
				if (name.getName().startsWith(methodVariablePrefix)) {
					namesToCleanUp.add(name);
				}
			}
			for (IName name : namesToCleanUp) {
				cleanUpLocalVariable(name, callerObjectContainer);
			}
		} else {
			// if class is not instrumented, there are only containers for parameters (no local variables)
			// speed up by not iterating over all names
			for (int i = 0; i < argsCount; i++) {
				IName argName = new NameBasic(methodVariablePrefix + DLM + "p" + (i+1));
				cleanUpLocalVariable(argName, callerObjectContainer);
			}
		}
	}

	private void cleanUpLocalVariable(IName varName, IContainer parentObjectContainer) {
		IContainer localVarContainer = _informationFlowModel.getContainer(varName);
		if (localVarContainer != null) {
			// reference type -> remove name but keep alias to caller object
			// value type -> remove whole container
			if (localVarContainer.getId().contains("@")) {
				_informationFlowModel.removeName(varName);
			} else {
				_informationFlowModel.remove(localVarContainer);
			}
		}
	}
}
