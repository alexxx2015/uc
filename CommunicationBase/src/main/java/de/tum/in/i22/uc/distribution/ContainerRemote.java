package de.tum.in.i22.uc.distribution;

import java.util.Objects;

import de.tum.in.i22.uc.cm.basic.ContainerBasic;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IName;

/**
 * Represents a remote container of which we only know a name and its location.
 * Methods getId() and getClassValue() will thus always return null.
 *
 * @author Florian Kelbert
 *
 */
public class ContainerRemote extends ContainerBasic implements IContainer {
	private final ILocation _location;

	public ContainerRemote(IName name, ILocation location) {
		_location = location;
	}

	public ILocation getLocation() {
		return _location;
	}

	@Override
	public String getId() {
		return null;
	}

	@Override
	public String getClassValue() {
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ContainerRemote) {
			ContainerRemote o = (ContainerRemote) obj;
			return super.equals(obj)
					&& Objects.equals(_location, o._location);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.getId(), super.getClass(), _location);
	}

}
