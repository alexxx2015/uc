package de.tum.in.i22.pip.core;

import java.util.List;

import org.apache.log4j.Logger;

import de.tum.in.i22.pip.core.actions.DefaultActionHandler;
import de.tum.in.i22.pip.core.manager.IActionHandlerCreator;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class PipHandler implements IPdp2Pip {
	
	private static final Logger _logger = Logger.getLogger(PipHandler.class);
	
	public IActionHandlerCreator _actionHandlerCreator;
	
	public PipHandler(IActionHandlerCreator actionHandlerCreator) {
		_actionHandlerCreator = actionHandlerCreator;
	}

	@Override
	public Boolean evaluatePredicate(IEvent event, String predicate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IContainer> getContainerForData(IData data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IData> getDataInContainer(IContainer container) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus notifyActualEvent(IEvent event) {
		String action = event.getName();
		_logger.debug("Action name: " + action);
		IActionHandler actionHandler = null;
		try {
			_logger.trace("Create action handler");
			actionHandler = _actionHandlerCreator.createActionHandler(action);
		} catch (IllegalAccessException | InstantiationException e) {
			_logger.error("Failed to create Action Handler for action " + action, e);
			// FIXME create error status with description
		} catch (ClassNotFoundException e ){
			_logger.error("Class not found for Action Handler " + action, e);
			// FIXME create error status with description
		}
		
		if (actionHandler == null) {
			_logger.trace("Create default action handler");
			actionHandler = new DefaultActionHandler();
		}
		
		actionHandler.setEvent(event);
		
		IStatus status =  actionHandler.execute();
		_logger.trace("Status to return: " + status);
		
		return status;
	}
}
