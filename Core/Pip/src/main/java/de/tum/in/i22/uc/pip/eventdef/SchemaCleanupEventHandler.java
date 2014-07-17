package de.tum.in.i22.uc.pip.eventdef;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;

public class SchemaCleanupEventHandler extends BaseEventHandler {

	@Override
	protected IStatus update() {
		// TODO Auto-generated method stub
		_informationFlowModel.reset();
		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
