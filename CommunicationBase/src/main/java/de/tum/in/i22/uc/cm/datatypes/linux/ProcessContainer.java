package de.tum.in.i22.uc.cm.datatypes.linux;

import java.util.Objects;

import de.tum.in.i22.uc.cm.basic.ContainerBasic;

/**
 * Class representing a processes container.
 * Corresponds to set P in NSS'09 paper.
 *
 * @author Florian Kelbert
 *
 */
public class ProcessContainer extends ContainerBasic {
	private final String _host;
	private final int _pid;

	public ProcessContainer(String host, int pid) {
		_host = host;
		_pid = pid;
	}

	public String getHost() {
		return _host;
	}

	public int getPid() {
		return _pid;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ProcessContainer) {
			ProcessContainer o = (ProcessContainer) obj;
			return Objects.equals(_host, o._host)
					&& Objects.equals(_pid, o._pid);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(_host, _pid);
	}
}
