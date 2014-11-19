package de.tum.in.i22.uc.pdp.core.operators;

import java.util.Collection;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.Trilean;
import de.tum.in.i22.uc.cm.datatypes.interfaces.AtomicOperator;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.pdp.core.EventMatch;
import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.core.PolicyDecisionPoint;
import de.tum.in.i22.uc.pdp.core.operators.State.StateVariable;

public class EventMatchOperator extends EventMatch implements AtomicOperator, Observer {
	private static Logger _logger = LoggerFactory.getLogger(EventMatchOperator.class);

	public EventMatchOperator() {
	}

	@Override
	protected void init(Mechanism mech, Operator parent, long ttl) {
		super.init(mech, parent, ttl);

		/*
		 * How often the event has occurred since the last tick.
		 */
		_state.set(StateVariable.SINCE_LAST_TICK, 0);

		/*
		 * How often the event occurred within the timestep that
		 * ended at the last tick.
		 */
		_state.set(StateVariable.VALUE_AT_LAST_TICK, 0);

		_state.set(StateVariable.SINCE_UPDATE, Trilean.UNDEF);

		_positivity = Trilean.TRUE;
	}

	@Override
	protected int initId(int id) {
		return setId(id + 1);
	}

//	public static final boolean considerHappenedForEntireTimestep = true;

	@Override
	public boolean tick(boolean endOfTimestep) {
//		Trilean sinceUpdate = _state.get(StateVariable.SINCE_UPDATE);
//
//		boolean result = false;
//
//		if (considerHappenedForEntireTimestep || sinceUpdate == Trilean.TRUE) {
//			int valueAtLastTick = _state.get(StateVariable.SINCE_LAST_TICK);
//
//			_state.set(StateVariable.VALUE_AT_LAST_TICK, valueAtLastTick);
//			_state.set(StateVariable.SINCE_LAST_TICK, 0);
//
//			result = valueAtLastTick > 0;
//		}
//		else {
//			_state.set(StateVariable.VALUE_AT_LAST_TICK, 0);
//			_state.set(StateVariable.SINCE_LAST_TICK, 0);
//		}
//
//		_state.set(StateVariable.SINCE_UPDATE, Trilean.UNDEF);
//
//		return result;

		int valueAtLastTick = _state.get(StateVariable.SINCE_LAST_TICK);

		_state.set(StateVariable.VALUE_AT_LAST_TICK, valueAtLastTick);
		_state.set(StateVariable.SINCE_LAST_TICK, 0);

		_state.set(StateVariable.SINCE_UPDATE, Trilean.UNDEF);

		return valueAtLastTick > 0;
	}

	@Override
	public boolean distributedTickPostprocessing(boolean endOfTimestep) {
		int valueAtLastTick = _state.get(StateVariable.VALUE_AT_LAST_TICK);

		if (valueAtLastTick == 0) {
//			Pair<Long,Long> fromTo = getFromTo(Settings.getInstance().getDistributionGranularity());
//
//			valueAtLastTick = _pdp.getDistributionManager().howOftenTrueInBetween(this, fromTo.getLeft(), fromTo.getRight());

			long lastTick = _mechanism.getLastTick();

			valueAtLastTick = _pdp.getDistributionManager().howOftenNotifiedInBetween(this, lastTick, lastTick + _mechanism.getTimestepSize());

			_state.set(StateVariable.VALUE_AT_LAST_TICK, valueAtLastTick);
		}

		return valueAtLastTick > 0;
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof PolicyDecisionPoint && arg instanceof IEvent) {

			if (matches((IEvent) arg)) {
				/*
				 * The event is happening. Increase the counter counting
				 * how often the event has happened and notify our observers.
				 */
				_state.set(StateVariable.SINCE_UPDATE, Trilean.TRUE);
				_state.set(StateVariable.SINCE_LAST_TICK, (int) _state.get(StateVariable.SINCE_LAST_TICK) + 1);
				setChanged();
				notifyObservers(_state);
			}
			else {
				_state.set(StateVariable.SINCE_UPDATE, Trilean.FALSE);
			}

			_logger.debug("Updating with event {}. Result: {}.", arg, _state.get(StateVariable.SINCE_LAST_TICK));
		}
	}

	int getCountAtLastTick() {
		return _state.get(StateVariable.VALUE_AT_LAST_TICK);
	}

	boolean getSinceUpdate() {
		return ((Trilean) _state.get(StateVariable.SINCE_UPDATE)) == Trilean.TRUE;
	}

	@Override
	public boolean getValueAtLastTick() {
		return (int) _state.get(StateVariable.VALUE_AT_LAST_TICK) > 0;
	}

	@Override
	public Collection<Observer> getObservers(Collection<Observer> observers) {
		observers.add(this);
		return observers;
	}
}
