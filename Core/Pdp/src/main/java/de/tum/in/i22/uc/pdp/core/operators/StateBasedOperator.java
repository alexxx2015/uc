package de.tum.in.i22.uc.pdp.core.operators;

import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.LiteralOperator;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.core.PolicyDecisionPoint;
import de.tum.in.i22.uc.pdp.core.operators.State.StateVariable;
import de.tum.in.i22.uc.pdp.xsd.StateBasedOperatorType;

public class StateBasedOperator extends StateBasedOperatorType implements LiteralOperator, Observer  {
	private static Logger _logger = LoggerFactory.getLogger(StateBasedOperator.class);

	public static final String OP_IS_COMBINED_WITH = "isCombinedWith";
	public static final String OP_IS_NOT_IN = "isNotIn";
	public static final String OP_IS_ONLY_IN = "isOnlyIn";

	protected String predicate;

	public StateBasedOperator() {
		_state.set(StateVariable.VALUE_AT_LAST_TICK, false);
		_state.set(StateVariable.SINCE_LAST_TICK, false);
	}

	@Override
	protected void init(Mechanism mech, Operator parent, long ttl) {
		super.init(mech, parent, ttl);
		_pdp.addObserver(this);

		String sep = Settings.getInstance().getSeparator1();
		predicate = operator + sep + param1 + sep + param2 + sep + param3;
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
			 * The value was not changed or looked up during the last timestep via
			 * on occurring event. Look up the value instantly.
			 */
			valueAtLastTick = _pdp.getPip().evaluatePredicateCurrentState(predicate);

			if (valueAtLastTick == isPositive() && Settings.getInstance().getDistributionEnabled()) {
				/*
				 * If the value has changed, notify our observers.
				 * In particular remote entities.
				 */
				setChanged();
				notifyObservers();
			}
		}
		else {
			valueAtLastTick = sinceLastTick;
		}

		if (valueAtLastTick != isPositive() && Settings.getInstance().getDistributionEnabled()) {
			/*
			 * Last resort: The StateBasedOperator might have changed its state remotely.
			 */
			valueAtLastTick = _pdp.getDistributionManager().wasTrueInBetween(this, _mechanism.getLastUpdate(), _mechanism.getLastUpdate() + _mechanism.getTimestepSize());
		}

		// initialize for next timestep
		sinceLastTick = valueAtLastTick;

		_state.set(StateVariable.VALUE_AT_LAST_TICK, valueAtLastTick);
		_state.set(StateVariable.SINCE_LAST_TICK, sinceLastTick);

		return valueAtLastTick;
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
	public boolean isPositive() {
		switch (operator) {
			case OP_IS_COMBINED_WITH:
				return true;
			case OP_IS_NOT_IN:
			case OP_IS_ONLY_IN:
				return false;
			default:
				throw new UnsupportedOperationException("Unknown Predicate type: " + operator);
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof PolicyDecisionPoint && arg instanceof IEvent) {

			boolean sinceLastTick = _state.get(StateVariable.SINCE_LAST_TICK);

			if (sinceLastTick == (boolean) _state.get(StateVariable.VALUE_AT_LAST_TICK)) {
				sinceLastTick = _pdp.getPip().evaluatePredicateSimulatingNextState((IEvent) arg, predicate);

				if (sinceLastTick == isPositive() && Settings.getInstance().getDistributionEnabled()) {
					/*
					 * The value of the operator has changed!
					 * Notify our observers.
					 */
					setChanged();
					notifyObservers();
				}
			}

			_state.set(StateVariable.SINCE_LAST_TICK, sinceLastTick);
			_logger.debug("Updating [{}] with event {}. Result: {}.", predicate, ((IEvent) arg).getName(), sinceLastTick);
		}
	}
}
