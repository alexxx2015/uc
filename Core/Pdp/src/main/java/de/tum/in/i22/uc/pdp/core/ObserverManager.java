package de.tum.in.i22.uc.pdp.core;

import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;

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
	private final Map<String, Deque<EventMatchOperator>> _eventMatchMap;

	/**
	 * Maps event names to {@link Mechanism}s
	 */
	private final Map<String, Deque<Mechanism>> _mechanismMap;

	private final Set<StateBasedOperator> _sboSet;
	private final Set<ConditionParamMatchOperator> _conditionSet;

	ObserverManager() {
		_eventMatchMap = new HashMap<>();
		_mechanismMap = new HashMap<>();
		_sboSet = new HashSet<>();
		_conditionSet = new HashSet<>();
	}

	private void add(EventMatchOperator e) {
		String actionName = e.getAction();
		Deque<EventMatchOperator> eventMatches;

		synchronized (_eventMatchMap) {
			eventMatches = _eventMatchMap.get(actionName);

			if (eventMatches == null) {
				eventMatches = new ConcurrentLinkedDeque<>();
				_eventMatchMap.put(actionName, eventMatches);
			}
		}

		eventMatches.add(e);
	}

	private void add(StateBasedOperator s) {
		synchronized (_sboSet) {
			_sboSet.add(s);
		}
	}

	private void add(ConditionParamMatchOperator c) {
		synchronized (_conditionSet) {
			_conditionSet.add(c);
		}
	}

	/**
	 * Map the mechanism's trigger event to the actual Mechanism.
	 *
	 * @param mechanism
	 */
	void add(Mechanism mechanism) {
		String actionName = mechanism.getTriggerEvent().getAction();
		Deque<Mechanism> mechanisms;

		synchronized (_mechanismMap) {
			mechanisms = _mechanismMap.get(actionName);

			if (mechanisms == null) {
				mechanisms = new ConcurrentLinkedDeque<>();
				_mechanismMap.put(actionName, mechanisms);
			}
		}

		mechanisms.add(mechanism);
	}

	void add(Collection<AtomicOperator> observers) {
		observers.forEach(o -> {
			switch(o.getOperatorType()) {
			case STATE_BASED:
				add((StateBasedOperator) o);
				break;
			case EVENT_MATCH:
				add((EventMatchOperator) o);
				break;
			case CONDITION_PARAM_MATCH:
				add((ConditionParamMatchOperator) o);
				break;
			default:
				break;
			}
		});
	}

	Collection<AtomicOperator> getAtomicOperators(String eventName) {
		List<AtomicOperator> result = new LinkedList<>();

		Deque<EventMatchOperator> eventMatches;

		synchronized (_eventMatchMap) {
			eventMatches = _eventMatchMap.get(eventName);
			if (eventMatches != null) {
				result.addAll(eventMatches);
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

		Deque<Mechanism> matching;

		synchronized (_mechanismMap) {
			matching = _mechanismMap.get(eventAction);
			if (matching != null) {
				result.addAll(matching);
			}

			matching = _mechanismMap.get(Settings.getInstance().getStarEvent());
			if (matching != null) {
				result.addAll(matching);
			}
		}

		return result;
	}

	void removeMechanism(String eventAction) {
		synchronized (_mechanismMap) {
			_mechanismMap.remove(eventAction);
		}
	}
}