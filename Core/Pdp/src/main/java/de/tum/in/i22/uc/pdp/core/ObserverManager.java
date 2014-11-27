package de.tum.in.i22.uc.pdp.core;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.interfaces.AtomicOperator;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pdp.core.operators.ConditionParamMatchOperator;
import de.tum.in.i22.uc.pdp.core.operators.EventMatchOperator;
import de.tum.in.i22.uc.pdp.core.operators.StateBasedOperator;


/**
 * This class manages the {@link PolicyDecisionPoint}s observers.
 *
 * @author Florian Kelbert
 *
 */
class ObserverManager {
	/**
	 * Maps event names to {@link EventMatch} objects
	 */
	private final Map<String, List<EventMatchOperator>> _eventMatchMap;

	/**
	 * Maps event names to {@link Mechanism}s
	 */
	private final Map<String, List<Mechanism>> _mechanismMap;

	private final Set<StateBasedOperator> _sboSet;
	private final Set<ConditionParamMatchOperator> _conditionSet;

	ObserverManager() {
		_eventMatchMap = Collections.synchronizedMap(new HashMap<String, List<EventMatchOperator>>());
		_mechanismMap = Collections.synchronizedMap(new HashMap<String, List<Mechanism>>());
		_sboSet = Collections.synchronizedSet(new HashSet<StateBasedOperator>());
		_conditionSet = Collections.synchronizedSet(new HashSet<ConditionParamMatchOperator>());
	}

	void add(EventMatchOperator e) {
		String actionName = e.getAction();
		List<EventMatchOperator> eventMatches = _eventMatchMap.get(actionName);

		if (eventMatches == null) {
			eventMatches = Collections.synchronizedList(new LinkedList<>());
			_eventMatchMap.put(actionName, eventMatches);
		}

		eventMatches.add(e);
	}

	void add(StateBasedOperator s) {
		_sboSet.add(s);
	}

	void add(ConditionParamMatchOperator c) {
		_conditionSet.add(c);
	}

	/**
	 * Map the mechanism's trigger event to the actual Mechanism.
	 *
	 * @param mechanism
	 */
	void add(Mechanism mechanism) {
		String actionName = mechanism.getTriggerEvent().getAction();
		List<Mechanism> mechanisms = _mechanismMap.get(actionName);

		if (mechanisms == null) {
			mechanisms = Collections.synchronizedList(new LinkedList<>());
			_mechanismMap.put(actionName, mechanisms);
		}

		mechanisms.add(mechanism);
	}

	Collection<AtomicOperator> getAtomicOperators(String eventName) {
		List<AtomicOperator> result = new LinkedList<>();

		List<EventMatchOperator> eventMatches = _eventMatchMap.get(eventName);
		if (eventMatches != null) {
			synchronized (eventMatches) {
				result.addAll(_eventMatchMap.get(eventName));
			}
		}

		synchronized (_sboSet) {
			result.addAll(_sboSet);
		}

		synchronized (_conditionSet) {
			result.addAll(_conditionSet);
		}

		return result;
	}

	/**
	 * Returns the list of {@link Mechanism}s for the specified eventAction.
	 *
	 * @param eventAction
	 * @return
	 */
	Collection<Mechanism> getMechanisms(String eventAction) {
		Collection<Mechanism> result = new LinkedList<>();

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