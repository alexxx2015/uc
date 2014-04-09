/* 
 * Auto-generated Thrift definitions.
 * Generated on 2014/04/09, 17:46:24
 * from the following interface definitions:
 * - de.tum.in.i22.uc.cm.interfaces.IPdp2Pip
 * - de.tum.in.i22.uc.cm.interfaces.IPip2Pmp
 * - de.tum.in.i22.uc.cm.interfaces.IPxp2Pdp
 * - de.tum.in.i22.uc.cm.interfaces.IAny2Pxp
 * - de.tum.in.i22.uc.cm.interfaces.IPmp2Pdp
 * - de.tum.in.i22.uc.cm.interfaces.IPdp2Any
 * - de.tum.in.i22.uc.cm.interfaces.IAny2Pmp
 * - de.tum.in.i22.uc.cm.interfaces.IPip2Any
 * - de.tum.in.i22.uc.cm.interfaces.IPmp2Pip
 * - de.tum.in.i22.uc.cm.interfaces.IPmp2Pmp
 * - de.tum.in.i22.uc.cm.interfaces.IAny2Any
 * - de.tum.in.i22.uc.cm.interfaces.IPdp2Pxp
 * - de.tum.in.i22.uc.cm.interfaces.IPip2Pip
 * - de.tum.in.i22.uc.cm.interfaces.IAny2Pip
 * - de.tum.in.i22.uc.cm.interfaces.IPep2Pip
 * - de.tum.in.i22.uc.cm.interfaces.IPmp2Any
 * - de.tum.in.i22.uc.cm.interfaces.IPep2Any
 * - de.tum.in.i22.uc.cm.interfaces.IPxp2Any
 * - de.tum.in.i22.uc.cm.interfaces.IAny2Pdp
 * - de.tum.in.i22.uc.cm.interfaces.IPep2Pdp
 */

namespace cpp de.tum.in.i22.uc.thrift.types
namespace csharp de.tum.in.i22.uc.thrift.types
namespace java de.tum.in.i22.uc.thrift.types

include "Types.thrift"

/*
 * Thrift interfaces defined by
 * de.tum.in.i22.uc.cm.interfaces.IPdp2Pip
 */
service TPdp2Pip {
	// public abstract boolean de.tum.in.i22.uc.cm.interfaces.IPdp2Pip.evaluatePredicateSimulatingNextState(de.tum.in.i22.uc.cm.datatypes.IEvent,java.lang.String)
	bool evaluatePredicateSimulatingNextState(1:Types.TEvent event, 2:string predicate),

	// public abstract boolean de.tum.in.i22.uc.cm.interfaces.IPdp2Pip.isSimulating()
	bool isSimulating(),

	// public abstract boolean de.tum.in.i22.uc.cm.interfaces.IPdp2Pip.evaluatePredicateCurrentState(java.lang.String)
	bool evaluatePredicatCurrentState(1:string predicate),

	// public abstract java.util.Set de.tum.in.i22.uc.cm.interfaces.IPdp2Pip.getContainersForData(de.tum.in.i22.uc.cm.datatypes.IData)
	set<Types.TContainer> getContainerForData(1:Types.TData data),

	// public abstract java.util.Set de.tum.in.i22.uc.cm.interfaces.IPdp2Pip.getDataInContainer(de.tum.in.i22.uc.cm.datatypes.IContainer)
	set<Types.TData> getDataInContainer(1:Types.TContainer container),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPdp2Pip.startSimulation()
	Types.TStatus startSimulation(),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPdp2Pip.stopSimulation()
	Types.TStatus stopSimulation(),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPdp2Pip.update(de.tum.in.i22.uc.cm.datatypes.IEvent)
	Types.TStatus update(1:Types.TEvent event)
}


/*
 * Thrift interfaces defined by
 * de.tum.in.i22.uc.cm.interfaces.IPip2Pmp
 */
service TPip2Pmp {
	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPip2Pmp.informRemoteDataFlow(de.tum.in.i22.uc.cm.distribution.Location,de.tum.in.i22.uc.cm.distribution.Location,java.util.Set)
	Types.TStatus informRemoteDataFlow(1: string srcAddress, 2: Types.int srcPort, 3: string dstAddress, 4: Types.int dstPort, 5: set<Types.TData> data)
}


