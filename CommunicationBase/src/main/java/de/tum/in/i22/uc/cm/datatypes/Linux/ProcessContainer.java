package de.tum.in.i22.uc.cm.datatypes.Linux;

import de.tum.in.i22.uc.cm.basic.ContainerBasic;

public class ProcessContainer extends ContainerBasic {
	private int _pid;
	
	public ProcessContainer(int pid) {
		super();
		this._pid = pid;
	}
	
	public int getPid() {
		return _pid;
	}
}
