package de.tum.in.i22.pip.core;

import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public interface IEventHandler {
	public IStatus executeEvent();

	public void setEvent(IEvent event);
	
	
	
//	public String getVersion();
	
// 	public long getDeploymentTimestamp();
}
