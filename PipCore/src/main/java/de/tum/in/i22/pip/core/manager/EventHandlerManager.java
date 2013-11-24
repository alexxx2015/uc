package de.tum.in.i22.pip.core.manager;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import de.tum.in.i22.pip.core.IActionHandler;
import de.tum.in.i22.pip.core.manager.db.EventHandlerDefinition;

public class EventHandlerManager implements IActionHandlerCreator {

	private static final Logger _logger = Logger.getLogger(EventHandlerManager.class);

	private Map<String, PipClassLoader> _classLoaderMap = null;

	public EventHandlerManager() {
		_classLoaderMap = new HashMap<>();
	}

	@Override
	public IActionHandler createEventHandler(String actionName) throws IllegalAccessException, InstantiationException,
			ClassNotFoundException {

		String className = "de.tum.in.i22.pip.core.eventdef." + actionName + "EventHandler";

		PipClassLoader pipClassLoader = _classLoaderMap.get(className);
		if (pipClassLoader != null) {
			_logger.trace("Load class (definition obtained from database): " + className);
			Class<?> actionHandlerClass = pipClassLoader.loadClass(className);
			_logger.trace("Create class " + className + " instance");
			IActionHandler actionHandler = (IActionHandler) actionHandlerClass.newInstance();
			return actionHandler;
		} else {
			// The specified class is not found in the database.
			// Try to load it from the class path.
			try {
				ClassLoader classLoader = EventHandlerManager.class.getClassLoader();
				_logger.trace("Load class from class path: " + className);
				Class<?> actionHandlerClass = classLoader.loadClass(className);
				_logger.trace("Create class " + className + " instance");
				IActionHandler actionHandler = (IActionHandler) actionHandlerClass.newInstance();
				return actionHandler;
			} catch (Exception e) {
				_logger.warn("Class " + className + " not found. Error: " + e.getMessage());
				return null;
			}
		}
	}

	public void setClassToBeLoaded(EventHandlerDefinition eventHandlerDefinition) {
		_logger.debug("Creating class loader for class: " + eventHandlerDefinition.getClassName());
		String className = eventHandlerDefinition.getClassName();

		PipClassLoader pipClassLoader = new PipClassLoader(PipClassLoader.class.getClassLoader(), className,
				eventHandlerDefinition.getClassFile());

		_classLoaderMap.put(className, pipClassLoader);

	}
}
