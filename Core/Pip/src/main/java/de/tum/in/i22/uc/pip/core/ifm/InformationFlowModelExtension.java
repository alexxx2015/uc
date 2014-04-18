package de.tum.in.i22.uc.pip.core.ifm;

import java.util.Objects;


/**
 *
 * @author Florian Kelbert
 *
 */
public abstract class InformationFlowModelExtension {

	protected abstract void reset();
	protected abstract void push();
	protected abstract void pop();
	protected abstract String niceString();

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
