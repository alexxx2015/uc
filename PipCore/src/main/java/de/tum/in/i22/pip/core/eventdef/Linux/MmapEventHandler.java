package de.tum.in.i22.pip.core.eventdef.Linux;

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
		String prot = null;
		String flags = null;

		try {
			host = getParameterValue("host");
			pid = getParameterValue("pid");
			fd = getParameterValue("fd");
			addr = getParameterValue("addr");
			prot = getParameterValue("prot");
			flags = getParameterValue("flags");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}

		Set<Prot> protSet = new HashSet<Prot>();
		for (String s : prot.split("\\|")) {
			Prot p = Prot.from(s);
			if (p != null) {
				protSet.add(p);
			}
		}
		
		Set<Flag> flagSet = new HashSet<Flag>();
		for (String s : flags.split("\\|")) {
			Flag f = Flag.from(s);
			if (f != null) {
				flagSet.add(f);
			}
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
		 */
		
		IName mmapName = MmapName.create(host, pid, addr);
		IContainer mmapCont = new MmapContainer(Integer.valueOf(pid), addr);

		IName procName = ProcessName.create(host, pid);
		IName fileName = FiledescrName.create(host, pid, fd);
		
		ifModel.addName(mmapName, mmapCont);
		
		ifModel.addAlias(fileName, mmapName);
		
		if (flagSet.contains(Flag.MAP_SHARED)) {
			ifModel.addAlias(mmapName, fileName);
		}
		
		if (protSet.contains(Prot.PROT_READ)) {
			ifModel.addAlias(mmapName, procName);
		}
		
		if (protSet.contains(Prot.PROT_WRITE)) {
			ifModel.addAlias(procName, mmapName);
		}
		
		// now copy data from file to mmap container and its aliases
		ifModel.addDataToContainerAndAliases(ifModel.getDataInContainer(fileName), mmapName);

		return _messageFactory.createStatus(EStatus.OKAY);
	}
	
	
	enum Prot {
		PROT_EXEC,
		PROT_READ,
		PROT_WRITE,
		PROT_NONE;
		
		static Prot from(String s) {
			switch(s) {
				case "PROT_READ":
					return PROT_READ;
				case "PROT_WRITE":
					return PROT_WRITE;
				case "PROT_EXEC":
					return PROT_EXEC;
				default:
					return PROT_NONE;
			}
		}
	}
	
	enum Flag {
		MAP_SHARED,
		MAP_PRIVATE,
		MAP_ANONYMOUS;
		
		static Flag from(String s) {
			switch(s) {
			case "MAP_SHARED":
				return MAP_SHARED;
			case "MAP_PRIVATE":
				return MAP_PRIVATE;
			case "MAP_ANON":
			case "MAP_ANONYMOUS":
				return MAP_ANONYMOUS;
			default:
				return null;
			}
		}
	}
}