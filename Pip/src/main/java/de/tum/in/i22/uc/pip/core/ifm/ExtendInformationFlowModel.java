package de.tum.in.i22.uc.pip.core.ifm;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.pip.extensions.crosslayer.ScopeInformationFlowModel;

/**
 * Wrapper class for the InformationFlowModel, that
 * (1) takes an InformationFlowModel,
 * (2) wraps it,
 * (3) forwards all calls to the wrapped InformationFlowModel.
 *
 * This class can be used to extend any InformationFlowModel instance,
 * e.g. with functionalities for data quantities, cross-layer behavior
 * ({@link ScopeInformationFlowModel}), or structured data.
 *
 * For an example extension see {@link ScopeInformationFlowModel}.
 *
 * @author Florian Kelbert
 *
 */
public class ExtendInformationFlowModel extends InformationFlowModel {
	private final InformationFlowModel _ifm;

	public ExtendInformationFlowModel(InformationFlowModel ifm) {
		_ifm = ifm;
	}

	@Override
	public String toString() {
		return _ifm.toString();
	}

	@Override
	public void reset() {
		_ifm.reset();
	}

	@Override
	protected void init() {
		_ifm.init();
	}

	@Override
	public boolean isSimulating() {
		return _ifm.isSimulating();
	}

	@Override
	public boolean push() {
		return _ifm.push();
	}

	@Override
	public boolean pop() {
		return _ifm.pop();
	}

	@Override
	public void remove(IData data) {
		_ifm.remove(data);
	}

	@Override
	public void remove(IContainer cont) {
		_ifm.remove(cont);
	}

	@Override
	public void emptyContainer(IContainer container) {
		_ifm.emptyContainer(container);
	}

	@Override
	public void emptyContainer(IName containerName) {
		_ifm.emptyContainer(containerName);
	}

	@Override
	public void addAlias(IContainer fromContainer, IContainer toContainer) {
		_ifm.addAlias(fromContainer, toContainer);
	}

	@Override
	public void addAlias(IName fromContainerName, IName toContainerName) {
		_ifm.addAlias(fromContainerName, toContainerName);
	}

	@Override
	public void removeAlias(IContainer fromContainer, IContainer toContainer) {
		_ifm.removeAlias(fromContainer, toContainer);
	}

	@Override
	public Collection<IContainer> getAliasesFrom(IContainer container) {
		return _ifm.getAliasesFrom(container);
	}

	@Override
	public Set<IContainer> getAliasTransitiveReflexiveClosure(
			IContainer container) {
		return _ifm.getAliasTransitiveReflexiveClosure(container);
	}

	@Override
	public void removeAllAliasesFrom(IContainer fromContainer) {
		_ifm.removeAllAliasesFrom(fromContainer);
	}

	@Override
	public void removeAllAliasesTo(IContainer toContainer) {
		_ifm.removeAllAliasesTo(toContainer);
	}

	@Override
	public Set<IContainer> getAliasesTo(IContainer container) {
		return _ifm.getAliasesTo(container);
	}

	@Override
	public Set<IContainer> getAliasTransitiveClosure(IContainer container) {
		return _ifm.getAliasTransitiveClosure(container);
	}

	@Override
	public void addDataToContainer(IData data, IContainer container) {
		_ifm.addDataToContainer(data, container);
	}

	@Override
	public void removeDataFromContainer(IData data, IContainer container) {
		_ifm.removeDataFromContainer(data, container);
	}

	@Override
	public Set<IData> getDataInContainer(IContainer container) {
		return _ifm.getDataInContainer(container);
	}

	@Override
	public Set<IData> getDataInContainer(IName containerName) {
		return _ifm.getDataInContainer(containerName);
	}

	@Override
	public boolean copyData(IName srcContainerName, IName dstContainerName) {
		return _ifm.copyData(srcContainerName, dstContainerName);
	}

	@Override
	public boolean copyData(IContainer srcContainer, IContainer dstContainer) {
		return _ifm.copyData(srcContainer, dstContainer);
	}

	@Override
	public void addDataToContainerAndAliases(Set<IData> data,
			IName dstContainerName) {
		_ifm.addDataToContainerAndAliases(data, dstContainerName);
	}

	@Override
	public void addDataToContainerAndAliases(Set<IData> data,
			IContainer dstContainer) {
		_ifm.addDataToContainerAndAliases(data, dstContainer);
	}

	@Override
	public Set<IContainer> getContainersForData(IData data) {
		return _ifm.getContainersForData(data);
	}

	@Override
	public void addDataToContainerMappings(Set<IData> data, IContainer container) {
		_ifm.addDataToContainerMappings(data, container);
	}

	@Override
	public void addName(IName name, IContainer container) {
		_ifm.addName(name, container);
	}

	@Override
	public void addName(IName oldName, IName newName) {
		_ifm.addName(oldName, newName);
	}

	@Override
	public void removeName(IName name) {
		_ifm.removeName(name);
	}

	@Override
	public IContainer getContainer(IName name) {
		return _ifm.getContainer(name);
	}

	@Override
	public IContainer getContainerRelaxed(IName name) {
		return _ifm.getContainerRelaxed(name);
	}

	@Override
	public Collection<IName> getAllNames() {
		return _ifm.getAllNames();
	}

	@Override
	public Collection<IName> getAllNames(IContainer container) {
		return _ifm.getAllNames(container);
	}

	@Override
	public <T extends IName> List<T> getAllNames(IName containerName,
			Class<T> type) {
		return _ifm.getAllNames(containerName, type);
	}

	@Override
	public <T extends IName> List<T> getAllNames(IContainer cont, Class<T> type) {
		return _ifm.getAllNames(cont, type);
	}

	@Override
	public List<IName> getAllNamingsFrom(IContainer pid) {
		return _ifm.getAllNamingsFrom(pid);
	}

	@Override
	public String niceString() {
		return _ifm.niceString();
	}

	@Override
	public int hashCode() {
		return _ifm.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return _ifm.equals(obj);
	}
}
