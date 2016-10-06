package de.tum.in.i22.uc.cm.datatypes.java.names;

public abstract class ReferenceName extends JavaName {

    public ReferenceName(String name) {
	super(name.trim());
    }

    public abstract String getAddress();
    public abstract String getClassOrType();

}
