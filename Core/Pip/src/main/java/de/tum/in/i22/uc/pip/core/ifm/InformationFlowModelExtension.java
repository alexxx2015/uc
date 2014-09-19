package de.tum.in.i22.uc.pip.core.ifm;

import java.util.Objects;

import de.tum.in.i22.uc.cm.pip.ifm.IAnyInformationFlowModel;

/**
 *
 * @author Florian Kelbert
 *
 */
public abstract class InformationFlowModelExtension extends InformationFlowModel {

	protected IAnyInformationFlowModel _ifModel;

	protected InformationFlowModelExtension(InformationFlowModelManager informationFlowModelManager) {
		_ifModel = informationFlowModelManager;
	}

	@Override
	public final int hashCode() {
		return Objects.hash(this.getClass());
	}

	@Override
	public final boolean equals(Object obj) {
		return obj != null ? this.getClass().equals(obj.getClass()) : false;
	}
}
