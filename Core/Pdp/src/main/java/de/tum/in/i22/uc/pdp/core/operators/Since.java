package de.tum.in.i22.uc.pdp.core.operators;

import java.util.Collection;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.AtomicOperator;
import de.tum.in.i22.uc.cm.datatypes.interfaces.EOperatorType;
import de.tum.in.i22.uc.cm.distribution.Threading;
import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.core.exceptions.InvalidOperatorException;
import de.tum.in.i22.uc.pdp.core.operators.State.StateVariable;
import de.tum.in.i22.uc.pdp.xsd.SinceType;

public class Since extends SinceType {
	private static Logger _logger = LoggerFactory.getLogger(Since.class);

	private Operator opA;
	private Operator opB;

	public Since() {
		_state.set(StateVariable.ALWAYS_A, true);
		_state.set(StateVariable.ALWAYS_A_SINCE_LAST_B, false);
	}

	@Override
	protected void init(Mechanism mech, Operator parent, long ttl) throws InvalidOperatorException {
		super.init(mech, parent, ttl);

		opA = (Operator) operators.get(0);
		opB = (Operator) operators.get(1);

		opA.init(mech, this, ttl);
		opB.init(mech, this, ttl);
	}

	@Override
	protected int initId(int id) {
		setId(opA.initId(id) + 1);
		return opB.initId(getId());
	}

	@Override
	public String toString() {
		return "SINCE (" + opA + ", " + opB + " )";
	}

	@Override
	public boolean tick(boolean endOfTimestep) {
		// A since B
		boolean stateA = opA.tick(endOfTimestep);
		boolean stateB = opB.tick(endOfTimestep);

		boolean valueAtLastTick = _state.get(StateVariable.VALUE_AT_LAST_TICK);

		if (!stateA) {
			_state.set(StateVariable.ALWAYS_A, false);
		}

		if ((boolean) _state.get(StateVariable.ALWAYS_A)) {
			valueAtLastTick = true;
			_logger.debug("A was always true. Result: true.");
		}
		else {
			boolean alwaysASinceLastB = _state.get(StateVariable.ALWAYS_A_SINCE_LAST_B);

			if (stateB) {
				alwaysASinceLastB = true;
				_logger.debug("B is happening at this timestep. Result: true.");
			}
			else {
				alwaysASinceLastB = alwaysASinceLastB && stateA;
				_logger.debug("A was {}always true since last B happened. Result: {}.", alwaysASinceLastB ? "": "NOT ", alwaysASinceLastB);
			}

			valueAtLastTick = alwaysASinceLastB;
			_state.set(StateVariable.ALWAYS_A_SINCE_LAST_B, alwaysASinceLastB);
		}

		_state.set(StateVariable.VALUE_AT_LAST_TICK, valueAtLastTick);

		return valueAtLastTick;
	}

	@Override
	public boolean distributedTickPostprocessing(boolean endOfTimestep) {
		Future<Boolean> opAFuture = null;
		Future<Boolean> opBFuture = null;

		if (!opA.getPositivity().is(opA.getValueAtLastTick())) {
			_logger.info("Performing distributed evaluation of A.");
			opAFuture = (Future<Boolean>) Threading.instance().submit(() -> opA.distributedTickPostprocessing(endOfTimestep));
		}

		if (!opB.getPositivity().is(opB.getValueAtLastTick())) {
			_logger.info("Performing distributed evaluation of B.");
			opBFuture = (Future<Boolean>) Threading.instance().submit(() -> opB.distributedTickPostprocessing(endOfTimestep));
		}

		boolean valueAtLastTick;
		if (opAFuture == null && opBFuture == null) {
			/*
			 * Local evaluation was sufficient.
			 */
			_logger.info("No distributed evaluation needed.");
			valueAtLastTick = _state.get(StateVariable.VALUE_AT_LAST_TICK);
		}
		else {
			boolean stateA = (opAFuture == null ? opA.getValueAtLastTick() : Threading.resultOf(opAFuture));
			boolean stateB;

			if (!stateA) {
				_state.set(StateVariable.ALWAYS_A, false);
			}

			if ((boolean) _state.get(StateVariable.ALWAYS_A)) {
				valueAtLastTick = true;
				_logger.debug("A was always true. Result: true.");
			}
			else {
				stateB = (opBFuture == null ? opB.getValueAtLastTick() : Threading.resultOf(opBFuture));
				boolean alwaysASinceLastB = _state.get(StateVariable.ALWAYS_A_SINCE_LAST_B);

				if (stateB) {
					alwaysASinceLastB = true;
					_logger.debug("B is happening at this timestep. Result: true.");
				}
				else {
					alwaysASinceLastB = alwaysASinceLastB && stateA;
					_logger.debug("A was {}always true since last B happened. Result: {}.", alwaysASinceLastB ? "": "NOT ", alwaysASinceLastB);
				}

				valueAtLastTick = alwaysASinceLastB;
				_state.set(StateVariable.ALWAYS_A_SINCE_LAST_B, alwaysASinceLastB);
			}
		}

		_state.set(StateVariable.VALUE_AT_LAST_TICK, valueAtLastTick);

		return valueAtLastTick;
	}

	@Override
	public void startSimulation() {
		super.startSimulation();
		opA.startSimulation();
		opB.startSimulation();
	}

	@Override
	public void stopSimulation() {
		super.stopSimulation();
		opA.stopSimulation();
		opB.stopSimulation();
	}

	@Override
	public Collection<AtomicOperator>  getObservers(Collection<AtomicOperator> observers) {
		opA.getObservers(observers);
		opB.getObservers(observers);
		return observers;
	}

	@Override
	public EOperatorType getOperatorType() {
		return EOperatorType.SINCE;
	}

	@Override
	public boolean isAtomic() {
		return false;
	}
//
//	@Override
//	public boolean isDNF() {
//		return true;
//	}

	@Override
	protected void setRelevance(boolean relevant) {
		super.setRelevance(relevant);
		opA.setRelevance(relevant);
		opB.setRelevance(relevant);
	}
}
