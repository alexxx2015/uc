package de.tum.in.i22.pip.core.eventdef.Linux;

import de.tum.in.i22.uc.cm.basic.NameBasic;



class FiledescrName extends NameBasic {

	private static final String PREFIX_FILE = "FILE_";

	private final String _host;
	private final String _pid;
	private final String _fd;

	private FiledescrName(String host, String pid, String fd, String name) {
		super(name);

		_host = host;
		_pid = pid;
		_fd = fd;
	}

	public static FiledescrName create(String host, String pid, String fd) {
		return new FiledescrName(host, pid, fd, PREFIX_FILE + host + "x" + pid + "x" + fd);
	}

	public String getHost() {
		return _host;
	}

	public String getPid() {
		return _pid;
	}

	public String getFd() {
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
}