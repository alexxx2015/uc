package de.tum.in.i22.uc.pdp.core.condition.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.LiteralOperator;
import de.tum.in.i22.uc.pdp.core.mechanisms.Mechanism;
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
		_id = id + 1;
		setFullId(_id);
		_logger.debug("My [{}] id is {}.", this, getFullId());
		return _id;
	}

	@Override
	public String toString() {
		return "EvalOperator [Type: " + this.getType() + ", [" + this.getContent() + "]]";
	}

	@Override
	protected boolean localEvaluation(IEvent curEvent) {
		// TODO: evalOperator evaluation NYI; forward to external evaluation engine
		throw new UnsupportedOperationException("evaluate EvalOperator. Not yet implemented.");
	}

	@Override
	public boolean isPositive() {
		throw new UnsupportedOperationException("EvalOperator. Not yet implemented.");
	}
}
