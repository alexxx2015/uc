package de.tum.in.i22.uc.pdp.core.operators;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorCompletionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.Trilean;
import de.tum.in.i22.uc.cm.datatypes.interfaces.AtomicOperator;
import de.tum.in.i22.uc.cm.datatypes.interfaces.EOperatorType;
import de.tum.in.i22.uc.cm.distribution.Threading;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.core.exceptions.InvalidOperatorException;
import de.tum.in.i22.uc.pdp.core.operators.State.StateVariable;
import de.tum.in.i22.uc.pdp.xsd.OrType;

public class OSLOr extends OrType {
	private static Logger _logger = LoggerFactory.getLogger(OSLOr.class);

	private Operator op1;
	private Operator op2;

	private ExecutorCompletionService<Boolean> _executorCompletionService;

	public OSLOr() {
	}

	@Override
	protected void init(Mechanism mech, Operator parent, long ttl) throws InvalidOperatorException {
		super.init(mech, parent, ttl);

		op1 = (Operator) operators.get(0);
		op2 = (Operator) operators.get(1);

		if (Settings.getInstance().getDistributionEnabled()) {
			ensureDNF();
		}

		op1.init(mech, this, ttl);
		op2.init(mech, this, ttl);

		_executorCompletionService = new ExecutorCompletionService<>(Threading.instance());

		_positivity = (op1.getPositivity() == op2.getPositivity()) ? op1.getPositivity() : Trilean.UNDEF;
	}

	@Override
	protected int initId(int id) {
		setId(op1.initId(id) + 1);
		return op2.initId(getId());
	}


	@Override
	public String toString() {
		return "(" + op1 + " || " + op2 + ")";
	}


	private boolean tickIntern(Callable<Boolean> callOp1, Callable<Boolean> callOp2) {

		_executorCompletionService.submit(callOp1);
		_executorCompletionService.submit(callOp2);


		// Wait for the evaluation of the first operand
		boolean valueAtLastTick = Threading.takeResult(_executorCompletionService);

		if (valueAtLastTick) {
			// Wait for the evaluation of the second operand without blocking.
			Threading.instance().submit(() -> Threading.take(_executorCompletionService));
			_logger.info("Result: true. (One of the operands was true, not waiting for the other operand to be evaluated)");
		}
		else {
			// Wait for the evaluation of the second operand
			valueAtLastTick = Threading.takeResult(_executorCompletionService);
			_logger.info("Result: {}. (After evaluating both operands)", valueAtLastTick);
		}

		_state.set(StateVariable.VALUE_AT_LAST_TICK, valueAtLastTick);

		return valueAtLastTick;
	}


	@Override
	public boolean tick(boolean endOfTimestep) {
		return tickIntern(() -> op1.tick(endOfTimestep), () -> op2.tick(endOfTimestep));
	}

	@Override
	public boolean distributedTickPostprocessing(boolean endOfTimestep) {
		return tickIntern(
				() -> op1.distributedTickPostprocessing(endOfTimestep),
				() -> op2.distributedTickPostprocessing(endOfTimestep));
	}


	/**
	 * If distribution is enabled, then conditions must be in DNF (cf. CANS 2014 paper).
	 * This method checks whether the operands of OR(.,.) are OR, AND, NOT, or a Literal.
	 * If this is not the case, an InvalidOperatorException is thrown.
	 *
	 * @throws InvalidOperatorException if this object is not in DNF.
	 */
	private void ensureDNF() throws InvalidOperatorException {
		if (!(op1 instanceof OSLOr) && !(op1 instanceof OSLAnd) && !(op1 instanceof OSLNot) && !(op1.isAtomic())) {
			throw new InvalidOperatorException("Parameter 'distributionEnabled' is true, but ECA-Condition was not in disjunctive normal form (first operand of "
						+ getClass() + " was of type " + op1.getClass() + ").");
		}
		if (!(op2 instanceof OSLOr) && !(op2 instanceof OSLAnd) && !(op2 instanceof OSLNot) && !(op2.isAtomic())) {
			throw new InvalidOperatorException("Parameter 'distributionEnabled' is true, but ECA-Condition was not in disjunctive normal form (second operand of "
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
	protected Collection<AtomicOperator> getObservers(Collection<AtomicOperator> observers) {
		op1.getObservers(observers);
		op2.getObservers(observers);
		return observers;
	}

	@Override
	public EOperatorType getOperatorType() {
		return EOperatorType.OSL_OR;
	}

	@Override
	public boolean isAtomic() {
		return false;
	}

	@Override
	public boolean isDNF() {
		try {
			ensureDNF();
			return true;
		} catch (InvalidOperatorException e) {
			return false;
		}
	}
}
