package de.tum.in.i22.uc.cm.datatypes.java.containers;

public class ArrayContainer extends ReferenceContainer {

    protected String type;
    
    public ArrayContainer(String id) {
	super(id);
	
	String comps[] = id.split("\\" + DLM);
	pid = comps[0];
	type = comps[1];
	address = comps[2];
    }
    
    public ArrayContainer(String pid, String type, String address) {
	this(pid + DLM + type + DLM + address);
    }
    
    public String getType() {
	return type;
    }

}
