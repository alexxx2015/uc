package de.tum.in.i22.uc.cm.datatypes.java.containers;

public class ObjectContainer extends ReferenceContainer {

    protected String className;
    
    public ObjectContainer(String id) {
	super(id);
	
	String comps[] = id.split("\\" + DLM);
	pid = comps[0];
	className = comps[1];
	address = comps[2];
    }
    
    public ObjectContainer(String pid, String className, String address) {
	this(pid + DLM + className + DLM + address);
    }
    
    public String getClassName() {
	return className;
    }

}
