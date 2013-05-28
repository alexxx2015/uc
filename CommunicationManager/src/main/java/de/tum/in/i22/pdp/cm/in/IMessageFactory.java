package de.tum.in.i22.pdp.cm.in;
import java.util.Map;

import de.tum.in.i22.pdp.datatypes.IEvent;


public interface IMessageFactory {
	public IEvent createEvent(String Name, Map<String, String> map);
}
