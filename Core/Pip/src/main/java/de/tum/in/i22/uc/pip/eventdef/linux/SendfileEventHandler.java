package de.tum.in.i22.uc.pip.eventdef.linux;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.datatypes.linux.FiledescrName;
import de.tum.in.i22.uc.cm.datatypes.linux.OSInternalName;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

/**
 *
 * @author Florian Kelbert
 *
 */
public class SendfileEventHandler extends LinuxEvents {

	@Override
	protected IStatus update() {
		String host = null;
		int pid;
		int infd;
		int outfd;
		String outfilename;

		try {
			host = getParameterValue("host");
			pid = Integer.valueOf(getParameterValue("pid"));
			outfd = Integer.valueOf(getParameterValue("outfd"));
			infd = Integer.valueOf(getParameterValue("infd"));
			outfilename = getParameterValue("outfilename");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		IName dstFdName = FiledescrName.create(host, pid, outfd);

		IContainer srcContainer = _informationFlowModel.getContainer(FiledescrName.create(host, pid, infd));
		IContainer dstContainer = _informationFlowModel.getContainer(dstFdName);

		if (dstContainer == null) {
			// This might be the case if the file descriptor has been shared across processes.
			// In this case we try to find the container using the os internal identifier.
			dstContainer = _informationFlowModel.getContainer(OSInternalName.create(host, outfilename));

			if (dstContainer == null) {
				return STATUS_ERROR;
			}

			_informationFlowModel.addName(dstFdName, dstContainer);
		}

		return copyDataTransitive(srcContainer, dstContainer);
	}
}
