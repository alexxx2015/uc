package de.tum.in.i22.uc.cm.datatypes.java.containers;

import java.util.UUID;

public class ValueContainer extends JavaContainer {

    private ValueContainer(String pid, String uuid) {
	super(pid + DLM + uuid);
    }
    
    protected ValueContainer(String id, boolean noUUID) {
	super(id);
    }
    
    public ValueContainer(String pid) {
	this(pid, UUID.randomUUID().toString());
    }

}
