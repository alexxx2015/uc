package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.RepSinceType;

public class RepSince extends RepSinceType {
	private static Logger _logger = LoggerFactory.getLogger(RepSince.class);

	private Operator op1;
	private Operator op2;

	public RepSince() {
	}

	@Override
	public void init(Mechanism mech) {
		super.init(mech);
		op1 = (Operator) operators.get(0);
		op2 = (Operator) operators.get(1);
	}

	@Override
	int initId(int id) {
		_id = op1.initId(id) + 1;
		_logger.debug("My [{}] id is {}.", this, _id);
		return op2.initId(_id);
	}


	@Override
	public String toString() {
		return "REPSINCE (" + this.getLimit() + ", " + op1 + ", " + op2 + " )";
	}

	@Override
	public boolean evaluate(IEvent curEvent) { // repsince(n, A, B); // n = limit
												// / A = op1 / B = op2
												// B(n) >= limit n times
												// subformula B since the last
												// occurrence of subformula A
		Boolean operand1state = op1.evaluate(curEvent);
		Boolean operand2state = op2.evaluate(curEvent);

		if (operand1state) {
			_logger.debug("[REPSINCE] Subformula A satisfied this timestep => TRUE");
			this._state.value = true;
		} else {
			long limitComparison = this._state.counter + (operand2state ? 1 : 0);
			_logger.debug("[REPSINCE] Counter for subformula B [{}]", limitComparison);

			if (this._state.subEverTrue) {
				_logger.debug("[REPSINCE] Subformula A was satisfied any previous timestep");
				if (limitComparison <= this.getLimit()) {
					_logger.debug("[REPSINCE] Amount of occurrences of subformula B <= limit ==> TRUE");
					this._state.value = true;
				} else {
					_logger.debug("[REPSINCE] Occurrence limitation exceeded! ==> FALSE");
					this._state.value = false;
				}
			} else {
				_logger.debug("[REPSINCE] Subformula A NOT satisfied this timestep or any previous timestep");
				if (limitComparison <= this.getLimit()) {
					_logger.debug("[REPSINCE] Global amount of occurrences of subformula B <= limit ==> TRUE");
					this._state.value = true;
				} else {
					_logger.debug("[REPSINCE] Global occurrence limitation exceeded! ==> FALSE");
					this._state.value = false;
				}

			}
		}

		if (curEvent == null) {
			if (operand1state) {
				_logger.debug("[REPSINCE] Subformula A satisfied this timestep => setting flag and resetting counter");
				this._state.subEverTrue = true;

				this._state.counter = 0;
				_logger.debug("[REPSINCE] Counter for subformula B [{}]", this._state.counter);
			}

			if (operand2state) {
				this._state.counter++;
				_logger.debug("[REPSINCE] Counter for subformula B [{}]", this._state.counter);
			}
		}

		_logger.debug("eval REPSINCE [{}]", this._state.value);
		return this._state.value;
	}
}
