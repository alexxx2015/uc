package de.tum.in.i22.uc.pip.eventdef.java;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;

public abstract class InvokeMethodEventHandler extends JavaEventHandler {

	/**
	 *	Copies values (data items) or references (adds names to containers) of variables with method parameters
	 *	from the parent method to the called method.
	 *	Adds aliases to corresponding object containers:
	 *		inner in called object
	 *		outer in parent object 
	 *	(last arguments can be null).
	 * 
	 * @param argNames
	 * @param argAddresses
	 * @param outerArgNamePrefix
	 * @param innerArgNamePrefix
	 * @param callerObjectContainer
	 * @param parentObjectContainer
	 */
	protected void insertArguments(String[] argNames, String[] argAddresses, 
			String outerArgNamePrefix, String innerArgNamePrefix, 
			IContainer callerObjectContainer, IContainer parentObjectContainer) {
		
		for (int i = 0; i < argAddresses.length; i++) {
			String argName = argNames[i];
			String argument = argAddresses[i];
			
			// constants as parameters -> no information flow, no outer arg container needed
			if (argName.startsWith("#")) continue;
			
			// 1. Add the arguments into the method 
			// reference type -> assign containers named by parent method var
			//		to name of called method's parameter
			// value type -> copy data from container named by parent method var
			// 		to the container named by called method's parameter (create container if necessary)
	
			IName outerArgName = new NameBasic(outerArgNamePrefix + DLM + argName);
			IName innerArgName = new NameBasic(innerArgNamePrefix + DLM + "p" + (i+1));
			
			IContainer innerArgContainer;
			IContainer outerArgContainer;
			
			if (argument.contains("@")) {
				// reference type
				outerArgContainer = addContainerIfNotExists(outerArgName, argument);
				innerArgContainer = outerArgContainer;
				_informationFlowModel.addName(innerArgName, outerArgContainer, false);
			} else {
				// value type
				innerArgContainer = addContainerIfNotExists(innerArgName);
				outerArgContainer = addContainerIfNotExists(outerArgName);
				_informationFlowModel.copyData(outerArgContainer, innerArgContainer);
			}
			
			_informationFlowModel.addAlias(outerArgContainer, parentObjectContainer);
			_informationFlowModel.addAlias(innerArgContainer, callerObjectContainer);
		}	
	}

}
