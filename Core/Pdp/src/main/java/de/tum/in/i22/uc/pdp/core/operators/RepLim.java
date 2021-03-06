package de.tum.in.i22.uc.pdp.core.operators;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.CircularArray;
import de.tum.in.i22.uc.cm.datatypes.interfaces.AtomicOperator;
import de.tum.in.i22.uc.cm.datatypes.interfaces.EOperatorType;
import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.core.TimeAmount;
import de.tum.in.i22.uc.pdp.core.exceptions.InvalidOperatorException;
import de.tum.in.i22.uc.pdp.core.operators.State.StateVariable;
import de.tum.in.i22.uc.pdp.xsd.RepLimType;

public class RepLim extends RepLimType {
	private static Logger _logger = LoggerFactory.getLogger(RepLim.class);
	private TimeAmount _timeAmount;
	private Operator op;

	public RepLim() {
	}

	@Override
	protected void init(Mechanism mech, Operator parent, long ttl) throws InvalidOperatorException {
		super.init(mech, parent, ttl);

		_timeAmount = new TimeAmount(amount, unit, mech.getTimestepSize());

		op = (Operator) operators;
		op.init(mech, this, Math.max(ttl, _timeAmount.getInterval() + mech.getTimestepSize()));

		CircularArray<Boolean> circArray = new CircularArray<>(_timeAmount.getTimestepInterval());
		for (int a = 0; a < _timeAmount.getTimestepInterval(); a++) {
			circArray.set(false, a);
		}

		_state.set(StateVariable.CIRC_ARRAY, circArray);
		_state.set(StateVariable.COUNTER, 0L);
	}

	@Override
	protected int initId(int id) {
		return setId(op.initId(id) + 1);
	}

	@Override
	public String toString() {
		return "REPLIM(" + _timeAmount + ", " + op + " )";
	}

	@Override
	public boolean tick(boolean endOfTimestep) {
		CircularArray<Boolean> circArray = _state.get(StateVariable.CIRC_ARRAY);
		long counter = _state.get(StateVariable.COUNTER);

		_logger.debug("circularArray: {}", circArray);

		boolean result = counter >= lowerLimit && counter <= upperLimit;

		if (circArray.pop()) {
			counter--;
			_logger.debug("Oldest value was true. Removing it. Therefore also decrementing counter to {}.", counter);
		}

		boolean operandState = op.tick(endOfTimestep);
		if (operandState) {
			counter++;
			_logger.debug("Current value is true. Therefore incrementing counter to {}.", counter);
		}

		circArray.push(operandState);
		_logger.debug("circularArray: {}", circArray);

		// Setting circArray is actually not necessary,
		// as we work on the original instance anyway
		// _state.set(StateVariable.CIRC_ARRAY, circArray);

		_state.set(StateVariable.VALUE_AT_LAST_TICK, result);

		return result;
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
		return EOperatorType.REPLIM;
	}

	@Override
	public boolean isAtomic() {
		return op.isAtomic();
	}

	@Override
	public boolean isDNF() {
		return op.isAtomic();
	}

	@Override
	protected void setRelevant(boolean relevant) {
		super.setRelevant(relevant);
		op.setRelevant(relevant);
	}
}
