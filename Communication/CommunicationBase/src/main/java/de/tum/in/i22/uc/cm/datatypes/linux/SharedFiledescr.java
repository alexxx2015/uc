package de.tum.in.i22.uc.cm.datatypes.linux;

import java.util.HashMap;
import java.util.Map;

/**
 * According to clone(2), processes might share their file descriptor table
 * if flag CLONE_FILES is set upon clone() system call.
 * This behavior is implemented by the methods of this class.
 *
 * @author Florian Kelbert
 *
 */
public class SharedFiledescr {
	/**
	 * Maps PIDs to PIDs. If a PID pid1 maps to another PID pid2, then
	 * pid2 must be used whenever a file descriptor is used by by pid1.
	 * This behavior is implemented by {@link FiledescrName#create(String, int, int)}.
	 */
	private static Map<Integer,Integer> shareFds = new HashMap<>();


	/**
	 * This method is to be invoked if two processes start to share their
	 * file descriptor, i.e. upon clone() with flag CLONE_FILES set.
	 *
	 * @param newpid the PID of the created process
	 * @param oldpid the PID of the process that called clone()
	 */
	public static void newShare(int newpid, int oldpid) {
		Integer entry = shareFds.get(oldpid);
		if (entry == null) {
			entry = oldpid;
			shareFds.put(oldpid, entry);
		}

		shareFds.put(newpid, entry);
	}

	/**
	 * This method returns the PID that is to be used for
	 * file descriptor names of the given process id. The result
	 * of this method might either be a PID of another process
	 * with which the specified process id shared their file descriptors
	 * before, or the calling processes' pid itself, if the file descriptors
	 * have not been shared with any other process.
	 *
	 * @param pid
	 * @return
	 */
	public static int getSharedPid(int pid) {
		Integer entry = shareFds.get(pid);
		return entry != null ? entry : pid;
	}

	public static void unshare(int pid) {


		/**
		 * TODO: If a process gets killed, there won't be a exit call
		 * and therefore the entry will remain in the table.
		 */

		shareFds.remove(pid);
	}
}