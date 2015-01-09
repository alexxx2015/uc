package de.tum.in.i22.uc.pdp.core.operators;

import java.util.Collection;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;

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
import de.tum.in.i22.uc.pdp.xsd.AndType;

public class OSLAnd extends AndType {
	private static Logger _logger = LoggerFactory.getLogger(OSLAnd.class);

	private Operator op1;
	private Operator op2;

	private ExecutorCompletionService<Boolean> _executorCompletionServiceLocal;
	private ExecutorCompletionService<Boolean> _executorCompletionServiceDistr;

	public OSLAnd() {
	}

	@Override
	protected void init(Mechanism mech, Operator parent, long ttl) throws InvalidOperatorException {
		super.init(mech, parent, ttl);

		op1 = (Operator) operators.get(0);
		op2 = (Operator) operators.get(1);

		op1.init(mech, this, ttl);
		op2.init(mech, this, ttl);

		if (Settings.getInstance().getDistributionEnabled()) {
			ensureDNF();
		}

		_executorCompletionServiceLocal = new ExecutorCompletionService<>(Threading.instance());
		_executorCompletionServiceDistr = new ExecutorCompletionService<>(Threading.instance());

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

		_executorCompletionServiceLocal.submit(() -> op1.tick(endOfTimestep));
		_executorCompletionServiceLocal.submit(() -> op2.tick(endOfTimestep));

		// Wait for the evaluation of the first operand
		boolean valueAtLastTick = Threading.takeResult(_executorCompletionServiceLocal);

		if (!valueAtLastTick) {
			// Wait for evaluation of the second operand without blocking
			Threading.instance().submit(() -> Threading.take(_executorCompletionServiceLocal));
			_logger.info("Result: false. (One of the operands was false, not waiting for the other operand to be evaluated)");
		}
		else {
			// Do wait for evaluation of the second operand
			valueAtLastTick = Threading.takeResult(_executorCompletionServiceLocal);
			_logger.info("Result: {}. (After evaluating both operands)", valueAtLastTick);
		}

		_state.set(StateVariable.VALUE_AT_LAST_TICK, valueAtLastTick);

		return valueAtLastTick;
	}

	@Override
	public boolean distributedTickPostprocessing(boolean endOfTimestep) {

		Future<Boolean> op1Future = null;
		Future<Boolean> op2Future = null;

		if (!op1.getPositivity().is(op1.getValueAtLastTick())) {
			_logger.info("Performing distributed evaluation of first operand.");
			op1Future = _executorCompletionServiceDistr.submit(() -> op1.distributedTickPostprocessing(endOfTimestep));
		}

		if (!op2.getPositivity().is(op2.getValueAtLastTick())) {
			_logger.info("Performing distributed evaluation of second operand.");
			op2Future = _executorCompletionServiceDistr.submit(() -> op2.distributedTickPostprocessing(endOfTimestep));
		}

		boolean valueAtLastTick;
		if (op1Future == null && op2Future == null) {
			/*
			 * Local evaluation was sufficient.
			 */
			_logger.info("No distributed evaluation needed.");
			valueAtLastTick = _state.get(StateVariable.VALUE_AT_LAST_TICK);
		}
		else if (op1Future != null && op2Future != null) {
			/*
			 * Both operators need to perform distributed lookup.
			 */

			// Wait for the evaluation of the first operand
			valueAtLastTick = Threading.takeResult(_executorCompletionServiceDistr);

			if (!valueAtLastTick) {
				// Wait for evaluation of the second operand without blocking
				Threading.instance().submit(() -> Threading.take(_executorCompletionServiceDistr));
				_logger.info("Result: false. (One of the operands was false, not waiting for the other operand to be evaluated)");
			}
			else {
				// Do wait for evaluation of the second operand
				valueAtLastTick = Threading.takeResult(_executorCompletionServiceDistr);
				_logger.info("Result: {}. (After evaluating both operands)", valueAtLastTick);
			}
		}
		else {
			/*
			 * One of the operators needs to perform distributed lookup.
			 */
			Future<Boolean> future = Threading.take(_executorCompletionServiceDistr);
			if (future == op1Future) {
				valueAtLastTick = Threading.resultOf(op1Future) && op2.getValueAtLastTick();
			}
			else if (future == op2Future) {
				valueAtLastTick = Threading.resultOf(op2Future) && op1.getValueAtLastTick();
			}
			else {
				throw new RuntimeException("Unknown Future object. Something is going wrong. This should not happen.");
			}
			_logger.info("Result: {} (After evaluating one operand).", valueAtLastTick);
		}

		_state.set(StateVariable.VALUE_AT_LAST_TICK, valueAtLastTick);

		return valueAtLastTick;
	}

	/**
	 * If distribution is enabled, then conditions must be in DNF (cf. CANS 2014 paper).
	 * This method checks whether the operands of AND(.,.) are AND, NOT, or a Literal.
	 * If this is not the case, an InvalidOperatorException is thrown.
	 *
	 * @throws InvalidOperatorException if this object is not in DNF.
	 */
	private void ensureDNF() throws InvalidOperatorException {
		if (!(op1 instanceof OSLAnd) && !(op1 instanceof OSLNot) && !(op1.isAtomic())) {
			throw new InvalidOperatorException("Parameter 'distributionEnabled' is true, but ECA-Condition was not in disjunctive normal form (first operand of "
						+ getClass() + " was of type " + op1.getClass() + ").");
		}
		if (!(op2 instanceof OSLAnd) && !(op2 instanceof OSLNot) && !(op2.isAtomic())) {
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
		return EOperatorType.OSL_AND;
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

	@Override
	protected void setRelevant(boolean relevant) {
		super.setRelevant(relevant);
		op1.setRelevant(relevant);
		op2.setRelevant(relevant);
	}
}
