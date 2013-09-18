package de.tum.in.i22.pip.core.manager;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import de.tum.in.i22.pip.core.IActionHandler;
import de.tum.in.i22.pip.core.manager.db.ActionHandlerDefinition;

public class ActionHandlerManager implements IActionHandlerCreator {
	
	
	private static final Logger _logger = Logger
			.getLogger(ActionHandlerManager.class);
	
	private Map<String, PipClassLoader> _classLoaderMap = null;
	
	public ActionHandlerManager() {
		_classLoaderMap = new HashMap<>();
	}
	
	@Override
	public IActionHandler createActionHandler(String actionName) 
		throws IllegalAccessException, InstantiationException, ClassNotFoundException {
		
		String className = "de.tum.in.i22.pip.core.actions." + actionName + "ActionHandler";

		PipClassLoader pipClassLoader = _classLoaderMap.get(className);
		if (pipClassLoader != null) {
			_logger.trace("Load class: " + className);
	        Class<?> actionHandlerClass = pipClassLoader.loadClass(className);
	        _logger.trace("Create class " + className + " instance");
	        IActionHandler actionHandler = (IActionHandler)actionHandlerClass.newInstance();
	        return actionHandler;
		} else {
			return null;
		}
	}

	public void setClassToBeLoaded(ActionHandlerDefinition actionHandlerDefinition) {
		_logger.debug("Creating class loader for class: " + actionHandlerDefinition.getClassName());
		String className = actionHandlerDefinition.getClassName();
		
		PipClassLoader pipClassLoader = new PipClassLoader(
				 PipClassLoader.class.getClassLoader(),
				 className,
				 actionHandlerDefinition.getClassFile());
		
		_classLoaderMap.put(className, pipClassLoader);
	
	}
}
