package de.tum.in.i22.uc.pdp.core.operators;

import java.util.Collection;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.core.operators.State.StateVariable;
import de.tum.in.i22.uc.pdp.xsd.ImpliesType;

public class OSLImplies extends ImpliesType {
	private static Logger _logger = LoggerFactory.getLogger(OSLImplies.class);

	private Operator op1;
	private Operator op2;

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
	public boolean tick() {
		/*
		 * Important: _Always_ evaluate both operators
		 */
		boolean op1state = op1.tick();
		boolean op2state = op2.tick();

		boolean valueAtLastTick = !op1state || op2state;

		_logger.info("op1: {}; op2: {}. Result: {}", op1state, op2state, valueAtLastTick);

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
	public Collection<Observer> getObservers(Collection<Observer> observers) {
		op1.getObservers(observers);
		op2.getObservers(observers);
		return observers;
	}
}
