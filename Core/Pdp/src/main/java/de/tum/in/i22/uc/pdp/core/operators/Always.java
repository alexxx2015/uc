package de.tum.in.i22.uc.pdp.core.operators;

import java.util.Collection;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.AtomicOperator;
import de.tum.in.i22.uc.cm.datatypes.interfaces.EOperatorType;
import de.tum.in.i22.uc.cm.distribution.Threading;
import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.core.exceptions.InvalidOperatorException;
import de.tum.in.i22.uc.pdp.core.operators.State.StateVariable;
import de.tum.in.i22.uc.pdp.xsd.AlwaysType;

public class Always extends AlwaysType {
	private static Logger _logger = LoggerFactory.getLogger(Always.class);

	private Operator op;

	public Always() {
		_state.set(StateVariable.IMMUTABLE, false);
		_state.set(StateVariable.VALUE_AT_LAST_TICK, true);
	}

	@Override
	protected void init(Mechanism mech, Operator parent, long ttl) throws InvalidOperatorException {
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


	private boolean tickIntern(Callable<Boolean> callOp) {
		if ((boolean) _state.get(StateVariable.IMMUTABLE)) {
			return false;
		}
		else {
			boolean valueAtLastTick = Threading.resultOf(Threading.instance().submit(callOp));

			if (!valueAtLastTick) {
				_state.set(StateVariable.VALUE_AT_LAST_TICK, false);
				_logger.debug("ALWAYS: activating IMMUTABILITY");
				_state.set(StateVariable.IMMUTABLE, true);
			}

			return valueAtLastTick;
		}
	}


	@Override
	public boolean tick(boolean endOfTimestep) {
		return tickIntern(() -> op.tick(endOfTimestep));
	}

	@Override
	public boolean distributedTickPostprocessing(boolean endOfTimestep) {
		return tickIntern(() -> op.distributedTickPostprocessing(endOfTimestep));
	}

	@Override
	public void startSimulation() {
		super.startSimulation();
		op.startSimulation();
	}

	@Override
	public void stopSimulation() {
		super.stopSimulation();
		op.stopSimulation();
	}

	@Override
	protected Collection<AtomicOperator> getObservers(Collection<AtomicOperator> observers) {
		return op.getObservers(observers);
	}

	@Override
	public EOperatorType getOperatorType() {
		return EOperatorType.ALWAYS;
	}

	@Override
	public boolean isAtomic() {
		return op.isAtomic();
	}

	@Override
	public boolean isDNF() {
		return op.isAtomic();
	}
}
