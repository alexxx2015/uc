package de.tum.in.i22.uc.cm.interfaces;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
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

	@AThriftMethod(signature="bool hasAllContainers(1: set<Types.TContainer> container)")
    public boolean hasAllContainers(Set<IContainer> container);

	@AThriftMethod(signature="bool hasAnyContainer(1: set<Types.TContainer> container)")
    public boolean hasAnyContainer(Set<IContainer> container);

	@AThriftMethod(signature="Types.TStatus update(1:Types.TEvent event)")
	public IStatus update(IEvent event);

	@AThriftMethod(signature="set<Types.TData> getDataInContainer(1:Types.TContainer container)")
	public Set<IData> getDataInContainer(IContainer container);

	@AThriftMethod(signature="set<Types.TContainer> getContainerForData(1:Types.TData data)")
	public Set<IContainer> getContainersForData(IData data);

	@AThriftMethod(signature="Types.TStatus initialRepresentation(1: Types.TName container,2: set<Types.TData> data)")
	public IStatus initialRepresentation(IName containerName, Set<IData> data);
}
