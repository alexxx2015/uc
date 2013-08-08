package de.tum.in.i22.uc.cm.datatypes;

import java.util.Map;

public interface IEvent {
	/**
	 * Event name
	 * @return The name of the event.
	 */
	public String getName();
	
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
