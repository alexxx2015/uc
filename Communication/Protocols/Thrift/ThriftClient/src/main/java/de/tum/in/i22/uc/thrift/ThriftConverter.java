package de.tum.in.i22.uc.thrift;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.i22.in.uc.thrift.types.TContainer;
import de.tum.i22.in.uc.thrift.types.TData;
import de.tum.i22.in.uc.thrift.types.TEvent;
import de.tum.i22.in.uc.thrift.types.TName;
import de.tum.i22.in.uc.thrift.types.TPxpSpec;
import de.tum.i22.in.uc.thrift.types.TResponse;
import de.tum.i22.in.uc.thrift.types.TStatus;
import de.tum.in.i22.uc.cm.basic.ContainerBasic;
import de.tum.in.i22.uc.cm.basic.DataBasic;
import de.tum.in.i22.uc.cm.basic.EventBasic;
import de.tum.in.i22.uc.cm.basic.NameBasic;
import de.tum.in.i22.uc.cm.basic.PxpSpec;
import de.tum.in.i22.uc.cm.basic.ResponseBasic;
import de.tum.in.i22.uc.cm.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IPxpSpec;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public final class ThriftConverter {

	protected static final Logger _logger = LoggerFactory
			.getLogger(ThriftConverter.class);

	public static IContainer fromThrift(TContainer c) {
		if (c == null) {
			_logger.debug("TContainer was null.");
			return null;
		}

		return new ContainerBasic(c.getClassValue(), c.getId());
	}

	public static IData fromThrift(TData d) {
		if (d == null) {
			_logger.debug("TData was null.");
			return null;
		}

		return new DataBasic(d.getId());
	}

	public static IEvent fromThrift(TEvent e) {
		if (e == null) {
			_logger.debug("TEvent was null.");
			return null;
		}

		return new EventBasic(e.name, e.parameters, e.isActual);
	}

	public static IPxpSpec fromThrift(TPxpSpec p) {
		if (p == null) {
			_logger.debug("TPxpSpec was null.");
			return null;
		}
		return new PxpSpec(p.getIp(), p.getPort(), p.getDescription(), p.getId());
	}

	public static IName fromThrift(TName n) {
		if (n == null) {
			_logger.debug("TName was null.");
			return null;
		}

		return new NameBasic(n.getName());
	}

	public static IResponse fromThrift(TResponse r) {
		if (r == null) {
			_logger.debug("TResponse was null.");
			return null;
		}

		return new ResponseBasic(ThriftConverter.fromThrift(r.getStatus()),
				ThriftConverter.fromThriftEventList(r.getExecuteEvents()),
				ThriftConverter.fromThrift(r.getModifiedEvents()));
	}

	public static IStatus fromThrift(TStatus s) {
		if (s == null) {
			_logger.debug("TStatus was null.");
			return null;
		}

		EStatus eStatus;

		switch (s) {
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
			_logger.debug("Unknown Status. Returning null.");
			return null;
		}

		return new StatusBasic(eStatus);
	}

	public static Set<IContainer> fromThriftContainerSet(
			Set<TContainer> containers) {
		if (containers == null || containers.size() == 0) {
			return Collections.emptySet();
		}

		Set<IContainer> res = new HashSet<>();
		for (TContainer c : containers) {
			res.add(ThriftConverter.fromThrift(c));
		}
		return res;
	}

	public static Set<IData> fromThriftDataSet(Set<TData> data) {
		if (data == null || data.size() == 0) {
			return Collections.emptySet();
		}

		Set<IData> res = new HashSet<>();
		for (TData d : data) {
			res.add(ThriftConverter.fromThrift(d));
		}
		return res;
	}

	public static List<IEvent> fromThriftEventList(List<TEvent> events) {
		if (events == null || events.size() == 0) {
			return Collections.emptyList();
		}

		List<IEvent> res = new LinkedList<>();
		for (TEvent e : events) {
			res.add(ThriftConverter.fromThrift(e));
		}
		return res;
	}

	public static TContainer toThrift(IContainer c) {
		if (c == null) {
			_logger.debug("IContainer was null.");
			return null;
		}

		return new TContainer(c.getClassValue(), c.getId());
	}

	public static TData toThrift(IData d) {
		if (d == null) {
			_logger.debug("IData was null.");
			return null;
		}

		return new TData(d.getId());
	}

	public static TEvent toThrift(IEvent e) {
		if (e == null) {
			_logger.debug("IEvent was null.");
			return null;
		}

		return new TEvent(e.getName(), e.getParameters(), e.getTimestamp(),
				e.isActual());
	}

	public static TName toThrift(IName n) {
		if (n == null) {
			_logger.debug("IName was null.");
			return null;
		}

		return new TName(n.getName());
	}

	public static TPxpSpec toThrift(IPxpSpec p) {
		if (p == null) {
			_logger.debug("IPxpSpec was null.");
			return null;
		}

		return new TPxpSpec(p.getIp(), p.getPort(), p.getDescription(),
				p.getId());
	}

	public static TResponse toThrift(IResponse r) {
		if (r == null) {
			_logger.debug("IResponse was null.");
			return null;
		}

		return new TResponse(ThriftConverter.toThrift(r
				.getAuthorizationAction()));
	}

	public static TStatus toThrift(IStatus s) {
		if (s == null) {
			_logger.debug("IStatus was null.");
			return null;
		}

		switch (s.getEStatus()) {
		case ALLOW:
			return TStatus.ALLOW;
		case ERROR:
			return TStatus.ERROR;
		case ERROR_EVENT_PARAMETER_MISSING:
			return TStatus.ERROR_EVENT_PARAMETER_MISSING;
		case INHIBIT:
			return TStatus.INHIBIT;
		case MODIFY:
			return TStatus.MODIFY;
		case OKAY:
			return TStatus.OKAY;
		default:
			_logger.debug("Unknown Status. Returning null.");
			return null;
		}
	}

	public static Set<TContainer> toThriftContainerSet(
			Set<IContainer> containers) {
		if (containers == null || containers.size() == 0) {
			return Collections.emptySet();
		}

		Set<TContainer> res = new HashSet<>();
		for (IContainer c : containers) {
			res.add(ThriftConverter.toThrift(c));
		}
		return res;
	}

	public static Set<TData> toThriftDataSet(Set<IData> data) {
		if (data == null || data.size() == 0) {
			return Collections.emptySet();
		}

		Set<TData> res = new HashSet<>();
		for (IData d : data) {
			res.add(ThriftConverter.toThrift(d));
		}
		return res;
	}

	public static List<TEvent> toThriftEventList(List<IEvent> events) {
		if (events == null || events.size() == 0) {
			return Collections.emptyList();
		}

		List<TEvent> res = new LinkedList<>();
		for (IEvent e : events) {
			res.add(ThriftConverter.toThrift(e));
		}
		return res;
	}
}
