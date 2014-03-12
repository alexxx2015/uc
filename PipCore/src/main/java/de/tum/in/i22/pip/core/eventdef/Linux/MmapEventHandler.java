package de.tum.in.i22.pip.core.eventdef.Linux;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;

import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.pip.core.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.datatypes.Linux.FiledescrName;
import de.tum.in.i22.uc.cm.datatypes.Linux.MmapContainer;
import de.tum.in.i22.uc.cm.datatypes.Linux.MmapName;
import de.tum.in.i22.uc.cm.datatypes.Linux.ProcessName;

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
		String fd = null;
		String addr = null;
		String flags = null;

		try {
			host = getParameterValue("host");
			pid = Integer.valueOf(getParameterValue("pid"));
			fd = getParameterValue("fd");
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

		IContainer mmapCont = new MmapContainer(Integer.valueOf(pid), addr);

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