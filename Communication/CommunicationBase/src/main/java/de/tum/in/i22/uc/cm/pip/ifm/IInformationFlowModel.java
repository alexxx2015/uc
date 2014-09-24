package de.tum.in.i22.uc.cm.pip.ifm;

import java.util.Observable;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;

public interface IInformationFlowModel {
	void reset();

	String niceString();

	/**
	 * In correspondence with {@link Observable#hasChanged()}
	 * and {@link IMyObservable#hasChanged()}.
	 *
	 * @return
	 */
	boolean hasChanged();

	void clearChanged();

	IStatus startSimulation();

	IStatus stopSimulation();

	boolean isSimulating();
}
