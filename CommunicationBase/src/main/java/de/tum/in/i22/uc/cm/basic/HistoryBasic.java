package de.tum.in.i22.uc.cm.basic;

import java.util.ArrayList;
import java.util.List;

import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IHistory;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpEvent;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpHistory;

public class HistoryBasic implements IHistory {

	private List<IEvent> _trace;
	
	public HistoryBasic(List<IEvent> trace) {
		super();
		_trace = trace;
	}
	
	public HistoryBasic(GpHistory gpHistory) {
		if (gpHistory == null)
			return;
		
		List<GpEvent> list = gpHistory.getTraceList();
		if (list != null && !list.isEmpty()) {
			_trace = new ArrayList<IEvent>();
			for (GpEvent event:list) {
				_trace.add(new EventBasic(event));
			}
		}
	}
	
	@Override
	public List<IEvent> getTrace() {
		return _trace;
	}
	
	/**
	 * 
	 * @param e
	 * @return Google Protocol Buffer object corresponding to IHistory
	 */
	public static GpHistory createGpbHistory(IHistory h) {
		if (h == null) 
			return null;
		
		GpHistory.Builder gp = GpHistory.newBuilder();
		if (h.getTrace() != null && !h.getTrace().isEmpty()) {
			List<IEvent> list = h.getTrace();
			for (IEvent event:list) {
				gp.addTrace(EventBasic.createGpbEvent(event));
			}
		}
		return gp.build();
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean isEqual = false;
		if (obj != null && this.getClass() == obj.getClass()) {
			HistoryBasic o = (HistoryBasic)obj;
			isEqual = CompareUtil.areListsEqual(_trace, o.getTrace());
		}
		return isEqual;
	}
}
