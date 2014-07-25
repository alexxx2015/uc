package de.tum.in.i22.uc.pdp.core;

import java.util.LinkedList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;


public class ActionDescriptionStore {
	private Map<String, List<EventMatch>> _eventMatchList = null;
	private Map<String, List<Mechanism>> _mechanismList = null;

	ActionDescriptionStore() {
		_eventMatchList = Collections.synchronizedMap(new HashMap<String, List<EventMatch>>());
		_mechanismList = Collections.synchronizedMap(new HashMap<String, List<Mechanism>>());
	}

	public void addEventMatch(EventMatch e) {
		List<EventMatch> eventMatchList = _eventMatchList.get(e.getAction());
		if (eventMatchList == null) {
			eventMatchList = Collections.synchronizedList(new LinkedList<EventMatch>());
		}
		eventMatchList.add(e);

		_eventMatchList.put(e.getAction(), eventMatchList);
	}

	public void addMechanism(Mechanism m) {
		List<Mechanism> mechanismList = _mechanismList.get(m.getTriggerEvent().getAction());
		if (mechanismList == null) {
			mechanismList = Collections.synchronizedList(new LinkedList<Mechanism>());
		}
		mechanismList.add(m);

		_mechanismList.put(m.getTriggerEvent().getAction(), mechanismList);
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
	public List<EventMatch> getEventList(String eventAction) {
		if (eventAction == null) {
			return Collections.emptyList();
		}
		return _eventMatchList.get(eventAction);
	}


	/**
	 * Returns the list of {@link Mechanism}s for the specified eventAction.
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
	public List<Mechanism> getMechanismList(String eventAction) {
		List<Mechanism> result = Collections.synchronizedList(new LinkedList<Mechanism>());
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