/*
 * Thrift interfaces defined by
 * de.tum.in.i22.uc.cm.interfaces.IPxp2Pdp
 */
service TPxp2Pdp {
	// public abstract boolean de.tum.in.i22.uc.cm.interfaces.IPxp2Pdp.registerPxp(de.tum.in.i22.uc.cm.basic.PxpSpec)
	bool registerPxp(1: Types.TPxpSpec pxp)
}


/*
 * Thrift interfaces defined by
 * de.tum.in.i22.uc.cm.interfaces.IAny2Pxp
 */
service TAny2Pxp {
	// public abstract void de.tum.in.i22.uc.cm.interfaces.IPdp2Pxp.executeAsync(java.util.List)
	oneway void executeAsync(1: list<Types.TEvent> eventList),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPdp2Pxp.executeSync(java.util.List)
	Types.TStatus executeSync(1: list<Types.TEvent> eventList)
}


/*
 * Thrift interfaces defined by
 * de.tum.in.i22.uc.cm.interfaces.IPmp2Pdp
 */
service TPmp2Pdp {
	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPmp2Pdp.revokePolicy(java.lang.String)
	Types.TStatus revokePolicy (1: string policyName),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPmp2Pdp.revokeMechanism(java.lang.String,java.lang.String)
	Types.TStatus revokeMechanism (1: string policyName, 2: string mechName),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPmp2Pdp.deployPolicyURI(java.lang.String)
	Types.TStatus deployPolicyURI (1: string policyFilePath),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPmp2Pdp.deployPolicyXML(java.lang.String)
	Types.TStatus deployPolicyXML (1: string XMLPolicy),

	// public abstract java.util.Map de.tum.in.i22.uc.cm.interfaces.IPmp2Pdp.listMechanisms()
	map<string,list<string>> listMechanisms(),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IMechanism de.tum.in.i22.uc.cm.interfaces.IPmp2Pdp.exportMechanism(java.lang.String)
	// TODO
}


/*
 * Thrift interfaces defined by
 * de.tum.in.i22.uc.cm.interfaces.IPdp2Any
 */
service TPdp2Any {
	// public abstract boolean de.tum.in.i22.uc.cm.interfaces.IPdp2Pip.evaluatePredicateSimulatingNextState(de.tum.in.i22.uc.cm.datatypes.IEvent,java.lang.String)
	bool evaluatePredicateSimulatingNextState(1:Types.TEvent event, 2:string predicate),

	// public abstract boolean de.tum.in.i22.uc.cm.interfaces.IPdp2Pip.isSimulating()
	bool isSimulating(),

	// public abstract boolean de.tum.in.i22.uc.cm.interfaces.IPdp2Pip.evaluatePredicateCurrentState(java.lang.String)
	bool evaluatePredicatCurrentState(1:string predicate),

	// public abstract java.util.Set de.tum.in.i22.uc.cm.interfaces.IPdp2Pip.getContainersForData(de.tum.in.i22.uc.cm.datatypes.IData)
	set<Types.TContainer> getContainerForData(1:Types.TData data),

	// public abstract java.util.Set de.tum.in.i22.uc.cm.interfaces.IPdp2Pip.getDataInContainer(de.tum.in.i22.uc.cm.datatypes.IContainer)
	set<Types.TData> getDataInContainer(1:Types.TContainer container),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPdp2Pip.startSimulation()
	Types.TStatus startSimulation(),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPdp2Pip.stopSimulation()
	Types.TStatus stopSimulation(),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPdp2Pip.update(de.tum.in.i22.uc.cm.datatypes.IEvent)
	Types.TStatus update(1:Types.TEvent event),

	// public abstract void de.tum.in.i22.uc.cm.interfaces.IPdp2Pxp.executeAsync(java.util.List)
	oneway void executeAsync(1: list<Types.TEvent> eventList),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPdp2Pxp.executeSync(java.util.List)
	Types.TStatus executeSync(1: list<Types.TEvent> eventList)
}


