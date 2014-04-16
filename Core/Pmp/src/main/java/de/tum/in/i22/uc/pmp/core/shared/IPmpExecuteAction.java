package de.tum.in.i22.uc.pmp.core.shared;

import java.util.Collection;

public interface IPmpExecuteAction {

	String getName();

	Collection<Param<?>> getParams();

	Param<?> getParameterForName(String name);

	String getProcessor();

	String getId();

}