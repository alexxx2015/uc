package de.tum.in.i22.uc.cm.basic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class CompareUtil {
	/**
	 * 
	 * @param s1
	 * @param s2
	 * @return Compares two string and returns true if both are null or if they are equal.
	 */
	public static boolean areObjectsEqual(Object o1, Object o2) {
		if (o1 == null && o2 == null) {
			return true;
		} else if (o1 != null && o2 != null && o1.equals(o2)) {
			return true;
		} else {
			return false;
		}
	}
	
	public static <T, P> boolean areMapsEqual(Map<T, P> map1, Map<T, P> map2) {
		boolean isEqual = false;
		if (map1 == null && map2 == null) {
			isEqual = true;
		} else if (map1 != null && map2 != null) {
			Set<Entry<T, P>> changedEntries = new HashSet<Entry<T, P>>(
			       map1.entrySet());
			changedEntries.removeAll(map2.entrySet());

			if (changedEntries.isEmpty())
				isEqual = true;
		}
		
		return isEqual;
	}
	
	public static <T> boolean areListsEqual(List<T> list1, List<T> list2) {
		boolean isEqual = false;
		if (list1 == null && list2 == null) {
			isEqual = true;
		} else if (list1 != null && list2 != null) {
			Collection<T> list1Copy = new ArrayList<T>(list1);
			Collection<T> list2Copy = new ArrayList<T>(list2);
			list1Copy.removeAll(list2Copy);
			if (list1Copy.isEmpty()) {
				list1Copy = new ArrayList<T>(list1);
				list2Copy.removeAll(list1Copy);
				if (list2Copy.isEmpty())
					isEqual = true;
			}	
		}
		return isEqual;
	}
}
