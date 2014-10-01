package de.tum.in.i22.uc.cm.interfaces;

import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.thrift.generator.AThriftMethod;
import de.tum.in.i22.uc.thrift.generator.AThriftService;

/**
 * Interface defining methods a PIP can invoke on a PIP.
 * @author Kelbert & Lovat
 *
 */
@AThriftService(name="TPip2Pip")
public interface IPip2Pip {
	@AThriftMethod(signature="Types.TData getDataFromId(1: string id)")
    public IData getDataFromId (String id);

	@AThriftMethod(signature="bool hasAllData(1: set<Types.TData> data)")
    public boolean hasAllData(Set<IData> data);

	@AThriftMethod(signature="bool hasAnyData(1: set<Types.TData> data)")
    public boolean hasAnyData(Set<IData> data);

	@AThriftMethod(signature="bool hasAllContainers(1: set<Types.TName> container)")
    public boolean hasAllContainers(Set<IName> container);

	@AThriftMethod(signature="bool hasAnyContainer(1: set<Types.TName> container)")
    public boolean hasAnyContainer(Set<IName> container);

	@AThriftMethod(signature="Types.TStatus update(1:Types.TEvent updateEvent)")
	public IStatus update(IEvent updateEvent);

	@AThriftMethod(signature="set<Types.TData> getDataInContainer(1:Types.TName containerName)")
	public Set<IData> getDataInContainer(IName containerName);

	@AThriftMethod(signature="set<Types.TContainer> getContainerForData(1:Types.TData data)")
	public Set<IContainer> getContainersForData(IData data);

	@AThriftMethod(signature="Types.TStatus initialRepresentation(1: Types.TName container,2: set<Types.TData> data)")
	public IStatus initialRepresentation(IName containerName, Set<IData> data);

	/**
	 * Returns a set of {@link Location}s that are 'aware' of at least one element
	 * within the specified data set. The returned set of {@link Location}s
	 * is not necessarily a complete set, i.e. there might exist further
	 * {@link Location}s that are aware of the data but that are not included
	 * in the returned set. Parameter recursionDepth describes how many recursive
	 * lookups shall be made.
	 *
	 * @param data the set of data items to look for
	 * @param the amount of recursive lookups to be made
	 * @return a set of {@link Location}s aware of at least one element of
	 * 		the specified data set. The returned set might not be complete.
	 */
	@AThriftMethod(signature="set<string> whoHasData(1: set<Types.TData> data, 2: Types.int recursionDepth)")
	public Set<Location> whoHasData(Set<IData> data, int recursionDepth);

	/*
	 * Structured dft methods
	 */
	
	@AThriftMethod(signature="Types.TData newStructuredData(1: map<string,set<Types.TData>> structure)")
	public IData newStructuredData(Map<String,Set<IData>> structure);

	@AThriftMethod(signature="map<string,set<Types.TData>> getStructureOf(1: Types.TData data)")
	public Map<String,Set<IData>>  getStructureOf(IData data);

	@AThriftMethod(signature="set<Types.TData> flattenStructure(1: Types.TData data)")
	public Set<IData>  flattenStructure(IData data);
	
}
