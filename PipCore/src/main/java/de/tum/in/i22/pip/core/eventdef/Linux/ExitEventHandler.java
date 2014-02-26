package de.tum.in.i22.pip.core.eventdef.Linux;


import java.util.Set;

import de.tum.in.i22.pip.core.InformationFlowModel;
import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.pip.core.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.cm.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class ExitEventHandler extends BaseEventHandler {

	@Override
	public IStatus execute() {
		String host = null;
		String pid = null;

		try {
			host = getParameterValue("host");
			pid = getParameterValue("pid");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		InformationFlowModel ifModel = getInformationFlowModel();

		IContainer processContainer = ifModel.getContainer(ProcessName.create(host, pid));

		// check if container for process exists
		if (processContainer != null) {
			ifModel.emptyContainer(processContainer);

			// also remove all depending containers
			Set<IContainer> closureSet = ifModel.getAliasTransitiveReflexiveClosure(processContainer);
			for (IContainer cont : closureSet) {
				ifModel.remove(cont);
			}

			ifModel.removeAllAliasesFrom(processContainer);
			ifModel.removeAllAliasesTo(processContainer);
			ifModel.remove(processContainer);

			for (IName nm : ifModel.getAllNamingsFrom(processContainer)) {
				LinuxEvents.close(nm);
			}
		}

		return _messageFactory.createStatus(EStatus.OKAY);
	}

}