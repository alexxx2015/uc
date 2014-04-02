package de.tum.in.i22.uc.pip.eventdef;

import de.tum.in.i22.uc.cm.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IStatus;


public class DefaultEventHandler extends BaseEventHandler {

	public DefaultEventHandler() {
		super();
	}

	@Override
	public IStatus execute() {
		return new StatusBasic(EStatus.OKAY);
	}
}
