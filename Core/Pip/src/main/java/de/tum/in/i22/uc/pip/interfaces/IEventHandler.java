package de.tum.in.i22.uc.pip.interfaces;

import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public interface IEventHandler {
	public IStatus performUpdate();

	/**
	 * Sets the event of this event handler and returns the event handler
	 * @param event the event to set
	 * @return the event handler itself
	 */
	public IEventHandler setEvent(IEvent event);
}
