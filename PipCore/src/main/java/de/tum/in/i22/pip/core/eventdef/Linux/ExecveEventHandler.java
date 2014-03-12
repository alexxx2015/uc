package de.tum.in.i22.pip.core.eventdef.Linux;
import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.pip.core.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.datatypes.Linux.FileContainer;
import de.tum.in.i22.uc.cm.datatypes.Linux.FilenameName;
import de.tum.in.i22.uc.cm.datatypes.Linux.ProcessContainer;
import de.tum.in.i22.uc.cm.datatypes.Linux.ProcessName;

/**
 *
 * @author Florian Kelbert
 *
 */
public class ExecveEventHandler extends BaseEventHandler {

	@Override
	public IStatus execute() {
		String host = null;
		int pid;
		String filename = null;

		try {
			host = getParameterValue("host");
			pid = Integer.valueOf(getParameterValue("pid"));
			filename = getParameterValue("filename");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		filename = LinuxEvents.toAbsoluteFilename(filename);

		IName fileName = FilenameName.create(host, filename);
		IName procName = ProcessName.create(host, pid);

		IContainer fileCont = ifModel.getContainer(fileName);
		IContainer procCont = ifModel.getContainer(procName);

		if (fileCont == null) {
			fileCont = new FileContainer();
			ifModel.addName(fileName, fileCont);
		}

		if (procCont == null) {
			procCont = new ProcessContainer(Integer.valueOf(pid));
			ifModel.addName(procName, procCont);
		}

		ifModel.copyData(fileName, procName);

		return _messageFactory.createStatus(EStatus.OKAY);
	}
}

