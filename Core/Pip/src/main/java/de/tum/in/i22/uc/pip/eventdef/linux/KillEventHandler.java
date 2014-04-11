package de.tum.in.i22.uc.pip.eventdef.linux;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
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
	protected IStatus update() {
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

		return LinuxEvents.copyDataTransitive(srcCont, dstCont);
	}
}
