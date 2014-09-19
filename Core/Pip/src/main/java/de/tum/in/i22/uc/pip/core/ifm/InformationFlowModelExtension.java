package de.tum.in.i22.uc.pip.core.ifm;

import java.util.Objects;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.interfaces.informationFlowModel.IAnyInformationFlowModel;


/**
 *
 * @author Florian Kelbert
 *
 */
public abstract class InformationFlowModelExtension {

	protected abstract IStatus startSimulation();
	protected abstract IStatus stopSimulation();
	protected abstract String niceString();
	protected abstract void reset();

	protected IAnyInformationFlowModel _ifModel;

	protected InformationFlowModelExtension(
			InformationFlowModelManager informationFlowModelManager) {
		_ifModel=informationFlowModelManager;
	}

	@Override
	public final int hashCode() {
		return Objects.hash(this.getClass());
	}

	@Override
	public final boolean equals(Object obj) {
		return obj != null
				? this.getClass().equals(obj.getClass())
				: false;
	}
}
