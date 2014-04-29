package de.tum.in.i22.uc.pip.eventdef.linux;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.datatypes.linux.FiledescrName;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

/**
 *
 * @author Florian Kelbert
 *
 */
public class TeeEventHandler extends LinuxEvents {

	@Override
	protected IStatus update() {
		String host = null;
		int pid;
		int srcfd;
		int dstfd;

		try {
			host = getParameterValue("host");
			pid = Integer.valueOf(getParameterValue("pid"));
			srcfd = Integer.valueOf(getParameterValue("srcfd"));
			dstfd = Integer.valueOf(getParameterValue("dstfd"));
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		/*
		 * TODO: Semantics are equivalent to splice()
		 */

		IContainer srcCont = _informationFlowModel.getContainer(FiledescrName.create(host, pid, srcfd));
		IContainer dstCont = _informationFlowModel.getContainer(FiledescrName.create(host, pid, dstfd));

		return copyDataTransitive(srcCont, dstCont);
	}

}
