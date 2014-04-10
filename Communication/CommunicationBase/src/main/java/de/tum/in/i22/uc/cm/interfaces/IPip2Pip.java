package de.tum.in.i22.uc.cm.interfaces;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
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
	@AThriftMethod(signature="bool hasAllData(1: set<Types.TData> data)")
    public boolean hasAllData(Set<IData> data);

	@AThriftMethod(signature="bool hasAnyData(1: set<Types.TData> data)")
    public boolean hasAnyData(Set<IData> data);

	@AThriftMethod(signature="bool hasAllContainers(1: set<Types.TName> container)")
    public boolean hasAllContainers(Set<IName> container);

	@AThriftMethod(signature="bool hasAnyContainer(1: set<Types.TName> container)")
    public boolean hasAnyContainer(Set<IName> container);

	@AThriftMethod(signature="Types.TStatus update(1:Types.TEvent event)")
	public IStatus update(IEvent event);

	@AThriftMethod(signature="set<Types.TData> getDataInContainer(1:Types.TContainer container)")
	public Set<IData> getDataInContainer(IContainer container);

	@AThriftMethod(signature="set<Types.TContainer> getContainerForData(1:Types.TData data)")
	public Set<IContainer> getContainersForData(IData data);

	@AThriftMethod(signature="Types.TStatus initialRepresentation(1: Types.TName container,2: set<Types.TData> data)")
	public IStatus initialRepresentation(IName containerName, Set<IData> data);

	/**
	 * Returns a set of {@link Location}s that are 'aware' of at least one element
	 * within the specified data set. The returned set of {@link Location}s
	 * is not necessarily a complete set, i.e. there might exist further
	 * {@link Location}s that are aware of the data but that are not included
	 * in the returned set. If parameter askRecursively is set, the callee
	 * will perform a recursive lookup.
	 *
	 * @param data the set of data items to look for
	 * @param askRecursively whether the callee should ask recursively
	 * @return a set of {@link Location}s aware of at least one element of
	 * 		the specified data set. The returned set might not be complete.
	 */
	@AThriftMethod(signature="set<string> whoHasData(1: set<Types.TData> data, 2: bool askRecursively)")
	public Set<Location> whoHasData(Set<IData> data, boolean askRecursively);
}
