package de.tum.in.i22.uc.pip.core.eventdef.Linux;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.datatypes.Linux.FilenameName;
import de.tum.in.i22.uc.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.uc.pip.core.eventdef.ParameterNotFoundException;

/**
 *
 * @author Florian Kelbert
 *
 */
public class RenameEventHandler extends BaseEventHandler {

	@Override
	public IStatus execute() {
		String host = null;
		String oldName = null;
		String newName = null;

		try {
			host = getParameterValue("host");
			oldName = getParameterValue("old");
			newName = getParameterValue("new");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		IName oldN = FilenameName.create(host, LinuxEvents.toRealPath(oldName));
		IName newN = FilenameName.create(host, LinuxEvents.toRealPath(newName));

		ifModel.removeName(newN);
		ifModel.addName(oldN, newN);
		ifModel.removeName(oldN);

		return STATUS_OKAY;
	}

}

