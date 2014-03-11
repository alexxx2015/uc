package de.tum.in.i22.uc.cm.datatypes.Linux;

import java.util.Objects;

import de.tum.in.i22.uc.cm.basic.NameBasic;

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
	private final String _pid;
	private final String _addr;

	private MmapName(String host, String pid, String addr, String name) {
		super(name);

		_host = host;
		_pid = pid;
		_addr = addr;
	}

	public static MmapName create(String host, String pid, String addr) {
		return new MmapName(host, pid, addr, PREFIX_MMAP + host + "x" + pid + "x" + addr);
	}

	public String getHost() {
		return _host;
	}

	@Override
	public String getPid() {
		return _pid;
	}

	public String getAddr() {
		return _addr;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MmapName) {
			MmapName o = (MmapName) obj;
			return Objects.equals(_host, o._host)
					&& Objects.equals(_pid, o._pid)
					&& Objects.equals(_addr, o._addr);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return Objects.hash(_host, _pid, _addr);
	}

	@Override
	public String toString() {
		return com.google.common.base.Objects.toStringHelper(this)
				.add("_host", _host)
				.add("_pid", _pid)
				.add("_addr", _addr)
				.toString();
	}

	@Override
	public IClonableForProcess cloneFor(String pid) {
		return MmapName.create(_host, pid, _addr);
	}
}