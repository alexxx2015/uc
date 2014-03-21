package de.tum.in.i22.uc.pip.core.manager;

import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.pip.core.IEventHandler;

public interface IEventHandlerCreator {
	public IEventHandler createEventHandler(IEvent event)
		throws IllegalAccessException, InstantiationException, ClassNotFoundException;
}
