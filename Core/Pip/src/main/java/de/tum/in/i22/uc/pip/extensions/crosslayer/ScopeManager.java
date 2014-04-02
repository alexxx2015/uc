package de.tum.in.i22.uc.pip.extensions.crosslayer;

import de.tum.in.i22.uc.pip.core.ifm.IInformationFlowModelExtension;
import de.tum.in.i22.uc.pip.core.ifm.InformationFlowModelExtensionManager;

public class ScopeManager extends InformationFlowModelExtensionManager {

	@Override
	protected IInformationFlowModelExtension getExtension() {
		return ScopeInformationFlowModel.getInstance();
	}
}
