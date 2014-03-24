package de.tum.in.i22.uc.pip.eventdef.linux;


import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.datatypes.linux.FiledescrName;
import de.tum.in.i22.uc.cm.datatypes.linux.MmapContainer;
import de.tum.in.i22.uc.cm.datatypes.linux.MmapName;
import de.tum.in.i22.uc.cm.datatypes.linux.ProcessName;
import de.tum.in.i22.uc.pip.eventdef.BaseEventHandler;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

/**
 *
 * @author Florian Kelbert
 *
 */
public class MmapEventHandler extends BaseEventHandler {

	@Override
	public IStatus execute() {
		String host = null;
		int pid;
		int fd;
		String addr = null;
		String flags = null;

		try {
			host = getParameterValue("host");
			pid = Integer.valueOf(getParameterValue("pid"));
			fd = Integer.valueOf(getParameterValue("fd"));
			addr = getParameterValue("addr");
			flags = getParameterValue("flags");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		/*
		 * Using an additional intermediate MmapContainer will
		 * allow us to remove the mapping/aliases upon munmap.
		 * The following aliases will be created depending on mmap's parameters:
		 *
		 * +------------------+  PROT_READ  +------------------+   ALWAYS    +------------------+
		 * |                  |<------------|                  |<------------|                  |
		 * |     process      |             |      mmap        |             |       file       |
		 * |                  |------------>|                  |------------>|                  |
		 * +------------------+  PROT_WRITE +------------------+ MAP_SHARED  +------------------+
		 *
		 * mmap events with flags PROT_NONE or MAP_ANONYMOUS will not be signalled.
		 * They are not interesting.
		 */

		IContainer fileCont = ifModel.getContainer(FiledescrName.create(host, pid, fd));
		if (fileCont == null) {
			return STATUS_OKAY;
		}

		IContainer procCont = ifModel.getContainer(ProcessName.create(host, pid));
		if (procCont == null) {
			return STATUS_OKAY;
		}

		IContainer mmapCont = new MmapContainer(host, pid, addr);

		ifModel.addName(MmapName.create(host, pid, addr), mmapCont);

		ifModel.addAlias(fileCont, mmapCont);

		if (flags.contains("s")) {		// MAP_SHARED
			ifModel.addAlias(mmapCont, fileCont);
		}
		if (flags.contains("r")) {		// PROT_READ
			ifModel.addAlias(mmapCont, procCont);
		}
		if (flags.contains("w")) {		// PROT_WRITE
			ifModel.addAlias(procCont, mmapCont);
		}

		// now copy data from file to mmap container and its aliases
		ifModel.addDataToContainerAndAliases(ifModel.getDataInContainer(fileCont), mmapCont);

		return STATUS_OKAY;
	}
}