package de.tum.in.i22.pip.core.manager;

import de.tum.in.i22.pip.core.IEventHandler;

public interface IEventHandlerCreator {
	public IEventHandler createEventHandler(String actionName)
		throws IllegalAccessException, InstantiationException, ClassNotFoundException;
}
