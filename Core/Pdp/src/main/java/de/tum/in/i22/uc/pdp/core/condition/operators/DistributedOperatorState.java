package de.tum.in.i22.uc.pdp.core.condition.operators;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IOperator;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IOperatorState;
import de.tum.in.i22.uc.cm.distribution.IDistributionManager;

class DistributedOperatorState implements IOperatorState {

	private final Operator _operator;

	private final IDistributionManager _distributionManager;

	DistributedOperatorState(Operator operator, IDistributionManager distributionManager) {
		_operator = operator;
		_distributionManager = distributionManager;
	}

	@Override
	public IOperator getOperator() {
		return _operator;
	}
}
