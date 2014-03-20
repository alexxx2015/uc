package de.tum.in.i22.uc.cm.datatypes.Linux;

import java.util.Objects;

import de.tum.in.i22.uc.cm.basic.NameBasic;

/**
 * Class representing file descriptors.
 * Corresponds to set F_{dsc} in NSS'09 paper.
 *
 * @author Florian Kelbert
 *
 */
public class FiledescrName extends NameBasic implements IProcessRelativeName, IClonableForProcess {

	private static final String PREFIX_FILE = "FDSC_";

	private final String _host;
	private final int _pid;
	private final int _fd;

	private FiledescrName(String host, int pid, int fd, String name) {
		super(name);

		_host = host;
		_pid = pid;
		_fd = fd;
	}

	public static FiledescrName create(String host, int pid, int fd) {
		return new FiledescrName(host, pid, fd, PREFIX_FILE + host + "." + pid + "." + fd);
	}

	public String getHost() {
		return _host;
	}

	@Override
	public int getPid() {
		return _pid;
	}

	public int getFd() {
		return _fd;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FiledescrName) {
			FiledescrName o = (FiledescrName) obj;
			return Objects.equals(_host, o._host)
					&& Objects.equals(_pid, o._pid)
					&& Objects.equals(_fd, o._fd);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return Objects.hash(_host, _pid, _fd);
	}

	@Override
	public String toString() {
		return com.google.common.base.Objects.toStringHelper(this)
				.add("_host", _host)
				.add("_pid", _pid)
				.add("_fd", _fd)
				.toString();
	}

	@Override
	public IClonableForProcess cloneFor(int pid) {
		return FiledescrName.create(_host, pid, _fd);
	}
}