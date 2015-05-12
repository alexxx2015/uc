package de.tum.in.i22.uc.cm.datatypes.linux;

import java.util.HashMap;
import java.util.Map;

public class Chroots {
	/*
	 * A map, mapping the chrooted process IDs to their chroot directory. 
	 */
	private static final Map<Integer,String> chroots;
	
	static {
		chroots = new HashMap<>();
	};
	
	
	public static void chroot(int pid, String dir) {
		chroots.put(pid, dir);
	}
	
	public static boolean isChrooted(int pid) {
		return chroots.containsKey(pid);
	}
	
	public static String directory(int pid) {
		return chroots.get(pid);
	}
	
	public static void exit(int pid) {
		chroots.remove(pid);
	}
}
