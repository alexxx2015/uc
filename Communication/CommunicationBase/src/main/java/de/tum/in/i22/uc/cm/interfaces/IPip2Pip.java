package de.tum.in.i22.uc.cm.interfaces;

import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
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

	@AThriftMethod(signature="Types.TStatus update(1:Types.TEvent updateEvent)")
	public IStatus update(IEvent updateEvent);

	@AThriftMethod(signature="set<Types.TData> getDataInContainer(1:Types.TName containerName)")
	public Set<IData> getDataInContainer(IName containerName);

	@AThriftMethod(signature="set<Types.TContainer> getContainerForData(1:Types.TData data)")
	public Set<IContainer> getContainersForData(IData data);

	@AThriftMethod(signature="Types.TStatus initialRepresentation(1: Types.TName container,2: set<Types.TData> data)")
	public IStatus initialRepresentation(IName containerName, Set<IData> data);


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
