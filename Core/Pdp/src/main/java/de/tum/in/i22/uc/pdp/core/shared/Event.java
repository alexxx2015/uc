package de.tum.in.i22.uc.pdp.core.shared;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import de.tum.in.i22.uc.cm.datatypes.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;

public class Event implements Serializable {
	private static final long serialVersionUID = 6399332064987815074L;

	protected String eventAction;
	protected boolean tryEvent;
	protected long timestamp;
	protected Hashtable<String, Param> params = new Hashtable<String, Param>();

	public Event() {
		this.eventAction = "noName";
		this.tryEvent = true;
		this.timestamp = System.currentTimeMillis();
	}

	/***
	 * Generate an (IESE) event out of a given (TUM) event
	 *
	 * @param ev
	 */
	public Event(IEvent ev) {
		if (ev != null) {
			this.eventAction = ev.getName();

			// NOTE that TUM events have isActual() while IESE events have
			// isTry()
			this.tryEvent = !ev.isActual();

			// TUM events only have strings parameters
			if (ev.getParameters() != null) {
				for (Map.Entry<String, String> entry : ev.getParameters().entrySet()) {
					addStringParameter(entry.getKey(), entry.getValue());
				}
			}
		}
	}

	public Event(String action, Collection<Param> params, boolean isTry) {
		this.eventAction = action;
		if (params != null) {
			for (Param p : params) {
				this.params.put(p.getName(), p);
			}
		}
		this.tryEvent = isTry;
		this.timestamp = System.currentTimeMillis();
	}

	public Event(String action, Collection<Param> params, boolean isTry, long time) {
		this.eventAction = action;
		this.tryEvent = isTry;
		this.timestamp = time;
	}

	public String getName() {
		return eventAction;
	}

	public boolean isActual() {
		return !tryEvent;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public Param getParameterForName(String name) {
		return params.get(name);
	}

	public void addStringParameter(String name, String value) {
		if (value != null)
			params.put(name, new Param(name, value, Constants.PARAMETER_TYPE_STRING));
	}

	@Override
	public String toString() {
		String str = "Event      action='" + eventAction + "' isTry='" + tryEvent + "' timestamp='" + timestamp
				+ "': [";
		for (Param param : params.values())
			str += param.toString() + ", ";
		str += "]";
		return str;
	}

	public IEvent toIEvent() {
		Map<String, String> m = new HashMap<String, String>();
		for (Param p : params.values()) {
			m.put(p.getName(), p.getValue().toString());
		}
		return new EventBasic(this.eventAction, m, !tryEvent);
	}

}
