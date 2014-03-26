package de.tum.in.i22.uc.cm.pdp.core;

import java.util.Collection;

public interface IPdpExecuteAction {

	String getName();

	Collection<Param<?>> getParams();

	Param<?> getParameterForName(String name);

	String getProcessor();

	String getId();

}