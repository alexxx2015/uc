package de.tum.in.i22.uc.pdp.core.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.LiteralOperator;
import de.tum.in.i22.uc.pdp.core.Mechanism;
import de.tum.in.i22.uc.pdp.xsd.EvalOperatorType;

public class EvalOperator extends EvalOperatorType implements LiteralOperator {
	private static Logger _logger = LoggerFactory.getLogger(EvalOperator.class);

	public EvalOperator() {
	}

	@Override
	protected void init(Mechanism mech, Operator parent, long ttl) {
		super.init(mech, parent, ttl);
	}

	@Override
	protected int initId(int id) {
		return setId(id + 1);
	}

	@Override
	public String toString() {
		return "EvalOperator [Type: " + this.getType() + ", [" + this.getContent() + "]]";
	}

	@Override
	public boolean tick() {
		// TODO: evalOperator evaluation NYI; forward to external evaluation engine
		return false;
	}

	@Override
	public boolean isPositive() {
		throw new UnsupportedOperationException("EvalOperator. Not yet implemented.");
	}

	@Override
	public void startSimulation() {
		super.startSimulation();
		// TODO: Implement
		return;
	}

	@Override
	public void stopSimulation() {
		super.stopSimulation();
		// TODO: Implement
		return;
	}
}
