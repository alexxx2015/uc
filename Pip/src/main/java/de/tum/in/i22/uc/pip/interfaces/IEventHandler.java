package de.tum.in.i22.uc.pip.interfaces;

import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public interface IEventHandler {
	public IStatus executeEvent();
	public void setEvent(IEvent event);
}
