package de.tum.in.i22.uc.cm.datatypes.java.containers;

public class ArrayElementContainer extends ValueContainer {

    protected String type;
    protected String address;
    protected int index;
    
    public ArrayElementContainer(String id) {
	super(id, true);
	
	String comps[] = id.split("\\" + DLM);
	pid = comps[0];
	type = comps[1];
	address = comps[2];
	index = Integer.valueOf(comps[3]);
    }
    
    public ArrayElementContainer(String pid, String type, String address, int index) {
	this(pid + DLM + type + DLM + address + DLM + index);
    }
    
    public String getType() {
	return type;
    }
    
    public String getAddress() {
	return address;
    }
    
    public int getIndex() {
	return index;
    }

}
