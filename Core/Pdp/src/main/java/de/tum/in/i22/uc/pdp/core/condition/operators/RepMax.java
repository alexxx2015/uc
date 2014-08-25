package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.pdp.core.condition.Operator;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.RepMaxType;

public class RepMax extends RepMaxType {
	private static Logger _logger = LoggerFactory.getLogger(RepMax.class);

	public RepMax() {
	}

	@Override
	public void init(Mechanism mech) {
		super.init(mech);
		((Operator) operators).init(mech);
	}

	@Override
	public String toString() {
		return "REPMAX (" + getLimit() + ", " + operators + ")";
	}

	@Override
	public boolean evaluate(IEvent curEvent) {
		if (!_state.immutable) {
			if (curEvent != null && ((Operator) operators).evaluate(curEvent)) {
				_state.counter++;
				_logger.debug("[REPMAX] Subformula was satisfied; counter incremented to [{}]", _state.counter);
			}

			_state.value = (_state.counter <= getLimit());

			if (curEvent == null && !_state.value) {
				_logger.debug("[REPMAX] Activating immutability");
				_state.immutable = true;
			}
		}

		_logger.debug("eval REPMAX [{}]", _state.value);
		return _state.value;
	}
}
