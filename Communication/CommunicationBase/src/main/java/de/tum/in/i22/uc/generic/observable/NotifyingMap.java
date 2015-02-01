package de.tum.in.i22.uc.generic.observable;

import java.util.Map;
import java.util.Observer;

import com.google.common.collect.ForwardingMap;

/**
 * A map that notifies registered {@link Observer}s
 * about changes to the map.
 *
 * @author Florian Kelbert
 *
 * @param <K>
 * @param <V>
 */
public class NotifyingMap<K, V> extends ForwardingMap<K, V> implements IMyObservable {
	private IMyObservable _observable;
	private final Map<K, V> _delegate;

	public NotifyingMap(Map<K, V> delegate, Observer o) {
		_delegate = delegate;
		_observable = new MyObservable();
		_observable.addObserver(o);
	}

	@Override
	protected Map<K, V> delegate() {
		return _delegate;
	}

	@Override
	public void clear() {
		setChanged();
		notifyObservers();
		super.clear();
	}

	@Override
	public V put(K key, V value) {
		setChanged();
		notifyObservers();
		return super.put(key, value);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> map) {
		setChanged();
		notifyObservers();
		super.putAll(map);
	}

	@Override
	public V remove(Object object) {
		setChanged();
		notifyObservers();
		return super.remove(object);
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
