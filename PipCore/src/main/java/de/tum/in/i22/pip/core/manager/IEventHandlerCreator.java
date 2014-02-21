package de.tum.in.i22.pip.core.manager;

import de.tum.in.i22.pip.core.IEventHandler;
import de.tum.in.i22.uc.cm.datatypes.IEvent;

public interface IEventHandlerCreator {
	public IEventHandler createEventHandler(IEvent event)
		throws IllegalAccessException, InstantiationException, ClassNotFoundException;
}
