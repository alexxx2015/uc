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

/**
 *
 * Rewritten by Florian Kelbert 2014/11/18.
 *
 * @author Enrico Lovat, Florian Kelbert
 *
 */
public class StateBasedOperator extends StateBasedOperatorType implements AtomicOperator, Observer  {
	private static Logger _logger = LoggerFactory.getLogger(StateBasedOperator.class);

	public static final String OP_IS_COMBINED_WITH = "isCombinedWith";
	public static final String OP_IS_NOT_IN = "isNotIn";
	public static final String OP_IS_ONLY_IN = "isOnlyIn";

	private String _predicate;

	public StateBasedOperator() {
	}

	@Override
	protected void init(Mechanism mech, Operator parent, long ttl) {
		super.init(mech, parent, ttl);

		String sep = Settings.getInstance().getSeparator1();
		_predicate = operator + sep + param1 + sep + param2 + sep + param3;

		/*
		 * I (FK) should be spending some explaining words on _positivity
		 * here. As obvious from the code, isCombinedWith has 'positive'
		 * positivity, while isNotIn and isOnlyIn have negative
		 * positivity. This is in correspondence with the CANS'14 paper,
		 * Section 4.2, predicate S.
		 *
		 * isCombinedWith: If this operator is true (i.e. satisfied) within one system
		 * then there is no way this operator might become false (i.e. violate)
		 * when considering additional systems at the same
		 * point in time. This is simply because it is sufficient that the two
		 * considered data items are combined at least once. In other words:
		 * if isCombinedWith is locally satisfied, then is it also globally
		 * satisfied. On the other hand, if isCombinedWith is false
		 * (i.e. violated) locally, then another system might still satisfy
		 * the operator by combining the two considered data items.
		 * In summary:
		 * - local satisfaction implies global satisfaction, but
		 * - local violation does not imply global violation.
		 *
		 * isNotIn/isOnlyIn: If those operators are true (i.e. satisfied) within
		 * one system, then this does not imply that they are true (i.e. satisfied)
		 * globally. Reason: if some data is not within a set of containers locally,
		 * then it might still be the case that at some remote system the data
		 * actually is within this set of containers; the same arguments apply to
		 * isOnlyIn. In other words: local satisfaction of those operators does
		 * not imply their global satisfaction. On the other hand, if isNotIn is
		 * violated locally (i.e. the data actually is in one of the forbidden
		 * containers), then the operator will also always be violated from a
		 * global perspective.
		 * In Summary:
		 * - local violation implies global violation, but
		 * - local satisfaction does not imply global satisfaction.
		 */
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

		/*
		 * Default values of the operators are the negated value of their
		 * positivity.
		 * Reasoning: In each case (i.e. for each operator type, i.e.
		 * with negative or positive positivity), we start with value
		 * that necessitates coordination with other systems. In other words,
		 * we assume the value that is 'worse'. For isCombinedWith, this value
		 * is false, because local violation does not imply global violation.
		 * For isNotIn/isOnlyIn, this value is true, because local satisfaction
		 * does not imply global satisfaction.
		 */
		_state.set(StateVariable.SINCE_LAST_TICK, !_positivity.value());
		_state.set(StateVariable.VALUE_AT_LAST_TICK, !_positivity.value());
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
	public Collection<Observer> getObservers(Collection<Observer> observers) {
		observers.add(this);
		return observers;
	}

	@Override
	public boolean tick(boolean endOfTimestep) {
		boolean sinceLastTick = _state.get(StateVariable.SINCE_LAST_TICK);
		boolean localResult = sinceLastTick;

		if (endOfTimestep) {
			boolean valueNow = _pdp.getPip().evaluatePredicateCurrentState(_predicate);

			if (_positivity.is(true) && valueNow || _positivity.is(false) && !valueNow) {
				localResult = _positivity.value();
				setChanged();
				notifyObservers(_state);
			}

			_state.set(StateVariable.VALUE_AT_LAST_TICK, localResult);
			_state.set(StateVariable.SINCE_LAST_TICK, valueNow);
		}

		return localResult;
	}

//	private boolean tickIsCombinedWith(boolean endOfTimestep) {
//		boolean sinceLastTick = _state.get(StateVariable.SINCE_LAST_TICK);
//		boolean localResult = sinceLastTick;
//
//		if (endOfTimestep) {
//			boolean valueNow = _pdp.getPip().evaluatePredicateCurrentState(_predicate);
//
//			if (valueNow) {
//				localResult = true;
//				setChanged();
//				notifyObservers(_state);
//			}
//
//			_state.set(StateVariable.VALUE_AT_LAST_TICK, localResult);
//			_state.set(StateVariable.SINCE_LAST_TICK, valueNow);
//		}
//
//		return localResult;
//	}
//
//	private boolean tickIsNotIn(boolean endOfTimestep) {
//		boolean sinceLastTick = _state.get(StateVariable.SINCE_LAST_TICK);
//		boolean localResult = sinceLastTick;
//
//		if (endOfTimestep) {
//			boolean valueNow = _pdp.getPip().evaluatePredicateCurrentState(_predicate);
//
//			if (!valueNow) {
//				localResult = false;
//				setChanged();
//				notifyObservers(_state);
//			}
//
//			_state.set(StateVariable.VALUE_AT_LAST_TICK, localResult);
//			_state.set(StateVariable.SINCE_LAST_TICK, valueNow);
//		}
//
//		return localResult;
//	}
//
//	private boolean tickIsOnlyIn(boolean endOfTimestep) {
//	return tickIsNotIn(endOfTimestep);
//}

	@Override
	public boolean distributedTickPostprocessing(boolean endOfTimestep) {
		boolean sinceLastTick = _state.get(StateVariable.SINCE_LAST_TICK);

		if (_positivity.is(true) && !sinceLastTick || _positivity.is(false) && sinceLastTick) {
			long lastTick = _mechanism.getLastTick();
			sinceLastTick = _pdp.getDistributionManager().wasTrueInBetween(this, lastTick, lastTick + _mechanism.getTimestepSize());
			_state.set(StateVariable.SINCE_LAST_TICK, sinceLastTick);
		}

		return sinceLastTick;
	}

//	private boolean distributedTickPostprocessingIsCombinedWith(boolean endOfTimestep) {
//		boolean sinceLastTick = _state.get(StateVariable.SINCE_LAST_TICK);
//
//		if (!sinceLastTick) {
//			long lastTick = _mechanism.getLastTick();
//			sinceLastTick = _pdp.getDistributionManager().wasTrueInBetween(this, lastTick, lastTick + _mechanism.getTimestepSize());
//			_state.set(StateVariable.SINCE_LAST_TICK, sinceLastTick);
//		}
//
//		return sinceLastTick;
//	}
//
//	private boolean distributedTickPostprocessingIsNotIn() {
//		boolean sinceLastTick = _state.get(StateVariable.SINCE_LAST_TICK);
//
//		if (sinceLastTick) {
//			long lastTick = _mechanism.getLastTick();
//			sinceLastTick = _pdp.getDistributionManager().wasTrueInBetween(this, lastTick, lastTick + _mechanism.getTimestepSize());
//			_state.set(StateVariable.SINCE_LAST_TICK, sinceLastTick);
//		}
//
//		return sinceLastTick;
//	}
//
//	private boolean distributedTickPostprocessingIsOnlyIn() {
//		return distributedTickPostprocessingIsNotIn();
//	}

	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof PolicyDecisionPoint && arg instanceof IEvent) {
			boolean sinceLastTick = _state.get(StateVariable.SINCE_LAST_TICK);

			if ((_positivity.is(true) && !sinceLastTick && _pdp.getPip().evaluatePredicateCurrentState(_predicate))
					|| (_positivity.is(false) && sinceLastTick && !_pdp.getPip().evaluatePredicateCurrentState(_predicate))) {
				_state.set(StateVariable.SINCE_LAST_TICK, _positivity.value());
				setChanged();
				notifyObservers(_state);
			}

			_logger.debug("Updating with event {}. Result: {}.", arg, _state.get(StateVariable.SINCE_LAST_TICK));
		}
	}

