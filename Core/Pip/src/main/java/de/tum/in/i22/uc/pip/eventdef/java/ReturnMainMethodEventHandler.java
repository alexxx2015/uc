package de.tum.in.i22.uc.pip.eventdef.java;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class ReturnMainMethodEventHandler extends ReturnMethodEventHandler {

	@Override
	protected IStatus update() {
		
		String threadId = null;
		String pid = null;
		String callerClass = null;
		String calledMethod = null;
		
		try {
			threadId = getParameterValue("threadId");
			pid = getParameterValue("processId");
			callerClass = getParameterValue("callerClass");
			calledMethod = getParameterValue("calledMethod");
		} catch (ParameterNotFoundException | ClassCastException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}
		
		String methodVariablePrefix = pid + DLM + threadId + DLM + callerClass + DLM + calledMethod;
		
		// Clean up method parameters and local variables
		cleanUpParamsAndLocals(methodVariablePrefix, true, null, 1);
		
		return _messageFactory.createStatus(EStatus.OKAY);
	}
	
}
