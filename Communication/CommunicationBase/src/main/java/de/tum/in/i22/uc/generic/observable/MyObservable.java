package de.tum.in.i22.uc.generic.observable;

import java.util.Observable;

/**
 * In original class {@link Observable}, methods
 * setChanged() and hasChanged() are protected.
 * Thus, it was impossible to use those methods
 * within the delegate pattern. This class works
 * around this problem.
 *
 * @author Florian Kelbert
 *
 */
class MyObservable extends Observable implements IMyObservable {
	@Override
	public void setChanged() {
		super.setChanged();
	}

	@Override
	public void clearChanged() {
		super.clearChanged();
	}
}
