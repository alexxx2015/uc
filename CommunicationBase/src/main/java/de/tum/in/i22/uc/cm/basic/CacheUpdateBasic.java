package de.tum.in.i22.uc.cm.basic;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.tum.in.i22.uc.cm.datatypes.ICacheUpdate;
import de.tum.in.i22.uc.cm.datatypes.IKey;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpCacheUpdate;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpCacheUpdate.GpCacheUpdateEntry;


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
	public String getScopeId() {
		return _scopeId;
	}
	
	
	public CacheUpdateBasic(){
		_scopeId="<scope not initialized>";
		_map=null;
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
					_map.put(new KeyBasic(entry.getKey()), entry.getValue().getValue());
				}
			}
				
		 }
		 
	}

	public void setScopeId(String _scopeId) {
		this._scopeId = _scopeId;
	}


	public void setMap(Map<IKey, Boolean> _map) {
		this._map = new HashMap<IKey, Boolean>(_map);
	}


	
}
