package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.CircularArray;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.pdp.core.condition.TimeAmount;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.BeforeType;

public class Before extends BeforeType {
	private static Logger _logger = LoggerFactory.getLogger(Before.class);
	private TimeAmount _timeAmount;

	private Operator op;

	private CircularArray<Boolean> _statCircArray;

	public Before() {
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

		_statCircArray = new CircularArray<>(_timeAmount.getTimestepInterval());
		for (int a = 0; a < _timeAmount.getTimestepInterval(); a++) {
			_statCircArray.set(false, a);
		}

		op.init(mech, this, Math.max(ttl, _timeAmount.getInterval() + mech.getTimestepSize()));
	}

	@Override
	protected int initId(int id) {
		_id = op.initId(id) + 1;
		setFullId(_id);
		_logger.debug("My [{}] id is {}.", this, getFullId());
		return _id;
	}

	@Override
	public String toString() {
		return "BEFORE(" + _timeAmount + ", " + op + " )";
	}

	@Override
	protected boolean localEvaluation(IEvent ev) {
		// before = at (currentTime - interval) operand was true
		_logger.debug("circularArray: {}", _statCircArray);

		// Look at the first entry of the array. The retrieved value
		// corresponds to the result of the evaluation at this point in time.
		boolean result = _statCircArray.peek();

		if (ev == null) {
			// If we are evaluating at the end of a timestep, then
			// (1) remove the first entry
			// (2) evaluate the internal operand at this point in time
			//     and push the result to the array
			_statCircArray.pop();
			_statCircArray.push(op.evaluate(null));

			_logger.debug("circularArray: {}", _statCircArray);
		}

		return result;
	}

//	@Override
//	protected boolean distributedEvaluation(boolean resultLocalEval, IEvent ev) {
//		long lastUpdate = _mechanism.getLastUpdate();
//
//		return _pdp.getDistributionManager().wasTrueInBetween(op,
//				lastUpdate - (_mechanism.getTimestepSize() * (_timeAmount.getTimestepInterval() + 1)),
//				lastUpdate - (_mechanism.getTimestepSize() * _timeAmount.getTimestepInterval()));
//	}
}
