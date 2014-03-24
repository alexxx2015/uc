package de.tum.in.i22.uc.pip.eventdef.windows;

import de.tum.in.i22.uc.cm.IMessageFactory;
import de.tum.in.i22.uc.cm.MessageFactoryCreator;
import de.tum.in.i22.uc.cm.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.pip.core.ifm.InformationFlowModel;

public class WindowsEvents {

	private final static InformationFlowModel ifModel = InformationFlowModel.getInstance();

	private final static IMessageFactory _messageFactory = MessageFactoryCreator.createMessageFactory();

	/**
	 * Checks if the process with given PID already exists, if not create a
	 * container, crate a name, and make a relation between them.
	 *
	 * @param processId
	 *            Process ID (PID)
	 * @return
	 */
	protected static IContainer instantiateProcess(String processId, String processName) {
		IContainer container = ifModel.getContainer(new NameBasic(processId));

		// check if container for process exists and create new container if not
		if (container == null) {
			container = _messageFactory.createContainer();
			ifModel.addName(new NameBasic(processId), container);
			ifModel.addName(new NameBasic(processName), container);
		}

		return container;
	}
}
