package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.pdp.core.EventMatch;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;

public class EventMatchOperator extends EventMatch {
	private static Logger _logger = LoggerFactory.getLogger(EventMatchOperator.class);

	public EventMatchOperator() {
	}

	@Override
	public void init(Mechanism mech) {
		super.init(mech);
		_pdp.addEventMatch(this);
	}

	@Override
	public boolean evaluate(IEvent ev) {
		boolean result;

		if (ev == null) {
			/*
			 * We are evaluating at the end of a timestep.
			 * Hence, the result to be returned is the state that has
			 * been accumulated during this timestep.
			 * Also, we prepare for the
			 * next timestep by resetting the state to false, indicating
			 * that the event did not yet happen.
			 */
			result = _state.value;
			_state.value = false;
		}
		else {
			/*
			 * We are evaluating in the presence of an event.
			 * If the given event matches, we set the internal state
			 * to true, implying that the event indicated by this object
			 * has happened at least once during this timestep.
			 *
			 * In fact, we only need to evaluate whether the specified event
			 * matches, if the current state is false. Otherwise, a matching
			 * event happened earlier within this timestep and the state is true anyway.
			 */
			if (!_state.value && matches(ev)) {
				_state.value = true;
			}
			result = _state.value;
		}

		_logger.debug("Evaluated [{}] with result [{}]", this, result);
		return result;
	}
}
