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
public class TeeEventHandler extends LinuxEvents {

	@Override
	protected IStatus update() {
		String host = null;
		int pid;
		int srcfd;
		int dstfd;
		String dstfilename;

		try {
			host = getParameterValue("host");
			pid = Integer.valueOf(getParameterValue("pid"));
			srcfd = Integer.valueOf(getParameterValue("srcfd"));
			dstfd = Integer.valueOf(getParameterValue("dstfd"));
			dstfilename = getParameterValue("dstfilename");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		/*
		 * TODO: Semantics are equivalent to splice()
		 */

		IName dstFdName = FiledescrName.create(host, pid, dstfd);

		IContainer srcCont = _informationFlowModel.getContainer(FiledescrName.create(host, pid, srcfd));
		IContainer dstCont = _informationFlowModel.getContainer(dstFdName);

		if (dstCont == null) {
			dstCont = _informationFlowModel.getContainer(OSInternalName.create(host, dstfilename));

			if (dstCont == null) {
				return STATUS_ERROR;
			}

			_informationFlowModel.addName(dstFdName, dstCont);
		}

		return copyDataTransitive(srcCont, dstCont);
	}

}
