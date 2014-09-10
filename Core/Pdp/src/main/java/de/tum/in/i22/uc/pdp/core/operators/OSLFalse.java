package de.tum.in.i22.uc.pdp.core.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.LiteralOperator;
import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.core.operators.State.StateVariable;
import de.tum.in.i22.uc.pdp.xsd.FalseType;

public class OSLFalse extends FalseType implements LiteralOperator {
	private static Logger _logger = LoggerFactory.getLogger(OSLFalse.class);

	public OSLFalse() {
		_state.set(StateVariable.VALUE_AT_LAST_TICK, false);
	}

	@Override
	protected void init(Mechanism mech, Operator parent, long ttl) {
		super.init(mech, parent, ttl);
	}

	@Override
	public String toString() {
		return "FALSE";
	}

	@Override
	public boolean isPositive() {
		return false;
	}

	@Override
	public boolean tick() {
		_logger.info("Evaluating FALSE. false.");
		return false;
	}
}
