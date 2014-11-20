package de.tum.in.i22.uc.pdp.core.operators;

import java.util.Collection;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.Trilean;
import de.tum.in.i22.uc.cm.datatypes.interfaces.AtomicOperator;
import de.tum.in.i22.uc.cm.datatypes.interfaces.EOperatorType;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pdp.PdpThreading;
import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.core.operators.State.StateVariable;
import de.tum.in.i22.uc.pdp.xsd.AndType;

public class OSLAnd extends AndType {
	private static Logger _logger = LoggerFactory.getLogger(OSLAnd.class);

	private Operator op1;
	private Operator op2;

	private ExecutorCompletionService<Boolean> _executorCompletionService;

	public OSLAnd() {
	}

	@Override
	protected void init(Mechanism mech, Operator parent, long ttl) {
		super.init(mech, parent, ttl);

		op1 = (Operator) operators.get(0);
		op2 = (Operator) operators.get(1);

		if (Settings.getInstance().getDistributionEnabled()) {
			ensureDNF();
		}

		op1.init(mech, this, ttl);
		op2.init(mech, this, ttl);

		_executorCompletionService = new ExecutorCompletionService<>(PdpThreading.instance());

		_positivity = (op1.getPositivity() == op2.getPositivity()) ? op1.getPositivity() : Trilean.UNDEF;
	}

	@Override
	protected int initId(int id) {
		setId(op1.initId(id) + 1);
		return op2.initId(getId());
	}

	@Override
	public String toString() {
		return op1 + " && " + op2;
	}

	@Override
	public boolean tick(boolean endOfTimestep) {

		Future<Boolean> op1Future = _executorCompletionService.submit(() -> op1.tick(endOfTimestep));
		Future<Boolean> op2Future = _executorCompletionService.submit(() -> op2.tick(endOfTimestep));

		boolean valueAtLastTick = PdpThreading.takeResult(_executorCompletionService);

		if (valueAtLastTick) {
			valueAtLastTick = PdpThreading.takeResult(_executorCompletionService);
		}
		else {
			PdpThreading.instance().submit(() -> PdpThreading.take(_executorCompletionService));
		}

		_logger.info("op1: {}; op2: {}. Result: {}", PdpThreading.resultOf(op1Future), PdpThreading.resultOf(op2Future), valueAtLastTick);

		_state.set(StateVariable.VALUE_AT_LAST_TICK, valueAtLastTick);

		return valueAtLastTick;

//		Future<Boolean> op1state = PdpThreading.instance().submit(() -> op1.tick(endOfTimestep));
//		Future<Boolean> op2state = PdpThreading.instance().submit(() -> op2.tick(endOfTimestep));
//
//		boolean valueAtLastTick = PdpThreading.resultOf(op1state) && PdpThreading.resultOf(op2state);
//
//		_logger.info("op1: {}; op2: {}. Result: {}", PdpThreading.resultOf(op1state), PdpThreading.resultOf(op2state), valueAtLastTick);
//
//		_state.set(StateVariable.VALUE_AT_LAST_TICK, valueAtLastTick);
//
//		return valueAtLastTick;

//		/*
//		 * Important: _Always_ evaluate both operators
//		 */
//		boolean op1state = op1.tick(endOfTimestep);
//		boolean op2state = op2.tick(endOfTimestep);
//
//		boolean valueAtLastTick = op1state && op2state;
//
//		_logger.info("op1: {}; op2: {}. Result: {}", op1state, op2state, valueAtLastTick);
//
//		_state.set(StateVariable.VALUE_AT_LAST_TICK, valueAtLastTick);
//
//		return valueAtLastTick;
	}

	@Override
	public boolean distributedTickPostprocessing(boolean endOfTimestep) {

		/*
		 * TODO parallelize
		 */
		boolean op1state = op1.distributedTickPostprocessing(endOfTimestep);
		boolean op2state = op2.distributedTickPostprocessing(endOfTimestep);

		boolean valueAtLastTick = op1state && op2state;

		_logger.info("op1: {}; op2: {}. Result: {}", op1state, op2state, valueAtLastTick);

		_state.set(StateVariable.VALUE_AT_LAST_TICK, valueAtLastTick);

		return valueAtLastTick;
	}

	/**
	 * If distribution is enabled, then conditions must be in DNF (cf. CANS 2014 paper).
	 * This method checks whether the operands of AND(.,.) are AND, NOT, or a Literal.
	 * If this is not the case, an IllegalStateException is thrown.
	 *
	 * @throws IllegalStateException if this object is not in DNF.
	 */
	private void ensureDNF() throws IllegalArgumentException {
		if (!(op1 instanceof OSLAnd) && !(op1 instanceof OSLNot) && !(op1 instanceof AtomicOperator)) {
			throw new IllegalStateException("Parameter 'distributionEnabled' is true, but ECA-Condition was not in disjunctive normal form (first operand of "
						+ getClass() + " was of type " + op1.getClass() + ").");
		}
		if (!(op2 instanceof OSLAnd) && !(op2 instanceof OSLNot) && !(op2 instanceof AtomicOperator)) {
			throw new IllegalStateException("Parameter 'distributionEnabled' is true, but ECA-Condition was not in disjunctive normal form (second operand of "
					+ getClass() + " was of type " + op2.getClass() + ").");
		}
	}

	@Override
	public void startSimulation() {
		super.startSimulation();
		op1.startSimulation();
		op2.startSimulation();
	}

	@Override
	public void stopSimulation() {
		super.stopSimulation();
		op1.stopSimulation();
		op2.stopSimulation();
	}

	@Override
	public Collection<AtomicOperator> getObservers(Collection<AtomicOperator> observers) {
		op1.getObservers(observers);
		op2.getObservers(observers);
		return observers;
	}

	@Override
	public EOperatorType getOperatorType() {
		return EOperatorType.OSL_AND;
	}
}
