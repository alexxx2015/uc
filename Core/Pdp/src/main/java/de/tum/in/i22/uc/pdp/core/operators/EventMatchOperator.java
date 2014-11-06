package de.tum.in.i22.uc.pdp.core.operators;

import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.AtomicOperator;
import de.tum.in.i22.uc.pdp.core.EventMatch;
import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.core.PolicyDecisionPoint;
import de.tum.in.i22.uc.pdp.core.operators.State.StateVariable;

public class EventMatchOperator extends EventMatch implements AtomicOperator, Observer {
	private static Logger _logger = LoggerFactory.getLogger(EventMatchOperator.class);

	public EventMatchOperator() {
		_state.set(StateVariable.VALUE_AT_LAST_TICK, false);
		_state.set(StateVariable.SINCE_LAST_TICK, false);
	}

	@Override
	protected void init(Mechanism mech, Operator parent, long ttl) {
		super.init(mech, parent, ttl);
		_pdp.addObserver(this);
	}

	@Override
	protected int initId(int id) {
		return setId(id + 1);
	}

	@Override
	public boolean tick() {
		boolean valueAtLastTick = _state.get(StateVariable.SINCE_LAST_TICK);

//		if (!valueAtLastTick && Settings.getInstance().getDistributionEnabled()) {
//			/*
//			 * Last resort: The event might have happened remotely
//			 */
//			valueAtLastTick = _pdp.getDistributionManager().wasTrueInBetween(this, _mechanism.getLastUpdate(), _mechanism.getLastUpdate() + _mechanism.getTimestepSize());
//		}

		_state.set(StateVariable.VALUE_AT_LAST_TICK, valueAtLastTick);
		_state.set(StateVariable.SINCE_LAST_TICK, false);

		return valueAtLastTick;
	}

	@Override
	public boolean distributedTickPostprocessing() {
		boolean isTrue = _state.get(StateVariable.VALUE_AT_LAST_TICK);

		if (!isTrue) {
			isTrue = _pdp.getDistributionManager().wasTrueInBetween(this, _mechanism.getLastUpdate(), _mechanism.getLastUpdate() + _mechanism.getTimestepSize());

			_state.set(StateVariable.VALUE_AT_LAST_TICK, isTrue);
		}

		return isTrue;
	}

	@Override
	public boolean isPositive() {
		return true;
	}

	@Override
	public void startSimulation() {
		super.startSimulation();
	}

	@Override
	public void stopSimulation() {
		super.stopSimulation();
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof PolicyDecisionPoint && arg instanceof IEvent) {

			if (matches((IEvent) arg)) {
				/*
				 * The event is happening.
				 */
				setChanged();
				notifyObservers(_state);
				_state.set(StateVariable.SINCE_LAST_TICK, true);
			}

			_logger.debug("Updating with event {}. Result: {}.", arg, _state.get(StateVariable.SINCE_LAST_TICK));
		}
	}
}
