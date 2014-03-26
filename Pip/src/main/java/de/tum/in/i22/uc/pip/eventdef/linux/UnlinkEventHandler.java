package de.tum.in.i22.uc.pip.eventdef.linux;

import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.datatypes.linux.FilenameName;
import de.tum.in.i22.uc.pip.eventdef.BaseEventHandler;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class UnlinkEventHandler extends BaseEventHandler {

	@Override
	public IStatus execute() {
		String host = null;
		String filename = null;

		try {
			host = getParameterValue("host");
			filename = getParameterValue("filename");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		basicIfModel.removeName(FilenameName.create(host, LinuxEvents.toRealPath(filename)));

		return STATUS_OKAY;
	}
}