package de.tum.in.i22.uc.cm.requests.pip;

import de.tum.in.i22.uc.cm.server.PipProcessor;

public class EvaluatePredicateCurrentStatePipRequest extends PipRequest<Boolean> {

	private final String _predicate;

	public EvaluatePredicateCurrentStatePipRequest(String predicate) {
		_predicate = predicate;
	}

	@Override
	public Boolean process(PipProcessor processor) {
		return processor.evaluatePredicateCurrentState(_predicate);
	}
}