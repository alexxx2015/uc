package de.tum.in.i22.pip.core;

import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public interface IEventHandler {
	public IStatus execute_event();

	public void setEvent(IEvent event);
	
	
	
//	public String getVersion();
	
// 	public long getDeploymentTimestamp();
}
