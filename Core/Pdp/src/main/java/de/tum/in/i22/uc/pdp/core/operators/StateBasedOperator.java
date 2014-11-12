package de.tum.in.i22.uc.pdp.core.operators;

import java.util.Collection;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;

import de.tum.in.i22.uc.cm.datatypes.basic.Trilean;
import de.tum.in.i22.uc.cm.datatypes.interfaces.AtomicOperator;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.core.PolicyDecisionPoint;
import de.tum.in.i22.uc.pdp.core.operators.State.StateVariable;
import de.tum.in.i22.uc.pdp.xsd.StateBasedOperatorType;

public class StateBasedOperator extends StateBasedOperatorType implements AtomicOperator, Observer  {
	private static Logger _logger = LoggerFactory.getLogger(StateBasedOperator.class);

	public static final String OP_IS_COMBINED_WITH = "isCombinedWith";
	public static final String OP_IS_NOT_IN = "isNotIn";
	public static final String OP_IS_ONLY_IN = "isOnlyIn";

	private String predicate;

	public StateBasedOperator() {
	}

	@Override
	protected void init(Mechanism mech, Operator parent, long ttl) {
		super.init(mech, parent, ttl);

		String sep = Settings.getInstance().getSeparator1();
		predicate = operator + sep + param1 + sep + param2 + sep + param3;

		_state.set(StateVariable.VALUE_AT_LAST_TICK, false);
		_state.set(StateVariable.SINCE_LAST_TICK, false);

		switch (operator) {
		case OP_IS_COMBINED_WITH:
			_positivity = Trilean.TRUE;
			break;
		case OP_IS_NOT_IN:
		case OP_IS_ONLY_IN:
			_positivity = Trilean.FALSE;
			break;
		default:
			throw new UnsupportedOperationException("Unknown Predicate type: " + operator);
		}
	}

	@Override
	protected int initId(int id) {
		return setId(id + 1);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(getClass())
				.add("operator", operator)
				.add("param1", param1)
				.add("param2", param2)
				.add("param3", param3)
				.toString();
	}

	@Override
	public boolean tick() {
		boolean valueAtLastTick = _state.get(StateVariable.VALUE_AT_LAST_TICK);
		boolean sinceLastTick = _state.get(StateVariable.SINCE_LAST_TICK);

		if (sinceLastTick == valueAtLastTick) {
			/*
			 * The value was not changed since the last tick. Since the information flow
			 * model might have changed in the meanwhile, we need to evaluate the
			 * predicate instantly.
			 */
			valueAtLastTick = _pdp.getPip().evaluatePredicateCurrentState(predicate);

			if (_positivity.is(valueAtLastTick)) {
				/*
				 * Now, if the evaluation result is such that the global result can
				 * be inferred from our local evaluation result, we inform our observers.
				 */
				setChanged();
				notifyObservers(_state);
			}
		}
		else {
			/*
			 * Otherwise, the value changed earlier during this timestep and we
			 * do not need to re-evaluate.
			 */
			valueAtLastTick = sinceLastTick;
		}

		_state.set(StateVariable.VALUE_AT_LAST_TICK, valueAtLastTick);
		_state.set(StateVariable.SINCE_LAST_TICK, valueAtLastTick);

		return valueAtLastTick;
	}

	@Override
	public boolean distributedTickPostprocessing() {
		boolean valueAtLastTick = _state.get(StateVariable.VALUE_AT_LAST_TICK);

		if (!_positivity.is(valueAtLastTick)) {
			/*
			 * The evaluation result is such that we cannot infer that our
			 * local result also holds globally.
			 * The Operator might have changed its state remotely.
			 */
			long lastTick = _mechanism.getLastTick();
			valueAtLastTick = _pdp.getDistributionManager().wasTrueInBetween(this, lastTick, lastTick + _mechanism.getTimestepSize());

			_state.set(StateVariable.VALUE_AT_LAST_TICK, valueAtLastTick);
			_state.set(StateVariable.SINCE_LAST_TICK, valueAtLastTick);
		}

		return valueAtLastTick;
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof PolicyDecisionPoint && arg instanceof IEvent) {

			boolean sinceLastTick = _state.get(StateVariable.SINCE_LAST_TICK);

			if (sinceLastTick == (boolean) _state.get(StateVariable.VALUE_AT_LAST_TICK)) {
				sinceLastTick = _pdp.getPip().evaluatePredicateSimulatingNextState((IEvent) arg, predicate);

				_state.set(StateVariable.SINCE_LAST_TICK, sinceLastTick);

				if (getPositivity().is(sinceLastTick) && Settings.getInstance().getDistributionEnabled()) {
					/*
					 * The value of the operator has changed!
					 * Notify our observers.
					 */
					setChanged();
					notifyObservers(_state);
				}
			}

			_logger.debug("Updating [{}] with event {}. Result: {}.", predicate, ((IEvent) arg).getName(), sinceLastTick);
		}
	}

	@Override
	public Collection<Observer>  getObservers(Collection<Observer> observers) {
		observers.add(this);
		return observers;
	}
}
