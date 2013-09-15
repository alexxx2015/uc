package de.tum.in.i22.pip.core.manager;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import de.tum.in.i22.pip.core.IActionHandler;
import de.tum.in.i22.pip.core.manager.db.ActionHandlerDefinition;

public class ActionHandlerManager implements IActionHandlerCreator {
	
	
	private static final Logger _logger = Logger
			.getLogger(ActionHandlerManager.class);
	
	private Map<String, byte[]> _map = null;
	private Map<String, PipClassLoader> _classLoaderMap = null;
	
	private PipClassLoader _pipClassLoader = null;
	
	public ActionHandlerManager() {
		_map = new HashMap<>();
		_classLoaderMap = new HashMap<>();
	}
	
	@Override
	public IActionHandler createActionHandler(String actionName) {
		String className = "de.tum.in.i22.pip.core.actions." + actionName + "ActionHandler";
		
		try {
			PipClassLoader _pipClassLoader = null;
			if (_map.containsKey(className)) {
				 _pipClassLoader = new PipClassLoader(ActionHandlerManager.class.getClassLoader(), _map);
				 _classLoaderMap.put(className, _pipClassLoader);
				 _map.remove(className);
			}
			else if (_classLoaderMap.containsKey(className)) {
				this._pipClassLoader = _classLoaderMap.get(className);
			} else {
				return null;
			}
			
			_logger.trace("Load class: " + className);
	        Class<?> actionHandlerClass = _pipClassLoader.loadClass(className);
	        _logger.trace("Create class " + className + " instance");
	        IActionHandler actionHandler = (IActionHandler)actionHandlerClass.newInstance();
	        return actionHandler;
		} catch (Exception e) {
			// TODO handle exceptions more gracefuly
		}
		return null;
	}

	public void setClassToBeLoaded(ActionHandlerDefinition actionHandlerDefinition) {
		_map.put(actionHandlerDefinition.getClassName(), actionHandlerDefinition.getClassFile());
	}
	
	public void loadClassDefinitions() {
		//TODO load from the database on startup
	}
}
