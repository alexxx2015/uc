package de.tum.in.i22.uc.pip.eventdef.excel;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;

public class CopyExternalEventHandler extends ExcelEvents {

	public CopyExternalEventHandler() {
		super();
	}

	@Override
	protected IStatus update() {
		
		pushOCB();
		
		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
