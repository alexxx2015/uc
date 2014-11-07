package de.tum.in.i22.uc.pdp.core.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.RepSinceType;

public class RepSince extends RepSinceType {
	private static Logger _logger = LoggerFactory.getLogger(RepSince.class);

	private Operator op1;
	private Operator op2;

//	private long _counter = 0;
//	private boolean _value = false;
//	private boolean _subEverTrue = false;

	public RepSince() {
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
		return "REPSINCE (" + getLimit() + ", " + op1 + ", " + op2 + " )";
	}

//	@Override
//	protected boolean localEvaluation(IEvent ev) { // repsince(n, A, B); // n = limit
//												// / A = op1 / B = op2
//												// B(n) >= limit n times
//												// subformula B since the last
//												// occurrence of subformula A
//		boolean operand1state = op1.evaluate(ev);
//		boolean operand2state = op2.evaluate(ev);
//
//		if (operand1state) {
//			_logger.debug("[REPSINCE] Subformula A satisfied this timestep => TRUE");
//			_value = true;
//		} else {
//			long limitComparison = _counter + (operand2state ? 1 : 0);
//
//			_value = (limitComparison <= limit);
//			_logger.debug("[REPSINCE] Subformula A was " + (_subEverTrue ? "satisfied" : "NOT satisfied this timestep or") +
//					" any previous timestep. B happened {} times.", limitComparison);
//		}
//
//		if (ev == null) {
//			if (operand1state) {
//				_subEverTrue = true;
//				_counter = 0;
//				_logger.debug("[REPSINCE] Subformula A satisfied this timestep => setting flag and resetting counter to 0.");
//			}
//
//			if (operand2state) {
//				_counter++;
//				_logger.debug("[REPSINCE] Counter for subformula B: {}.", _counter);
//			}
//		}
//
//		return _value;
//	}


	@Override
	public boolean tick() {
		// TO BE IMPLEMENTED
		return false;
	}

	@Override
	public void startSimulation() {
		super.startSimulation();
		op1.startSimulation();
		op2.startSimulation();
		throw new UnsupportedOperationException("Not yet implemented.");
	}

	@Override
	public void stopSimulation() {
		super.stopSimulation();
		op1.stopSimulation();
		op2.stopSimulation();
		throw new UnsupportedOperationException("Not yet implemented.");
	}
}
