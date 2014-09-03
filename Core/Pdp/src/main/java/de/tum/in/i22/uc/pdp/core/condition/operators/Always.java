package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.AlwaysType;

public class Always extends AlwaysType {
	private static Logger _logger = LoggerFactory.getLogger(Always.class);

	private Operator op;

	private boolean _immutable = false;
	private boolean _value = false;

	public Always() {
	}

	@Override
	protected void init(Mechanism mech, Operator parent, long ttl) {
		super.init(mech, parent, ttl);
		op = ((Operator) operators);
		op.init(mech, this, ttl);
	}

	@Override
	protected int initId(int id) {
		return setId(op.initId(id) + 1);
	}

	@Override
	public String toString() {
		return "ALWAYS (" + op + ")";
	}

	@Override
	protected boolean localEvaluation(IEvent ev) {
		// If immutable, then there is nothing to do. State remains as-is.
		if (!_immutable) {
			_value = op.evaluate(ev);

			// if state turns false, then the formula will always be violated from now on.
			if (!_value && ev == null) {
				_logger.debug("evaluating ALWAYS: activating IMMUTABILITY");
				_immutable = true;
			}
		}

		return _value;
	}
}
