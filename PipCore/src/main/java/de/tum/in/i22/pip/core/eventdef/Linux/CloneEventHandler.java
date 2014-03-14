package de.tum.in.i22.pip.core.eventdef.Linux;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.pip.core.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.datatypes.Linux.IClonableForProcess;
import de.tum.in.i22.uc.cm.datatypes.Linux.ProcessContainer;
import de.tum.in.i22.uc.cm.datatypes.Linux.ProcessName;

public class CloneEventHandler extends BaseEventHandler {

	@Override
	public IStatus execute() {
		String host = null;
		int childPid;
		int parentPid;
		String flags = null;

		try {
			host = getParameterValue("host");
			childPid = Integer.valueOf(getParameterValue("cpid"));
			parentPid = Integer.valueOf(getParameterValue("ppid"));
			flags = getParameterValue("flags");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		Set<String> flagSet = new HashSet<String>(Arrays.asList(flags.split("\\|")));

		IName newProcName = ProcessName.create(host, childPid);
		IName oldProcName = ProcessName.create(host, parentPid);

		IContainer newProcCont = new ProcessContainer(Integer.valueOf(childPid));
		ProcessContainer oldProcCont = (ProcessContainer) ifModel.getContainer(oldProcName);

		// Add a container for the old process, if it did not yet exist (should not happen).
		if (oldProcCont == null) {
			oldProcCont = new ProcessContainer(Integer.valueOf(parentPid));
			ifModel.addName(oldProcName, oldProcCont);
		}

		if (flagSet.contains("CLONE_FILES")) {
			_logger.fatal("CLONE_FILES was set, but is not implemented.");
			return STATUS_ERROR;
		}

		// Add the container for the newly created child
		ifModel.addName(newProcName, newProcCont);

		// Copy all process relative names from the old parent process to the new child process
		for (IName name : LinuxEvents.getAllProcessRelativeNames(oldProcCont.getPid())) {
			if (name instanceof IClonableForProcess) {
				IClonableForProcess n = (IClonableForProcess) name;
				ifModel.addName(n.cloneFor(childPid), ifModel.getContainer(n));
			}
		}

		// Clone all aliases
		for (IContainer cont : ifModel.getAliasesFrom(oldProcCont)) {
			ifModel.addAlias(newProcCont, cont);
		}
		for (IContainer cont : ifModel.getAliasesTo(oldProcCont)) {
			ifModel.addAlias(cont, newProcCont);
		}

		// Copy all data from old process
		for (IContainer cont : ifModel.getAliasTransitiveClosure(newProcCont)) {
			ifModel.copyData(oldProcCont, cont);
		}

		return STATUS_OKAY;
	}

}