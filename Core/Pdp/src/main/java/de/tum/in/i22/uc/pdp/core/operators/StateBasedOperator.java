package de.tum.in.i22.uc.pdp.core.operators;

import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.LiteralOperator;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pdp.core.PolicyDecisionPoint;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.StateBasedOperatorType;

public class StateBasedOperator extends StateBasedOperatorType implements LiteralOperator, Observer  {
	private static Logger _logger = LoggerFactory.getLogger(StateBasedOperator.class);

	public static final String OP_IS_COMBINED_WITH = "isCombinedWith";
	public static final String OP_IS_NOT_IN = "isNotIn";
	public static final String OP_IS_ONLY_IN = "isOnlyIn";

	protected String predicate;

	/*
	 * Stores whether this operator's value has changed since
	 * the last tick(). The value might change because
	 * an event occurs that makes the operator true.
	 */
	private boolean _valueSinceLastTick;

	private boolean _backupValueSinceLastTick;

	public StateBasedOperator() {
	}

	@Override
	protected void init(Mechanism mech, Operator parent, long ttl) {
		super.init(mech, parent, ttl);
		_pdp.addObserver(this);

		String sep = Settings.getInstance().getSeparator1();
		predicate = operator + sep + param1 + sep + param2 + sep + param3;
		_valueSinceLastTick = !isPositive();
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
//
//	@Override
//	protected boolean localEvaluation(IEvent ev) {
//		IPdp2Pip pip = _pdp.getPip();
//		String separator = Settings.getInstance().getSeparator1();
//
//		String p = operator + separator + param1 + separator + param2 + separator + param3;
//
//		boolean result;
//
//		if (ev == null) {
//			/*
//			 * We are evaluating at the end of a timestep
//			 */
//			result = pip.evaluatePredicateCurrentState(p);
//		}
//		else {
//			/*
//			 * We are evaluating in the presence of a given event
//			 */
//			result = pip.evaluatePredicateSimulatingNextState(ev, p);
//		}
//
//
//		if (result == isPositive()) {
//			/*
//			 * There are two situations in which this condition may turn true:
//			 * a) a 'positive' state based operator's state turned true.
//			 * b) a 'non-positive' state based operator's state turned false.
//			 *
//			 * In both cases we want to signal the state change to our observers.
//			 */
//			setChanged();
//			notifyObservers();
//		}
//
//		return result;
//	}
//
//	@Override
//	protected boolean distributedEvaluation(boolean resultLocalEval, IEvent ev) {
//		boolean result = resultLocalEval;
//
//		if (resultLocalEval != isPositive()) {
//			/*
//			 * There are two situations in which this condition may turn true:
//			 * a) the operator's state is locally satisfied and this does
//			 *    _not_ imply global satisfaction (e.g. isNotIn)
//			 * b) the operator's state is locally violated and this does
//			 *    _not_ imply global violation (e.g. isCombinedWith)
//			 *
//			 * In both cases we ask the DistributionManager what the remote state
//			 * of those operators is.
//			 */
//			result = _pdp.getDistributionManager().wasTrueInBetween(this, _mechanism.getLastUpdate(), _mechanism.getLastUpdate() + _mechanism.getTimestepSize());
//		}
//
//		return result;
//	}

	@Override
	public boolean tick() {
		// Let's see what we have accumulated during the last timestep.
		_valueAtLastTick = _valueSinceLastTick;

		if (_valueAtLastTick != isPositive()) {
			/*
			 * The value was not changed or looked up during the last timestep via
			 * on occurring event. Look up the value instantly.
			 */
			_valueAtLastTick = _pdp.getPip().evaluatePredicateCurrentState(predicate);

			if (_valueAtLastTick == isPositive()) {
				/*
				 * If the value has changed, notify our observers.
				 * In particular remote entities.
				 */
				setChanged();
				notifyObservers();
			}
		}

		if (_valueAtLastTick != isPositive() && Settings.getInstance().getDistributionEnabled()) {
			/*
			 * Last resort: The StateBasedOperator might have changed its state remotely.
			 */
			_valueAtLastTick = _pdp.getDistributionManager().wasTrueInBetween(this, _mechanism.getLastUpdate(), _mechanism.getLastUpdate() + _mechanism.getTimestepSize());
		}

		// initialize for next timestep
		_valueSinceLastTick = !isPositive();

		return _valueAtLastTick;
	}

	@Override
	public void startSimulation() {
		super.startSimulation();
		_backupValueSinceLastTick = _valueSinceLastTick;
	}

	@Override
	public void stopSimulation() {
		super.stopSimulation();
		_valueSinceLastTick = _backupValueSinceLastTick;
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
			if (_valueSinceLastTick != isPositive()) {
				_valueSinceLastTick = _pdp.getPip().evaluatePredicateSimulatingNextState((IEvent) arg, predicate);

				if (_valueSinceLastTick == isPositive()) {
					/*
					 * The value of the operator has changed!
					 * Notify our observers.
					 */
					setChanged();
					notifyObservers();
				}
			}
			_logger.debug("Updating with event {}. Result: {}.", arg, _valueSinceLastTick);
		}
	}
}
