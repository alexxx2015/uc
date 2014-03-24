package de.tum.in.i22.uc.cm.datatypes.linux;
//package de.tum.in.i22.uc.cm.datatypes.Linux;
//
//import java.util.HashSet;
//import java.util.Objects;
//import java.util.Set;
//
//import de.tum.in.i22.uc.cm.basic.NameBasic;
//
///**
// * According to clone(2), file descriptors may be shared
// * across processes if flag CLONE_FILES is set upon clone() syscall.
// *
// * @author Florian Kelbert
// *
// */
//public class SharedFiledescrName extends NameBasic {
//
//	private static final String PREFIX_FILE = "FILE_";
//
//	private final String _host;
//	private final Set<Integer> _pids = new HashSet<Integer>();
//	private final String _fd;
//
//	private SharedFiledescrName(String host, Set<Integer> pids, String fd, String name) {
//		super(name);
//
//		_host = host;
//		_pids.addAll(pids);
//		_fd = fd;
//	}
//
//	public static SharedFiledescrName create(String host, Set<Integer> pids, String fd) {
//		return new SharedFiledescrName(host, pids, fd, PREFIX_FILE + host + "." + pids + "." + fd);
//	}
//
//	public String getHost() {
//		return _host;
//	}
//
//	public Set<Integer> getPids() {
//		return _pids;
//	}
//
//	public String getFd() {
//		return _fd;
//	}
//
//	public void shareWith(int pid) {
//		_pids.add(pid);
//	}
//
//	public void unshareWith(int pid) {
//		_pids.remove(pid);
//	}
//
//	public boolean isSharedWith(int pid) {
//		return _pids.contains(pid);
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (obj instanceof SharedFiledescrName) {
//			SharedFiledescrName o = (SharedFiledescrName) obj;
//			return Objects.equals(_host, o._host)
//					&& Objects.equals(_pids, o._pids)
//					&& Objects.equals(_fd, o._fd);
//		}
//		return super.equals(obj);
//	}
//
//	@Override
//	public int hashCode() {
//		return Objects.hash(_host, _pids, _fd);
//	}
//
//	@Override
//	public String toString() {
//		return com.google.common.base.Objects.toStringHelper(this)
//				.add("_host", _host)
//				.add("_pids", _pids)
//				.add("_fd", _fd)
//				.toString();
//	}
//}