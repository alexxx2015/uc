package de.tum.in.i22.uc.cm.interfaces;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.thrift.generator.AThriftMethod;
import de.tum.in.i22.uc.thrift.generator.AThriftService;


@AThriftService(name = "TAny2PtEditor")
public interface IAny2PtEditor {
		
	@AThriftMethod(signature="Types.TStatus specifyPolicyFor(1: set<Types.TContainer> representations, 2:string dataClass)")
	public IStatus specifyPolicyFor(Set<IContainer> representations, String dataClass);
	
}
