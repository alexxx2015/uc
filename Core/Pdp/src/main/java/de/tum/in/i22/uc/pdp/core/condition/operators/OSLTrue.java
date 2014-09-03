package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.LiteralOperator;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.TrueType;

public class OSLTrue extends TrueType implements LiteralOperator {
	private static Logger _logger = LoggerFactory.getLogger(OSLTrue.class);

	public OSLTrue() {
		_valueAtLastTick = true;
	}

	@Override
	protected void init(Mechanism mech, Operator parent, long ttl) {
		super.init(mech, parent, ttl);
	}

	@Override
	public String toString() {
		return "TRUE";
	}

	@Override
	protected boolean localEvaluation(IEvent curEvent) {
		_logger.debug("eval TRUE");
		return true;
	}

	@Override
	public boolean isPositive() {
		return true;
	}

	@Override
	protected boolean tick() {
		return true;
	}
}
