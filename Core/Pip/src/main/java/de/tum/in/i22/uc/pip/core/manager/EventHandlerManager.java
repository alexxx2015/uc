package de.tum.in.i22.uc.pip.core.manager;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.pip.interfaces.IEventHandler;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pip.core.db.EventHandlerDefinition;

public class EventHandlerManager {

	private static final String EVENT_HANDLER_SUFFIX = Settings.getInstance()
			.getPipEventHandlerSuffix();
	private static final String EVENT_HANDLER_PACKAGE = Settings.getInstance()
			.getPipEventHandlerPackage();

	private static final Logger _logger = LoggerFactory
			.getLogger(EventHandlerManager.class);

	private static Map<String, PipClassLoader> _classLoaderMap = new HashMap<>();

	private static Map<String, IEventHandler> _evNameToHandler = new HashMap<String, IEventHandler>();

	public static IEventHandler createEventHandler(IEvent event)
			throws IllegalAccessException, InstantiationException,
			ClassNotFoundException {

		String className = EVENT_HANDLER_PACKAGE
				+ ((event.getPep() != null) ? event.getPep().toLowerCase()
						+ "." : "") + event.getName() + EVENT_HANDLER_SUFFIX;

		IEventHandler result = _evNameToHandler.get(className);
		if (result == null) {
			PipClassLoader pipClassLoader = _classLoaderMap.get(className);
			if (pipClassLoader != null) {
				_logger.trace("Load class (definition obtained from database): "
						+ className);
				Class<?> actionHandlerClass = pipClassLoader
						.loadClass(className);
				_logger.trace("Create class " + className + " instance");
				result = (IEventHandler) actionHandlerClass.newInstance();
				_evNameToHandler.put(className, result);
				return result;
			} else {
				// The specified class is not found in the database.
				// Try to load it from the class path.
				try {
					ClassLoader classLoader = EventHandlerManager.class
							.getClassLoader();
					_logger.trace("Load class from class path: " + className);
					Class<?> actionHandlerClass = classLoader
							.loadClass(className);
					_logger.trace("Create class " + className + " instance");
					result = (IEventHandler) actionHandlerClass.newInstance();
					_evNameToHandler.put(className, result);
					return result;
				} catch (Exception e) {
					_logger.warn("Class " + className + " not found. Error: "
							+ e.getMessage());
					return null;
				}
			}
		} else {
			_logger.trace("Loaded class " + className
					+ " instance from hashtable");
			result.reset();
			return result;
		}
	}

	public static void setClassToBeLoaded(
			EventHandlerDefinition eventHandlerDefinition) {
		_logger.debug("Creating class loader for class: "
				+ eventHandlerDefinition.getClassName());
		String className = eventHandlerDefinition.getClassName();

		PipClassLoader pipClassLoader = new PipClassLoader(
				PipClassLoader.class.getClassLoader(), className,
				eventHandlerDefinition.getClassFile());

		_classLoaderMap.put(className, pipClassLoader);

	}

	public static void clearDefinitions() {
		_evNameToHandler.clear();
	}
}
