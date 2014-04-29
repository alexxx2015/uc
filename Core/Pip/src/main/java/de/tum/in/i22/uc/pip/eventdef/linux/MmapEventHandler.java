package de.tum.in.i22.uc.pip.eventdef.linux;


import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.datatypes.linux.FiledescrName;
import de.tum.in.i22.uc.cm.datatypes.linux.MmapContainer;
import de.tum.in.i22.uc.cm.datatypes.linux.MmapName;
import de.tum.in.i22.uc.cm.datatypes.linux.ProcessName;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

/**
 *
 * @author Florian Kelbert
 *
 */
public class MmapEventHandler extends LinuxEvents {

	@Override
	protected IStatus update() {
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

		IContainer fileCont = _informationFlowModel.getContainer(FiledescrName.create(host, pid, fd));
		if (fileCont == null) {
			return STATUS_OKAY;
		}

		IContainer procCont = _informationFlowModel.getContainer(ProcessName.create(host, pid));
		if (procCont == null) {
			return STATUS_OKAY;
		}

		IContainer mmapCont = new MmapContainer(host, pid, addr);

		_informationFlowModel.addName(MmapName.create(host, pid, addr), mmapCont);

		_informationFlowModel.addAlias(fileCont, mmapCont);

		if (flags.contains("s")) {		// MAP_SHARED
			_informationFlowModel.addAlias(mmapCont, fileCont);
		}
		if (flags.contains("r")) {		// PROT_READ
			_informationFlowModel.addAlias(mmapCont, procCont);
		}
		if (flags.contains("w")) {		// PROT_WRITE
			_informationFlowModel.addAlias(procCont, mmapCont);
		}

		return copyDataTransitive(fileCont, mmapCont);
	}
}