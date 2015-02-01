package de.tum.in.i22.uc.pip.core.statebased;

import com.google.common.base.MoreObjects;

import de.tum.in.i22.uc.cm.pip.ifm.IAnyInformationFlowModel;
import de.tum.in.i22.uc.cm.pip.interfaces.IStateBasedPredicate;

public abstract class StateBasedPredicate implements IStateBasedPredicate {
	private final String _predicate;

	protected final IAnyInformationFlowModel _informationFlowModel;

	public StateBasedPredicate(String predicate, IAnyInformationFlowModel informationFlowModel) {
		_predicate = predicate;
		_informationFlowModel = informationFlowModel;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("_predicate", _predicate)
				.toString();
	}
}
