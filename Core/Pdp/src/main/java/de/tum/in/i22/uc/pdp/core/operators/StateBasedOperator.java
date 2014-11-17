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

	private String _predicate;

	private EStateBasedOperatorType _type;

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
		_type = EStateBasedOperatorType.IS_COMBINED_WITH;
		_positivity = Trilean.TRUE;
		_state.set(StateVariable.SINCE_LAST_TICK, false);
		_state.set(StateVariable.VALUE_AT_LAST_TICK, false);
	}

	private void initIsNotIn(Mechanism mech, Operator parent, long ttl) {
		_type = EStateBasedOperatorType.IS_NOT_IN;
		_positivity = Trilean.FALSE;
	}

	private void initIsOnlyIn(Mechanism mech, Operator parent, long ttl) {
		_type = EStateBasedOperatorType.IS_ONLY_IN;
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

	@Override
	public Collection<Observer>  getObservers(Collection<Observer> observers) {
		observers.add(this);
		return observers;
	}

	@Override
	public boolean tick(boolean endOfTimestep) {
		switch (_type) {
		case IS_COMBINED_WITH:
			return tickIsCombinedWith(endOfTimestep);
		case IS_NOT_IN:
			return tickIsNotIn();
		case IS_ONLY_IN:
			return tickIsOnlyIn();
		default:
			throw new UnsupportedOperationException("Unknown Predicate type.");
		}
	}

	@Override
	public boolean distributedTickPostprocessing(boolean endOfTimestep) {
		switch (_type) {
		case IS_COMBINED_WITH:
			return distributedTickPostprocessingIsCombinedWith(endOfTimestep);
		case IS_NOT_IN:
			return distributedTickPostprocessingIsNotIn();
		case IS_ONLY_IN:
			return distributedTickPostprocessingIsOnlyIn();
		default:
			throw new UnsupportedOperationException("Unknown Predicate type.");
		}
	}

	private boolean tickIsCombinedWith(boolean endOfTimestep) {
		boolean sinceLastTick = _state.get(StateVariable.SINCE_LAST_TICK);
		boolean localResult = sinceLastTick;

		if (endOfTimestep) {
			boolean valueNow = _pdp.getPip().evaluatePredicateCurrentState(_predicate);

			if (valueNow) {
				localResult = true;
				setChanged();
				notifyObservers(_state);
			}

			_state.set(StateVariable.VALUE_AT_LAST_TICK, localResult);
			_state.set(StateVariable.SINCE_LAST_TICK, valueNow);
		}

		return localResult;
	}

	private boolean distributedTickPostprocessingIsCombinedWith(boolean endOfTimestep) {
		boolean sinceLastTick = _state.get(StateVariable.SINCE_LAST_TICK);

		if (!sinceLastTick) {
			long lastTick = _mechanism.getLastTick();
			sinceLastTick = _pdp.getDistributionManager().wasTrueInBetween(this, lastTick, lastTick + _mechanism.getTimestepSize());
			_state.set(StateVariable.SINCE_LAST_TICK, sinceLastTick);
		}

		return sinceLastTick;
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
			boolean sinceLastTick = _state.get(StateVariable.SINCE_LAST_TICK);

			if (!sinceLastTick && _pdp.getPip().evaluatePredicateCurrentState(_predicate)) {
				_state.set(StateVariable.SINCE_LAST_TICK, true);
				setChanged();
				notifyObservers(_state);
			}

			_logger.debug("Updating with event {}. Result: {}.", arg, _state.get(StateVariable.SINCE_LAST_TICK));
		}
	}

	private void updateIsNotIn(Observable o, Object arg) {
		// TODO Auto-generated method stub

	}

	private void updateIsOnlyIn(Observable o, Object arg) {
		// TODO Auto-generated method stub

	}

	public enum EStateBasedOperatorType {
		IS_COMBINED_WITH,
		IS_ONLY_IN,
		IS_NOT_IN
	}

	public EStateBasedOperatorType getType() {
		return _type;
	}

	public boolean is(EStateBasedOperatorType t) {
		return _type == t;
	}

	public boolean getSinceLastTick() {
		return _state.get(StateVariable.SINCE_LAST_TICK);
	}
}
