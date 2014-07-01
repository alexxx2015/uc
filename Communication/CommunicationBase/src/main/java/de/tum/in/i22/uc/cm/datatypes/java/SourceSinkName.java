package de.tum.in.i22.uc.cm.datatypes.java;

import org.junit.Assert;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.settings.Settings;

/**
 * Class representing the name of a Point of interest (POI), i.e. either a
 * source or a sink in an instrumented
 * 
 * @author Enrico Lovat
 * 
 */
public class SourceSinkName extends NameBasic {
	private static final String sep = Settings.getInstance()
			.getJoanaPidPoiSeparator();
	private int pid = -1;
	private String poiName = "";
	private String type="";
	
	public SourceSinkName(String poiName) {
		super(poiName);
		Assert.assertNotNull(poiName);
		Assert.assertNotEquals(poiName, "");

		String[] parts = poiName.split(sep);
		Assert.assertEquals(3, parts.length);

		pid = Integer.valueOf(parts[0]);
		type = parts[1];
		poiName = parts[2];
	}

	public SourceSinkName(int pid, String type, String poiName) {
		this(pid + sep + type+ sep + poiName);
	}

	public int getPid() {
		return pid;
	}

	public String getPoiName() {
		return poiName;
	}

	public String getType() {
		return type;
	}

	public String toString() {
		return (pid + sep + type + poiName);
	}
}
