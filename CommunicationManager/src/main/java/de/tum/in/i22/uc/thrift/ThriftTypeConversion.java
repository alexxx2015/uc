package de.tum.in.i22.uc.thrift;

import de.tum.i22.in.uc.cm.thrift.Event;
import de.tum.in.i22.uc.cm.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.IEvent;

public final class ThriftTypeConversion {
	public static IEvent convert(Event e) {
		return new EventBasic(e.name, e.parameters, e.isActual);
	}

	public static Event convert(IEvent e) {
		return new Event(e.getName(), e.getParameters(), e.getTimestamp(), e.isActual());
	}
}
