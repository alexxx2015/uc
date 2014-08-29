package de.tum.in.i22.uc.pdp.core.condition.operators;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IOperator;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IOperatorState;

class DistributedOperatorState implements IOperatorState {

	private final Operator _operator;

	DistributedOperatorState(Operator operator) {
		_operator = operator;
	}

	@Override
	public IOperator getOperator() {
		return _operator;
	}
}
