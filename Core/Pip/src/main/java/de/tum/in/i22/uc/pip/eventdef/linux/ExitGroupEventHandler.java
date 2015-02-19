package de.tum.in.i22.uc.pip.eventdef.linux;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IScope;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.pip.interfaces.EScopeState;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class ExitGroupEventHandler extends LinuxEvents {

	@Override
	protected IStatus update() {
		String host = null;
		String pids = null;

		try {
			host = getParameterValue("host");
			pids = getParameterValue("pids");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		for (String pid : pids.split(":")) {
			exit(host, Integer.valueOf(pid));
		}

		return STATUS_OKAY;
	}

	@Override
	protected Set<Pair<EScopeState, IScope>> XDelim() {
		Set<IScope> scopes = _informationFlowModel.getAllOpenedScopes();
		Set<Pair<EScopeState, IScope>> result = new HashSet<Pair<EScopeState, IScope>>();

		if (scopes == null) {
			_logger.debug("Empty set of active scopes. nothing to do.");
			return result;
		}

		String pids;

		try {
			pids = getParameterValue("pids");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return result;
		}
		for (String spid : pids.split(":")) {
			int pid = Integer.valueOf(spid);
			for (IScope s : scopes) {
				// close all the scopes the exiting process still holds open
				if (("" + pid).equals(spid)) {
					result.add(Pair.of(EScopeState.CLOSE, s));
				}
			}
		}
		return result;
	}
}