/*
 * Thrift interfaces defined by
 * de.tum.in.i22.uc.cm.interfaces.IAny2Pmp
 */
service TAny2Pmp {
	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPip2Pmp.informRemoteDataFlow(de.tum.in.i22.uc.cm.distribution.Location,de.tum.in.i22.uc.cm.distribution.Location,java.util.Set)
	Types.TStatus informRemoteDataFlow(1: string srcAddress, 2: Types.int srcPort, 3: string dstAddress, 4: Types.int dstPort, 5: set<Types.TData> data),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPmp2Pmp.receivePolicies(java.util.Set)
	Types.TStatus remotePolicyTransfer(1: set<string> policies)
}


/*
 * Thrift interfaces defined by
 * de.tum.in.i22.uc.cm.interfaces.IPip2Any
 */
service TPip2Any {
	// public abstract java.util.Set de.tum.in.i22.uc.cm.interfaces.IPip2Pip.getContainersForData(de.tum.in.i22.uc.cm.datatypes.IData)
	set<Types.TContainer> getContainerForData(1:Types.TData data),

	// public abstract java.util.Set de.tum.in.i22.uc.cm.interfaces.IPip2Pip.getDataInContainer(de.tum.in.i22.uc.cm.datatypes.IContainer)
	set<Types.TData> getDataInContainer(1:Types.TContainer container),

	// public abstract boolean de.tum.in.i22.uc.cm.interfaces.IPip2Pip.hasAllData(java.util.Set)
	bool hasAllData(1: set<Types.TData> data),

	// public abstract boolean de.tum.in.i22.uc.cm.interfaces.IPip2Pip.hasAnyData(java.util.Set)
	bool hasAnyData(1: set<Types.TData> data),

	// public abstract boolean de.tum.in.i22.uc.cm.interfaces.IPip2Pip.hasAllContainers(java.util.Set)
	bool hasAllContainers(1: set<Types.TContainer> container),

	// public abstract boolean de.tum.in.i22.uc.cm.interfaces.IPip2Pip.hasAnyContainer(java.util.Set)
	bool hasAnyContainer(1: set<Types.TContainer> container),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPip2Pip.initialRepresentation(de.tum.in.i22.uc.cm.datatypes.IName,java.util.Set)
	Types.TStatus initialRepresentation(1: Types.TName container,2: set<Types.TData> data),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPip2Pip.update(de.tum.in.i22.uc.cm.datatypes.IEvent)
	Types.TStatus update(1:Types.TEvent event),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPip2Pmp.informRemoteDataFlow(de.tum.in.i22.uc.cm.distribution.Location,de.tum.in.i22.uc.cm.distribution.Location,java.util.Set)
	Types.TStatus informRemoteDataFlow(1: string srcAddress, 2: Types.int srcPort, 3: string dstAddress, 4: Types.int dstPort, 5: set<Types.TData> data)
}


/*
 * Thrift interfaces defined by
 * de.tum.in.i22.uc.cm.interfaces.IPmp2Pip
 */
service TPmp2Pip {
	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPmp2Pip.initialRepresentation(de.tum.in.i22.uc.cm.datatypes.IName,java.util.Set)
	Types.TStatus initialRepresentation(1: Types.TName container,2: set<Types.TData> data)
}


/*
 * Thrift interfaces defined by
 * de.tum.in.i22.uc.cm.interfaces.IPmp2Pmp
 */
service TPmp2Pmp {
	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPmp2Pmp.receivePolicies(java.util.Set)
	Types.TStatus remotePolicyTransfer(1: set<string> policies)
}


/*
 * Thrift interfaces defined by
 * de.tum.in.i22.uc.cm.interfaces.IAny2Any
 */
