package de.tum.in.i22.uc.pdp.core.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.Trilean;
import de.tum.in.i22.uc.cm.datatypes.interfaces.AtomicOperator;
import de.tum.in.i22.uc.cm.datatypes.interfaces.EOperatorType;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.FalseType;

public class OSLFalse extends FalseType implements AtomicOperator {
	private static Logger _logger = LoggerFactory.getLogger(OSLFalse.class);

	public OSLFalse() {
	}

	@Override
	protected void init(Mechanism mech, Operator parent, long ttl) {
		super.init(mech, parent, ttl);

		_positivity = Trilean.FALSE;
	}

	@Override
	public String toString() {
		return "FALSE";
	}

	@Override
	public boolean tick(boolean endOfTimestep) {
		_logger.info("Evaluating FALSE. false.");
		return false;
	}

	@Override
	public boolean getValueAtLastTick() {
		return false;
	}

	@Override
	public EOperatorType getOperatorType() {
		return EOperatorType.OSL_FALSE;
	}

	@Override
	public void update(IEvent event) {
	}
}
