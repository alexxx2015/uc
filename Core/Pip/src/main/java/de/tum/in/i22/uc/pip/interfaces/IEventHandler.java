package de.tum.in.i22.uc.pip.interfaces;

import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public interface IEventHandler {
	public IStatus executeEvent();

	/**
	 * Sets the event of this event handler and returns the event handler
	 * @param event the event to set
	 * @return the event handler itself
	 */
	public IEventHandler setEvent(IEvent event);

	/**
	 * Returns true if this event shall be executed in case it is actual
	 * @return
	 */
	public boolean executeIfActual();

	/**
	 * Returns true if this event shall be executed in case it is desired
	 * @return
	 */
	public boolean executeIfDesired();
}
