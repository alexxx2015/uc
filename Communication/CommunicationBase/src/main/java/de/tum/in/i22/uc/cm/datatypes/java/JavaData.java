package de.tum.in.i22.uc.cm.datatypes.java;

import de.tum.in.i22.uc.cm.datatypes.basic.DataBasic;
import de.tum.in.i22.uc.cm.settings.Settings;

public abstract class JavaData extends DataBasic {

    protected static final String DLM = Settings.getInstance().getJavaNamingDelimiter();

    public JavaData(String id) {
	super(id);
    }

}
