package de.tum.in.i22.uc.pip.eventdef.bindft;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IScope;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.pip.interfaces.EBehavior;
import de.tum.in.i22.uc.cm.pip.interfaces.EScopeType;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class BinDeleteEventHandler extends BinDftEventHandler {

	@Override
	protected IStatus update() {
		return update(EBehavior.INTRA, null);
	}

	@Override
	protected IScope buildScope(String delimiter) {
		return buildScope(EScopeType.JBC_GENERIC_LOAD);
	}

	@Override
	protected IStatus update(EBehavior direction, IScope scope) {
		String nonce=null;
		try {
			nonce = getParameterValue("nonce");
		} catch (ParameterNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		_informationFlowModel.emptyContainer(new NameBasic(nonce));

		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
