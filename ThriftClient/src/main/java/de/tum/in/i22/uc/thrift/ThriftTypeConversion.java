package de.tum.in.i22.uc.thrift;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.i22.in.uc.thrift.types.Container;
import de.tum.i22.in.uc.thrift.types.Data;
import de.tum.i22.in.uc.thrift.types.Event;
import de.tum.i22.in.uc.thrift.types.Name;
import de.tum.i22.in.uc.thrift.types.Response;
import de.tum.i22.in.uc.thrift.types.StatusType;
import de.tum.in.i22.uc.cm.basic.ContainerBasic;
import de.tum.in.i22.uc.cm.basic.DataBasic;
import de.tum.in.i22.uc.cm.basic.EventBasic;
import de.tum.in.i22.uc.cm.basic.NameBasic;
import de.tum.in.i22.uc.cm.basic.ResponseBasic;
import de.tum.in.i22.uc.cm.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public final class ThriftTypeConversion {

	protected static final Logger _logger = LoggerFactory.getLogger(ThriftTypeConversion.class);

	public static IEvent fromThrift(Event e) {
		if (e == null) {
			_logger.debug("Event was null.");
			return null;
		}
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
			res.add(ThriftTypeConversion.fromThrift(d));
		}
		return res;
	}

	public static Set<Data> toThriftDataSet(Set<IData> data) {
		if (data == null || data.size() == 0) {
			return Collections.emptySet();
		}

		Set<Data> res = new HashSet<>();
		for (IData d : data) {
			res.add(ThriftTypeConversion.toThrift(d));
		}
		return res;
	}

	public static Set<Container> toThriftContainerSet(Set<IContainer> containers) {
		if (containers == null || containers.size() == 0) {
			return Collections.emptySet();
		}

		Set<Container> res = new HashSet<>();
		for (IContainer c : containers) {
			res.add(ThriftTypeConversion.toThrift(c));
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
			res.add(ThriftTypeConversion.fromThrift(c));
		}
		return res;
	}

	private static IContainer fromThrift(Container c) {
		return new ContainerBasic(c.getClassValue(), c.getId());
	}

	public static IData fromThrift(Data data) {
		return new DataBasic(data.getId());
	}

	public static IResponse fromThrift(Response r) {
		return new ResponseBasic(
				ThriftTypeConversion.fromThrift(r.getStatus()),
				ThriftTypeConversion.fromThriftEventList(r.getExecuteEvents()),
				ThriftTypeConversion.fromThrift(r.getModifiedEvents()));
	}

	public static List<IEvent> fromThriftEventList(List<Event> events) {
		if (events == null || events.size() == 0) {
			return Collections.emptyList();
		}

		List<IEvent> res = new LinkedList<>();
		for (Event e : events) {
			res.add(ThriftTypeConversion.fromThrift(e));
		}
		return res;
	}
}
