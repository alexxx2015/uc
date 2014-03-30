package de.tum.in.i22.uc.cm.thrift;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.tum.i22.in.uc.cm.thrift.Data;
import de.tum.i22.in.uc.cm.thrift.Event;
import de.tum.i22.in.uc.cm.thrift.Name;
import de.tum.i22.in.uc.cm.thrift.Response;
import de.tum.i22.in.uc.cm.thrift.StatusType;
import de.tum.in.i22.uc.cm.basic.DataBasic;
import de.tum.in.i22.uc.cm.basic.EventBasic;
import de.tum.in.i22.uc.cm.basic.NameBasic;
import de.tum.in.i22.uc.cm.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public final class ThriftTypeConversion {
	public static IEvent convert(Event e) {
		return new EventBasic(e.name, e.parameters, e.isActual);
	}

	public static Event convert(IEvent e) {
		return new Event(e.getName(), e.getParameters(), e.getTimestamp(), e.isActual());
	}

	public static StatusType convert(IStatus s) {
		switch (s.getEStatus()) {
			case ALLOW:
				return StatusType.ALLOW;
			case ERROR:
				return StatusType.ERROR;
			case ERROR_EVENT_PARAMETER_MISSING:
				return StatusType.ERROR_EVENT_PARAMETER_MISSING;
			case INHIBIT:
				return StatusType.INHIBIT;
			case MODIFY:
				return StatusType.MODIFY;
			case OKAY:
				return StatusType.OKAY;
			default:
				throw new RuntimeException("Unknown StatusType.");
		}
	}

	public static IStatus convert(StatusType s) {
		EStatus eStatus;

		switch(s) {
			case ALLOW:
				eStatus = EStatus.ALLOW;
				break;
			case ERROR:
				eStatus = EStatus.ERROR;
				break;
			case ERROR_EVENT_PARAMETER_MISSING:
				eStatus = EStatus.ERROR_EVENT_PARAMETER_MISSING;
				break;
			case INHIBIT:
				eStatus = EStatus.INHIBIT;
				break;
			case MODIFY:
				eStatus = EStatus.MODIFY;
				break;
			case OKAY:
				eStatus = EStatus.OKAY;
				break;
			default:
				throw new RuntimeException("Unknown StatusType.");
		}

		return new StatusBasic(eStatus);
	}

	public static Response convert(IResponse r) {
		return new Response(ThriftTypeConversion.convert(r.getAuthorizationAction()));
	}

	public static IName convert(Name n) {
		return new NameBasic(n.getName());
	}

	public static Set<IData> convert(Set<Data> data) {
		if (data == null || data.size() == 0) {
			return Collections.emptySet();
		}

		Set<IData> res = new HashSet<>();
		for (Data d : data) {
			res.add(new DataBasic(d.getId()));
		}
		return res;
	}
}
