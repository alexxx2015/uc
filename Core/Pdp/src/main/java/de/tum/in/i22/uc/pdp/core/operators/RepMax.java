package de.tum.in.i22.uc.pdp.core.operators;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.AtomicOperator;
import de.tum.in.i22.uc.cm.datatypes.interfaces.EOperatorType;
import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.core.exceptions.InvalidOperatorException;
import de.tum.in.i22.uc.pdp.core.operators.State.StateVariable;
import de.tum.in.i22.uc.pdp.xsd.RepMaxType;

public class RepMax extends RepMaxType {
	private static Logger _logger = LoggerFactory.getLogger(RepMax.class);
	private Operator op;

	public RepMax() {
		_state.set(StateVariable.IMMUTABLE, false);
		_state.set(StateVariable.COUNTER, 0);
	}

	@Override
	protected void init(Mechanism mech, Operator parent, long ttl) throws InvalidOperatorException {
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
		return "REPMAX(" + getLimit() + "," + op + ")";
	}

	@Override
	public boolean tick(boolean endOfTimestep) {
		if ((boolean) _state.get(StateVariable.IMMUTABLE)) {
			return false;
		}
		else {
			long counter = _state.get(StateVariable.COUNTER);

			if (op.tick(endOfTimestep)) {
				counter++;
				_logger.debug("Subformula was true. Incrementing counter to {}.", counter);
			}

			boolean valueAtLastTick = (counter <= limit);

			if (!valueAtLastTick) {
				_logger.debug("[REPMAX] Activating immutability");
				_state.set(StateVariable.IMMUTABLE, true);
			}

			_state.set(StateVariable.COUNTER, counter);

			_state.set(StateVariable.VALUE_AT_LAST_TICK, valueAtLastTick);

			return valueAtLastTick;
		}
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
		op.getObservers(observers);
		return observers;
	}

	@Override
	public EOperatorType getOperatorType() {
		return EOperatorType.REPMAX;
	}

	@Override
	protected void setRelevance(boolean relevant) {
		super.setRelevance(relevant);
		op.setRelevance(relevant);
	}
}