service TAny2Any {
	// public abstract void de.tum.in.i22.uc.cm.interfaces.IPep2Pdp.notifyEventAsync(de.tum.in.i22.uc.cm.datatypes.IEvent)
	oneway void notifyEventAsync(1: Types.TEvent e),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IResponse de.tum.in.i22.uc.cm.interfaces.IPep2Pdp.notifyEventSync(de.tum.in.i22.uc.cm.datatypes.IEvent)
	Types.TResponse notifyEventSync(1: Types.TEvent e),

	// public abstract boolean de.tum.in.i22.uc.cm.interfaces.IPxp2Pdp.registerPxp(de.tum.in.i22.uc.cm.basic.PxpSpec)
	bool registerPxp(1: Types.TPxpSpec pxp),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPmp2Pdp.revokePolicy(java.lang.String)
	Types.TStatus revokePolicy (1: string policyName),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPmp2Pdp.revokeMechanism(java.lang.String,java.lang.String)
	Types.TStatus revokeMechanism (1: string policyName, 2: string mechName),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPmp2Pdp.deployPolicyURI(java.lang.String)
	Types.TStatus deployPolicyURI (1: string policyFilePath),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPmp2Pdp.deployPolicyXML(java.lang.String)
	Types.TStatus deployPolicyXML (1: string XMLPolicy),

	// public abstract java.util.Map de.tum.in.i22.uc.cm.interfaces.IPmp2Pdp.listMechanisms()
	map<string,list<string>> listMechanisms(),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IMechanism de.tum.in.i22.uc.cm.interfaces.IPmp2Pdp.exportMechanism(java.lang.String)
	// TODO,

	// public abstract boolean de.tum.in.i22.uc.cm.interfaces.IPdp2Pip.evaluatePredicateSimulatingNextState(de.tum.in.i22.uc.cm.datatypes.IEvent,java.lang.String)
	bool evaluatePredicateSimulatingNextState(1:Types.TEvent event, 2:string predicate),

	// public abstract boolean de.tum.in.i22.uc.cm.interfaces.IPdp2Pip.isSimulating()
	bool isSimulating(),

	// public abstract boolean de.tum.in.i22.uc.cm.interfaces.IPdp2Pip.evaluatePredicateCurrentState(java.lang.String)
	bool evaluatePredicatCurrentState(1:string predicate),

	// public abstract java.util.Set de.tum.in.i22.uc.cm.interfaces.IPdp2Pip.getContainersForData(de.tum.in.i22.uc.cm.datatypes.IData)
	set<Types.TContainer> getContainerForData(1:Types.TData data),

	// public abstract java.util.Set de.tum.in.i22.uc.cm.interfaces.IPdp2Pip.getDataInContainer(de.tum.in.i22.uc.cm.datatypes.IContainer)
	set<Types.TData> getDataInContainer(1:Types.TContainer container),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPdp2Pip.startSimulation()
	Types.TStatus startSimulation(),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPdp2Pip.stopSimulation()
	Types.TStatus stopSimulation(),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPdp2Pip.update(de.tum.in.i22.uc.cm.datatypes.IEvent)
	Types.TStatus update(1:Types.TEvent event),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPep2Pip.updateInformationFlowSemantics(de.tum.in.i22.uc.cm.datatypes.IPipDeployer,java.io.File,de.tum.in.i22.uc.cm.datatypes.EConflictResolution)
	// public IStatus updateInformationFlowSemantics(IPipDeployer deployer, File jarFile, ConflictResolution flagForTheConflictResolution),

	// public abstract boolean de.tum.in.i22.uc.cm.interfaces.IPip2Pip.hasAllData(java.util.Set)
	bool hasAllData(1: set<Types.TData> data),

	// public abstract boolean de.tum.in.i22.uc.cm.interfaces.IPip2Pip.hasAnyData(java.util.Set)
	bool hasAnyData(1: set<Types.TData> data),

	// public abstract boolean de.tum.in.i22.uc.cm.interfaces.IPip2Pip.hasAllContainers(java.util.Set)
	bool hasAllContainers(1: set<Types.TContainer> container),

	// public abstract boolean de.tum.in.i22.uc.cm.interfaces.IPip2Pip.hasAnyContainer(java.util.Set)
	bool hasAnyContainer(1: set<Types.TContainer> container),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPip2Pip.initialRepresentation(de.tum.in.i22.uc.cm.datatypes.IName,java.util.Set)
	Types.TStatus initialRepresentation(1: Types.TName container,2: set<Types.TData> data),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPip2Pmp.informRemoteDataFlow(de.tum.in.i22.uc.cm.distribution.Location,de.tum.in.i22.uc.cm.distribution.Location,java.util.Set)
	Types.TStatus informRemoteDataFlow(1: string srcAddress, 2: Types.int srcPort, 3: string dstAddress, 4: Types.int dstPort, 5: set<Types.TData> data),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPmp2Pmp.receivePolicies(java.util.Set)
	Types.TStatus remotePolicyTransfer(1: set<string> policies),

	// public abstract void de.tum.in.i22.uc.cm.interfaces.IPdp2Pxp.executeAsync(java.util.List)
	oneway void executeAsync(1: list<Types.TEvent> eventList),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPdp2Pxp.executeSync(java.util.List)
	Types.TStatus executeSync(1: list<Types.TEvent> eventList)
}


