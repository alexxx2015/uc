package de.tum.in.i22.uc.pip.eventdef.linux;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.datatypes.linux.FiledescrName;
import de.tum.in.i22.uc.cm.datatypes.linux.OSInternalName;
import de.tum.in.i22.uc.cm.datatypes.linux.ProcessName;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

/**
 *
 * @author Florian Kelbert
 *
 */
public class WriteEventHandler extends LinuxEvents {

	@Override
	protected IStatus update() {
		String host = null;
		int pid;
		int fd;
		String filename = null;

		try {
			host = getParameterValue("host");
			pid = Integer.valueOf(getParameterValue("pid"));
			fd = Integer.valueOf(getParameterValue("fd"));
			filename = getParameterValue("filename");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}


		IName dstFdName = FiledescrName.create(host, pid, fd);

		IContainer srcCont = _informationFlowModel.getContainer(ProcessName.create(host, pid));
		IContainer dstCont = _informationFlowModel.getContainer(dstFdName);

		if (dstCont == null) {
			dstCont = _informationFlowModel.getContainer(OSInternalName.create(host, filename));

			if (dstCont == null) {
				return STATUS_ERROR;
			}

			_informationFlowModel.addName(dstFdName, dstCont);
		}

		return copyDataTransitive(srcCont, dstCont);
	}
}
