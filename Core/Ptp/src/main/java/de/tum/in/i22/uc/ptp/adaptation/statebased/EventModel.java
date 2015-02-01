package de.tum.in.i22.uc.ptp.adaptation.statebased;

/**
 * @author Cipri
 * The class is used to wrap the events defined in eventDecl.xml file.
 */
public class EventModel {

	private String eventName;
	private String eventClass;
	
	public EventModel(String eventName, String eventClass){
		this.setEventClass(eventClass);
		this.setEventName(eventName);
	}

	/**
	 * @return the eventName
	 */
	public String getEventName() {
		return eventName;
	}

	/**
	 * @param eventName the eventName to set
	 */
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	/**
	 * @return the eventClass
	 */
	public String getEventClass() {
		return eventClass;
	}

	/**
	 * @param eventClass the eventClass to set
	 */
	public void setEventClass(String eventClass) {
		this.eventClass = eventClass;
	}
	
	
	
}
