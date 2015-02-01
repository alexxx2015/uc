package de.tum.in.i22.uc.pip.eventdef.linux;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.BaseEventHandler;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class UnlinkEventHandler extends BaseEventHandler {

	@Override
	protected IStatus update() {
		String host = null;
		String filename = null;

		try {
			host = getParameterValue("host");
			filename = getParameterValue("filename");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		//_informationFlowModel.removeName(FilenameName.create(host, LinuxEvents.toRealPath(filename)));
		_informationFlowModel.removeName(new NameBasic(LinuxEvents.toRealPath(filename)));

		return STATUS_OKAY;
	}
}