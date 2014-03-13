package de.tum.in.i22.uc.cm.datatypes.Linux;

import java.util.Objects;

import de.tum.in.i22.uc.distribution.ILocation;

/**
 * Represents a remote container of which we only know a name and its location.
 * Methods getId() and getClassValue() will thus always return null.
 *
 * @author Florian Kelbert
 *
 */
public class RemoteSocketContainer extends SocketContainer {
	private final ILocation _location;

	public RemoteSocketContainer(Domain domain, Type type, ILocation location) {
		super(domain, type);
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
		if (obj instanceof RemoteSocketContainer) {
			RemoteSocketContainer o = (RemoteSocketContainer) obj;
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
