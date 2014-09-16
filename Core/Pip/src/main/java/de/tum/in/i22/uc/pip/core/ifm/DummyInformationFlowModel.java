package de.tum.in.i22.uc.pip.core.ifm;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IScope;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.interfaces.informationFlowModel.IInformationFlowModel;
import de.tum.in.i22.uc.cm.pip.EInformationFlowModel;
import de.tum.in.i22.uc.cm.pip.interfaces.EBehavior;
import de.tum.in.i22.uc.cm.pip.interfaces.EScopeState;
import de.tum.in.i22.uc.cm.pip.interfaces.IEventHandler;

public class DummyInformationFlowModel extends InformationFlowModelExtension
		implements IInformationFlowModel {
	protected DummyInformationFlowModel(
			InformationFlowModelManager informationFlowModelManager) {
		super(informationFlowModelManager);
	}

	private static final Logger _logger = LoggerFactory
			.getLogger(DummyInformationFlowModel.class);

	@Override
	public void remove(IData data) {
		_logger.error("Method remove() not implemented.");
	}

	@Override
	public void remove(IContainer cont) {
		_logger.error("Method remove() not implemented.");
	}

	@Override
	public void emptyContainer(IContainer container) {
		_logger.error("Method emptyContainer() not implemented.");
	}

	@Override
	public void emptyContainer(IName containerName) {
		_logger.error("Method emptyContainer() not implemented.");
	}

	@Override
	public void addAlias(IContainer fromContainer, IContainer toContainer) {
		_logger.error("Method addAlias() not implemented.");

	}

	@Override
	public void addAlias(IName fromContainerName, IName toContainerName) {
		_logger.error("Method addAlias() not implemented.");

	}

	@Override
	public void removeAlias(IContainer fromContainer, IContainer toContainer) {
		_logger.error("Method removeAlias() not implemented.");

	}

	@Override
	public Collection<IContainer> getAliasesFrom(IContainer container) {
		_logger.error("Method getAliasesFrom() not implemented.");
		return null;
	}

	@Override
	public Set<IContainer> getAliasTransitiveReflexiveClosure(
			IContainer container) {
		_logger.error("Method getAliasTransitiveReflexiveClosure() not implemented.");
		return null;
	}

	@Override
	public void removeAllAliasesFrom(IContainer fromContainer) {
		_logger.error("Method removeAllAliasesFrom() not implemented.");

	}

	@Override
	public void removeAllAliasesTo(IContainer toContainer) {
		_logger.error("Method removeAllAliasesTo() not implemented.");

	}

	@Override
	public Set<IContainer> getAliasesTo(IContainer container) {
		_logger.error("Method getAliasesTo() not implemented.");
		return null;
	}

	@Override
	public Set<IContainer> getAliasTransitiveClosure(IContainer container) {
		_logger.error("Method getAliasTransitiveClosure() not implemented.");
		return null;
	}

	@Override
	public void addData(IData data, IContainer container) {
		_logger.error("Method addData() not implemented.");

	}

	@Override
	public void removeData(IData data, IContainer container) {
		_logger.error("Method removeData() not implemented.");

	}

	@Override
	public Set<IData> getData(IContainer container) {
		_logger.error("Method getData() not implemented.");
		return null;
	}

	@Override
	public Set<IData> getData(IName containerName) {
		_logger.error("Method getData() not implemented.");
		return null;
	}

	@Override
	public boolean copyData(IName srcContainerName, IName dstContainerName) {
		_logger.error("Method copyData() not implemented.");
		return false;
	}

	@Override
	public boolean copyData(IContainer srcContainer, IContainer dstContainer) {
		_logger.error("Method copyData() not implemented.");
		return false;
	}

	@Override
	public void addDataTransitively(Collection<IData> data,
			IName dstContainerName) {
		_logger.error("Method addDataTransitively() not implemented.");

	}

	@Override
	public void addDataTransitively(Collection<IData> data,
			IContainer dstContainer) {
		_logger.error("Method addDataTransitively() not implemented.");

	}

	@Override
	public Set<IContainer> getContainers(IData data) {
		_logger.error("Method getContainers() not implemented.");
		return null;
	}

	@Override
	public <T extends IContainer> Set<T> getContainers(IData data, Class<T> type) {
		_logger.error("Method getContainers() not implemented.");
		return null;
	}

	@Override
	public void addData(Collection<IData> data, IContainer container) {
		_logger.error("Method addData() not implemented.");

	}

	@Override
	public void addName(IName name, IContainer container) {
		_logger.error("Method addName() not implemented.");

	}

	@Override
	public void addName(IName oldName, IName newName) {
		_logger.error("Method addName() not implemented.");

	}

	@Override
	public void removeName(IName name) {
		_logger.error("Method removeName() not implemented.");

	}

	@Override
	public void removeName(IName name, boolean deleteUnreferencedContainer) {
		_logger.error("Method removeName() not implemented.");

	}

	@Override
	public IContainer getContainer(IName name) {
		_logger.error("Method getContainer() not implemented.");
		return null;
	}

	@Override
	public Set<IContainer> getAllContainers() {
		_logger.error("Method getAllContainers() not implemented.");
		return null;
	}

	@Override
	public Collection<IName> getAllNames() {
		_logger.error("Method getAllNames() not implemented.");
		return null;
	}

	@Override
	public <T extends IName> Collection<T> getAllNames(Class<T> type) {
		_logger.error("Method getAllNames() not implemented.");
		return null;
	}

	@Override
	public Collection<IName> getAllNames(IContainer container) {
		_logger.error("Method getAllNames() not implemented.");
		return null;
	}

	@Override
	public <T extends IName> List<T> getAllNames(IName containerName,
			Class<T> type) {
		_logger.error("Method getAllNames() not implemented.");
		return null;
	}

	@Override
	public <T extends IName> List<T> getAllNames(IContainer cont, Class<T> type) {
		_logger.error("Method getAllNames() not implemented.");
		return null;
	}

	@Override
	public void push() {
		_logger.error("Method push() not implemented.");

	}

	@Override
	public void pop() {
		_logger.error("Method pop() not implemented.");

	}

	@Override
	public boolean openScope(IScope scope) {
		_logger.error("Method openScope() not implemented.");
		return false;
	}

	@Override
	public boolean closeScope(IScope scope) {
		_logger.error("Method closeScope() not implemented.");
		return false;
	}

	@Override
	public boolean isScopeOpened(IScope scope) {
		_logger.error("Method isScopeOpened() not implemented.");
		return false;
	}

	@Override
	public IScope getOpenedScope(IScope scope) {
		_logger.error("Method getOpenedScope() not implemented.");
		return null;
	}

	@Override
	public boolean isEnabled(EInformationFlowModel ifm) {
		_logger.error("Method isEnabled() not implemented.");
		return false;
	}

	@Override
	public boolean isSimulating() {
		_logger.error("Method isSimulating() not implemented.");
		return false;
	}

	@Override
	public IStatus startSimulation() {
		_logger.error("Method startSimulation() not implemented.");
		return null;
	}

	@Override
	public IStatus stopSimulation() {
		_logger.error("Method stopSimulation() not implemented.");
		return null;
	}

	@Override
	public void reset() {
		_logger.error("Method reset() not implemented.");

	}

	@Override
	public String niceString() {
		_logger.error("Method niceString() not implemented.");
		return null;
	}

	@Override
	public IData newStructuredData(Map<String, Set<IData>> structure) {
		_logger.error("Method newStructuredData() not implemented.");
		return null;
	}

	@Override
	public Map<String, Set<IData>> getStructureOf(IData data) {
		_logger.error("Method getStructureOf() not implemented.");
		return null;
	}

	@Override
	public Set<IData> flattenStructure(IData data) {
		_logger.error("Method flattenStructure() not implemented.");
		return null;
	}

	@Override
	public Entry<EBehavior, IScope> XBehav(IEventHandler eventHandler) {
		_logger.error("Method XBehav() not implemented.");
		return null;
	}

	@Override
	public Set<Entry<IScope, EScopeState>> XDelim(IEventHandler eventHandler) {
		_logger.error("Method XDelim() not implemented.");
		return null;
	}

	@Override
	public Map<IContainer, Set<IContainer>> XAlias(IEventHandler eventHandler) {
		_logger.error("Method XAlias() not implemented.");
		return null;
	}

	@Override
	public void addName(IName name, IContainer container,
			boolean deleteUnreferencedContainer) {
		_logger.error("Method addName() not implemented.");
	}

	@Override
	public IData getDataFromId(String id) {
		_logger.error("Method getDataFromId() not implemented.");
		return null;
	}

}
