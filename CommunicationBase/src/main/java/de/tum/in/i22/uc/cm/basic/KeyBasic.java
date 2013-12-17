package de.tum.in.i22.uc.cm.basic;

import de.tum.in.i22.uc.cm.datatypes.IKey;
import de.tum.in.i22.uc.cm.gpb.PdpProtos;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpKey;

public class KeyBasic implements IKey {
	
	private int _key;
	private static int counter=0;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + _key;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KeyBasic other = (KeyBasic) obj;
		if (_key != other._key)
			return false;
		return true;
	}

	@Override
	public int getKey() {
		return _key;
	}
	
	public void setKey(int key){
		_key=key;
	}

	public KeyBasic (GpKey key){
		if (key.isInitialized()) 
			_key=key.getKey();
		else
			_key=-1;
	}
	
	public KeyBasic (int _key){
			this._key=_key;
	}
	
	public static GpKey createGpbKey(IKey k) {
		PdpProtos.GpKey.Builder gpKey = PdpProtos.GpKey.newBuilder();
		if (k.getKey() != -1)
			gpKey.setKey(k.getKey());
		else
			gpKey.setKey(-1); //TODO: find something more clever to do if key is not valid
		return gpKey.build();
	}


	public static IKey createNewKey(){
		return new KeyBasic(counter++);
	}
	
	public static IKey keyfromString(String stringKey){
		if (stringKey==null) return null;
		if (stringKey=="") return null;
		if (stringKey.matches("[0-9]*")) return new KeyBasic(Integer.decode(stringKey));
		return null;
	}
	
	
	
}
