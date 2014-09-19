package de.tum.in.i22.uc.generic.observable;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import com.google.common.collect.ForwardingMap;

/**
 * Note: Observable/Observer related code has been taken from:
 * http://www.docjar.com/projects/openjdk-7-java.html
 *
 * The problem here was that it is not possible to extend both
 * {@link ForwardingMap} and {@link Observable}.
 *
 * @author Florian Kelbert
 *
 * @param <K>
 * @param <V>
 */
public class ObservableMap<K, V> extends ForwardingMap<K, V> implements IMyObservable {
	private IMyObservable _observable;
	private final Map<K, V> _delegate;

	public ObservableMap(Map<K, V> delegate) {
		_delegate = delegate;
		_observable = new MyObservable();
	}

	@Override
	protected Map<K, V> delegate() {
		return _delegate;
	}

	@Override
	public void clear() {
		setChanged();
		super.clear();
	}

	@Override
	public V put(K key, V value) {
		setChanged();
		return super.put(key, value);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> map) {
		setChanged();
		super.putAll(map);
	}

	@Override
	public V remove(Object object) {
		setChanged();
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
