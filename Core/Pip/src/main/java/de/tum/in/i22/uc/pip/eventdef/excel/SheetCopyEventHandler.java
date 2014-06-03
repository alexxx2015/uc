package de.tum.in.i22.uc.pip.eventdef.excel;

import de.tum.in.i22.uc.cm.datatypes.basic.ContainerBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;

public class SheetCopyEventHandler extends ExcelEvents {

	public SheetCopyEventHandler() {
		super();
	}

	@Override
	protected IStatus update() {
		_informationFlowModel.addName(new NameBasic("SheetCopy"),new ContainerBasic());
		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
