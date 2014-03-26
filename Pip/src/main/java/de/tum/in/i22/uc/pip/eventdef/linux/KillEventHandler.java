package de.tum.in.i22.uc.pip.eventdef.linux;

import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.datatypes.linux.ProcessName;
import de.tum.in.i22.uc.pip.eventdef.BaseEventHandler;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

/**
 *
 * @author Florian Kelbert
 *
 */
public class KillEventHandler extends BaseEventHandler {

	@Override
	public IStatus execute() {
		String host = null;
		int srcPid;
		int dstPid;

		try {
			host = getParameterValue("host");
			srcPid = Integer.valueOf(getParameterValue("srcPid"));
			dstPid = Integer.valueOf(getParameterValue("dstPid"));
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		IContainer srcCont = basicIfModel.getContainer(ProcessName.create(host, srcPid));
		IContainer dstCont = basicIfModel.getContainer(ProcessName.create(host, dstPid));

		if (srcCont != null) {
			for (IContainer c : basicIfModel.getAliasTransitiveReflexiveClosure(dstCont)) {
				basicIfModel.copyData(srcCont, c);
			}
		}

		return STATUS_OKAY;
	}
}
