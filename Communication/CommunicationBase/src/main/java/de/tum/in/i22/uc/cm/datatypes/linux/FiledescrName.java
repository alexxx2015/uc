package de.tum.in.i22.uc.cm.datatypes.linux;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;

/**
 * Class representing a file descriptor.
 * Corresponds to set F_{dsc} in NSS'09 paper.
 *
 * According to clone(2), file descriptors may be shared
 * across processes if flag CLONE_FILES is set upon clone() syscall.
 * For this reason, method {@link FiledescrName#shareWith} allows to add arbitrary
 * many PIDs.
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
		pid = SharedFiledescr.getSharedPid(pid);
		return new FiledescrName(host, pid, fd, PREFIX_FILE + host + "." + pid + "." + fd);
	}

	public String getHost() {
		return _host;
	}

	@Override
	public boolean hasPid(int pid) {
		return _pid == pid;
	}

	public int getFd() {
		return _fd;
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