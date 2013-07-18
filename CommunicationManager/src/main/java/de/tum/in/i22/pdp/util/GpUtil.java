package de.tum.in.i22.pdp.util;

import de.tum.in.i22.pdp.gpb.PdpProtos.GpString;

public class GpUtil {
	public static GpString createGpString(String par) {
		GpString.Builder gpStringBuilder = GpString.newBuilder();
		gpStringBuilder.setValue(par);
		return gpStringBuilder.build();
	}
}
