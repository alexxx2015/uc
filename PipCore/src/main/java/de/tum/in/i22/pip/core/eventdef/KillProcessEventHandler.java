package de.tum.in.i22.pip.core.eventdef;


import java.util.Set;

import de.tum.in.i22.pip.core.InformationFlowModel;
import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.pip.core.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.cm.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class KillProcessEventHandler extends BaseEventHandler {

	public KillProcessEventHandler() {
		super();
	}

	@Override
	public IStatus execute() {
		String pid = null;
//		String processName = null;

		try {
			pid = getParameterValue("PID_Child");
//			processName = getParameterValue("ChildProcessName");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		InformationFlowModel ifModel = getInformationFlowModel();
		String processContainerId = ifModel.getContainerIdByName(new NameBasic(pid));

		// check if container for process exists
		if (processContainerId != null) {
			ifModel.emptyContainer(processContainerId);

			// also remove all depending containers
			Set<String> closureSet = ifModel.getAliasTransitiveReflexiveClosure(processContainerId);
			for (String contId : closureSet) {
				ifModel.removeContainer(contId);
			}

			ifModel.removeAllAliasesFrom(processContainerId);
			ifModel.removeAllAliasesTo(processContainerId);
			ifModel.removeContainer(processContainerId);

			for (IName nm : ifModel.getAllNamingsFrom(processContainerId)) {
				ifModel.removeName(nm);
			}
		}

		return _messageFactory.createStatus(EStatus.OKAY);
	}
}
