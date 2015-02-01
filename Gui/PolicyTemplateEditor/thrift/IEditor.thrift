namespace java de.tum.in.i22.policyeditor.server  // defines the namespace   

/**
* @Author Cipri L.
*/
    

include "Types.thrift"
    	    
    service TAny2Editor { 
    	// public abstract de.tum.in.i22.policyeditor.server.IStatus de.tum.in.i22.uc.cm.interfaces.IPmp2Pmp.specifyPolicyFor(java.util.Set,java.lang.String)
		Types.TStatus specifyPolicyFor(1: set<Types.TContainer> representations, 2:string dataClass),
    }
    
    
