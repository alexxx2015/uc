package de.tum.in.i22.pdp.datatypes.basic;

import java.util.List;

import de.tum.in.i22.pdp.datatypes.IEvent;
import de.tum.in.i22.pdp.datatypes.IHistory;

public class HistoryBasic implements IHistory {

	private List<IEvent> _trace;
	
	public HistoryBasic(List<IEvent> trace) {
		super();
		_trace = trace;
	}
	
	@Override
	public List<IEvent> getTrace() {
		return _trace;
	}

}
