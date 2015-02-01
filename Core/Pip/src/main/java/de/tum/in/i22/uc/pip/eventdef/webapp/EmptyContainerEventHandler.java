package de.tum.in.i22.uc.pip.eventdef.webapp;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.scope.AbstractScopeEventHandler;

public class EmptyContainerEventHandler extends AbstractScopeEventHandler {

	public EmptyContainerEventHandler() {
		super();
	}

	@Override
	protected IStatus update() {
		String container = "myIFSWebAppInternalContainer";
//		try {
////			container = getParameterValue("myIFSWebAppInternalContainer");
//		} catch (ParameterNotFoundException e) {
//			_logger.error(e.getMessage());
//			return new StatusBasic(EStatus.ERROR_EVENT_PARAMETER_MISSING);
//		}
		_informationFlowModel.emptyContainer(new NameBasic(container));
		return new StatusBasic(EStatus.OKAY);
	}

}
