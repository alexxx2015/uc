package de.tum.in.i22.pdp.util;

import java.util.ArrayList;
import java.util.List;

import de.tum.in.i22.pdp.datatypes.IContainer;
import de.tum.in.i22.pdp.datatypes.IData;
import de.tum.in.i22.pdp.datatypes.basic.ContainerBasic;
import de.tum.in.i22.pdp.datatypes.basic.DataBasic;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpBoolean;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpContainer;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpContainerList;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpData;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpDataList;
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
	
	public static List<IContainer> convertToList(GpContainerList gpList) {
		List<IContainer> list = new ArrayList<IContainer>();
		if (gpList != null && gpList.isInitialized()) {
			List<GpContainer> gpListList = gpList.getContainerElementList();
			for (GpContainer gpContainer:gpListList) {
				list.add(new ContainerBasic(gpContainer));
			}
		}
		return list;
	}
	
	public static List<IData> convertToList(GpDataList gpList) {
		List<IData> list = new ArrayList<IData>();
		if (gpList != null && gpList.isInitialized()) {
			List<GpData> gpListList = gpList.getDataElementList();
			for (GpData gpData:gpListList) {
				list.add(new DataBasic(gpData));
			}
		}
		return list;
	}
	
	public static GpContainerList convertToGpContainerList(List<IContainer> list) {
		GpContainerList.Builder gp = GpContainerList.newBuilder();
		if (list != null)
			for (IContainer c:list) {
				gp.addContainerElement(ContainerBasic.createGpbContainer(c));
			}
		return gp.build();
	}
	
	public static GpDataList convertToGpList(List<IData> list) {
		GpDataList.Builder gp = GpDataList.newBuilder();
		if (list != null)
			for (IData d:list) {
				gp.addDataElement(DataBasic.createGpbData(d));
			}
		
		return gp.build();
	}
}
