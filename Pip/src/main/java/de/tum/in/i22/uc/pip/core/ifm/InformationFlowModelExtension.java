package de.tum.in.i22.uc.pip.core.ifm;

import java.util.Objects;


/**
 *
 * @author Florian Kelbert
 *
 */
public abstract class InformationFlowModelExtension implements IInformationFlowModelExtension {

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
