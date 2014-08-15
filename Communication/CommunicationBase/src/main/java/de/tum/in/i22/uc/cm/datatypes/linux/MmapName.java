package de.tum.in.i22.uc.cm.datatypes.linux;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;

/**
 * Class representing a mmap() mapping container, consisting of
 * + a process id (i.e. the process that mapped a file) and
 * + an address (i.e. the memory address to which the file has been mapped).
 *
 * This concept was not described in any paper, but it allows to conveniently
 * remove corresponding mmap containers upon munmap() calls, thus reducing data flow
 * tracking overapproximations after a munmap() call. Before, munmap() had been ignored.
 *
 * Also see class MmapContainer and the corresponding mmap() and munmap() event handlers.
 *
 * @author Florian Kelbert
 *
 */
public class MmapName extends NameBasic implements IProcessRelativeName, IClonableForProcess {

	private static final String PREFIX_MMAP = "MMAP_";

	private final String _host;
	private final int _pid;
	private final String _addr;

	private MmapName(String host, int pid, String addr, String name) {
		super(name);

		_host = host;
		_pid = pid;
		_addr = addr;
	}

	public static MmapName create(String host, int pid, String addr) {
		return new MmapName(host, pid, addr, PREFIX_MMAP + host + "." + pid + "." + addr);
	}

	public String getHost() {
		return _host;
	}

	@Override
	public boolean hasPid(int pid) {
		return _pid == pid;
	}

	public String getAddr() {
		return _addr;
	}

	@Override
	public String toString() {
		return com.google.common.base.MoreObjects.toStringHelper(this)
				.add("_host", _host)
				.add("_pid", _pid)
				.add("_addr", _addr)
				.toString();
	}

	@Override
	public IClonableForProcess cloneFor(int pid) {
		return MmapName.create(_host, pid, _addr);
	}
}