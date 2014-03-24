package de.tum.in.i22.uc.pip.core.eventdef.linux;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.datatypes.linux.FilenameName;
import de.tum.in.i22.uc.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.uc.pip.core.eventdef.ParameterNotFoundException;

/**
 *
 * @author Florian Kelbert
 *
 */
public class TruncateEventHandler extends BaseEventHandler {

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

		ifModel.emptyContainer(FilenameName.create(host, LinuxEvents.toRealPath(filename)));

		return STATUS_OKAY;
	}

}