/*
 * Thrift interfaces defined by
 * de.tum.in.i22.uc.cm.interfaces.IPdp2Pxp
 */
service TPdp2Pxp {
	// public abstract void de.tum.in.i22.uc.cm.interfaces.IPdp2Pxp.executeAsync(java.util.List)
	oneway void executeAsync(1: list<Types.TEvent> eventList),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPdp2Pxp.executeSync(java.util.List)
	Types.TStatus executeSync(1: list<Types.TEvent> eventList)
}


/*
 * Thrift interfaces defined by
 * de.tum.in.i22.uc.cm.interfaces.IPip2Pip
 */
service TPip2Pip {
	// public abstract java.util.Set de.tum.in.i22.uc.cm.interfaces.IPip2Pip.getContainersForData(de.tum.in.i22.uc.cm.datatypes.IData)
	set<Types.TContainer> getContainerForData(1:Types.TData data),

	// public abstract java.util.Set de.tum.in.i22.uc.cm.interfaces.IPip2Pip.getDataInContainer(de.tum.in.i22.uc.cm.datatypes.IContainer)
	set<Types.TData> getDataInContainer(1:Types.TContainer container),

	// public abstract boolean de.tum.in.i22.uc.cm.interfaces.IPip2Pip.hasAllData(java.util.Set)
	bool hasAllData(1: set<Types.TData> data),

	// public abstract boolean de.tum.in.i22.uc.cm.interfaces.IPip2Pip.hasAnyData(java.util.Set)
	bool hasAnyData(1: set<Types.TData> data),

	// public abstract boolean de.tum.in.i22.uc.cm.interfaces.IPip2Pip.hasAllContainers(java.util.Set)
	bool hasAllContainers(1: set<Types.TContainer> container),

	// public abstract boolean de.tum.in.i22.uc.cm.interfaces.IPip2Pip.hasAnyContainer(java.util.Set)
	bool hasAnyContainer(1: set<Types.TContainer> container),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPip2Pip.initialRepresentation(de.tum.in.i22.uc.cm.datatypes.IName,java.util.Set)
	Types.TStatus initialRepresentation(1: Types.TName container,2: set<Types.TData> data),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPip2Pip.update(de.tum.in.i22.uc.cm.datatypes.IEvent)
	Types.TStatus update(1:Types.TEvent event)
}


/*
 * Thrift interfaces defined by
 * de.tum.in.i22.uc.cm.interfaces.IAny2Pip
 */
