package de.tum.in.i22.uc.pdp.core.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.Trilean;
import de.tum.in.i22.uc.cm.datatypes.interfaces.AtomicOperator;
import de.tum.in.i22.uc.cm.datatypes.interfaces.EOperatorType;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.core.exceptions.InvalidOperatorException;
import de.tum.in.i22.uc.pdp.core.operators.State.StateVariable;
import de.tum.in.i22.uc.pdp.xsd.TrueType;

public class OSLTrue extends TrueType implements AtomicOperator {
	private static Logger _logger = LoggerFactory.getLogger(OSLTrue.class);

	public OSLTrue() {
	}

	@Override
	protected void init(Mechanism mech, Operator parent, long ttl) throws InvalidOperatorException {
		super.init(mech, parent, ttl);

		_positivity = Trilean.TRUE;
		_state.set(StateVariable.VALUE_AT_LAST_TICK, true);
	}

	@Override
	public String toString() {
		return "TRUE";
	}

	@Override
	public boolean tick(boolean endOfTimestep) {
		_logger.info("Evaluating TRUE. true.");
		return true;
	}

	@Override
	public boolean getValueAtLastTick() {
		return true;
	}

	@Override
	public EOperatorType getOperatorType() {
		return EOperatorType.OSL_TRUE;
	}

	@Override
	public void update(IEvent event) {
	}

	@Override
	public boolean isDNF() {
		return true;
	}

	@Override
	public boolean isAtomic() {
		return true;
	}
}
