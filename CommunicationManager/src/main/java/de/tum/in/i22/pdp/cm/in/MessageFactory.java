package de.tum.in.i22.pdp.cm.in;

import java.util.Map;

import de.tum.in.i22.pdp.datatypes.EventBasic;
import de.tum.in.i22.pdp.datatypes.IEvent;

/**
 * Google Protocol Buffer Message Factory
 * @author Stoimenov
 *
 */
public class MessageFactory implements IMessageFactory {
	
	public static IMessageFactory getInstance() {
		return new MessageFactory();
	}

	public IEvent createEvent(String name, Map<String, String> map) {		
		IEvent e =  new EventBasic(name, map);
		return e;
	}
	
}
