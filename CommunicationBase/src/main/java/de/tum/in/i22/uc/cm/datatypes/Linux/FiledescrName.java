package de.tum.in.i22.uc.cm.datatypes.Linux;

import java.util.Objects;

import de.tum.in.i22.uc.cm.basic.NameBasic;

public class FiledescrName extends NameBasic {

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
}