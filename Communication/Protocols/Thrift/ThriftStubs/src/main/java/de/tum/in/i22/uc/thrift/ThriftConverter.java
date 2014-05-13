package de.tum.in.i22.uc.thrift;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.AttributeBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.AttributeBasic.EAttributeName;
import de.tum.in.i22.uc.cm.datatypes.basic.ContainerBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.DataBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.PxpSpec;
import de.tum.in.i22.uc.cm.datatypes.basic.ResponseBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IAttribute;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IResponse;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.thrift.types.TAttribute;
import de.tum.in.i22.uc.thrift.types.TAttributeName;
import de.tum.in.i22.uc.thrift.types.TContainer;
import de.tum.in.i22.uc.thrift.types.TData;
import de.tum.in.i22.uc.thrift.types.TEvent;
import de.tum.in.i22.uc.thrift.types.TName;
import de.tum.in.i22.uc.thrift.types.TPxpSpec;
import de.tum.in.i22.uc.thrift.types.TResponse;
import de.tum.in.i22.uc.thrift.types.TStatus;
import de.tum.in.i22.uc.thrift.types.TXmlPolicy;

public final class ThriftConverter {

	protected static final Logger _logger = LoggerFactory
			.getLogger(ThriftConverter.class);

	public static IContainer fromThrift(TContainer c) {
		if (c == null) {
			_logger.debug("TContainer was null.");
			return null;
		}

		return new ContainerBasic(c.getId(), ThriftConverter.fromThriftAttributeList(c.getAttributes()));
	}

	private static List<IAttribute> fromThriftAttributeList(List<TAttribute> attributes) {
		if (attributes == null || attributes.size() == 0) {
			return Collections.emptyList();
		}

		List<IAttribute> result = new LinkedList<>();
		for (TAttribute attr : attributes) {
			result.add(ThriftConverter.fromThrift(attr));
		}
		return result;
	}

	private static IAttribute fromThrift(TAttribute attr) {
		return new AttributeBasic(ThriftConverter.fromThrift(attr.name), attr.value);
	}

	private static EAttributeName fromThrift(TAttributeName name) {
		switch (name) {
			case CLASS:
				return EAttributeName.CLASS;
			case CREATION_TIME:
				return EAttributeName.CREATION_TIME;
			case MODIFICATION_TIME:
				return EAttributeName.MODIFICATION_TIME;
			case OWNER:
				return EAttributeName.OWNER;
			case SIZE:
				return EAttributeName.SIZE;
			case TYPE:
				return EAttributeName.TYPE;
			case WILDCARD:
				return EAttributeName.WILDCARD;
			default:
				_logger.warn("Unknown AttributeName [" + name + "]. Returning null.");
				return null;
		}
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
		return new EventBasic(e.name, e.parameters, e.isActual, e.getTimeStamp());
	}

	public static PxpSpec fromThrift(TPxpSpec p) {
		if (p == null) {
			_logger.debug("TPxpSpec was null.");
			return null;
		}
		return new PxpSpec(p.getIp(), p.getPort(), p.getId(),p.getDescription());
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


	public static Location fromThrift(String location) {
		if (location == null || location.equals("")) {
			return null;
		}
		return new IPLocation(location);
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
				_logger.warn("Unknown Status [" + s + "]. Returning null.");
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

		List<IEvent> res = new ArrayList<>(events.size());
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

		return new TContainer(c.getId(), ThriftConverter.toThriftAttributeList(c.getAttributes()));
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
		TEvent res=new TEvent(e.getName(), e.getParameters(), e.getTimestamp());
		res.setIsActual(e.isActual());
		return res;
	}

	public static TName toThrift(IName n) {
		if (n == null) {
			_logger.debug("IName was null.");
			return null;
		}

		return new TName(n.getName());
	}

	public static TPxpSpec toThrift(PxpSpec p) {
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

		TResponse res= new TResponse(ThriftConverter.toThrift(r
				.getAuthorizationAction()));
		List<TEvent> execTList = new LinkedList<TEvent>();
		for (IEvent ev : r.getExecuteActions()){
			execTList.add(ThriftConverter.toThrift(ev));
		}
		res.setExecuteEvents(execTList);
		res.setExecuteEventsIsSet(true);
		res.setModifiedEvents(ThriftConverter.toThrift(r.getModifiedEvent()));
		res.setModifiedEventsIsSet(true);
		
		return res;
	}

	public static String toThrift(Location location) {
		if (location == null) {
			return "";
		}
		return location.asString();
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
				_logger.warn("Unknown Status [" + s.getEStatus() + "]. Returning null.");
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

	public static Set<TName> toThriftNameSet(Set<IName> names) {
		if (names == null || names.size() == 0) {
			return Collections.emptySet();
		}

		Set<TName> res = new HashSet<>();
		for (IName n : names) {
			res.add(ThriftConverter.toThrift(n));
		}
		return res;
	}

	public static Set<IName> fromThriftNameSet(Set<TName> names) {
		if (names == null || names.size() == 0) {
			return Collections.emptySet();
		}

		Set<IName> res = new HashSet<>();
		for (TName n : names) {
			res.add(ThriftConverter.fromThrift(n));
		}
		return res;
	}

	public static Set<Location> fromThriftLocationSet(Set<String> locations) {
		if (locations == null || locations.size() == 0) {
			return Collections.emptySet();
		}

		Set<Location> result = new HashSet<>();
		for (String loc : locations) {
			try {
				result.add(fromThrift(loc));
			} catch (Exception e) {
				_logger.warn("Unable to cast [" + loc + "] to " + IPLocation.class.getSimpleName());
			}
		}
		return result;
	}

	public static Set<String> toThriftLocationSet(Set<Location> locations) {
		if (locations == null || locations.size() == 0) {
			return Collections.emptySet();
		}

		Set<String> result = new HashSet<>();
		for (Location loc : locations) {
			result.add(toThrift(loc));
		}
		return result;
	}


	private static List<TAttribute> toThriftAttributeList(Collection<IAttribute> attributes) {
		if (attributes == null || attributes.size() == 0) {
			return Collections.emptyList();
		}

		List<TAttribute> result = new ArrayList<>(attributes.size());
		for (IAttribute attr : attributes) {
			result.add(ThriftConverter.toThrift(attr));
		}
		return result;
	}

	private static TAttribute toThrift(IAttribute attr) {
		return new TAttribute(ThriftConverter.toThrift(attr.getName()), attr.getValue());
	}

	private static TAttributeName toThrift(EAttributeName name) {
		switch (name) {
			case CLASS:
				return TAttributeName.CLASS;
			case CREATION_TIME:
				return TAttributeName.CREATION_TIME;
			case MODIFICATION_TIME:
				return TAttributeName.MODIFICATION_TIME;
			case OWNER:
				return TAttributeName.OWNER;
			case SIZE:
				return TAttributeName.SIZE;
			case TYPE:
				return TAttributeName.TYPE;
			case WILDCARD:
				return TAttributeName.WILDCARD;
			default:
				_logger.warn("Unkown AttributeName [" + name + "]. Returning null.");
				return null;
		}
	}

	public static TXmlPolicy toThrift(XmlPolicy xmlPolicy) {
		return new TXmlPolicy(xmlPolicy.getName(), xmlPolicy.getXml());
	}

	public static XmlPolicy fromThrift(TXmlPolicy xMLPolicy) {
		return new XmlPolicy(xMLPolicy.name, xMLPolicy.xml);
	}


}
