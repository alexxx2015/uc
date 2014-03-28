namespace cpp de.tum.i22.in.uc.cm.thrift
namespace csharp de.tum.i22.in.uc.cm.thrift
namespace java de.tum.i22.in.uc.cm.thrift

include "Types.thrift"

service TAny2Pdp  {
	
	///////////////	
	// PEP 2 PDP //
	///////////////	
	Types.Response notifyEventSync(1: Types.Event e),
	oneway void notifyEventAsync(1: Types.Event e),
	
	
	///////////////	
	// PXP 2 PDP //
	///////////////	
	bool registerPxp(1: Types.Pxp pxp),	
	
	
	///////////////	
	// PMP 2 PDP //
	///////////////	
	Types.StatusType deployMechanism(1: string mechanism),
	
	//TODO: This method returns the mechanism together with the current evaluation status.
	//      i.e. in case of cardinalities, the value of the counter so far
	// IMechanism exportMechanism(1:string par);
	
	//Overloading is not supported
	Types.StatusType revokeMechanism1 (1: string policyName),
	Types.StatusType revokeMechanism2 (1: string policyName, 2: string mechName),

	// This method would make sense to be remotely invoked only if the path was a URL,
	// but this is not supported yet. Still, for completeness, we expose this method too.
	Types.StatusType deployPolicy (1: string policyFilePath),
	
	//HashMap<String, ArrayList<IPdpMechanism>> listMechanisms();
	map<string,list<string>> listMechanisms()
}




service TAny2Pip  {
	
	///////////////	
	// PEP 2 PIP //
	///////////////	
	// Still no idea how to handle this
	// public IStatus updateInformationFlowSemantics(IPipDeployer deployer, File jarFile, ConflictResolution flagForTheConflictResolution);
	
	
	///////////////	
	// PMP 2 PIP //
	///////////////	
	Types.StatusType initialRepresentation(1: Types.Container container,2: Types.Data data),
	
	    
	///////////////	
	// PIP 2 PIP //    
    ///////////////	
	bool hasAllData(1: set<Types.Data> data),
    bool hasAnyData(1: set<Types.Data> data),
    bool hasAllContainers(1: set<Types.Container> container),
    bool hasAnyContainer(1: set<Types.Container> container),
    Types.StatusType notifyActualEvent(1:Types.Event event),
    Types.StatusType notifyDataTransfer(1:Types.Name containerName, 2:set<Types.Data> data)
	
	
	///////////////	
	// PDP 2 PIP //
	///////////////	
	bool evaluatePredicateSimulatingNextState(1:Types.Event event, 2:string predicate),
	bool evaluatePredicatCurrentState(1:string predicate),
	set<Types.Container> getContainerForData(1:Types.Data data),
	set<Types.Data> getDataInContainer(1:Types.Container container),
	//Already present in  PIP2PIP, with same behavior --> StatusType notifyActualEvent(1:Types.Event event),
	Types.StatusType startSimulation(),
	Types.StatusType stopSimulation(),
	bool isSimulating()

}



service TAny2Pmp  {

	///////////////	
	// PMP 2 PMP //
    ///////////////	
	// Not sure yet what the interface between PMPs will be,
    // so for the time being we duplicate the policy management methods
    // relative to pdps. Names take Pmp as suffix to be distignuishable in TAny2any.
    // notice that these methods need to be cahnged, as soon as we know what the 
    // interface between 2 PMPs will look like

	Types.StatusType deployMechanismPmp(1: string mechanism),

	//Overloading is not supported
	Types.StatusType revokeMechanism1Pmp (1: string policyName),
	Types.StatusType revokeMechanism2Pmp (1: string policyName, 2: string mechName),

	// This method would make sense to be remotely invoked only if the path was a URL,
	// but this is not supported yet. Still, for completeness, we expose this method too.
	Types.StatusType deployPolicyPmp (1: string policyFilePath),

	//TODO: This method returns the mechanism together with the current evaluation status.
	//      i.e. in case of cardinalities, the value of the counter so far
	// IMechanism exportMechanism(1:string par);
	
	//HashMap<String, ArrayList<IPdpMechanism>> listMechanisms();
	map<string, list<string>> listMechanismsPmp()
	
 }



