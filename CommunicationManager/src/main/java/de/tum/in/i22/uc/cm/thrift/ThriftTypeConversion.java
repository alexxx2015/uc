package de.tum.in.i22.uc.cm.thrift;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.tum.i22.in.uc.cm.thrift.Container;
import de.tum.i22.in.uc.cm.thrift.Data;
import de.tum.i22.in.uc.cm.thrift.Event;
import de.tum.i22.in.uc.cm.thrift.Name;
import de.tum.i22.in.uc.cm.thrift.Response;
import de.tum.i22.in.uc.cm.thrift.StatusType;
import de.tum.in.i22.uc.cm.basic.ContainerBasic;
import de.tum.in.i22.uc.cm.basic.DataBasic;
import de.tum.in.i22.uc.cm.basic.EventBasic;
import de.tum.in.i22.uc.cm.basic.NameBasic;
import de.tum.in.i22.uc.cm.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public final class ThriftTypeConversion {
	public static IEvent fromThrift(Event e) {
		return new EventBasic(e.name, e.parameters, e.isActual);
	}

	public static Event toThrift(IEvent e) {
		return new Event(e.getName(), e.getParameters(), e.getTimestamp(), e.isActual());
	}

	public static StatusType toThrift(IStatus s) {
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

	public static IStatus fromThrift(StatusType s) {
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

	public static Response toThrift(IResponse r) {
		return new Response(ThriftTypeConversion.toThrift(r.getAuthorizationAction()));
	}

	public static IName fromThrift(Name n) {
		return new NameBasic(n.getName());
	}

	public static Set<IData> fromThriftDataSet(Set<Data> data) {
		if (data == null || data.size() == 0) {
			return Collections.emptySet();
		}

		Set<IData> res = new HashSet<>();
		for (Data d : data) {
			res.add(new DataBasic(d.getId()));
		}
		return res;
	}

	public static Set<Data> toThriftDataSet(Set<IData> data) {
		if (data == null || data.size() == 0) {
			return Collections.emptySet();
		}

		Set<Data> res = new HashSet<>();
		for (IData d : data) {
			res.add(new Data(d.getId()));
		}
		return res;
	}

	public static Set<Container> toThriftContainerSet(Set<IContainer> containers) {
		if (containers == null || containers.size() == 0) {
			return Collections.emptySet();
		}

		Set<Container> res = new HashSet<>();
		for (IContainer c : containers) {
			res.add(new Container(c.getClassValue(), c.getId()));
		}
		return res;
	}

	public static Name toThrift(IName name) {
		return new Name(name.getName());
	}

	public static Container toThrift(IContainer container) {
		return new Container(container.getClassValue(),container.getId());
	}

	public static Data toThrift(IData data) {
		return new Data(data.getId());
	}

	public static Set<IContainer> fromThriftContainerSet(Set<Container> containers) {
		if (containers == null || containers.size() == 0) {
			return Collections.emptySet();
		}

		Set<IContainer> res = new HashSet<>();
		for (Container c : containers) {
			res.add(new ContainerBasic(c.getClassValue(), c.getId()));
		}
		return res;
	}

}
