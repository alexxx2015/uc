package de.tum.in.i22.uc.pip.eventdef.linux;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.datatypes.linux.FilenameName;
import de.tum.in.i22.uc.pip.eventdef.BaseEventHandler;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

/**
 *
 * @author Florian Kelbert
 *
 */
public class RenameEventHandler extends BaseEventHandler {

	@Override
	protected IStatus update() {
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

		basicIfModel.removeName(newN);
		basicIfModel.addName(oldN, newN);
		basicIfModel.removeName(oldN);

		return STATUS_OKAY;
	}

}