service TAny2Any  {

	// This method aggregates all the servers before
	// If thrift supported multiple inheritance, this would be equivalent to
	//
	// service TAny2Any extends TAny2Pdp,TAny2Pip,TAny2Pmp
	

	///////////////	
	// PEP 2 PDP //
	///////////////	
	Types.Response notifyEventSync(1: Types.Event e),
	oneway void notifyEventAsync(1: Types.Event e),
	
	
	///////////////	
	// PXP 2 PDP //
	///////////////	
	bool registerPxp(1: Types.Pxp pxp),	
	
	
	///////////////	
	// PMP 2 PDP //
	///////////////	
	Types.StatusType deployMechanism(1: string mechanism),
	
	//TODO: This method returns the mechanism together with the current evaluation status.
	//      i.e. in case of cardinalities, the value of the counter so far
	// IMechanism exportMechanism(1:string par);
	
	//Overloading is not supported
	Types.StatusType revokeMechanism1 (1: string policyName),
	Types.StatusType revokeMechanism2 (1: string policyName, 2: string mechName),

	// This method would make sense to be remotely invoked only if the path was a URL,
	// but this is not supported yet. Still, for completeness, we expose this method too.
	Types.StatusType deployPolicy (1: string policyFilePath),
	
	//HashMap<String, ArrayList<IPdpMechanism>> listMechanisms();
	map<string,list<string>> listMechanisms()
	
	///////////////	
	// PEP 2 PIP //
	///////////////	
	// Still no idea how to handle this
	// public IStatus updateInformationFlowSemantics(IPipDeployer deployer, File jarFile, ConflictResolution flagForTheConflictResolution);
	
	
	///////////////	
	// PMP 2 PIP //
	///////////////	
	Types.StatusType initialRepresentation(1: Types.Container container,2: Types.Data data),
	
	    
	///////////////	
	// PIP 2 PIP //    
    ///////////////	
	bool hasAllData(1: set<Types.Data> data),
    bool hasAnyData(1: set<Types.Data> data),
    bool hasAllContainers(1: set<Types.Container> container),
    bool hasAnyContainer(1: set<Types.Container> container),
    Types.StatusType notifyActualEvent(1:Types.Event event),
    Types.StatusType notifyDataTransfer(1:Types.Name containerName, 2:set<Types.Data> data)
	
	
	///////////////	
	// PDP 2 PIP //
	///////////////	
	bool evaluatePredicateSimulatingNextState(1:Types.Event event, 2:string predicate),
	bool evaluatePredicatCurrentState(1:string predicate),
	set<Types.Container> getContainerForData(1:Types.Data data),
	set<Types.Data> getDataInContainer(1:Types.Container container),
	//Already present in  PIP2PIP, with same behavior --> StatusType notifyActualEvent(1:Types.Event event),
	Types.StatusType startSimulation(),
	Types.StatusType stopSimulation(),
	bool isSimulating()
	
		///////////////	
	// PMP 2 PMP //
    ///////////////	
	// Not sure yet what the interface between PMPs will be,
    // so for the time being we duplicate the policy management methods
    // relative to pdps. Names take Pmp as suffix to be distignuishable in TAny2any.
    // notice that these methods need to be cahnged, as soon as we know what the 
    // interface between 2 PMPs will look like

	Types.StatusType deployMechanismPmp(1: string mechanism),

	//Overloading is not supported
	Types.StatusType revokeMechanism1Pmp (1: string policyName),
	Types.StatusType revokeMechanism2Pmp (1: string policyName, 2: string mechName),

	// This method would make sense to be remotely invoked only if the path was a URL,
	// but this is not supported yet. Still, for completeness, we expose this method too.
	Types.StatusType deployPolicyPmp (1: string policyFilePath),

	//TODO: This method returns the mechanism together with the current evaluation status.
	//      i.e. in case of cardinalities, the value of the counter so far
	// IMechanism exportMechanism(1:string par);
	
	//HashMap<String, ArrayList<IPdpMechanism>> listMechanisms();
	map<string, list<string>> listMechanismsPmp()
	
	
}











/*****************************************
 *****************************************
 *** Old methods used by Tobias's code ***
 *****************************************
 *****************************************
 

service GenericThriftConnector {

	oneway void processEventAsync(1: Types.Event e),

	Types.Response processEventSync(1: Types.Event e)

}

service ExtendedThriftConnector extends GenericThriftConnector {
	
	oneway void processEventAsync(1: Types.Event e, 2: string senderID),

	Types.Response processEventSync(1: Types.Event e, 2: string senderID)
	
}


service MWThriftConnector {

	oneway void dumpGraph(1: string graphName),
	
	oneway void setDetectionMode(1: DetectionMode mode),
	
	string getGraphInfo(1: string graphName),

	string getFullGraph(1: string graphName),
	
	string getPartialGraph(1: string graphName, 2: long startTime, 3: long endTime)
	
}
*/
