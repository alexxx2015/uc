package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.SinceType;

public class Since extends SinceType {
	private static Logger _logger = LoggerFactory.getLogger(Since.class);

	private Operator op1;
	private Operator op2;

	public Since() {
	}

	@Override
	public void init(Mechanism mech) {
		super.init(mech);

		op1 = (Operator) operators.get(0);
		op2 = (Operator) operators.get(1);

		op1.init(mech);
		op2.init(mech);
	}

//	@Override
//	int initId(int id) {
//		_id = op1.initId(id) + 1;
//		setFullId(_id);
//		_logger.debug("My [{}] id is {}.", this, getFullId());
//		return op2.initId(_id);
//	}

	@Override
	public String toString() {
		return "SINCE (" + op1 + ", " + op2 + " )";
	}

	@Override
	protected boolean localEvaluation(IEvent curEvent) { // A occurs, SINCE is satisfied
												// (LTL doesn't state anything
												// about B in the timestep when
												// A happens)
		boolean operand1state = op1.evaluate(curEvent);
		boolean operand2state = op2.evaluate(curEvent);

		if (operand1state) {
			_logger.debug("[SINCE] Subformula A satisfied this timestep => TRUE");
			this._state.setValue(true);
		} else {
			if (!this._state.isImmutable()) { // until now B occurred every following
											// timestep

				if (this._state.getCounter() == 1) {
					_logger.debug("[SINCE] Subformula A was satisfied any previous timestep");

					if (operand2state) {
						_logger.debug("[SINCE] Subformula B is satisfied this timestep => TRUE");
						this._state.setValue(true);
					} else {
						_logger.debug("[SINCE] Subformula B NOT satisfied this timestep => FALSE");
						this._state.setValue(false);
					}
				} else {
					_logger.debug("[SINCE] Subformula A NOT satisfied this timestep or any previous timestep");
					_logger.debug("[SINCE] Not yet immutable; check (ALWAYS B) part of since");

					if (operand2state) {
						_logger.debug("[SINCE] Subformula B is satisfied this timestep => TRUE");
						this._state.setValue(true);
					} else {
						_logger.debug("[SINCE] Subformula B NOT satisfied this timestep => FALSE");
						this._state.setValue(false);
					}
				}
			}
		}

		if (curEvent == null) {
			if (!this._state.value()) {
				if (!this._state.isImmutable()) { // immutable until next occurence
												// of subformula A
					_logger.debug("[SINCE] Evaluating current state value was FALSE =>  activating IMMUTABILITY");
					this._state.setImmutable(true);
				}
			}

			if (operand1state) {
				_logger.debug("[SINCE] Subformula A satisfied this timestep => setting counter flag");
				this._state.setCounter(1);
				if (this._state.isImmutable()) {
					_logger.debug("[SINCE] Deactivating immutability");
					this._state.setImmutable(false);
				}
			}

			if (!this._state.isSubEverTrue() && !operand2state) {
				_logger.debug("[SINCE] Subformula B was previously always satisfied, but NOT this timestep => 2nd part of since can never be satisfied any more");
				_logger.debug("[SINCE] Setting subEverFalse flag and activating immutability");
				this._state.setSubEverTrue(); // intention here subformula was
												// ever FALSE (in contrast to
												// name...)
				this._state.setImmutable(true);
			}
		}

		_logger.debug("eval SINCE [{}]", this._state.value());
		return this._state.value();
	}
}
