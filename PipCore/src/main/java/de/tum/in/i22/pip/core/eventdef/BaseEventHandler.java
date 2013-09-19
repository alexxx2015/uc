package de.tum.in.i22.pip.core.eventdef;


import java.util.Map;

import de.tum.in.i22.pip.core.IActionHandler;
import de.tum.in.i22.pip.core.InformationFlowModel;
import de.tum.in.i22.pip.core.Name;
import de.tum.in.i22.uc.cm.IMessageFactory;
import de.tum.in.i22.uc.cm.MessageFactoryCreator;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IEvent;

public abstract class BaseEventHandler implements IActionHandler {
	protected final IMessageFactory _messageFactory = MessageFactoryCreator.createMessageFactory();
	
	private IEvent _event;
	public BaseEventHandler() {
		super();
	}
	
	public IEvent getEvent() {
		return _event;
	}
	
	@Override
	public void setEvent(IEvent event) {
		_event = event;
	}
	
	protected String getParameterValue(String key) throws ParameterNotFoundException {
		Map<String, String> parameters = getEvent().getParameters();
		if (parameters != null && parameters.containsKey(key)) {
			return parameters.get(key);
		} else {
			throw new ParameterNotFoundException(key);
		}
	}
	
	protected InformationFlowModel getInformationFlowModel() {
		return InformationFlowModel.getInstance();
	}
	
	/**
	 * Checks if the process with given PID already exists, if not create a
	 * container, crate a name, and make a relation between them.
	 * 
	 * @param processId Process ID (PID)
	 * @return
	 */
	protected String instantiateProcess(String processId, String processName) {
		InformationFlowModel ifModel = getInformationFlowModel();
		String containerID = ifModel
				.getContainerIdByName(new Name(processId));

		// check if container for process exists and create new container if not
		if (containerID == null) {
			IContainer container = _messageFactory.createContainer();
			containerID = ifModel.addContainer(container);
			ifModel.addName(new Name(processId), containerID);
			ifModel.addName(new Name(processName), containerID);
		}

		return containerID;
	}
}
