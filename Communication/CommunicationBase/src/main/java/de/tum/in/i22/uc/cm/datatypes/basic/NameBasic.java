package de.tum.in.i22.uc.cm.datatypes.basic;

import java.util.Objects;

import com.google.common.base.MoreObjects;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;

/**
 * A container can have several representations.
 * PipName is a representation of the container.
 * @author Stoimenov
 *
 */
public class NameBasic implements IName {
	private final String _name ;

	public NameBasic(String name) {
		_name = name;
	}

	@Override
	public final String getName() {
		return _name;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("_name", _name)
				.toString();
	}

	@Override
	public final int hashCode() {
		return Objects.hash(_name);
	}

	@Override
	public final boolean equals(Object obj) {
		if (obj instanceof NameBasic) {
			return Objects.equals(_name, ((NameBasic) obj)._name);
		}
		return false;
	}
}
