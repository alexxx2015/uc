import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import de.tum.in.i22.pdp.datatypes.basic.CompareUtil;


public class CompareUtilTest {

	@Test
	public void testCompareString() {
		Assert.assertTrue(CompareUtil.areObjectsEqual(null, null));
		Assert.assertTrue(CompareUtil.areObjectsEqual("pdp", "pdp"));
		Assert.assertFalse(CompareUtil.areObjectsEqual(null, "pdp"));
		Assert.assertFalse(CompareUtil.areObjectsEqual("pdp", null));
	}
	
	@Test
	public void testCompareLists() {
		List<String> list1 = new ArrayList<String>(Arrays.asList("a", "bc", "def"));
		List<String> list2 = new ArrayList<String>(Arrays.asList("bc", "a", "def"));
		List<String> list3 = new ArrayList<String>(Arrays.asList("a", "bc", "def"));
		List<String> list4 = new ArrayList<String>(Arrays.asList("a", "bc", "def", "t"));
		
		Assert.assertTrue(CompareUtil.areListsEqual(list1, list2));
		Assert.assertTrue(CompareUtil.areListsEqual(list3, list1));
		Assert.assertFalse(CompareUtil.areListsEqual(list1, list4));
		Assert.assertTrue(CompareUtil.areListsEqual(null, null));
	}
	
	@Test
	public void testCompareMaps() {
		Map<String, String> map1 = new HashMap<String, String>();
		map1.put("k1", "v1");
		map1.put("k2", "v2");
		
		Map<String, String> map2 = new HashMap<String, String>();
		map2.put("k1", "v1");
		map2.put("k2", "v2");
		
		Map<String, String> map3 = new HashMap<String, String>();
		map3.put("k4", "v1");
		map3.put("k2", "v2");
		
		Assert.assertTrue(CompareUtil.areMapsEqual(map1, map2));
		Assert.assertFalse(CompareUtil.areMapsEqual(map1, map3));
		Assert.assertFalse(CompareUtil.areMapsEqual(map3, map2));
		Assert.assertTrue(CompareUtil.areMapsEqual(null, null));
	}

}
