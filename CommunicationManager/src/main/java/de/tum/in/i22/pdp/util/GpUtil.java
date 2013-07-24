package de.tum.in.i22.pdp.util;

import de.tum.in.i22.pdp.gpb.PdpProtos.GpBoolean;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpString;

public class GpUtil {
	public static GpString createGpString(String par) {
		GpString.Builder gpStringBuilder = GpString.newBuilder();
		gpStringBuilder.setValue(par);
		return gpStringBuilder.build();
	}
	
	public static GpBoolean createGpBoolean(boolean par) {
		GpBoolean.Builder gpBooleanBuilder = GpBoolean.newBuilder();
		gpBooleanBuilder.setValue(par);
		return gpBooleanBuilder.build();
	}
}
