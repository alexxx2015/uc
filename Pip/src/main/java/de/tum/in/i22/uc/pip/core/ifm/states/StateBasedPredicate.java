package de.tum.in.i22.uc.pip.core.ifm.states;

import de.tum.in.i22.uc.pip.core.ifm.InformationFlowModel;
import de.tum.in.i22.uc.pip.interfaces.IStateBasedPredicate;

public abstract class StateBasedPredicate implements IStateBasedPredicate {
	protected InformationFlowModel _ifModel = InformationFlowModel.getInstance();

	private final String _predicate;

	// TODO: evaluatePredicateCurrentState
	// TODO: add code to evaluate generic predicate
	// Note that the three parameters of the predicate (State-based formula,
	// parameter1, parameter2) should be separated by separator1, while list
	// of elements (containers or data) should be separated by separator2
	public final static String separator1 = "\\|";
	protected final static String separator2 = "#";


	public StateBasedPredicate(String predicate) {
		_predicate = predicate;
	}
}
