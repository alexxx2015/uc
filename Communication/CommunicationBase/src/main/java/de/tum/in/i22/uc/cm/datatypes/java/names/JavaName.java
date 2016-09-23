package de.tum.in.i22.uc.cm.datatypes.java.names;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.settings.Settings;

public abstract class JavaName extends NameBasic {

	public static final String DLM = Settings.getInstance().getJavaNamingDelimiter();

	public JavaName(String name) {
		super(name);
	}
	
	public abstract String getPid();

}