//	private void updateIsCombinedWith(Observable o, Object arg) {
//		if (o instanceof PolicyDecisionPoint && arg instanceof IEvent) {
//			boolean sinceLastTick = _state.get(StateVariable.SINCE_LAST_TICK);
//
//			if (!sinceLastTick && _pdp.getPip().evaluatePredicateCurrentState(_predicate)) {
//				_state.set(StateVariable.SINCE_LAST_TICK, true);
//				setChanged();
//				notifyObservers(_state);
//			}
//
//			_logger.debug("Updating with event {}. Result: {}.", arg, _state.get(StateVariable.SINCE_LAST_TICK));
//		}
//	}
//
//	private void updateIsNotIn(Observable o, Object arg) {
//		if (o instanceof PolicyDecisionPoint && arg instanceof IEvent) {
//			boolean sinceLastTick = _state.get(StateVariable.SINCE_LAST_TICK);
//
//			if (sinceLastTick && !_pdp.getPip().evaluatePredicateCurrentState(_predicate)) {
//				_state.set(StateVariable.SINCE_LAST_TICK, false);
//				setChanged();
//				notifyObservers(_state);
//			}
//
//			_logger.debug("Updating with event {}. Result: {}.", arg, _state.get(StateVariable.SINCE_LAST_TICK));
//		}
//	}
//
//	private void updateIsOnlyIn(Observable o, Object arg) {
//		updateIsNotIn(o, arg);
//	}
//
//	public EStateBasedFormulaType getType() {
//		return _type;
//	}
//
//	public boolean is(EStateBasedFormulaType t) {
//		return _type == t;
//	}
//
//	public boolean getSinceLastTick() {
//		return _state.get(StateVariable.SINCE_LAST_TICK);
//	}
}
