package de.tum.in.i22.uc.pdp.core.operators;

import java.util.Collection;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.AtomicOperator;
import de.tum.in.i22.uc.cm.datatypes.interfaces.EOperatorType;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pdp.PdpThreading;
import de.tum.in.i22.uc.pdp.core.Mechanism;
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
	protected void init(Mechanism mech, Operator parent, long ttl) {
		super.init(mech, parent, ttl);

		op1 = (Operator) operators.get(0);
		op2 = (Operator) operators.get(1);

		if (Settings.getInstance().getDistributionEnabled()) {
			throw new IllegalStateException(getClass() + " operator is not allowed if parameter 'distributionEnabled' is true. Shouldn't be to hard to be rewritten as DNF.");
		}

		_executorCompletionService = new ExecutorCompletionService<>(PdpThreading.instance());

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

		Future<Boolean> taken = PdpThreading.take(_executorCompletionService);
		if (taken == op1Future) {
			if (!PdpThreading.resultOf(taken)) {
				valueAtLastTick = true;
				PdpThreading.instance().submit(() -> PdpThreading.take(_executorCompletionService));
			}
			else {
				valueAtLastTick = PdpThreading.takeResult(_executorCompletionService);
			}
		}
		else /*if (taken == op2Future)*/ {
			if (PdpThreading.resultOf(taken)) {
				valueAtLastTick = true;
				PdpThreading.instance().submit(() -> PdpThreading.take(_executorCompletionService));
			}
			else {
				valueAtLastTick = !PdpThreading.takeResult(_executorCompletionService);
			}
		}

		_logger.info("op1: {}; op2: {}. Result: {}", PdpThreading.resultOf(op1Future), PdpThreading.resultOf(op2Future), valueAtLastTick);

		_state.set(StateVariable.VALUE_AT_LAST_TICK, valueAtLastTick);

		return valueAtLastTick;


//		Future<Boolean> op1state = PdpThreading.instance().submit(() -> op1.tick(endOfTimestep));
//		Future<Boolean> op2state = PdpThreading.instance().submit(() -> op2.tick(endOfTimestep));
//
//		boolean valueAtLastTick = !PdpThreading.resultOf(op1state) || PdpThreading.resultOf(op2state);
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
//		boolean valueAtLastTick = !op1state || op2state;
//
//		_logger.info("op1: {}; op2: {}. Result: {}", op1state, op2state, valueAtLastTick);
//
//		_state.set(StateVariable.VALUE_AT_LAST_TICK, valueAtLastTick);
//
//		return valueAtLastTick;
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
		return EOperatorType.OSL_IMPLIES;
	}
}
