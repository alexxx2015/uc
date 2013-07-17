package de.tum.in.i22.pdp.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import de.tum.in.i22.pdp.gpb.PdpProtos.GpString;

public class GpUtil {
	public static GpString createGpString(String par) {
		GpString.Builder gpStringBuilder = GpString.newBuilder();
		gpStringBuilder.setValue(par);
		return gpStringBuilder.build();
	}
	
	public static int convertToInt(byte[] bytes) {
		ByteBuffer bb = ByteBuffer.wrap(bytes);
		bb.order(ByteOrder.BIG_ENDIAN);
		return bb.getInt();
	}
}
