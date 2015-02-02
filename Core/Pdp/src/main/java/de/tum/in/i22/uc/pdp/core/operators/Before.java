package de.tum.in.i22.uc.pdp.core.operators;

import java.util.Collection;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.CircularArray;
import de.tum.in.i22.uc.cm.datatypes.interfaces.AtomicOperator;
import de.tum.in.i22.uc.cm.datatypes.interfaces.EOperatorType;
import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.core.TimeAmount;
import de.tum.in.i22.uc.pdp.core.exceptions.InvalidOperatorException;
import de.tum.in.i22.uc.pdp.core.operators.State.StateVariable;
import de.tum.in.i22.uc.pdp.xsd.BeforeType;

public class Before extends BeforeType implements Observer {
	private static Logger _logger = LoggerFactory.getLogger(Before.class);
	private TimeAmount _timeAmount;

	private Operator op;

	public Before() {
	}

	@Override
	protected void init(Mechanism mech, Operator parent, long ttl) throws InvalidOperatorException {
		super.init(mech, parent, ttl);

		_timeAmount = new TimeAmount(amount, unit, mech.getTimestepSize());

		op = ((Operator) operators);
		op.init(mech, this, Math.max(ttl, _timeAmount.getInterval() + mech.getTimestepSize()));
		op.addObserver(this);

//		if (Settings.getInstance().getDistributionEnabled()) {
//			ensureDNF();
//		}

		CircularArray<Boolean> stateCircArray = new CircularArray<>(_timeAmount.getTimesteps());
		for (int a = 0; a < _timeAmount.getTimesteps(); a++) {
			stateCircArray.set(false, a);
		}

		_state.set(StateVariable.CIRC_ARRAY, stateCircArray);

		_positivity = op.getPositivity();
	}

//	private void ensureDNF() throws InvalidOperatorException {
//		/*
//		 * The following formulas are equivalent:
//		 * - not(a) before j 	== not(a before j)
//		 * - (a and b) before j == (a before j) and (b before j)
//		 * - (a or b) before j 	== (a before j) or (b before j)
//		 *
//		 */
//		if (!(op instanceof AtomicOperator)) {
//			throw new InvalidOperatorException(
//					"Parameter 'distributionEnabled' is true, but ECA-Condition was not in disjunctive normal form (operand of "
//							+ getClass() + " was not of type " + AtomicOperator.class + ").");
//		}
//	}

	@Override
	protected int initId(int id) {
		return setId(op.initId(id) + 1);
	}

	@Override
	public String toString() {
		return "BEFORE(" + _timeAmount + ", " + op + " )";
	}

	@Override
	public boolean tick(boolean endOfTimestep) {
		CircularArray<Boolean> circArray = _state.get(StateVariable.CIRC_ARRAY);
		_logger.debug("circularArray: {}", circArray);

		if (!endOfTimestep) {
			return circArray.peek();
		}
		else {
			// The value being popped from the array corresponds to the
			// Operator's value at time (now() - timeAmount). Therefore,
			// this value is the result of the evaluation of BEFORE at this timestep.
			_state.set(StateVariable.VALUE_AT_LAST_TICK, circArray.pop());

			// Evaluate the Operator. Push the result to the array, from where
			// it will be popped when time is ripe (i.e. after timeAmount).
			circArray.push(op.tick(endOfTimestep));

			_logger.debug("Value [{}] was popped from circularArray. Result: {}. New circularArray: {}", _state.get(StateVariable.VALUE_AT_LAST_TICK), _state.get(StateVariable.VALUE_AT_LAST_TICK), circArray);

			// For the next timestep our child
			// operator is relevant again.
			op.setRelevance(true);

			return _state.get(StateVariable.VALUE_AT_LAST_TICK);
		}
	}

	@Override
	public boolean distributedTickPostprocessing(boolean endOfTimestep) {
//		CircularArray<Boolean> circArray = _state.get(StateVariable.CIRC_ARRAY);
//
//		if (!op.getPositivity().is(circArray.peekLast())) {
//			/*
//			 * If the value that we have pushed to the array is not equal to the operator's
//			 * positivity, then we need to perform a remote lookup. We when remove
//			 * the 'wrong' local evaluation result from the array and insert the correct (distributed) one.
//			 * The inserted element will be popped when time is ripe (i.e. after timeAmount).
//			 */
//			boolean distributedTickProcess = op.distributedTickPostprocessing(endOfTimestep);
//			_logger.debug("Result of distributed evaluation: {}", distributedTickProcess);
//			if (op.getPositivity().is(distributedTickProcess)) {
//				circArray.removeLast();
//				circArray.push(distributedTickProcess);
//			}
//		}
//
//		// The actual result at _this_ timestep is not changed
//		return _state.get(StateVariable.VALUE_AT_LAST_TICK);

		boolean valueAtLastTick = _state.get(StateVariable.VALUE_AT_LAST_TICK);

		if (endOfTimestep && !op.getPositivity().is(valueAtLastTick)) {
			if (op.isAtomic()) {
				valueAtLastTick = _pdp.getDistributionManager().wasNotifiedAtTimestep((AtomicOperator) op, _mechanism.getTimestep() - _timeAmount.getTimesteps());
				_state.set(StateVariable.VALUE_AT_LAST_TICK, valueAtLastTick);
			}
			else {
				boolean distributedTickProcess = op.distributedTickPostprocessing(endOfTimestep);
				if (op.getPositivity().is(distributedTickProcess)) {
					CircularArray<Boolean> circArray = _state.get(StateVariable.CIRC_ARRAY);
					circArray.removeLast();
					circArray.push(distributedTickProcess);
				}

				_logger.debug("Result of distributed evaluation: {}", distributedTickProcess);
			}
		}

		return valueAtLastTick;
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
	protected Collection<AtomicOperator> getObservers(Collection<AtomicOperator> observers) {
		op.getObservers(observers);
		return observers;
	}

	@Override
	public EOperatorType getOperatorType() {
		return EOperatorType.BEFORE;
	}

	@Override
	public boolean isAtomic() {
		return op.isAtomic();
	}

//	@Override
//	public boolean isDNF() {
//		return op.isAtomic();
//	}

	@Override
	protected void setRelevance(boolean relevant) {
		super.setRelevance(relevant);
		op.setRelevance(relevant);
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o == op) {
			/*
			 * Our child operator notifies that it has changed its state
			 * within _this_ timestep. From now on, there is no need to evaluate
			 * it any further until the end of _this_ timestep.
			 */
			op.setRelevance(false);
		}
	}
}
