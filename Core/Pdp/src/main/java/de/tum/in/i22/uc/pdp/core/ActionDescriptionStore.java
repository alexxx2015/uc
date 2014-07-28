package de.tum.in.i22.uc.pdp.core;

import java.util.LinkedList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;


class ActionDescriptionStore {
	/**
	 * Maps event names to {@link EventMatch} objects
	 */
	private final Map<String, List<EventMatch>> _eventMatchMap;

	/**
	 * Maps event names to {@link Mechanism}s
	 */
	private final Map<String, List<Mechanism>> _mechanismMap;

	ActionDescriptionStore() {
		_eventMatchMap = Collections.synchronizedMap(new HashMap<String, List<EventMatch>>());
		_mechanismMap = Collections.synchronizedMap(new HashMap<String, List<Mechanism>>());
	}

	void addEventMatch(EventMatch e) {
		List<EventMatch> eventMatchList = _eventMatchMap.get(e.getAction());
		if (eventMatchList == null) {
			eventMatchList = Collections.synchronizedList(new LinkedList<EventMatch>());
		}
		eventMatchList.add(e);

		_eventMatchMap.put(e.getAction(), eventMatchList);
	}

	void addMechanism(Mechanism m) {
		List<Mechanism> mechanismList = _mechanismMap.get(m.getTriggerEvent().getAction());
		if (mechanismList == null) {
			mechanismList = Collections.synchronizedList(new LinkedList<Mechanism>());
		}
		mechanismList.add(m);

		_mechanismMap.put(m.getTriggerEvent().getAction(), mechanismList);
	}

	/**
	 * Returns the list of {@link EventMatch}es for the specified eventAction.
	 *
	 * IMPORTANT NOTE:
	 * 		The returned list is a synchronized list (cf. java.util.Collections.synchronizedList()).
	 * 		While all atomic operations are synchronized by the Java API,
	 * 		the caller must synchronize on the list whenever he is iterating over it:
	 *
	 * 		synchronized(list) {
	 * 			for (Object o : list) {
	 * 				...
	 * 			}
	 * 		}
	 *
	 *
	 * @param eventAction
	 * @return
	 */
	 List<EventMatch> getEventList(String eventAction) {
		if (eventAction == null) {
			return Collections.emptyList();
		}
		return _eventMatchMap.get(eventAction);
	}


	/**
	 * Returns the list of {@link Mechanism}s for the specified eventAction.
	 *
	 * Different from {@link ActionDescriptionStore#getEventList(String)}, this
	 * method returns a new list that must not be synchronized upon usage.
	 *
	 * @param eventAction
	 * @return
	 */
	List<Mechanism> getMechanismList(String eventAction) {
		List<Mechanism> result = new LinkedList<Mechanism>();

		List<Mechanism> matchingEvent = _mechanismMap.get(eventAction);
		if (matchingEvent != null) {
			synchronized (matchingEvent) {
				result.addAll(matchingEvent);
			}
		}

		List<Mechanism> matchingStar = _mechanismMap.get(Settings.getInstance().getStarEvent());
		if (matchingStar != null) {
			synchronized (matchingStar) {
				result.addAll(matchingStar);
			}
		}

		return result;
	}

	void removeMechanism(String eventAction) {
		_mechanismMap.remove(eventAction);
	}
}
