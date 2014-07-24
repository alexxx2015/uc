package de.tum.in.i22.uc.cm.datatypes.basic;

import java.util.Objects;

public class Pair<A, B> {
	private final A _first;
	private final B _second;

	public Pair(A first, B second) {
		_first = first;
		_second = second;
	}

	@Override
	public int hashCode() {
		return Objects.hash(_first, _second);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Pair) {
			Pair<?, ?> other = (Pair<?, ?>) obj;
			return Objects.equals(_first, other._first) && Objects.equals(_second, other._second);
		}

		return false;
	}

	@Override
	public String toString() {
		return "(" + _first + ", " + _second + ")";
	}

	public A getFirst() {
		return _first;
	}

	public B getSecond() {
		return _second;
	}

}
