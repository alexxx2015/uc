package de.tum.in.i22.pip.core.actions;

import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpStatus.EStatus;


public class DefaultActionHandler extends BaseActionHandler {

	public DefaultActionHandler() {
		super(null, null);
	}

	@Override
	public EStatus execute() {
		//FIXME what is the return status, it was 1 so far
		return EStatus.OKAY;
	}
	
	@Override
	public void setEvent(IEvent event) {
		
	}

}
