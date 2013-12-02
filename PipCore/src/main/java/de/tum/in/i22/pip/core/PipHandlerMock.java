package de.tum.in.i22.pip.core;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
import de.tum.in.i22.uc.cm.datatypes.ICacheUpdate;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IKey;
import de.tum.in.i22.uc.cm.datatypes.IPipDeployer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

/**
 * Mock class, used for testing only
 * @author Stoimenov
 *
 */
public class PipHandlerMock implements IPdp2Pip ,IPipCacher2Pip 
{
	
	private static final Logger _logger = Logger.getLogger(PipHandlerMock.class);
	
	private IEventHandlerCreator _actionHandlerCreator;
	private IPipManager _pipManager; 
	
	public PipHandlerMock() {
		EventHandlerManager eventHandlerManager = new EventHandlerManager();
		PipManager pipManager = new PipManager(eventHandlerManager);
		pipManager.initialize();
		
		_actionHandlerCreator = eventHandlerManager;
		_pipManager = pipManager;
	}

	public Boolean evaluatePredicateSimulatingNextState(IEvent event, String predicate) {
		// returns always true, for testing purposes only
		return true;
	}

	public Set<IContainer> getContainerForData(IData arg0) {
		// returns dummy list, for testing purposes only
		Set<IContainer> set = new HashSet<IContainer>();
		for (int i = 0; i < 6; i++) {
			IContainer container = new ContainerBasic("dummy class", null);
			set.add(container);
		}
		return set;
	}

	public Set<IData> getDataInContainer(IContainer container) {
		// returns dummy list, for testing purposes only
		Set<IData> set = new HashSet<IData>();
		for (int i = 0; i < 10; i++) {
			IData data = new DataBasic(UUID.randomUUID().toString());
			set.add(data);
		}
		return set;
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
		} catch (ClassNotFoundException e ){
			_logger.error("Class not found for event handler " + action, e);
		}
		
		if (actionHandler == null) {
			_logger.trace("Create default event handler");
			actionHandler = new DefaultEventHandler();
		}
		
		actionHandler.setEvent(event);
		
		IStatus status =  actionHandler.executeEvent();
		_logger.trace("Status to return: " + status);
		
		return status;
	}

	@Override
	public IStatus updateInformationFlowSemantics(IPipDeployer deployer,
			File jarFile, EConflictResolution flagForTheConflictResolution) {
		
		return _pipManager.updateInformationFlowSemantics(deployer, jarFile, flagForTheConflictResolution);
	}

	@Override
	public Boolean evaluatePredicatCurrentState(String predicate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus startSimulation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus stopSimulation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICacheUpdate refresh(IEvent e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus addPredicates(Map<String, IKey> predicates) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus revokePredicates(Set<IKey> keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSimulating() {
		// TODO Auto-generated method stub
		return false;
	}
}
