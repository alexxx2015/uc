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

	private boolean _value = false;
	private boolean _immutable = false;
	private boolean _A_wasTrue = false;
	private boolean _subEverTrue = false;

	public Since() {
	}

	@Override
	protected void init(Mechanism mech, Operator parent, long ttl) {
		super.init(mech, parent, ttl);

		op1 = (Operator) operators.get(0);
		op2 = (Operator) operators.get(1);

		op1.init(mech, this, ttl);
		op2.init(mech, this, ttl);
	}

	@Override
	protected int initId(int id) {
		_id = op1.initId(id) + 1;
		setFullId(_id);
		_logger.debug("My [{}] id is {}.", this, getFullId());
		return op2.initId(_id);
	}

	@Override
	public String toString() {
		return "SINCE (" + op1 + ", " + op2 + " )";
	}

	@Override
	protected boolean localEvaluation(IEvent ev) { // A occurs, SINCE is satisfied
												// (LTL doesn't state anything
												// about B in the timestep when
												// A happens)
		boolean operand1state = op1.evaluate(ev);
		boolean operand2state = op2.evaluate(ev);

		if (operand1state) {
			_logger.debug("[SINCE] Subformula A satisfied this timestep => TRUE");
			_value = true;
		} else {
			if (!_immutable) {
				/*
				 * until now B occurred every following timestep
				 */

				if (_A_wasTrue) {
					_logger.debug("[SINCE] Subformula A was satisfied any previous timestep");

					_value = operand2state;
					_logger.debug("[SINCE] Subformula B is " + (operand2state ? "" : "NOT") + " satisfied this timestep.");
				} else {
					_logger.debug("[SINCE] Subformula A NOT satisfied this timestep or any previous timestep");
					_logger.debug("[SINCE] Not yet immutable; check (ALWAYS B) part of since");

					_value = operand2state;
					_logger.debug("[SINCE] Subformula B is " + (operand2state ? "" : "NOT") + " satisfied this timestep.");
				}
			}
		}

		if (ev == null) {
			if (!_value) {
				if (!_immutable) { // immutable until next occurence
												// of subformula A
					_logger.debug("[SINCE] Evaluating current state value was FALSE =>  activating IMMUTABILITY");
					_immutable = true;
				}
			}

			if (operand1state) {
				_logger.debug("[SINCE] Subformula A satisfied this timestep.");
				_A_wasTrue = true;
				if (_immutable) {
					_logger.debug("[SINCE] Deactivating immutability");
					_immutable = false;
				}
			}

			if (!_subEverTrue && !operand2state) {
				_logger.debug("[SINCE] Subformula B was previously always satisfied, but NOT this timestep => 2nd part of since can never be satisfied any more");
				_logger.debug("[SINCE] Setting subEverFalse flag and activating immutability");
				_subEverTrue = true; // intention here subformula was
												// ever FALSE (in contrast to
												// name...)
				_immutable = true;
			}
		}

		return _value;
	}
}
