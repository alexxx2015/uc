package de.tum.in.i22.uc.cm.datatypes.Linux;

import java.util.Objects;

import de.tum.in.i22.uc.cm.out.AbstractConnection;

/**
 * Represents a remote container of which we only know a name and its location.
 * Methods getId() and getClassValue() will thus always return null.
 *
 * @author Florian Kelbert
 *
 */
public class RemoteSocketContainer extends SocketContainer {
	private final AbstractConnection _connection;

	public RemoteSocketContainer(Domain domain, Type type, AbstractConnection connection) {
		super(domain, type);
		_connection = connection;
	}

	public AbstractConnection getConnection() {
		return _connection;
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
					&& Objects.equals(_connection, o._connection);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.getId(), super.getClass(), _connection);
	}

}
