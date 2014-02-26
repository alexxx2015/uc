package de.tum.in.i22.pip.core.eventdef.Linux;

import de.tum.in.i22.uc.cm.basic.NameBasic;


class ProcessName extends NameBasic {

	private static final String PREFIX_PROCESS = "PROC_";

	private final String _host;
	private final String _pid;

	private ProcessName(String host, String pid, String name) {
		super(name);

		_host = host;
		_pid = pid;
	}

	public static ProcessName create(String host, String pid) {
		return new ProcessName(host, pid, PREFIX_PROCESS + host + "x" + pid);
	}

	public String getHost() {
		return _host;
	}

	public String getPid() {
		return _pid;
	}

	@Override
	public String toString() {
		return super.toString();
	}
}