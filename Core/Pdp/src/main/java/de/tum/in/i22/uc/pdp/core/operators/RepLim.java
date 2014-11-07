package de.tum.in.i22.uc.pdp.core.operators;

import java.util.Collection;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.CircularArray;
import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.core.TimeAmount;
import de.tum.in.i22.uc.pdp.core.operators.State.StateVariable;
import de.tum.in.i22.uc.pdp.xsd.RepLimType;

public class RepLim extends RepLimType {
	private static Logger _logger = LoggerFactory.getLogger(RepLim.class);
	private TimeAmount timeAmount = null;
	private Operator op;

	public RepLim() {
	}

	@Override
	protected void init(Mechanism mech, Operator parent, long ttl) {
		super.init(mech, parent, ttl);

		if (amount <= 0) {
			throw new IllegalArgumentException("Amount must be positive.");
		}

		timeAmount = new TimeAmount(amount, unit, mech.getTimestepSize());
		if (timeAmount.getTimestepInterval() <= 0) {
			throw new IllegalStateException("Arguments must result in a positive timestepValue.");
		}

		CircularArray<Boolean> circArray = new CircularArray<>(timeAmount.getTimestepInterval());
		for (int a = 0; a < timeAmount.getTimestepInterval(); a++) {
			circArray.set(false, a);
		}

		_state.set(StateVariable.CIRC_ARRAY, circArray);
		_state.set(StateVariable.COUNTER, 0L);

		op = (Operator) operators;
		op.init(mech, this, Math.max(ttl, timeAmount.getInterval() + mech.getTimestepSize()));
	}

	@Override
	protected int initId(int id) {
		return setId(op.initId(id) + 1);
	}

	@Override
	public String toString() {
		return "REPLIM(" + timeAmount + ", " + op + " )";
	}

	@Override
	public boolean tick() {
		CircularArray<Boolean> circArray = _state.get(StateVariable.CIRC_ARRAY);
		long counter = _state.get(StateVariable.COUNTER);

		_logger.debug("circularArray: {}", circArray);

		_state.set(StateVariable.VALUE_AT_LAST_TICK, (counter >= lowerLimit && counter <= upperLimit));

		if (circArray.pop()) {
			counter--;
			_logger.debug("Oldest value was true. Removing it. Therefore also decrementing counter to {}.", counter);
		}

		boolean operandState = op.tick();
		if (operandState) {
			counter++;
			_logger.debug("Current value is true. Therefore incrementing counter to {}.", counter);
		}

		circArray.push(operandState);
		_logger.debug("circularArray: {}", circArray);

		// Setting circArray is actually not necessary,
		// as we work on the original instance anyway
		// _state.set(StateVariable.CIRC_ARRAY, circArray);

		return _state.get(StateVariable.VALUE_AT_LAST_TICK);
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
	public Collection<Observer> getObservers(Collection<Observer> observers) {
		op.getObservers(observers);
		return observers;
	}
}
