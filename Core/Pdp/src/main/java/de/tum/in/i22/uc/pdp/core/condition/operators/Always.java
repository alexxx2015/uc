package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.AlwaysType;

public class Always extends AlwaysType {
	private static Logger _logger = LoggerFactory.getLogger(Always.class);

	private Operator op;

	public Always() {
	}

	@Override
	public void init(Mechanism mech) {
		super.init(mech);
		op = ((Operator) operators);
		op.init(mech);
	}

//	@Override
//	int initId(int id) {
//		_id = op.initId(id) + 1;
//		setFullId(_id);
//		_logger.debug("My [{}] id is {}.", this, getFullId());
//		return _id;
//	}

	@Override
	public String toString() {
		return "ALWAYS (" + op + ")";
	}

	@Override
	public boolean evaluate(IEvent curEvent) {
		// If immutable, then there is nothing to do. State remains as-is.
		if (!_state.immutable) {
			boolean newStateValue = op.evaluate(curEvent);
			boolean newStateImmutable = false;

			// if state turns false, then the formula will always be violated from now on.
			if (!newStateValue && curEvent == null) {
				_logger.debug("evaluating ALWAYS: activating IMMUTABILITY");
				newStateImmutable = true;
			}

//			if (newStateValue != _state.value || newStateImmutable != _state.immutable) {
//				_state.value = newStateValue;
//				_state.immutable = newStateImmutable;
//				setChanged();
//				notifyObservers(_state);
//			}

			_state.value = newStateValue;
			_state.immutable = newStateImmutable;
		}

		_logger.debug("eval ALWAYS [{}]", _state.value);
		return _state.value;
	}
}