service TAny2Pip {
	// public abstract boolean de.tum.in.i22.uc.cm.interfaces.IPdp2Pip.evaluatePredicateSimulatingNextState(de.tum.in.i22.uc.cm.datatypes.IEvent,java.lang.String)
	bool evaluatePredicateSimulatingNextState(1:Types.TEvent event, 2:string predicate),

	// public abstract boolean de.tum.in.i22.uc.cm.interfaces.IPdp2Pip.isSimulating()
	bool isSimulating(),

	// public abstract boolean de.tum.in.i22.uc.cm.interfaces.IPdp2Pip.evaluatePredicateCurrentState(java.lang.String)
	bool evaluatePredicatCurrentState(1:string predicate),

	// public abstract java.util.Set de.tum.in.i22.uc.cm.interfaces.IPdp2Pip.getContainersForData(de.tum.in.i22.uc.cm.datatypes.IData)
	set<Types.TContainer> getContainerForData(1:Types.TData data),

	// public abstract java.util.Set de.tum.in.i22.uc.cm.interfaces.IPdp2Pip.getDataInContainer(de.tum.in.i22.uc.cm.datatypes.IContainer)
	set<Types.TData> getDataInContainer(1:Types.TContainer container),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPdp2Pip.startSimulation()
	Types.TStatus startSimulation(),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPdp2Pip.stopSimulation()
	Types.TStatus stopSimulation(),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPdp2Pip.update(de.tum.in.i22.uc.cm.datatypes.IEvent)
	Types.TStatus update(1:Types.TEvent event),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPep2Pip.updateInformationFlowSemantics(de.tum.in.i22.uc.cm.datatypes.IPipDeployer,java.io.File,de.tum.in.i22.uc.cm.datatypes.EConflictResolution)
	// public IStatus updateInformationFlowSemantics(IPipDeployer deployer, File jarFile, ConflictResolution flagForTheConflictResolution),

	// public abstract boolean de.tum.in.i22.uc.cm.interfaces.IPip2Pip.hasAllData(java.util.Set)
	bool hasAllData(1: set<Types.TData> data),

	// public abstract boolean de.tum.in.i22.uc.cm.interfaces.IPip2Pip.hasAnyData(java.util.Set)
	bool hasAnyData(1: set<Types.TData> data),

	// public abstract boolean de.tum.in.i22.uc.cm.interfaces.IPip2Pip.hasAllContainers(java.util.Set)
	bool hasAllContainers(1: set<Types.TContainer> container),

	// public abstract boolean de.tum.in.i22.uc.cm.interfaces.IPip2Pip.hasAnyContainer(java.util.Set)
	bool hasAnyContainer(1: set<Types.TContainer> container),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPip2Pip.initialRepresentation(de.tum.in.i22.uc.cm.datatypes.IName,java.util.Set)
	Types.TStatus initialRepresentation(1: Types.TName container,2: set<Types.TData> data)
}


/*
 * Thrift interfaces defined by
 * de.tum.in.i22.uc.cm.interfaces.IPep2Pip
 */
service TPep2Pip {
	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPep2Pip.updateInformationFlowSemantics(de.tum.in.i22.uc.cm.datatypes.IPipDeployer,java.io.File,de.tum.in.i22.uc.cm.datatypes.EConflictResolution)
	// public IStatus updateInformationFlowSemantics(IPipDeployer deployer, File jarFile, ConflictResolution flagForTheConflictResolution)
}


/*
 * Thrift interfaces defined by
 * de.tum.in.i22.uc.cm.interfaces.IPmp2Any
 */
service TPmp2Any {
	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPmp2Pdp.revokePolicy(java.lang.String)
	Types.TStatus revokePolicy (1: string policyName),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPmp2Pdp.revokeMechanism(java.lang.String,java.lang.String)
	Types.TStatus revokeMechanism (1: string policyName, 2: string mechName),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPmp2Pdp.deployPolicyURI(java.lang.String)
	Types.TStatus deployPolicyURI (1: string policyFilePath),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPmp2Pdp.deployPolicyXML(java.lang.String)
	Types.TStatus deployPolicyXML (1: string XMLPolicy),

	// public abstract java.util.Map de.tum.in.i22.uc.cm.interfaces.IPmp2Pdp.listMechanisms()
	map<string,list<string>> listMechanisms(),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IMechanism de.tum.in.i22.uc.cm.interfaces.IPmp2Pdp.exportMechanism(java.lang.String)
	// TODO,

	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPmp2Pip.initialRepresentation(de.tum.in.i22.uc.cm.datatypes.IName,java.util.Set)
	Types.TStatus initialRepresentation(1: Types.TName container,2: set<Types.TData> data),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPmp2Pmp.receivePolicies(java.util.Set)
	Types.TStatus remotePolicyTransfer(1: set<string> policies)
}


