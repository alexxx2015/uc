package de.tum.in.i22.uc.generic.observable;

import java.util.Collection;
import java.util.Observer;
import java.util.Set;

import com.google.common.collect.ForwardingSet;

/**
 * A set that notifies registered {@link Observer}s
 * about changes to the set.
 *
 * @author Florian Kelbert
 *
 * @param <E>
 *
 */
public class NotifyingSet<E> extends ForwardingSet<E> implements IMyObservable {
	private final IMyObservable _observable;
	private final Set<E> _delegate;

	public NotifyingSet(Set<E> delegate, Observer o) {
		_delegate = delegate;
		_observable = new MyObservable();
		_observable.addObserver(o);
	}

	@Override
	protected Set<E> delegate() {
		return _delegate;
	}

	@Override
	public boolean add(E element) {
		setChanged();
		notifyObservers();
		return super.add(element);
	}

	@Override
	public boolean addAll(Collection<? extends E> collection) {
		setChanged();
		notifyObservers();
		return super.addAll(collection);
	}

	@Override
	public void clear() {
		setChanged();
		notifyObservers();
		super.clear();
	}

	@Override
	public boolean remove(Object object) {
		setChanged();
		notifyObservers();
		return super.remove(object);
	}

	@Override
	public boolean removeAll(Collection<?> collection) {
		setChanged();
		notifyObservers();
		return super.removeAll(collection);
	}

	@Override
	public boolean retainAll(Collection<?> collection) {
		setChanged();
		notifyObservers();
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
