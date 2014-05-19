package de.tum.in.i22.uc.pip.eventdef.windows;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.pip.eventdef.BaseEventHandler;
import de.tum.in.i22.uc.pip.eventdef.scope.AbstractScopeEventHandler;

public abstract class WindowsEvents extends AbstractScopeEventHandler {

	/**
	 * Checks if the process with given PID already exists, if not create a
	 * container, crate a name, and make a relation between them.
	 *
	 * @param processId
	 *            Process ID (PID)
	 * @return
	 */
	protected IContainer instantiateProcess(String processId, String processName) {
		IContainer container = _informationFlowModel.getContainer(new NameBasic(processId));

		// check if container for process exists and create new container if not
		if (container == null) {
			container = _messageFactory.createContainer();
			_informationFlowModel.addName(new NameBasic(processId), container);
			_informationFlowModel.addName(new NameBasic(processName), container);
		}

		return container;
	}
}
