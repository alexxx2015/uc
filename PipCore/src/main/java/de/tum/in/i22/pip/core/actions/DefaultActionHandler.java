package de.tum.in.i22.pip.core.actions;

import testutil.DummyMessageGen;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IStatus;


public class DefaultActionHandler extends BaseActionHandler {

	public DefaultActionHandler() {
		super(null, null);
	}

	@Override
	public IStatus execute() {
		//FIXME what is the return status, it was 1 so far
		return DummyMessageGen.createOkStatus();
	}
	
	@Override
	public void setEvent(IEvent event) {
		
	}

}
