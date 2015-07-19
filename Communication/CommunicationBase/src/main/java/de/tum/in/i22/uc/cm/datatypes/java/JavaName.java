package de.tum.in.i22.uc.cm.datatypes.java;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.settings.Settings;

public abstract class JavaName extends NameBasic {

	protected static final String DLM = Settings.getInstance().getJavaNamingDelimiter();

	public JavaName(String name) {
		super(name);
	}

}