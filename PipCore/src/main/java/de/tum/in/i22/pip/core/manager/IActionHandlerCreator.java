package de.tum.in.i22.pip.core.manager;

import de.tum.in.i22.pip.core.IActionHandler;

public interface IActionHandlerCreator {
	public IActionHandler createActionHandler(String actionName)
		throws IllegalAccessException, InstantiationException, ClassNotFoundException;
}
