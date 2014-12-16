package de.tum.in.i22.uc.pdp.core.operators;

import java.util.Collection;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.AtomicOperator;
import de.tum.in.i22.uc.cm.datatypes.interfaces.EOperatorType;
import de.tum.in.i22.uc.cm.distribution.Threading;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.core.exceptions.InvalidOperatorException;
import de.tum.in.i22.uc.pdp.core.operators.State.StateVariable;
import de.tum.in.i22.uc.pdp.xsd.ImpliesType;

public class OSLImplies extends ImpliesType {
	private static Logger _logger = LoggerFactory.getLogger(OSLImplies.class);

	private Operator op1;
	private Operator op2;

	private ExecutorCompletionService<Boolean> _executorCompletionService;

	public OSLImplies() {
	}

	@Override
	protected void init(Mechanism mech, Operator parent, long ttl) throws InvalidOperatorException {
		super.init(mech, parent, ttl);

		op1 = (Operator) operators.get(0);
		op2 = (Operator) operators.get(1);

		if (Settings.getInstance().getDistributionEnabled()) {
			throw new InvalidOperatorException(getClass() + " operator is not allowed if parameter 'distributionEnabled' is true. Shouldn't be to hard to be rewritten as DNF.");
		}

		_executorCompletionService = new ExecutorCompletionService<>(Threading.instance());

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
		return "(" + op1 + "  ==> " + op2 + ")";
	}

	@Override
	public boolean tick(boolean endOfTimestep) {

		Future<Boolean> op1Future = _executorCompletionService.submit(() -> op1.tick(endOfTimestep));
		Future<Boolean> op2Future = _executorCompletionService.submit(() -> op2.tick(endOfTimestep));

		boolean valueAtLastTick;

		Future<Boolean> taken = Threading.take(_executorCompletionService);
		if (taken == op1Future) {
			if (!Threading.resultOf(taken)) {
				valueAtLastTick = true;
				Threading.instance().submit(() -> Threading.take(_executorCompletionService));
				_logger.info("Result: true. (op1 was false. Not waiting for op2");
			}
			else {
				valueAtLastTick = Threading.takeResult(_executorCompletionService);
				_logger.info("Result: {}. (After evaluating both operands)", valueAtLastTick);
			}
		}
		else /*if (taken == op2Future)*/ {
			if (Threading.resultOf(taken)) {
				valueAtLastTick = true;
				Threading.instance().submit(() -> Threading.take(_executorCompletionService));
				_logger.info("Result: true. (op2 was true. Not waiting for op1");
			}
			else {
				valueAtLastTick = !Threading.takeResult(_executorCompletionService);
				_logger.info("Result: {}. (After evaluating both operands)", valueAtLastTick);
			}
		}

		_state.set(StateVariable.VALUE_AT_LAST_TICK, valueAtLastTick);

		return valueAtLastTick;
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
		return EOperatorType.OSL_IMPLIES;
	}

	@Override
	public boolean isDNF() {
		return false;
	}

	@Override
	public boolean isAtomic() {
		return false;
	}
}
