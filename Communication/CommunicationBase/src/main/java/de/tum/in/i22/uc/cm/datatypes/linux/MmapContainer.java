package de.tum.in.i22.uc.cm.datatypes.linux;

import java.util.Objects;

import de.tum.in.i22.uc.cm.datatypes.basic.ContainerBasic;

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
	private final String _host;
	private final int _pid;
	private final String _addr;

	public MmapContainer(String host, int pid, String addr) {
		_host = host;
		_pid = pid;
		_addr = addr;
	}

	public int getPid() {
		return _pid;
	}

	public String getAddr() {
		return _addr;
	}

	public String getHost() {
		return _host;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MmapContainer) {
			MmapContainer o = (MmapContainer) obj;
			return Objects.equals(_host, o._host)
					&& Objects.equals(_pid, o._pid)
					&& Objects.equals(_addr, o._addr);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(_host, _pid, _addr);
	}
}
