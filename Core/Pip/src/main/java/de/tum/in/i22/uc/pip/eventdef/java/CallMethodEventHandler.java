package de.tum.in.i22.uc.pip.eventdef.java;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.factories.JavaNameFactory;

public abstract class CallMethodEventHandler extends JavaEventHandler {

    /**
     * Copies values (data items) or references (adds names to containers) of
     * variables with method parameters from the parent method to the called
     * method. Adds aliases from parameters to @{code callerObjectContainer}.
     *
     * (last argument can be null).
     * 
     * @param argNames
     * @param argAddresses
     * @param outerArgNamePrefix
     * @param innerArgNamePrefix
     * @param callerObjectContainer
     *            is only != null if its class is not instrumented
     * @param parentObjectContainer
     */
    protected void insertArguments(String[] argNames, String[] argTypes, String[] argAddresses, String pid, String threadId,
	    String parentClass, String parentObjectAddress, String parentMethod, String callerClass,
	    String callerObjectAddress, String calledMethod, IContainer callerObjectContainer) {

	for (int i = 0; i < argAddresses.length; i++) {
	    String argName = argNames[i];
	    String argType = argTypes[i];
	    String argAddress = argAddresses[i];

	    // constants as parameters -> no information flow, no outer arg
	    // container needed
	    if (argName.startsWith("#"))
		continue;

	    // 1. Add the arguments into the method
	    // reference type -> assign containers named by parent method var
	    // to name of called method's parameter
	    // value type -> copy data from container named by parent method var
	    // to the container named by called method's parameter (create
	    // container if necessary)

	    IName actualParamName = JavaNameFactory.createLocalVarName(pid, threadId, parentClass, parentObjectAddress,
		    parentMethod, argName);
	    IName formalParamName = JavaNameFactory.createLocalVarName(pid, threadId, callerClass, callerObjectAddress,
		    calledMethod, "p" + (i + 1));

	    IContainer formalParamContainer;
	    IContainer actualParamContainer;

	    if (isValidAddress(argAddress)) {
		actualParamContainer = addContainerIfNotExists(actualParamName, argType, argAddress);
		formalParamContainer = actualParamContainer;
		_informationFlowModel.addName(formalParamName, actualParamContainer, false);
	    } else {
		formalParamContainer = addContainerIfNotExists(formalParamName, null, null);
		actualParamContainer = addContainerIfNotExists(actualParamName, null, null);
		_informationFlowModel.copyData(actualParamContainer, formalParamContainer);
	    }

	    if (callerObjectContainer != null) {
		_informationFlowModel.addAlias(formalParamContainer, callerObjectContainer);
	    }
	}
    }

}
