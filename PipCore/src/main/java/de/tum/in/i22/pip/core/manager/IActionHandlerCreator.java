package de.tum.in.i22.pip.core.manager;

import de.tum.in.i22.pip.core.IActionHandler;

public interface IActionHandlerCreator {
	public IActionHandler createEventHandler(String actionName)
		throws IllegalAccessException, InstantiationException, ClassNotFoundException;
}
