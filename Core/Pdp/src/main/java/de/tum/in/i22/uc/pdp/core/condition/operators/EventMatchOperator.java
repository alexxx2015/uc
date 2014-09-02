package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.LiteralOperator;
import de.tum.in.i22.uc.pdp.core.EventMatch;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;

public class EventMatchOperator extends EventMatch implements LiteralOperator {
	private static Logger _logger = LoggerFactory.getLogger(EventMatchOperator.class);

	private boolean _value = false;

	public EventMatchOperator() {
	}

	@Override
	protected void init(Mechanism mech, Operator parent, long ttl) {
		super.init(mech, parent, ttl);
		_pdp.addEventMatch(this);
	}

	@Override
	protected int initId(int id) {
		_id = id + 1;
		setFullId(_id);
		_logger.debug("My [{}] id is {}.", this, getFullId());
		return _id;
	}

	@Override
	protected boolean localEvaluation(IEvent ev) {
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
			result = _value;
			_value = false;
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
			if (!_value && ev.isActual() && matches(ev)) {
				_value = true;

				setChanged();
				notifyObservers();
			}
			result = _value;
		}

		return result;
	}

	@Override
	protected boolean distributedEvaluation(boolean resultLocalEval, IEvent ev) {
		boolean result = resultLocalEval;

		if (!resultLocalEval) {
			/*
			 * The event did not happen locally. Therefore, ask the DistributionManager
			 * whether the event happened remotely. If so, the result is true.
			 * If we are evaluating in the presence of an event, i.e. _not_ at the end
			 * of a timestep, we set the state value to true for further lookups.
			 */
			if (_pdp.getDistributionManager().wasTrueSince(this, _mechanism.getLastUpdate())) {
				result = true;

				if (ev != null) {
					_value = true;
				}
			}
		}

		return result;
	}

	@Override
	public boolean isPositive() {
		return true;
	}
}
