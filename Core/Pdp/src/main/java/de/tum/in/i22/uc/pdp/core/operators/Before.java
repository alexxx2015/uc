package de.tum.in.i22.uc.pdp.core.operators;

import java.util.ArrayDeque;
import java.util.Deque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.CircularArray;
import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.core.TimeAmount;
import de.tum.in.i22.uc.pdp.xsd.BeforeType;

public class Before extends BeforeType {
	private static Logger _logger = LoggerFactory.getLogger(Before.class);
	private TimeAmount _timeAmount;

	private Operator op;

	private CircularArray<Boolean> _stateCircArray;

	private final Deque<CircularArray<Boolean>> _backupStateCircArray;

	public Before() {
		_backupStateCircArray = new ArrayDeque<>(2);
	}

	@Override
	protected void init(Mechanism mech, Operator parent, long ttl) {
		super.init(mech, parent, ttl);

		op = ((Operator) operators);

		if (amount <= 0) {
			throw new IllegalArgumentException("Amount must be positive.");
		}

		_timeAmount = new TimeAmount(amount, unit, mech.getTimestepSize());
		if (_timeAmount.getTimestepInterval() <= 0) {
			throw new IllegalStateException("Arguments must result in a positive timestepValue.");
		}

		_stateCircArray = new CircularArray<>(_timeAmount.getTimestepInterval());
		for (int a = 0; a < _timeAmount.getTimestepInterval(); a++) {
			_stateCircArray.set(false, a);
		}

		op.init(mech, this, Math.max(ttl, _timeAmount.getInterval() + 2 * mech.getTimestepSize()));
	}

	@Override
	protected int initId(int id) {
		return setId(op.initId(id) + 1);
	}

	@Override
	public String toString() {
		return "BEFORE(" + _timeAmount + ", " + op + " )";
	}

	@Override
	public boolean tick() {
		// before = at (currentTime - interval) operand was true
		_logger.debug("circularArray: {}", _stateCircArray);

		// Retrieve the first entry of the array. The retrieved value
		// corresponds to the result of the tick().
		_valueAtLastTick = _stateCircArray.pop();
		_stateCircArray.push(op.tick());

		_logger.debug("Value [{}] was popped from circularArray. Result: {}. New circularArray: {}", _valueAtLastTick, _valueAtLastTick, _stateCircArray);

		return _valueAtLastTick;
	}

	@Override
	public void startSimulation() {
		super.startSimulation();
		op.startSimulation();
		_backupStateCircArray.addFirst(_stateCircArray.clone());
	}

	@Override
	public void stopSimulation() {
		super.stopSimulation();
		op.stopSimulation();
		_stateCircArray = _backupStateCircArray.getFirst();
	}
}
