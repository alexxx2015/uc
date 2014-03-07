package de.tum.in.i22.pip.core.eventdef.Linux;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
public class OpenAtEventHandler extends BaseEventHandler {

	@Override
	public IStatus execute() {
		String host = null;
		String pid = null;
		String newfd = null;
		String dirfd = null;
		String filename = null;
		boolean at_fdcwd = false;
		boolean truncate = false;

		try {
			host = getParameterValue("host");
			pid = getParameterValue("pid");
			newfd = getParameterValue("newfd");
			dirfd = getParameterValue("dirfd");
			filename = getParameterValue("filename");
			truncate = Boolean.valueOf(getParameterValue("trunc"));
			at_fdcwd = Boolean.valueOf(getParameterValue("at_fdcwd"));
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}
		
		IName newfdName = FiledescrName.create(host, pid, newfd);
		IName dirfdName = FiledescrName.create(host, pid, dirfd);
		IName fnName;
		
		File file = new File(filename);
		
		if (!file.isAbsolute() && !at_fdcwd) {
			List<IName> names = ifModel.getAllNames(dirfdName, FilenameName.class);
			
			// the resulting list should always be of size 1.
			if (names.size() != 1) {
				_logger.error("There was not exactly one filename for " + dirfdName);
			}
			else {
				File path = new File(((FilenameName) names.get(0)).getFilename());
				
				String pathStr;
				try {
					pathStr = path.getCanonicalPath();
				} catch (IOException e) {
					pathStr = path.getAbsolutePath();
				}
				
				if (path.isDirectory()) {
					file = new File(pathStr, filename);
				}
				else {
					file = new File(path.getParent(), filename);
				}
			}
		}
		
		try {
			fnName = FilenameName.create(host, file.getCanonicalPath());
		} catch (IOException e) {
			fnName = FilenameName.create(host, file.getAbsolutePath());
		}
		
	
		// get the file's container (if present)
		IContainer cont = ifModel.getContainer(fnName);

		if (cont != null) {
			if (truncate) {
				ifModel.emptyContainer(cont);
			}
		}
		else {
			cont = _messageFactory.createContainer();
			ifModel.addName(fnName, cont);
		}
		ifModel.addName(newfdName, cont);

		return _messageFactory.createStatus(EStatus.OKAY);
	}

}

