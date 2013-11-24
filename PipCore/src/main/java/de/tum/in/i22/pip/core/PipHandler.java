package de.tum.in.i22.pip.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;

import de.tum.in.i22.pip.core.eventdef.DefaultEventHandler;
import de.tum.in.i22.pip.core.manager.EventHandlerManager;
import de.tum.in.i22.pip.core.manager.IEventHandlerCreator;
import de.tum.in.i22.pip.core.manager.IPipManager;
import de.tum.in.i22.pip.core.manager.PipManager;
import de.tum.in.i22.uc.cm.basic.ContainerBasic;
import de.tum.in.i22.uc.cm.basic.DataBasic;
import de.tum.in.i22.uc.cm.datatypes.EConflictResolution;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

//FIXME use IPipCacher2Pip when the file with the interface definition has been added to repo
public class PipHandler implements IPdp2Pip //,IPipCacher2Pip 
{
	
	private static final Logger _logger = Logger.getLogger(PipHandler.class);
	
	private IEventHandlerCreator _actionHandlerCreator;
	private IPipManager _pipManager; 
	
	
	private static IPdp2Pip _instance = null;
	
	public static IPdp2Pip getInstance() {
		if (_instance == null) {
			_instance = new PipHandler();
		}
		return _instance;
	}
	
//	private static IPipCacher2Pip _instance = null;
//	public static IPipCacher2Pip getInstance() {
//		if (_instance == null) {
//			_instance = new PipHandler();
//		}
//		return _instance;
//	}
	
	private PipHandler() {
		EventHandlerManager eventHandlerManager = new EventHandlerManager();
		PipManager pipManager = new PipManager(eventHandlerManager);
		pipManager.initialize();
		
		_actionHandlerCreator = eventHandlerManager;
		_pipManager = pipManager;
	}

	public Boolean evaluatePredicate(IEvent event, String predicate) {
		// returns always true, for testing purposes only
		//FIXME provide real implementation of the method
		return true;
	}

	public List<IContainer> getContainerForData(IData arg0) {
		// returns dummy list, for testing purposes only
		//FIXME provide real implementation of the method
		List<IContainer> list = new ArrayList<IContainer>();
		for (int i = 0; i < 6; i++) {
			IContainer container = new ContainerBasic("dummy class", null);
			list.add(container);
		}
		return list;
	}

	public List<IData> getDataInContainer(IContainer container) {
		// returns dummy list, for testing purposes only
		//FIXME provide real implementation of the method
		List<IData> list = new ArrayList<IData>();
		for (int i = 0; i < 10; i++) {
			IData data = new DataBasic(UUID.randomUUID().toString());
			list.add(data);
		}
		return list;
	}

	@Override
	public IStatus notifyActualEvent(IEvent event) {
		String action = event.getName();
		_logger.debug("Action name: " + action);
		IEventHandler actionHandler = null;
		try {
			_logger.trace("Create event handler");
			actionHandler = _actionHandlerCreator.createEventHandler(action);
		} catch (IllegalAccessException | InstantiationException e) {
			_logger.error("Failed to create event handler for action " + action, e);
			// FIXME create error status with description
		} catch (ClassNotFoundException e ){
			_logger.error("Class not found for event handler " + action, e);
			// FIXME create error status with description
		}
		
		if (actionHandler == null) {
			_logger.trace("Create default event handler");
			actionHandler = new DefaultEventHandler();
		}
		
		actionHandler.setEvent(event);
		
		IStatus status =  actionHandler.execute();
		_logger.trace("Status to return: " + status);
		
		return status;
	}

	@Override
	public IStatus updateInformationFlowSemantics(IPipDeployer deployer,
			File jarFile, EConflictResolution flagForTheConflictResolution) {
		
		return _pipManager.updateInformationFlowSemantics(deployer, jarFile, flagForTheConflictResolution);
	}
}
