package de.tum.in.i22.uc.pip.eventdef.linux;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.datatypes.linux.FileContainer;
import de.tum.in.i22.uc.cm.datatypes.linux.FilenameName;
import de.tum.in.i22.uc.cm.datatypes.linux.ProcessContainer;
import de.tum.in.i22.uc.cm.datatypes.linux.ProcessName;
import de.tum.in.i22.uc.pip.eventdef.BaseEventHandler;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

/**
 *
 * @author Florian Kelbert
 *
 */
public class ExecveEventHandler extends BaseEventHandler {

	@Override
	protected IStatus update() {
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

		IName fileName = FilenameName.create(host, LinuxEvents.toRealPath(filename));
		IName procName = ProcessName.create(host, pid);

		IContainer fileCont = _informationFlowModel.getContainer(fileName);
		IContainer procCont = _informationFlowModel.getContainer(procName);

		if (fileCont == null) {
			fileCont = new FileContainer();
			_informationFlowModel.addName(fileName, fileCont);
		}

		if (procCont == null) {
			procCont = new ProcessContainer(host, pid);
			_informationFlowModel.addName(procName, procCont);
		}

		_informationFlowModel.copyData(fileName, procName);

		return STATUS_OKAY;
	}
}

