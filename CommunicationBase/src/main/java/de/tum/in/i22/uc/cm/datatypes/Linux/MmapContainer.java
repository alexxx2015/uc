package de.tum.in.i22.uc.cm.datatypes.Linux;

import de.tum.in.i22.uc.cm.basic.ContainerBasic;

/**
 * Class representing a memory mapped file (syscall mmap()) within
 * a processes' memory.
 *
 * Also see documentation of MmapName.
 *
 * @author Florian Kelbert
 *
 */
public class MmapContainer extends ContainerBasic {
	private final int _pid;
	private final String _addr;

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
