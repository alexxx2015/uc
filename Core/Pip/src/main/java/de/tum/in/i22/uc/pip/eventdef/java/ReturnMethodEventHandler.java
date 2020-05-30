package de.tum.in.i22.uc.pip.eventdef.java;

import java.util.HashSet;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.datatypes.java.containers.ReferenceContainer;
import de.tum.in.i22.uc.cm.datatypes.java.containers.SinkSourceContainer;
import de.tum.in.i22.uc.cm.datatypes.java.names.InstanceMethodVariableName;
import de.tum.in.i22.uc.cm.datatypes.java.names.StaticMethodVariableName;
import de.tum.in.i22.uc.cm.factories.JavaNameFactory;

public abstract class ReturnMethodEventHandler extends JavaEventHandler {

    @Override
    protected IStatus update() {

	return _messageFactory.createStatus(EStatus.OKAY);
    }

    // also copies data from caller object to reference type parameters if class
    // of caller object is not instrumented
    protected void cleanUpParamsAndLocals(String pid, String threadId, String callerClass, String callerObjectAddress,
	    String calledMethod, boolean classIsInstrumented, IContainer callerObjectContainer, int argsCount) {
    	if(true)
    		return;
	if (classIsInstrumented) {
	    Set<IName> namesToCleanUp = new HashSet<IName>();
	    for (IName name : _informationFlowModel.getAllNames()) {
		if (name instanceof InstanceMethodVariableName) {
		    InstanceMethodVariableName localVarName = (InstanceMethodVariableName) name;
		    if (localVarName.getPid().equals(pid) && localVarName.getThreadId().equals(threadId)
			    && localVarName.getClassName().equals(callerClass)
			    && localVarName.getObjectAddress().equals(callerObjectAddress)
			    && localVarName.getMethodName().equals(calledMethod)) {
			namesToCleanUp.add(localVarName);
		    }
		} else if (name instanceof StaticMethodVariableName) {
		    StaticMethodVariableName localVariableName = (StaticMethodVariableName) name;
		    if (localVariableName.getPid().equals(pid) && localVariableName.getThreadId().equals(threadId)
			    && localVariableName.getClassName().equals(callerClass)
			    && localVariableName.getMethodName().equals(calledMethod)) {
			namesToCleanUp.add(localVariableName);
		    }
		}
	    }
	    for (IName name : namesToCleanUp) {
		cleanUpLocalVariable(name, null);
	    }
	} else {
	    // if class is not instrumented, there are only containers for
	    // parameters (no local variables)
	    // speed up by not iterating over all names
	    for (int i = 0; i < argsCount; i++) {
		IName argName = JavaNameFactory.createLocalVarName(pid, threadId, callerClass, callerObjectAddress,
			calledMethod, "p" + (i + 1));
		copyDataFromCallerObjectToParam(argName, callerObjectContainer);
		cleanUpLocalVariable(argName, callerObjectContainer);
	    }
	}
    }

    /**
     * 
     * @param varName
     * @param callerObjectContainer
     *            only pass container in here if you want to copy data from
     *            local var to this container
     */
    private void cleanUpLocalVariable(IName varName, IContainer callerObjectContainer) {
	IContainer localVarContainer = _informationFlowModel.getContainer(varName);
	if (localVarContainer != null) {
	    // reference type -> remove name
	    // value type -> remove whole container
	    if (localVarContainer instanceof ReferenceContainer || localVarContainer instanceof SinkSourceContainer) {
		// no need to copy data because this container will not be
		// destroyed (all object containers are kept alive by global
		// name)
		_informationFlowModel.removeName(varName);
	    } else {
		_informationFlowModel.addData(_informationFlowModel.getData(localVarContainer), callerObjectContainer);
		_informationFlowModel.remove(localVarContainer);
	    }
	}
    }

    private void copyDataFromCallerObjectToParam(IName paramName, IContainer callerObjectContainer) {
	IContainer paramContainer = _informationFlowModel.getContainer(paramName);
	if (paramContainer instanceof ReferenceContainer) {
	    Set<IData> callerObjectData = getDataTransitively(callerObjectContainer);
	    _informationFlowModel.addData(callerObjectData, paramContainer);
	}
    }
}
