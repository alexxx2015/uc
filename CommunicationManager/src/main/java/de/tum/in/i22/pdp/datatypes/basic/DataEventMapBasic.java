package de.tum.in.i22.pdp.datatypes.basic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import de.tum.in.i22.pdp.datatypes.IData;
import de.tum.in.i22.pdp.datatypes.IDataEventMap;
import de.tum.in.i22.pdp.datatypes.IEvent;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpData;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpDataEventMap;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpDataEventMap.GpDataEventMapEntry;
import de.tum.in.i22.pdp.gpb.PdpProtos.GpEvent;

public class DataEventMapBasic implements IDataEventMap {
	private static final Logger _logger = Logger.getLogger(DataEventMapBasic.class);
	
	private Map<IData, IEvent> _map;
	
	public DataEventMapBasic(Map<IData, IEvent> map) {
		super();
		_map = map;
	}

	public DataEventMapBasic(GpDataEventMap gpDataEventMap) {
		if (gpDataEventMap == null)
			return;
		
		List<GpDataEventMapEntry> list = gpDataEventMap.getMapEntryList();
		if (list != null && !list.isEmpty()) {
			_map = new HashMap<>();
			
			for (GpDataEventMapEntry entry:list) {
				GpData gpData = entry.getKey();
				GpEvent gpEvent = entry.getValue();
				
				_map.put(new DataBasic(gpData), new EventBasic(gpEvent));
			}
		}
	}

	@Override
	public Map<IData, IEvent> getMap() {
		return _map;
	}
	
	/**
	 * 
	 * @return Google Protocol Buffer object corresponding to IDataEventMap
	 */
	public static GpDataEventMap createGpbDataEventMap(
			IDataEventMap dataEventMap) {
		if (dataEventMap == null)
			return null;
		_logger.trace("Build GpDataEventMap");
		GpDataEventMap.Builder gp = GpDataEventMap.newBuilder();
		Map<IData, IEvent> map = dataEventMap.getMap();
		if (map != null && !map.isEmpty()) {
			Set<IData> keySet = map.keySet();
			for (IData d:keySet) {
				GpDataEventMapEntry.Builder gpe = GpDataEventMapEntry.newBuilder();
				gpe.setKey(DataBasic.createGpbData(d));
				gpe.setValue(EventBasic.createGpbEvent(map.get(d)));
				gp.addMapEntry(gpe);
			}
		}
		return gp.build();
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean isEqual = false;
		if (obj != null && this.getClass() == obj.getClass()) {
			DataEventMapBasic o = (DataEventMapBasic)obj;
			isEqual = CompareUtil.areMapsEqual(_map, o.getMap());
		}
		return isEqual;
	}

}
