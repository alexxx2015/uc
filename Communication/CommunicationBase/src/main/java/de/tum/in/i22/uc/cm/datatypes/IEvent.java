package de.tum.in.i22.uc.cm.datatypes;

import java.util.Map;

public interface IEvent {
	/**
	 * This event's name, prefixed with the PEP that issued this event (if any).
	 * @return the PEP-prefixed event name
	 */
	public String getPrefixedName();
	
	/**
	 * This event's name. 
	 * @return
	 */
	public String getName();
	
	/**
	 * The PEP that issued this event
	 * @return the identifier of the PEP that issued this event
	 */
	public String getPep();

	/**
	 * Event parameters.
	 * @return Empty or non-empty map containing the parameters.
	 */
	public Map<String, String> getParameters();
	
	/**
	 * @return Timestamp which is inserted when the event is received.
	 */
	public long getTimestamp();
	
	/**
	 * 
	 * @return true if the event is actual.
	 */
	public boolean isActual();
}
