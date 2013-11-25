package de.tum.in.i22.uc.cm.datatypes;

import java.util.Map;

public interface ICacheUpdate {
	public String getScopeId();

	boolean getVal(IKey k);

	Map<IKey, Boolean> getMap();

	
	
}