/*
 * Thrift interfaces defined by
 * de.tum.in.i22.uc.cm.interfaces.IPep2Any
 */
service TPep2Any {
	// public abstract void de.tum.in.i22.uc.cm.interfaces.IPep2Pdp.notifyEventAsync(de.tum.in.i22.uc.cm.datatypes.IEvent)
	oneway void notifyEventAsync(1: Types.TEvent e),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IResponse de.tum.in.i22.uc.cm.interfaces.IPep2Pdp.notifyEventSync(de.tum.in.i22.uc.cm.datatypes.IEvent)
	Types.TResponse notifyEventSync(1: Types.TEvent e),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPep2Pip.updateInformationFlowSemantics(de.tum.in.i22.uc.cm.datatypes.IPipDeployer,java.io.File,de.tum.in.i22.uc.cm.datatypes.EConflictResolution)
	// public IStatus updateInformationFlowSemantics(IPipDeployer deployer, File jarFile, ConflictResolution flagForTheConflictResolution)
}


/*
 * Thrift interfaces defined by
 * de.tum.in.i22.uc.cm.interfaces.IPxp2Any
 */
service TPxp2Any {
	// public abstract boolean de.tum.in.i22.uc.cm.interfaces.IPxp2Pdp.registerPxp(de.tum.in.i22.uc.cm.basic.PxpSpec)
	bool registerPxp(1: Types.TPxpSpec pxp)
}


/*
 * Thrift interfaces defined by
 * de.tum.in.i22.uc.cm.interfaces.IAny2Pdp
 */
service TAny2Pdp {
	// public abstract void de.tum.in.i22.uc.cm.interfaces.IPep2Pdp.notifyEventAsync(de.tum.in.i22.uc.cm.datatypes.IEvent)
	oneway void notifyEventAsync(1: Types.TEvent e),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IResponse de.tum.in.i22.uc.cm.interfaces.IPep2Pdp.notifyEventSync(de.tum.in.i22.uc.cm.datatypes.IEvent)
	Types.TResponse notifyEventSync(1: Types.TEvent e),

	// public abstract boolean de.tum.in.i22.uc.cm.interfaces.IPxp2Pdp.registerPxp(de.tum.in.i22.uc.cm.basic.PxpSpec)
	bool registerPxp(1: Types.TPxpSpec pxp),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPmp2Pdp.revokePolicy(java.lang.String)
	Types.TStatus revokePolicy (1: string policyName),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPmp2Pdp.revokeMechanism(java.lang.String,java.lang.String)
	Types.TStatus revokeMechanism (1: string policyName, 2: string mechName),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPmp2Pdp.deployPolicyURI(java.lang.String)
	Types.TStatus deployPolicyURI (1: string policyFilePath),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IStatus de.tum.in.i22.uc.cm.interfaces.IPmp2Pdp.deployPolicyXML(java.lang.String)
	Types.TStatus deployPolicyXML (1: string XMLPolicy),

	// public abstract java.util.Map de.tum.in.i22.uc.cm.interfaces.IPmp2Pdp.listMechanisms()
	map<string,list<string>> listMechanisms(),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IMechanism de.tum.in.i22.uc.cm.interfaces.IPmp2Pdp.exportMechanism(java.lang.String)
	// TODO
}


/*
 * Thrift interfaces defined by
 * de.tum.in.i22.uc.cm.interfaces.IPep2Pdp
 */
service TPep2Pdp {
	// public abstract void de.tum.in.i22.uc.cm.interfaces.IPep2Pdp.notifyEventAsync(de.tum.in.i22.uc.cm.datatypes.IEvent)
	oneway void notifyEventAsync(1: Types.TEvent e),

	// public abstract de.tum.in.i22.uc.cm.datatypes.IResponse de.tum.in.i22.uc.cm.interfaces.IPep2Pdp.notifyEventSync(de.tum.in.i22.uc.cm.datatypes.IEvent)
	Types.TResponse notifyEventSync(1: Types.TEvent e)
}



