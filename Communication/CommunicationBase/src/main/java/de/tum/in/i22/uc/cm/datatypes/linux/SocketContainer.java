package de.tum.in.i22.uc.cm.datatypes.linux;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import de.tum.in.i22.uc.cm.datatypes.basic.ContainerBasic;
import de.tum.in.i22.uc.cm.distribution.IPLocation;

/**
 * Class representing socket containers.
 * Corresponds to set C_S in CODASPY'13 paper.
 *
 * @author Florian Kelbert
 *
 */
public class SocketContainer extends ContainerBasic {
	private final Domain _domain;
	private final Type _type;
	private final IPLocation _responsibleLocation;
	private SocketName _socketName;

	public SocketContainer(Domain domain, Type type, IPLocation responsibleLocation) {
		_domain = domain;
		_type = type;
		_responsibleLocation = responsibleLocation;
	}

	public SocketContainer(Domain domain, Type type, IPLocation responsibleLocation, SocketName socketName) {
		this(domain, type, responsibleLocation);
		_socketName = socketName;
	}

	public Domain getDomain() {
		return _domain;
	}

	public Type getType() {
		return _type;
	}

	public IPLocation getResponsibleLocation() {
		return _responsibleLocation;
	}

	public void setSocketName(SocketName socketName) {
		_socketName = socketName;
	}

	public SocketName getSocketName() {
		return _socketName;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SocketContainer) {
			SocketContainer other = (SocketContainer) obj;
			return super.equals(obj)
					&& Objects.equal(_domain, other._domain)
					&& Objects.equal(_type, other._type)
					&& Objects.equal(_responsibleLocation, other._responsibleLocation);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ Objects.hashCode(_domain, _type, _responsibleLocation);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("_id", getId())
				.add("_socketName", _socketName)
				.add("_domain", _domain)
				.add("_type", _type)
				.add("_responsibleLocation", _responsibleLocation)
				.toString();
	}

	public enum Domain {
		AF_INET,
		AF_INET6,
		AF_LOCAL;

		public static Domain from(String s) {
			switch (s) {
				case "AF_INET":
				case "PF_INET":
					return AF_INET;
				case "AF_INET6":
				case "PF_INET6":
					return AF_INET6;
				case "AF_UNIX":
				case "PF_UNIX":
				case "AF_LOCAL":
				case "PF_LOCAL":
					return AF_LOCAL;
				default:
					return null;
			}
		}
	}

	public enum Type {
		SOCK_STREAM,
		SOCK_DGRAM,
		SOCK_RAW;

		public static Type from(String s) {
			switch(s) {
				case "SOCK_STREAM":
					return SOCK_STREAM;
				case "SOCK_DGRAM":
					return SOCK_DGRAM;
				case "SOCK_RAW":
					return SOCK_RAW;
				default:
					return null;
			}
		}
	}
}
