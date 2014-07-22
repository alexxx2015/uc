package de.tum.in.i22.uc.pip.eventdef.java;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class ContextEventHandler extends JavaEventHandler{

	@Override
	protected IStatus update() {
		// TODO Auto-generated method stub
		try {
			String objectId = getParameterValue(_paramObjectId);
			String contextId = getParameterValue(_paramContextId);
			String contextLocation = getParameterValue(_paramContextLocation);
			String contextOffset = getParameterValue(_paramContextOffset);
			String processId = getParameterValue(_paramPID);
			String threadId = getParameterValue(_paramThreadId);
			contextToObject.put(contextId, objectId);
		} catch (ParameterNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
