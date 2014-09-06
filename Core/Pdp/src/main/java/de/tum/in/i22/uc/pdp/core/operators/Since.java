package de.tum.in.i22.uc.pdp.core.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.SinceType;

public class Since extends SinceType {
	private static Logger _logger = LoggerFactory.getLogger(Since.class);

	private Operator op1;
	private Operator op2;

	private boolean _alwaysASinceLastB = false;
	private boolean _alwaysA = true;

	private boolean _backupAlwaysASinceLastB;
	private boolean _backupAlwaysA;

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
		setId(op1.initId(id) + 1);
		return op2.initId(getId());
	}

	@Override
	public String toString() {
		return "SINCE (" + op1 + ", " + op2 + " )";
	}

//	@Override
//	protected boolean localEvaluation(IEvent ev) { // A occurs, SINCE is satisfied
//												// (LTL doesn't state anything
//												// about B in the timestep when
//												// A happens)
//		boolean operand1state = op1.evaluate(ev);
//		boolean operand2state = op2.evaluate(ev);
//
//		if (operand1state) {
//			_logger.debug("[SINCE] Subformula A satisfied this timestep => TRUE");
//			_value = true;
//		} else {
//			if (!_immutable) {
//				/*
//				 * until now B occurred every following timestep
//				 */
//
//				if (_A_wasTrue) {
//					_logger.debug("[SINCE] Subformula A was satisfied any previous timestep");
//
//					_value = operand2state;
//					_logger.debug("[SINCE] Subformula B is " + (operand2state ? "" : "NOT") + " satisfied this timestep.");
//				} else {
//					_logger.debug("[SINCE] Subformula A NOT satisfied this timestep or any previous timestep");
//					_logger.debug("[SINCE] Not yet immutable; check (ALWAYS B) part of since");
//
//					_value = operand2state;
//					_logger.debug("[SINCE] Subformula B is " + (operand2state ? "" : "NOT") + " satisfied this timestep.");
//				}
//			}
//		}
//
//		if (ev == null) {
//			if (!_value) {
//				if (!_immutable) { // immutable until next occurence
//												// of subformula A
//					_logger.debug("[SINCE] Evaluating current state value was FALSE =>  activating IMMUTABILITY");
//					_immutable = true;
//				}
//			}
//
//			if (operand1state) {
//				_logger.debug("[SINCE] Subformula A satisfied this timestep.");
//				_A_wasTrue = true;
//				if (_immutable) {
//					_logger.debug("[SINCE] Deactivating immutability");
//					_immutable = false;
//				}
//			}
//
//			if (!_subEverTrue && !operand2state) {
//				_logger.debug("[SINCE] Subformula B was previously always satisfied, but NOT this timestep => 2nd part of since can never be satisfied any more");
//				_logger.debug("[SINCE] Setting subEverFalse flag and activating immutability");
//				_subEverTrue = true; // intention here subformula was
//												// ever FALSE (in contrast to
//												// name...)
//				_immutable = true;
//			}
//		}
//
//		return _value;
//	}



	@Override
	public boolean tick() {
		// A since B
		boolean stateA = op1.tick();
		boolean stateB = op2.tick();

		if (!stateA) {
			_alwaysA = false;
		}

		if (_alwaysA) {
			_valueAtLastTick = true;
			_logger.debug("A was always true. Result: {}.", _valueAtLastTick);
		}
		else {
			if (stateB) {
				_valueAtLastTick = true;
				_alwaysASinceLastB = true;
				_logger.debug("B is happening at this timestep. Result: {}.", _valueAtLastTick);
			}
			else {
				if (stateA && _alwaysASinceLastB) {
					_valueAtLastTick = true;
					_logger.debug("A was always true since last B happened. Result: {}.", _valueAtLastTick);
				}
				else {
					_valueAtLastTick = false;
					_alwaysASinceLastB = false;
					_logger.debug("A was NOT always true since last B happened. Result: {}.", _valueAtLastTick);
				}
			}
		}

		return _valueAtLastTick;
	}

	@Override
	public void startSimulation() {
		super.startSimulation();
		op1.startSimulation();
		op2.startSimulation();
		_backupAlwaysA = _alwaysA;
		_backupAlwaysASinceLastB = _alwaysASinceLastB;
	}

	@Override
	public void stopSimulation() {
		super.stopSimulation();
		op1.stopSimulation();
		op2.stopSimulation();
		_alwaysA = _backupAlwaysA;
		_alwaysASinceLastB = _backupAlwaysASinceLastB;
	}
}
