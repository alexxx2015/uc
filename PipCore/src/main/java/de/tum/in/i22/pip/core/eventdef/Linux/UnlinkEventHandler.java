package de.tum.in.i22.pip.core.eventdef.Linux;

import java.io.File;
import java.io.IOException;

import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.pip.core.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class UnlinkEventHandler extends BaseEventHandler {

	@Override
	public IStatus execute() {
		String host = null;
		String filename = null;

		try {
			host = getParameterValue("host");
			filename = getParameterValue("filename");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}
		
		IName fnName;
		
		File file = new File(filename);
		
		try {
			fnName = FilenameName.create(host, file.getCanonicalPath());
		} catch (IOException e) {
			fnName = FilenameName.create(host, file.getAbsolutePath());
		}
		
		ifModel.removeName(fnName);

		return _messageFactory.createStatus(EStatus.OKAY);
	}
}