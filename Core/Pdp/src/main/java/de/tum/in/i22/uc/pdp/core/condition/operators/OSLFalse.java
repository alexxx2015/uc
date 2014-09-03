package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.LiteralOperator;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.FalseType;

public class OSLFalse extends FalseType implements LiteralOperator {
	private static Logger _logger = LoggerFactory.getLogger(OSLFalse.class);

	public OSLFalse() {
		_valueAtLastTick = false;
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
	protected boolean localEvaluation(IEvent curEvent) {
		_logger.debug("eval FALSE");
		return false;
	}

	@Override
	public boolean isPositive() {
		return false;
	}

	@Override
	protected boolean tick() {
		return false;
	}
}
