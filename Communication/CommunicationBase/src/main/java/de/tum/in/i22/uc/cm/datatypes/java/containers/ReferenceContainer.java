package de.tum.in.i22.uc.cm.datatypes.java.containers;

public abstract class ReferenceContainer extends JavaContainer {

    protected ReferenceContainer(String id) {
	super(id);
    }

    protected String address;
    
    public String getAddress() {
	return address;
    }
    
}
