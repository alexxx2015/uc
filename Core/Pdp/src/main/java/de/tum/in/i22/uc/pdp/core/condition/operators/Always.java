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

	@Override
	int initId(int id) {
		_id = op.initId(id) + 1;
		_logger.debug("My [{}] id is {}.", this, _id);
		return _id;
	}

	@Override
	public String toString() {
		return "ALWAYS (" + op + ")";
	}

	@Override
	public boolean evaluate(IEvent curEvent) {
		if (!_state.immutable) {
			_state.value = op.evaluate(curEvent);
			if (!_state.value && curEvent == null) {
				_logger.debug("evaluating ALWAYS: activating IMMUTABILITY");
				_state.immutable = true;
			}
		}
		_logger.debug("eval ALWAYS [{}]", _state.value);
		return _state.value;
	}
}
