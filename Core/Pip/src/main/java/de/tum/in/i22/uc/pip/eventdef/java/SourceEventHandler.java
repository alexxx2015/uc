package de.tum.in.i22.uc.pip.eventdef.java;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;

public class SourceEventHandler extends JavaEventHandler {

	@Override
	protected IStatus update() {
		// TODO Auto-generated method stub
		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
