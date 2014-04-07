package de.tum.in.i22.uc.pip.eventdef.linux;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.datatypes.linux.FileContainer;
import de.tum.in.i22.uc.cm.datatypes.linux.FiledescrName;
import de.tum.in.i22.uc.cm.datatypes.linux.FilenameName;
import de.tum.in.i22.uc.pip.eventdef.BaseEventHandler;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

/**
 *
 * @author Florian Kelbert
 *
 */
public class OpenEventHandler extends BaseEventHandler {

	@Override
	protected IStatus update() {
		String host = null;
		int pid;
		int fd;
		String filename = null;
		boolean truncate = false;

		try {
			host = getParameterValue("host");
			pid = Integer.valueOf(getParameterValue("pid"));
			fd = Integer.valueOf(getParameterValue("fd"));
			filename = getParameterValue("filename");
			truncate = Boolean.valueOf(getParameterValue("trunc"));
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		IName fdName = FiledescrName.create(host, pid, fd);
		IName fnName = FilenameName.create(host, LinuxEvents.toRealPath(filename));

		// get the file's container (if present)
		IContainer cont = basicIfModel.getContainer(fnName);

		if (cont == null) {
			cont = new FileContainer();
			basicIfModel.addName(fnName, cont);
		}
		else if (truncate) {
			basicIfModel.emptyContainer(cont);
		}

		basicIfModel.addName(fdName, cont);

		return STATUS_OKAY;
	}

}

