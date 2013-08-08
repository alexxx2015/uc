package de.tum.in.i22.uc.cm.in;
import java.util.Map;

import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpEvent;


public interface IMessageFactory {
	public IEvent createEvent(String Name, Map<String, String> map);
	public IEvent createEvent(GpEvent gpEvent, long timestamp);
	public IEvent createEvent(GpEvent gpEvent);
}
