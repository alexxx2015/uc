package de.tum.in.i22.uc.cm.datatypes.linux;

import com.google.common.base.MoreObjects;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;

/**
 * Class representing a process name; basically a process id.
 * @author Florian Kelbert
 *
 */
public class ProcessName extends NameBasic implements IProcessRelativeName {

	private static final String PREFIX_PROCESS = "PROC_";

	private final String _host;
	private final int _pid;

	private ProcessName(String host, int pid, String name) {
		super(name);

		_host = host;
		_pid = pid;
	}

	public static ProcessName create(String host, int pid) {
		return new ProcessName(host, pid, PREFIX_PROCESS + host + "." + pid);
	}

	public String getHost() {
		return _host;
	}

	@Override
	public boolean hasPid(int pid) {
		return _pid == pid;
	}
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("_host", _host)
				.add("_pid", _pid)
				.toString();
	}
}
