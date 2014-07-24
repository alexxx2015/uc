package de.tum.in.i22.uc.pdp.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tum.in.i22.uc.cm.settings.Settings;

public class ActionDescriptionStore {
	private Map<String, List<EventMatch>> _eventMatchList = null;
	private Map<String, List<Mechanism>> _mechanismList = null;

	ActionDescriptionStore() {
		_eventMatchList = new HashMap<String, List<EventMatch>>();
		_mechanismList = new HashMap<String, List<Mechanism>>();
	}

	public void addEventMatch(EventMatch e) {
		List<EventMatch> eventMatchList = _eventMatchList.get(e.getAction());
		if (eventMatchList == null) {
			eventMatchList = new ArrayList<EventMatch>();
		}
		eventMatchList.add(e);

		_eventMatchList.put(e.getAction(), eventMatchList);
	}

	public void addMechanism(Mechanism m) {
		List<Mechanism> mechanismList = _mechanismList.get(m.getTriggerEvent().getAction());
		if (mechanismList == null)
			mechanismList = new ArrayList<Mechanism>();
		mechanismList.add(m);

		_mechanismList.put(m.getTriggerEvent().getAction(), mechanismList);
	}

	public List<EventMatch> getEventList(String eventAction) {
		if (eventAction == null) {
			return Collections.emptyList();
		}
		return _eventMatchList.get(eventAction);
	}

	public List<Mechanism> getMechanismList(String eventAction) {
		List<Mechanism> result = new ArrayList<Mechanism>();
		List<Mechanism> matchingEvent = _mechanismList.get(eventAction);
		List<Mechanism> matchingStar = _mechanismList.get(Settings.getInstance().getStarEvent());
		if (matchingEvent != null)
			result.addAll(matchingEvent);
		if (matchingStar != null)
			result.addAll(matchingStar);
		if (result.size() != 0)
			return result;
		return null;
	}

	public void removeMechanism(String eventAction) {
		_mechanismList.remove(eventAction);
	}
}
