package de.tum.in.i22.uc.cm.basic;

import de.tum.in.i22.uc.cm.datatypes.IContainer;

/**
 * Represents a remote container of which we only know a name.
 * Methods getId() and getClassValue() will thus always return null.
 *
 * @author Florian Kelbert
 *
 */
public class ContainerRemote implements IContainer {
	private final ContainerName _name;

	public ContainerRemote(ContainerName name) {
		_name = name;
	}

	public ContainerName getName() {
		return _name;
	}

	@Override
	public String getId() {
		return null;
	}

	@Override
	public String getClassValue() {
		return null;
	}

}
