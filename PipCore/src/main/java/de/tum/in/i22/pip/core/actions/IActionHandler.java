package de.tum.in.i22.pip.core.actions;

import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public interface IActionHandler {
	public IStatus execute();

	public void setEvent(IEvent event);
}
