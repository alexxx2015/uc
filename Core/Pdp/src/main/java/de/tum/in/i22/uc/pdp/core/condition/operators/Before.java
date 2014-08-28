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

	public Before() {
	}

	@Override
	public void init(Mechanism mech) {
		super.init(mech);

		op = ((Operator) operators);

		_timeAmount = new TimeAmount(amount, unit, mech.getTimestepSize());
		_state.circArray = new CircularArray<Boolean>(_timeAmount.getTimestepInterval());
		for (int a = 0; a < _timeAmount.getTimestepInterval(); a++) {
			_state.circArray.set(false, a);
		}
		op.init(mech);
	}

	@Override
	int initId(int id) {
		_id = op.initId(id) + 1;
		setFullId(_id);
		_logger.debug("My [{}] id is {}.", this, getFullId());
		return _id;
	}

	@Override
	public String toString() {
		return "BEFORE (" + _timeAmount + ", " + op + " )";
	}

	@Override
	public boolean evaluate(IEvent curEvent) {
		// before = at (currentTime - interval) operand was true
		_logger.debug("circularArray: {}", _state.circArray);

		// Look at the first entry of the array. The retrieved value
		// corresponds to the result of the evaluation at this point in time.
		boolean newStateValue = _state.circArray.peek();

		if (curEvent == null) {
			// If we are evaluating at the end of a timestep, then
			// (1) remove the first entry
			// (2) evaluate the internal operand at this point in time
			//     and push the result to the array
			_state.circArray.pop();
			_state.circArray.push(op.evaluate(null));

			_logger.debug("circularArray: {}", _state.circArray);
		}

		if (newStateValue != _state.value) {
			_state.value = newStateValue;
			setChanged();
			notifyObservers(_state);
		}

		_logger.debug("eval BEFORE [{}]", _state.value);
		return _state.value;
	}
}
