package de.tum.in.i22.uc.pip.eventdef.linux;

import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.datatypes.linux.MmapName;
import de.tum.in.i22.uc.pip.eventdef.BaseEventHandler;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

/**
 *
 * @author Florian Kelbert
 *
 */
public class MunmapEventHandler extends BaseEventHandler {

	@Override
	protected IStatus update() {
		String host = null;
		int pid;
		String addr = null;

		try {
			host = getParameterValue("host");
			pid = Integer.valueOf(getParameterValue("pid"));
			addr = getParameterValue("addr");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		basicIfModel.remove(basicIfModel.getContainer(MmapName.create(host, pid, addr)));

		return STATUS_OKAY;
	}
}