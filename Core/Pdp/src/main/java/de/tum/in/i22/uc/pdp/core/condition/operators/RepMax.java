package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.RepMaxType;

public class RepMax extends RepMaxType {
	private static Logger _logger = LoggerFactory.getLogger(RepMax.class);
	private Operator op;

	boolean _value = false;
	boolean _immutable = false;
	long _stateCounter = 0;

	public RepMax() {
	}

	@Override
	protected void init(Mechanism mech, Operator parent, long ttl) {
		super.init(mech, parent, ttl);
		op = (Operator) operators;
		op.init(mech, this, ttl);
	}

	@Override
	protected int initId(int id) {
		return setId(op.initId(id) + 1);
	}

	@Override
	public String toString() {
		return "REPMAX (" + getLimit() + ", " + op + ")";
	}

	@Override
	protected boolean localEvaluation(IEvent ev) {
		if (!_immutable) {
			if (ev != null && op.evaluate(ev)) {
				/*
				 * Evaluating in the presence of an event
				 * and the operator evaluated to true
				 */
				_stateCounter++;
				_logger.debug("Subformula was true. Incrementing counter to {}.", _stateCounter);
			}

			_value = (_stateCounter <= limit);

			if (ev == null && !_value) {
				/*
				 * Evaluating at the end of a timestep and
				 * the counter reached its limit.
				 */
				_logger.debug("[REPMAX] Activating immutability");
				_immutable = true;
			}
		}

		return _value;
	}
}
