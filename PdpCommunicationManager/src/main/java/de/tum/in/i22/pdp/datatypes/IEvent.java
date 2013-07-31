package de.tum.in.i22.pdp.datatypes;

import java.util.Map;

public interface IEvent {
	/**
	 * Event name
	 * @return
	 */
	public String getName();
	
	/**
	 * Event parameters
	 * @return empty or non-empty map
	 */
	public Map<String, String> getParameters();
	
	/**
	 * Timestamp, inserted when the event is received
	 */
	public long getTimestamp();
	
	
	//TODO implement
	//public boolean isActual();
}
