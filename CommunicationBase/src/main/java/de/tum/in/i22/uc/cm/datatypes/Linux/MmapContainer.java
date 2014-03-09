package de.tum.in.i22.uc.cm.datatypes.Linux;

import de.tum.in.i22.uc.cm.basic.ContainerBasic;

public class MmapContainer extends ContainerBasic {
	private int _pid;
	private String _addr;
	
	public MmapContainer(int pid, String addr) {
		super();
		this._pid = pid;
		this._addr = addr;
	}
	
	public int getPid() {
		return _pid;
	}
	
	public String getAddr() {
		return _addr;
	}
}
