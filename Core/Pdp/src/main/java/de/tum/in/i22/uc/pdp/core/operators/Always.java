package de.tum.in.i22.uc.pdp.core.operators;

import java.util.Collection;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.core.operators.State.StateVariable;
import de.tum.in.i22.uc.pdp.xsd.AlwaysType;

public class Always extends AlwaysType {
	private static Logger _logger = LoggerFactory.getLogger(Always.class);

	private Operator op;

	public Always() {
		_state.set(StateVariable.IMMUTABLE, false);
	}

	@Override
	protected void init(Mechanism mech, Operator parent, long ttl) {
		super.init(mech, parent, ttl);
		op = ((Operator) operators);
		op.init(mech, this, ttl);
	}

	@Override
	protected int initId(int id) {
		return setId(op.initId(id) + 1);
	}

	@Override
	public String toString() {
		return "ALWAYS (" + op + ")";
	}

	@Override
	public boolean tick() {
		if (_state.get(StateVariable.IMMUTABLE)) {
			return false;
		}
		else {
			boolean valueAtLastTick = op.tick();

			if (!valueAtLastTick) {
				_logger.debug("ALWAYS: activating IMMUTABILITY");
				_state.set(StateVariable.IMMUTABLE, true);
			}

			return valueAtLastTick;
		}
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
	public Collection<Observer> getObservers(Collection<Observer> observers) {
		return op.getObservers(observers);
	}

}
