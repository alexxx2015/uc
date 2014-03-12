package de.tum.in.i22.pip.core.eventdef.Linux;

import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.pip.core.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.datatypes.Linux.FiledescrName;

/**
 *
 * @author Florian Kelbert
 *
 */
public class SpliceEventHandler extends BaseEventHandler {

	@Override
	public IStatus execute() {
		String host = null;
		int pid;
		String srcfd = null;
		String dstfd = null;

		try {
			host = getParameterValue("host");
			pid = Integer.valueOf(getParameterValue("pid"));
			srcfd = getParameterValue("srcfd");
			dstfd = getParameterValue("dstfd");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		IName srcName = FiledescrName.create(host, pid, srcfd);
		IName dstName = FiledescrName.create(host, pid, dstfd);

		ifModel.copyData(srcName, dstName);

		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
