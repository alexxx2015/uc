package de.tum.in.i22.uc.pip.eventdef;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;


public class DefaultEventHandler extends BaseEventHandler {

	public DefaultEventHandler() {
		super();
	}

	@Override
	public IStatus update() {
		return new StatusBasic(EStatus.OKAY);
	}
}
