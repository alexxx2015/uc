package de.tum.in.i22.uc.pmp.core.shared;

import java.util.List;

public interface IPmpAuthorizationAction {

	boolean getAuthorizationAction();

	List<IPmpExecuteAction> getExecuteActions();

	List<Param<?>> getModifiers();

	void setExecuteActions(List<IPmpExecuteAction> executeActions);

	void setModifiers(List<Param<?>> modifiers);

	void addModifier(Param<?> parameter);

	void addExecuteAction(IPmpExecuteAction executeAction);

	long getDelay();

	void setDelay(long delay);

	boolean getType();

	void setType(boolean type);

	String getName();

	void setName(String name);

	IPmpAuthorizationAction getFallback();

	void setFallback(IPmpAuthorizationAction fallback);

	String getFallbackName();

	void setFallbackName(String fallbackName);

}