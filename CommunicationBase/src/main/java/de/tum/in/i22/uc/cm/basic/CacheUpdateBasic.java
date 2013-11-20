package de.tum.in.i22.uc.cm.basic;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.tum.in.i22.uc.cm.datatypes.ICacheUpdate;
import de.tum.in.i22.uc.cm.datatypes.IKey;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpCacheUpdate;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpCacheUpdate.GpCacheUpdateEntry;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpKey;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpEvent.GpMapEntry;


public class CacheUpdateBasic implements ICacheUpdate {
	private String _scopeId;
	private HashMap<IKey,Boolean> _map;
	
	
	@Override
	public Map<IKey,Boolean> getMap() {
		return _map;
	}
	
	
	@Override
	public boolean getVal(IKey k) {
		return false;
	}


	@Override
	public String getScopeID() {
		return _scopeId;
	}
	
	
	
	public CacheUpdateBasic(GpCacheUpdate gpCU) {
		 if (gpCU.isInitialized()) {
			 //read scope
			 if (gpCU.hasScopeId()){
				 _scopeId=gpCU.getScopeId();
			 } else {
				 _scopeId="<scope not initialized>";
			 }
			 
			 //read map
			int count = gpCU.getMapCount();
			if (count > 0) {
				_map = new HashMap<IKey,Boolean>();
				Iterator<GpCacheUpdateEntry> it = gpCU.getMapList().iterator();
				while (it.hasNext()) {
					GpCacheUpdateEntry entry = it.next();
					// FIXME: incomplete implementation
					//_map.put(entry.getKey(), entry.getValue());
				}
			}
				
		 }
		 
	}
	
	
	
}
