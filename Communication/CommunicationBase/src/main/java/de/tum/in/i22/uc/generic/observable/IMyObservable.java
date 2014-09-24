package de.tum.in.i22.uc.generic.observable;

import java.util.Observable;
import java.util.Observer;

/**
 * Methods of this interface correspond to the ones in
 * {@link Observable}. The problem here was that a class
 * can not extend {@link Observable} if it already extends
 * another class. We use the Delegate-Pattern to achieve
 * something similar.
 *
 * @author Florian Kelbert
 *
 */
interface IMyObservable {

	public void addObserver(Observer o);

	public void clearChanged();

	public int countObservers();

	public void deleteObserver(Observer o);

	public void deleteObservers();

	public boolean hasChanged();

	public void notifyObservers();

	public void notifyObservers(Object arg);

	void setChanged();
}