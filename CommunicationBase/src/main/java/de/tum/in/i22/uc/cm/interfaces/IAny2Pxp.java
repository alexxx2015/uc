package de.tum.in.i22.uc.cm.interfaces;

import java.util.List;

import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public interface IAny2Pxp {
	/*
	 * From PDP
	 */
	//void sendExecAction2Pxp(IPxpSpec pxpSpec, ExecuteAction execAction);
	public IStatus execute(List<IEvent> event);
}
