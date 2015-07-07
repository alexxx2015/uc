package de.tum.in.i22.uc.pip.eventdef.java;

import java.util.HashSet;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;

public abstract class ReturnMethodEventHandler extends JavaEventHandler {

	@Override
	protected IStatus update() {
		
		return _messageFactory.createStatus(EStatus.OKAY);
	}
	
	// also copies data from caller object to reference type parameters if class of caller object is not instrumented
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
				cleanUpLocalVariable(name);
			}
		} else {
			// if class is not instrumented, there are only containers for parameters (no local variables)
			// speed up by not iterating over all names
			for (int i = 0; i < argsCount; i++) {
				IName argName = new NameBasic(methodVariablePrefix + DLM + "p" + (i+1));
				cleanUpLocalVariable(argName);
				copyDataFromCallerObjectToParam(argName, callerObjectContainer);
			}
		}
	}

	private void cleanUpLocalVariable(IName varName) {
		IContainer localVarContainer = _informationFlowModel.getContainer(varName);
		if (localVarContainer != null) {
			// reference type -> remove name
			// value type -> remove whole container
			if (containerIsReference(localVarContainer)) {
				_informationFlowModel.removeName(varName);
			} else {
				_informationFlowModel.remove(localVarContainer);
			}
		}
	}
	
	private void copyDataFromCallerObjectToParam(IName paramName, IContainer callerObjectContainer) {
		IContainer paramContainer = _informationFlowModel.getContainer(paramName);
		if (containerIsReference(paramContainer)) {
			Set<IData> callerObjectData = getDataTransitively(callerObjectContainer);
			_informationFlowModel.addData(callerObjectData, paramContainer);
		}
	}
}
