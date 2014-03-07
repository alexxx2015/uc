package de.tum.in.i22.pip.core.eventdef.Linux;

import org.apache.derby.impl.sql.catalog.SYSUSERSRowFactory;

import de.tum.in.i22.pip.core.eventdef.BaseEventHandler;
import de.tum.in.i22.pip.core.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
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
		String prot = null;
		String flags = null;

		try {
			host = getParameterValue("host");
			pid = getParameterValue("pid");
			fd = getParameterValue("fd");
			prot = getParameterValue("prot");
			flags = getParameterValue("flags");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}
		
		System.out.println("=================");
		System.out.println(host);
		System.out.println(pid);
		System.out.println(fd);
		System.out.println(prot);
		System.out.println(flags);
		System.out.println("=================");

		

		return _messageFactory.createStatus(EStatus.OKAY);
	}
	
	
	enum Prot {
		PROT_EXEC,
		PROT_READ,
		PROT_WRITE,
		PROT_NONE;
		
		static Prot from(String s) {
			switch(s) {
				case "PROT_EXEC":
					return PROT_EXEC;
				case "PROT_READ":
					return PROT_READ;
				case "PROT_WRITE":
					return PROT_WRITE;
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