package de.tum.in.i22.uc.cm.datatypes.basic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.DeepCloneable;

public class CircularArray<T> implements DeepCloneable<CircularArray<T>> {
	private static Logger _logger = LoggerFactory.getLogger(CircularArray.class);
	private T values[] = null;

	private int first = 0;
	private int next = 0;

	private CircularArray() {
	}

	@SuppressWarnings("unchecked")
	public CircularArray(int size) {
		values = (T[]) new Object[size];
	}

	@SuppressWarnings("unchecked")
	public CircularArray(long size) {
		values = (T[]) new Object[(int) size];
	}

	public T get(int pos) {
		return values[pos];
	}

	public void set(T val, int pos) {
		values[pos] = val;
	}

	public int size() {
		return values.length;
	}

	/**
	 * Retrieves, but does not remove, the first entry.
	 * @return
	 */
	public T peek() {
		T val = values[this.first];
		_logger.trace("peek (first={}) -> {}", this.first, val);
		return val;
	}

	/**
	 * Retrieves and removes the first entry.
	 * @return
	 */
	public T pop() {
		T val = values[this.first];
		_logger.trace("pop (first={}) -> {}", this.first, val);

		this.first++;
		_logger.trace("first++ -> {}", this.first);
		if (this.first == this.values.length) {
			_logger.trace("first reached boundary, resetting to 0");
			this.first = 0;
		}
		return val;
	}

	public void push(T val) {
		_logger.trace("push (next={}) -> {}", this.next, val);
		this.values[this.next] = val;

		this.next++;
		_logger.trace("next++ -> {}", this.next);

		if (this.next == this.values.length) {
			this.next = 0;
			_logger.trace("next reached boundary, resetting to 0");
		}
	}

	public T peekLast() {
		return this.values[(this.next != 0 ? this.next : this.values.length) - 1];
	}


	@Override
	public String toString() {
		String str = "[";
		for (int a = 0; a < values.length; a++) {
			if (a > 0)
				str += ", ";
			str += values[a];
			if (a == first) {
				str += "(F";
				if (a == next)
					str += ",N";
				str += ")";
			}
			if (a != first && a == next)
				str += "(N)";
		}
		str += "] (" + first + "," + next + ")";

		return str;
	}

//	@SuppressWarnings("unchecked")
//	@Override
//	public CircularArray<T> clone() {
//		CircularArray<T> cloned = null;
//		try {
//			cloned = (CircularArray<T>) super.clone();
//		} catch (CloneNotSupportedException e) {
//		}
//
//		if (cloned == null) {
//			cloned = new CircularArray<>(this.values.length);
//		}
//
//		cloned.values = (T[]) new Object[this.values.length];
//		cloned.first = this.first;
//		cloned.next = this.next;
//
//		for (int i = 0; i < this.values.length; i++) {
//			cloned.values[i] = this.values[i];
//		}
//
//		return cloned;
//	}

	@SuppressWarnings("unchecked")
	@Override
	public CircularArray<T> deepClone() {
		CircularArray<T> clone;
		try {
			clone = (CircularArray<T>) super.clone();
		} catch (CloneNotSupportedException e) {
			clone = new CircularArray<>();
		}

		clone.values = (T[]) new Object[values.length];
		clone.first = this.first;
		clone.next = this.next;

		for (int i = 0; i < this.values.length; i++) {
			if (values[i] instanceof DeepCloneable<?>) {
				clone.values[i] = (T) ((DeepCloneable<?>) values[i]).deepClone();
			}
			else {
				clone.values[i] = values[i];
			}
		}

		return clone;
	}
}
