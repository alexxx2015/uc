package de.tum.in.i22.uc.pip.requests;

import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.processing.PipProcessor;

public class EvaluatePredicateSimulatingNextStatePipRequest extends PipRequest<Boolean> {

	private final IEvent _event;
	private final String _predicate;

	public EvaluatePredicateSimulatingNextStatePipRequest(IEvent event, String predicate) {
		_event = event;
		_predicate = predicate;
	}

	@Override
	public Boolean process(PipProcessor processor) {
		return processor.evaluatePredicateSimulatingNextState(_event, _predicate);
	}
}
