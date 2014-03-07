package de.tum.in.i22.pip.core.eventdef.Linux;

import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.pip.core.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.datatypes.Linux.ProcessName;

/**
 *
 * @author Florian Kelbert
 *
 */
public class KillEventHandler extends BaseEventHandler {

	@Override
	public IStatus execute() {
		String host = null;
		String srcPid = null;
		String dstPid = null;

		try {
			host = getParameterValue("host");
			srcPid = getParameterValue("srcPid");
			dstPid = getParameterValue("dstPid");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		ifModel.copyData(ProcessName.create(host, srcPid), ProcessName.create(host, dstPid));

		return _messageFactory.createStatus(EStatus.OKAY);
	}
}
