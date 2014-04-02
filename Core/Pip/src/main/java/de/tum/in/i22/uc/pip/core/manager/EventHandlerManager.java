package de.tum.in.i22.uc.pip.core.manager;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pip.core.db.EventHandlerDefinition;
import de.tum.in.i22.uc.pip.interfaces.IEventHandler;

public class EventHandlerManager {

	private static final String EVENT_HANDLER_SUFFIX = Settings.getInstance().getPipEventHandlerSuffix();
	private static final String EVENT_HANDLER_PACKAGE = Settings.getInstance().getPipEventHandlerPackage();

	private static final Logger _logger = LoggerFactory.getLogger(EventHandlerManager.class);

	private static Map<String, PipClassLoader> _classLoaderMap = new HashMap<>();

	public static IEventHandler createEventHandler(IEvent event) throws IllegalAccessException, InstantiationException, ClassNotFoundException {

		String className =  EVENT_HANDLER_PACKAGE
				+ ((event.getPep() != null) ? event.getPep().toLowerCase() + "." : "")
				+ event.getName()
				+ EVENT_HANDLER_SUFFIX;

		PipClassLoader pipClassLoader = _classLoaderMap.get(className);
		if (pipClassLoader != null) {
			_logger.trace("Load class (definition obtained from database): " + className);
			Class<?> actionHandlerClass = pipClassLoader.loadClass(className);
			_logger.trace("Create class " + className + " instance");
			IEventHandler actionHandler = (IEventHandler) actionHandlerClass.newInstance();
			return actionHandler;
		} else {
			// The specified class is not found in the database.
			// Try to load it from the class path.
			try {
				ClassLoader classLoader = EventHandlerManager.class.getClassLoader();
				_logger.trace("Load class from class path: " + className);
				Class<?> actionHandlerClass = classLoader.loadClass(className);
				_logger.trace("Create class " + className + " instance");
				IEventHandler actionHandler = (IEventHandler) actionHandlerClass.newInstance();
				return actionHandler;
			} catch (Exception e) {
				_logger.warn("Class " + className + " not found. Error: " + e.getMessage());
				return null;
			}
		}
	}

	public static void setClassToBeLoaded(EventHandlerDefinition eventHandlerDefinition) {
		_logger.debug("Creating class loader for class: " + eventHandlerDefinition.getClassName());
		String className = eventHandlerDefinition.getClassName();

		PipClassLoader pipClassLoader = new PipClassLoader(PipClassLoader.class.getClassLoader(), className,
				eventHandlerDefinition.getClassFile());

		_classLoaderMap.put(className, pipClassLoader);

	}
}
