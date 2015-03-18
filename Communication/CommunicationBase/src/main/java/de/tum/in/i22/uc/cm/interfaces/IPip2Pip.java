package de.tum.in.i22.uc.cm.interfaces;

import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IChecksum;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
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

	@AThriftMethod(signature="bool newChecksum(1: Types.TData data, 2:Types.TChecksum checksum, 3:bool overwrite)")
	public boolean newChecksum(IData data, IChecksum checksum, boolean overwrite);

	@AThriftMethod(signature="Types.TChecksum getChecksumOf(1:Types.TData data)")
	public IChecksum getChecksumOf(IData data);

	@AThriftMethod(signature="bool deleteChecksum(1:Types.TData d)")
	public boolean deleteChecksum(IData d);
	
	@AThriftMethod(signature="bool deleteStructure(1:Types.TData d)")
	public boolean deleteStructure(IData d) ;
}
