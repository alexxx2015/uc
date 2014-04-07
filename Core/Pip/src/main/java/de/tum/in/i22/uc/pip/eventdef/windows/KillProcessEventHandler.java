package de.tum.in.i22.uc.pip.eventdef.windows;


import java.util.Set;

import de.tum.in.i22.uc.cm.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.pip.eventdef.BaseEventHandler;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class KillProcessEventHandler extends BaseEventHandler {

	public KillProcessEventHandler() {
		super();
	}

	@Override
	protected IStatus update() {
		String pid = null;
//		String processName = null;

		try {
			pid = getParameterValue("PID_Child");
//			processName = getParameterValue("ChildProcessName");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		IContainer processContainer = basicIfModel.getContainer(new NameBasic(pid));

		// check if container for process exists
		if (processContainer != null) {
			basicIfModel.emptyContainer(processContainer);

			// also remove all depending containers
			Set<IContainer> closureSet = basicIfModel.getAliasTransitiveReflexiveClosure(processContainer);
			for (IContainer cont : closureSet) {
				basicIfModel.remove(cont);
			}

			basicIfModel.removeAllAliasesFrom(processContainer);
			basicIfModel.removeAllAliasesTo(processContainer);
			basicIfModel.remove(processContainer);

			for (IName nm : basicIfModel.getAllNamingsFrom(processContainer)) {
				basicIfModel.removeName(nm);
			}
		}

		return _messageFactory.createStatus(EStatus.OKAY);
	}
}
