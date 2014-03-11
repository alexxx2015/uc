package de.tum.in.i22.uc.cm.datatypes.Linux;

import de.tum.in.i22.uc.cm.basic.ContainerBasic;

/**
 * Class representing a processes container.
 * Correspongs to set P in NSS'09 paper.
 *
 * @author Florian Kelbert
 *
 */
public class ProcessContainer extends ContainerBasic {
	private final int _pid;

	public ProcessContainer(int pid) {
		super();
		this._pid = pid;
	}

	public int getPid() {
		return _pid;
	}
}
