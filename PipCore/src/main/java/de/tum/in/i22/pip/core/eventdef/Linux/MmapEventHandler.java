package de.tum.in.i22.pip.core.eventdef.Linux;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
		String pid = null;
		String fd = null;
		String addr = null;
		String flags = null;

		try {
			host = getParameterValue("host");
			pid = getParameterValue("pid");
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
		
		Set<String> flagSet = new HashSet<String>(Arrays.asList(flags.split("\\|"))); 
		
		IName mmapName = MmapName.create(host, pid, addr);
		IContainer mmapCont = new MmapContainer(Integer.valueOf(pid), addr);

		IName procName = ProcessName.create(host, pid);
		IName fileName = FiledescrName.create(host, pid, fd);
		
		ifModel.addName(mmapName, mmapCont);
		
		ifModel.addAlias(fileName, mmapName);
		
		if (flagSet.contains("MAP_SHARED")) {
			ifModel.addAlias(mmapName, fileName);
		}
		
		if (flagSet.contains("PROT_READ")) {
			ifModel.addAlias(mmapName, procName);
		}
		
		if (flagSet.contains("PROT_WRITE")) {
			ifModel.addAlias(procName, mmapName);
		}
		
		// now copy data from file to mmap container and its aliases
		ifModel.addDataToContainerAndAliases(ifModel.getDataInContainer(fileName), mmapName);

		return _messageFactory.createStatus(EStatus.OKAY);
	}
}