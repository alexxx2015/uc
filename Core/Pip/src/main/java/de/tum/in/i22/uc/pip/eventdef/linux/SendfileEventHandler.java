package de.tum.in.i22.uc.pip.eventdef.linux;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.datatypes.linux.FiledescrName;
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

		try {
			host = getParameterValue("host");
			pid = Integer.valueOf(getParameterValue("pid"));
			outfd = Integer.valueOf(getParameterValue("outfd"));
			infd = Integer.valueOf(getParameterValue("infd"));
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		return copyDataTransitive(
				_informationFlowModel.getContainer(FiledescrName.create(host, pid, infd)),
				_informationFlowModel.getContainer(FiledescrName.create(host, pid, outfd)));
	}
}
