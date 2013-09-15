package de.tum.in.i22.pip.core;

import org.apache.log4j.Logger;

import de.tum.in.i22.pip.core.actions.DefaultActionHandler;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class PipSemantics {
	
	private static final Logger _logger = Logger.getLogger(PipSemantics.class);
	
	public IStatus processEvent(IEvent event) {
		
		String action = event.getName();
		_logger.debug("Action name: " + action);
		
		_logger.trace("Create action handler");
		
		IActionHandler actionHandler = null;
		try {
			actionHandler = getActionHandler(action, event);
		} catch (IllegalAccessException | InstantiationException e) {
			_logger.error("Failed to create Action Handler for action " + action, e);
			
		}
		
		if (actionHandler == null) {
			_logger.trace("Create default action handler");
			
			actionHandler = new DefaultActionHandler();
		}
		
		IStatus status =  actionHandler.execute();
		_logger.trace("Status to return: " + status);
		return status;
	}

	private IActionHandler getActionHandler(String action, IEvent event) 
			throws InstantiationException, IllegalAccessException {
		
		ClassLoader classLoader = PipSemantics.class.getClassLoader();

		String className = "de.tum.in.i22.pip.core.actions." + action + "ActionHandler";
	    try {
	    	_logger.trace("Load class: " + className);
	        Class<?> actionHandlerClass = classLoader.loadClass(className);
	        _logger.trace("Create class " + className + " instance");
	        IActionHandler actionHandler = (IActionHandler)actionHandlerClass.newInstance();
	        actionHandler.setEvent(event);
	        return actionHandler;
	    } catch (ClassNotFoundException e) {
	        _logger.error("Class " + className + " not found.", e);
	        return null;
	    }
	}

}
