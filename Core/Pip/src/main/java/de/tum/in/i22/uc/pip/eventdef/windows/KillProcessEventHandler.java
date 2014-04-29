package de.tum.in.i22.uc.pip.eventdef.windows;


import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
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

		IContainer processContainer = _informationFlowModel.getContainer(new NameBasic(pid));

		// check if container for process exists
		if (processContainer != null) {
			_informationFlowModel.emptyContainer(processContainer);

			// also remove all depending containers
			Set<IContainer> closureSet = _informationFlowModel.getAliasTransitiveReflexiveClosure(processContainer);
			for (IContainer cont : closureSet) {
				_informationFlowModel.remove(cont);
			}

			_informationFlowModel.removeAllAliasesFrom(processContainer);
			_informationFlowModel.removeAllAliasesTo(processContainer);
			_informationFlowModel.remove(processContainer);

			for (IName nm : _informationFlowModel.getAllNamingsFrom(processContainer)) {
				_informationFlowModel.removeName(nm);
			}
		}

		return _messageFactory.createStatus(EStatus.OKAY);
	}
}
