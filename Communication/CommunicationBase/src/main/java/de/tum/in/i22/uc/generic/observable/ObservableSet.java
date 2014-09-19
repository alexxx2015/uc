package de.tum.in.i22.uc.generic.observable;

import java.util.Collection;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import com.google.common.collect.ForwardingSet;

/**
 * Note: Observable/Observer related code has been taken from:
 * http://www.docjar.com/projects/openjdk-7-java.html
 *
 * The problem here was that it is not possible to extend both
 * {@link ForwardingSet} and {@link Observable}.
 *
 * @author Florian Kelbert
 *
 * @param <E>
 *
 */
public class ObservableSet<E> extends ForwardingSet<E> implements IMyObservable {
	private final IMyObservable _observable;
	private final Set<E> _delegate;

	public ObservableSet(Set<E> delegate) {
		_delegate = delegate;
		_observable = new MyObservable();
	}

	@Override
	protected Set<E> delegate() {
		return _delegate;
	}

	@Override
	public boolean add(E element) {
		setChanged();
		return super.add(element);
	}

	@Override
	public boolean addAll(Collection<? extends E> collection) {
		setChanged();
		return super.addAll(collection);
	}

	@Override
	public void clear() {
		setChanged();
		super.clear();
	}

	@Override
	public boolean remove(Object object) {
		setChanged();
		return super.remove(object);
	}

	@Override
	public boolean removeAll(Collection<?> collection) {
		setChanged();
		return super.removeAll(collection);
	}

	@Override
	public boolean retainAll(Collection<?> collection) {
		setChanged();
		return super.retainAll(collection);
	}

	@Override
	public void addObserver(Observer o) {
		_observable.addObserver(o);
	}

	@Override
	public int countObservers() {
		return _observable.countObservers();
	}

	@Override
	public void deleteObserver(Observer o) {
		_observable.deleteObserver(o);
	}

	@Override
	public void deleteObservers() {
		_observable.deleteObservers();
	}

	@Override
	public boolean hasChanged() {
		return _observable.hasChanged();
	}

	@Override
	public void setChanged() {
		_observable.setChanged();
	}

	@Override
	public void notifyObservers() {
		_observable.notifyObservers();
	}

	@Override
	public void notifyObservers(Object arg) {
		_observable.notifyObservers(arg);
	}

	@Override
	public void clearChanged() {
		_observable.clearChanged();
	}
}
