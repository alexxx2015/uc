package de.tum.in.i22.pip.core.eventdef.Linux;

import java.io.File;

import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.pip.core.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

/**
 *
 * @author Florian Kelbert
 *
 */
public class OpenEventHandler extends BaseEventHandler {

	@Override
	public IStatus execute() {
		String host = null;
		String pid = null;
		String fd = null;
		String filename = null;
		boolean truncate = false;

		try {
			host = getParameterValue("host");
			pid = getParameterValue("pid");
			fd = getParameterValue("fd");
			filename = getParameterValue("filename");
			truncate = Boolean.valueOf(getParameterValue("trunc"));
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		// get filedescriptor and filename
		IName fdName = FiledescrName.create(host, pid, fd);
		IName fnName = FilenameName.create(host, new File(filename).getAbsolutePath());

		// get the file's container (if present)
		IContainer cont = ifModel.getContainer(fnName);

		// truncate the file
		if (truncate && cont != null) {
			ifModel.emptyContainer(cont);
		}

		// create container (if necessary) and assign names
		if (cont == null) {
			cont = _messageFactory.createContainer();
			ifModel.addName(fnName, cont);
		}
		ifModel.addName(fdName, cont);

		return _messageFactory.createStatus(EStatus.OKAY);
	}

}

