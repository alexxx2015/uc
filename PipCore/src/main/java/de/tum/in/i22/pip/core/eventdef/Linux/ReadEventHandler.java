package de.tum.in.i22.pip.core.eventdef.Linux;

import java.util.Set;

import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.pip.core.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.datatypes.Linux.FiledescrName;
import de.tum.in.i22.uc.cm.datatypes.Linux.ProcessName;

/**
 *
 * @author Florian Kelbert
 *
 */
public class ReadEventHandler extends BaseEventHandler {

	@Override
	public IStatus execute() {
		String host = null;
		int pid;
		String fd = null;

		try {
			host = getParameterValue("host");
			pid = Integer.valueOf(getParameterValue("pid"));
			fd = getParameterValue("fd");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		IContainer fileCont = ifModel.getContainer(FiledescrName.create(host, pid, fd));
		if (fileCont == null) {
			return _messageFactory.createStatus(EStatus.OKAY);
		}

		IContainer procCont = ifModel.getContainer(ProcessName.create(host, pid));
		if (procCont == null) {
			return _messageFactory.createStatus(EStatus.OKAY);
		}

		Set<IData> data = ifModel.getDataInContainer(fileCont);
		if (data == null || data.size() == 0) {
			return _messageFactory.createStatus(EStatus.OKAY);
		}

		for (IContainer c : ifModel.getAliasTransitiveReflexiveClosure(procCont)) {
			ifModel.addDataToContainerMappings(data, c);
		}

		return _messageFactory.createStatus(EStatus.OKAY);
	}
}
