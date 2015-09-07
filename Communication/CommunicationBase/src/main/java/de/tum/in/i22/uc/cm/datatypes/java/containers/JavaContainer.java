package de.tum.in.i22.uc.cm.datatypes.java.containers;

import java.util.List;

import de.tum.in.i22.uc.cm.datatypes.basic.ContainerBasic;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IAttribute;
import de.tum.in.i22.uc.cm.settings.Settings;

public abstract class JavaContainer extends ContainerBasic {

    protected static final String DLM = Settings.getInstance().getJavaNamingDelimiter();
    
    protected String pid;
    
    protected JavaContainer(String id) {
	super(id);
    }

    public String getPid() {
	return pid;
    }

}
