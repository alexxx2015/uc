package de.tum.in.i22.uc.pdp.core.operators;

import java.util.Collection;
import java.util.Observable;
import java.util.Observer;

import org.apache.commons.lang3.tuple.Pair;
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

	private String _predicate;

	private EStateBasedOperator _type;

	public StateBasedOperator() {
	}

	@Override
	protected void init(Mechanism mech, Operator parent, long ttl) {
		super.init(mech, parent, ttl);

		String sep = Settings.getInstance().getSeparator1();
		_predicate = operator + sep + param1 + sep + param2 + sep + param3;

		switch (operator) {
		case OP_IS_COMBINED_WITH:
			initIsCombinedWith(mech, parent, ttl);
			break;
		case OP_IS_NOT_IN:
			initIsNotIn(mech, parent, ttl);
			break;
		case OP_IS_ONLY_IN:
			initIsOnlyIn(mech, parent, ttl);
			break;
		default:
			throw new UnsupportedOperationException("Unknown Predicate type: " + operator);
		}
	}

	private void initIsCombinedWith(Mechanism mech, Operator parent, long ttl) {
		_type = EStateBasedOperator.IS_COMBINED_WITH;
		_positivity = Trilean.TRUE;
		_state.set(StateVariable.SINCE_UPDATE, Trilean.UNDEF);
		_state.set(StateVariable.VALUE_AT_LAST_TICK, false);
	}

	private void initIsNotIn(Mechanism mech, Operator parent, long ttl) {
		_type = EStateBasedOperator.IS_NOT_IN;
		_positivity = Trilean.FALSE;
	}

	private void initIsOnlyIn(Mechanism mech, Operator parent, long ttl) {
		_type = EStateBasedOperator.IS_ONLY_IN;
		_positivity = Trilean.FALSE;
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

//	@Override
//	public boolean tick() {
//		boolean valueAtLastTick = _state.get(StateVariable.VALUE_AT_LAST_TICK);
//		boolean sinceLastTick = _state.get(StateVariable.SINCE_LAST_TICK);
//
//		if (sinceLastTick == valueAtLastTick) {
//			/*
//			 * The value was not changed since the last tick. Since the information flow
//			 * model might have changed in the meanwhile, we need to evaluate the
//			 * predicate instantly.
//			 */
//			valueAtLastTick = _pdp.getPip().evaluatePredicateCurrentState(predicate);
//
//			if (_positivity.is(valueAtLastTick)) {
//				/*
//				 * Now, if the evaluation result is such that the global result can
//				 * be inferred from our local evaluation result, we inform our observers.
//				 */
//				setChanged();
//				notifyObservers(_state);
//			}
//		}
//		else {
//			/*
//			 * Otherwise, the value changed earlier during this timestep and we
//			 * do not need to re-evaluate.
//			 */
//			valueAtLastTick = sinceLastTick;
//		}
//
//		_state.set(StateVariable.VALUE_AT_LAST_TICK, valueAtLastTick);
//		_state.set(StateVariable.SINCE_LAST_TICK, valueAtLastTick);
//
//		return valueAtLastTick;
//	}
//
//	@Override
//	public boolean distributedTickPostprocessing() {
//		boolean valueAtLastTick = _state.get(StateVariable.VALUE_AT_LAST_TICK);
//
//		if (!_positivity.is(valueAtLastTick)) {
//			/*
//			 * The evaluation result is such that we cannot infer that our
//			 * local result also holds globally.
//			 * The Operator might have changed its state remotely.
//			 */
//			long lastTick = _mechanism.getLastTick();
//			valueAtLastTick = _pdp.getDistributionManager().wasTrueInBetween(this, lastTick, lastTick + _mechanism.getTimestepSize());
//
//			_state.set(StateVariable.VALUE_AT_LAST_TICK, valueAtLastTick);
//			_state.set(StateVariable.SINCE_LAST_TICK, valueAtLastTick);
//		}
//
//		return valueAtLastTick;
//	}
//
//	@Override
//	public void update(Observable o, Object arg) {
//		if (o instanceof PolicyDecisionPoint && arg instanceof IEvent) {
//
//			boolean sinceLastTick = _state.get(StateVariable.SINCE_LAST_TICK);
//			boolean valueAtLastTick = _state.get(StateVariable.VALUE_AT_LAST_TICK);
//
//			if (sinceLastTick == valueAtLastTick) {
//				sinceLastTick = _pdp.getPip().evaluatePredicateSimulatingNextState((IEvent) arg, predicate);
//
//				if (sinceLastTick != valueAtLastTick && _positivity.is(b)) {
//					/*
//					 * The value of the operator has changed!
//					 * Notify our observers.
//					 */
//					setChanged();
//					notifyObservers(_state);
//
//					_state.set(StateVariable.SINCE_LAST_TICK, sinceLastTick);
//				}
//			}
//
//			_logger.debug("Updating [{}] with event {}. Result: {}.", predicate, ((IEvent) arg).getName(), sinceLastTick);
//		}
//	}

	@Override
	public Collection<Observer>  getObservers(Collection<Observer> observers) {
		observers.add(this);
		return observers;
	}

	@Override
	public boolean tick() {
		switch (_type) {
		case IS_COMBINED_WITH:
			return tickIsCombinedWith();
		case IS_NOT_IN:
			return tickIsNotIn();
		case IS_ONLY_IN:
			return tickIsOnlyIn();
		default:
			throw new UnsupportedOperationException("Unknown Predicate type.");
		}
	}

	@Override
	public boolean distributedTickPostprocessing() {
		switch (_type) {
		case IS_COMBINED_WITH:
			return distributedTickPostprocessingIsCombinedWith();
		case IS_NOT_IN:
			return distributedTickPostprocessingIsNotIn();
		case IS_ONLY_IN:
			return distributedTickPostprocessingIsOnlyIn();
		default:
			throw new UnsupportedOperationException("Unknown Predicate type.");
		}
	}

	private boolean tickIsCombinedWith() {
		Trilean sinceUpdate = _state.get(StateVariable.SINCE_UPDATE);

		boolean result;

		switch (sinceUpdate) {
		case TRUE:
			/*
			 * We tick in the presence of an event and it _did_ make the predicate true.
			 */
			result = true;
			break;
		case FALSE:
			/*
			 * We tick in the presence of an event and it did _not_ make the predicate true.
			 */
			result = false;
			break;
		case UNDEF:
		default:
			/*
			 * We tick at the end of a timestep.
			 */
			result = _pdp.getPip().evaluatePredicateCurrentState(_predicate);

			if (result) {
				setChanged();
				notifyObservers(_state);
			}

			break;
		}

		_state.set(StateVariable.SINCE_UPDATE, Trilean.UNDEF);
		_state.set(StateVariable.VALUE_AT_LAST_TICK, result);

		return result;

//		boolean sinceLastTick = _state.get(StateVariable.SINCE_LAST_TICK);
//
//		_logger.debug("sinceLastTick: {}", sinceLastTick);
//
//		if (!sinceLastTick) {
//			sinceLastTick = _pdp.getPip().evaluatePredicateCurrentState(_predicate);
//			_logger.debug("Result of evaluation: {}", sinceLastTick);
//		}
//
//		if (sinceLastTick) {
//			_state.set(StateVariable.SINCE_LAST_TICK, true);
//			setChanged();
//			notifyObservers(_state);
//		}
//
//		_state.set(StateVariable.VALUE_AT_LAST_TICK, sinceLastTick);
//		_state.set(StateVariable.SINCE_LAST_TICK, false);
//
//		return sinceLastTick;
	}

	private boolean distributedTickPostprocessingIsCombinedWith() {
		boolean valueAtLastTick = _state.get(StateVariable.VALUE_AT_LAST_TICK);

		if (!valueAtLastTick) {
//			long lastTick = _mechanism.getLastTick();
//			valueAtLastTick = _pdp.getDistributionManager().wasTrueInBetween(this, lastTick, lastTick + _mechanism.getTimestepSize());

			Pair<Long,Long> fromTo = getFromTo(Settings.getInstance().getDistributionGranularity());

			valueAtLastTick = _pdp.getDistributionManager().wasTrueInBetween(this, fromTo.getLeft(), fromTo.getRight());
		}

		_state.set(StateVariable.VALUE_AT_LAST_TICK, valueAtLastTick);

		return valueAtLastTick;
	}

	private boolean tickIsNotIn() {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean tickIsOnlyIn() {
		// TODO Auto-generated method stub
		return false;
	}




	private boolean distributedTickPostprocessingIsNotIn() {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean distributedTickPostprocessingIsOnlyIn() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void update(Observable o, Object arg) {
		switch (_type) {
		case IS_COMBINED_WITH:
			updateIsCombinedWith(o, arg);
			break;
		case IS_NOT_IN:
			updateIsNotIn(o, arg);
			break;
		case IS_ONLY_IN:
			updateIsOnlyIn(o, arg);
			break;
		default:
			throw new UnsupportedOperationException("Unknown Predicate type.");
		}
	}

	private void updateIsCombinedWith(Observable o, Object arg) {
		if (o instanceof PolicyDecisionPoint && arg instanceof IEvent) {

			_pdp.getPip().update((IEvent) arg);
			if (_pdp.getPip().evaluatePredicateCurrentState(_predicate)) {
				_state.set(StateVariable.SINCE_UPDATE, Trilean.TRUE);
				setChanged();
				notifyObservers(_state);
			}
			else {
				_state.set(StateVariable.SINCE_UPDATE, Trilean.FALSE);
			}

			_logger.debug("Updating with event {}. Result: {}.", arg, _state.get(StateVariable.SINCE_UPDATE));
		}
	}

	private void updateIsNotIn(Observable o, Object arg) {
		// TODO Auto-generated method stub

	}

	private void updateIsOnlyIn(Observable o, Object arg) {
		// TODO Auto-generated method stub

	}

	enum EStateBasedOperator {
		IS_COMBINED_WITH,
		IS_ONLY_IN,
		IS_NOT_IN
	}
}
