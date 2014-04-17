package de.tum.in.i22.uc.pdp.requests;

import de.tum.in.i22.uc.cm.datatypes.basic.ResponseBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IResponse;
import de.tum.in.i22.uc.cm.processing.PdpProcessor;


/**
 *
 * @author Florian Kelbert & Enrico Lovat
 *
 */
public class NotifyEventPdpRequest extends PdpRequest<IResponse> {
	private final IEvent _event;
	private final boolean _sync;
	
	public NotifyEventPdpRequest(IEvent event, boolean sync) {
		_event = event;
		_sync = sync;
	}

	public NotifyEventPdpRequest(IEvent event) {
		this(event,false);
	}

	@Override
	public IResponse process(PdpProcessor processor) {
		if(_sync){
			return processor.notifyEventSync(_event);
		}
		else{
			processor.notifyEventAsync(_event);
			return new ResponseBasic(new StatusBasic(EStatus.ALLOW), null, null);		
		}		
	}

}
