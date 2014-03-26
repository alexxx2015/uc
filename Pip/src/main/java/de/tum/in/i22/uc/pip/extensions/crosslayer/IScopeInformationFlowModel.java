package de.tum.in.i22.uc.pip.extensions.crosslayer;

import de.tum.in.i22.uc.pip.core.ifm.IInformationFlowModelExtension;


public interface IScopeInformationFlowModel extends IInformationFlowModelExtension {

	@Override
	public abstract String toString();

	public boolean addScope(Scope scope);
	public boolean openScope(Scope scope);

	public boolean removeScope(Scope scope);

	public boolean closeScope(Scope scope);
	public boolean isScopeOpened(Scope scope);

	public Scope getOpenedScope(Scope scope);

	public String niceString();
}
