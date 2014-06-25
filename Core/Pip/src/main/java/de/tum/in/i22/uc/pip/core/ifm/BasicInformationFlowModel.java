package de.tum.in.i22.uc.pip.core.ifm;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.interfaces.informationFlowModel.IBasicInformationFlowModel;
import de.tum.in.i22.uc.cm.settings.Settings;

/**
 * Information flow model Singleton.
 */
public final class BasicInformationFlowModel implements
IBasicInformationFlowModel {
	private static final Logger _logger = LoggerFactory
			.getLogger(BasicInformationFlowModel.class);

	private Map<IContainer, Set<IData>> _containerToDataMap;
	private Map<IContainer, Set<IContainer>> _aliasesMap;
	private Map<IName, IContainer> _namingMap;

	// BACKUP TABLES FOR SIMULATION
	private Map<IContainer, Set<IData>> _containerToDataMapBackup;
	private Map<IContainer, Set<IContainer>> _aliasesMapBackup;
	private Map<IName, IContainer> _namingSetBackup;

	BasicInformationFlowModel() {
		reset();
	}

	/**
	 * Resets the information flow model to its initial (empty) state.
	 */
	void reset() {
		_containerToDataMap = new HashMap<>();
		_aliasesMap = new HashMap<>();
		_namingMap = new HashMap<>();

		_containerToDataMapBackup = null;
		_aliasesMapBackup = null;
		_namingSetBackup = null;
	}

	/**
	 * Simulation step: push. Stores the current IF state, if not already stored
	 * 
	 * @return true if the state has been successfully pushed, false otherwise
	 */
	void push() {
		_logger.info("Pushing current PIP state.");

		_containerToDataMapBackup = new HashMap<IContainer, Set<IData>>();
		for (Entry<IContainer, Set<IData>> e : _containerToDataMap.entrySet()) {
			Set<IData> s = new HashSet<IData>(e.getValue());
			_containerToDataMapBackup.put(e.getKey(), s);
		}

		_aliasesMapBackup = new HashMap<>();
		for (Entry<IContainer, Set<IContainer>> e : _aliasesMap.entrySet()) {
			Set<IContainer> s = new HashSet<IContainer>(e.getValue());
			_aliasesMapBackup.put(e.getKey(), s);
		}

		_namingSetBackup = new HashMap<IName, IContainer>(_namingMap);
	}

	/**
	 * Simulation step: pop. Restore a previously pushed IF state, if any.
	 * 
	 * @return true if the state has been successfully restored, false otherwise
	 */
	void pop() {
		_logger.info("Popping current PIP state.");

		_containerToDataMap = _containerToDataMapBackup;
		_aliasesMap = _aliasesMapBackup;
		_namingMap = _namingSetBackup;

		_containerToDataMapBackup = null;
		_aliasesMapBackup = null;
		_namingSetBackup = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tum.in.i22.uc.pip.core.ifm.IBasicInformationFlowModel#remove(de.tum
	 * .in.i22.uc.cm.datatypes.interfaces.IData)
	 */
	@Override
	public void remove(IData data) {
		if (data != null) {
			for (IContainer cont : _containerToDataMap.keySet()) {
				_containerToDataMap.get(cont).remove(data);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tum.in.i22.uc.pip.core.ifm.IBasicInformationFlowModel#remove(de.tum
	 * .in.i22.uc.cm.datatypes.interfaces.IContainer)
	 */
	@Override
	public void remove(IContainer cont) {
		if (cont != null) {
			_logger.info("remove container: " + cont);
			removeAllAliasesTo(cont);
			removeAllAliasesFrom(cont);
			removeAllNames(cont);
			emptyContainer(cont);
		}
	}

	private void removeAllNames(IContainer cont) {
		if (cont != null) {
			Set<IName> toRemove = new HashSet<IName>();
			for (Entry<IName, IContainer> entry : _namingMap.entrySet()) {
				if (cont.equals(entry.getValue())) {
					toRemove.add(entry.getKey());
				}
			}
			for (IName key : toRemove) {
				_namingMap.remove(key);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tum.in.i22.uc.pip.core.ifm.IBasicInformationFlowModel#emptyContainer
	 * (de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer)
	 */
	@Override
	public void emptyContainer(IContainer container) {
		if (container != null) {
			_logger.info("Emptying container " + container);
			_containerToDataMap.remove(container);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tum.in.i22.uc.pip.core.ifm.IBasicInformationFlowModel#emptyContainer
	 * (de.tum.in.i22.uc.cm.datatypes.interfaces.IName)
	 */
	@Override
	public void emptyContainer(IName containerName) {
		if (containerName != null) {
			emptyContainer(_namingMap.get(containerName));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tum.in.i22.uc.pip.core.ifm.IBasicInformationFlowModel#addAlias(de.
	 * tum.in.i22.uc.cm.datatypes.interfaces.IContainer,
	 * de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer)
	 */
	@Override
	public void addAlias(IContainer fromContainer, IContainer toContainer) {
		if (fromContainer == null || toContainer == null
				|| fromContainer.equals(toContainer)) {
			return;
		}

		_logger.info("addAlias from " + fromContainer + " to " + toContainer);

		Set<IContainer> aliases = _aliasesMap.get(fromContainer);
		if (aliases == null) {
			aliases = new HashSet<>();
			_aliasesMap.put(fromContainer, aliases);
		}
		aliases.add(toContainer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tum.in.i22.uc.pip.core.ifm.IBasicInformationFlowModel#addAlias(de.
	 * tum.in.i22.uc.cm.datatypes.interfaces.IName,
	 * de.tum.in.i22.uc.cm.datatypes.interfaces.IName)
	 */
	@Override
	public void addAlias(IName fromContainerName, IName toContainerName) {
		if (fromContainerName == null || toContainerName == null) {
			return;
		}

		addAlias(_namingMap.get(fromContainerName),
				_namingMap.get(toContainerName));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tum.in.i22.uc.pip.core.ifm.IBasicInformationFlowModel#removeAlias(
	 * de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer,
	 * de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer)
	 */
	@Override
	public void removeAlias(IContainer fromContainer, IContainer toContainer) {
		if (fromContainer == null || toContainer == null) {
			return;
		}

		Set<IContainer> aliases;

		if ((aliases = _aliasesMap.get(fromContainer)) != null) {
			aliases.remove(toContainer);

			if (aliases.size() == 0) {
				_aliasesMap.remove(fromContainer);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tum.in.i22.uc.pip.core.ifm.IBasicInformationFlowModel#getAliasesFrom
	 * (de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer)
	 */
	@Override
	public Collection<IContainer> getAliasesFrom(IContainer container) {
		Set<IContainer> result;

		if (container == null || (result = _aliasesMap.get(container)) == null) {
			return Collections.emptySet();
		}

		return Collections.unmodifiableSet(result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.tum.in.i22.uc.pip.core.ifm.IBasicInformationFlowModel#
	 * getAliasTransitiveReflexiveClosure
	 * (de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer)
	 */
	@Override
	public Set<IContainer> getAliasTransitiveReflexiveClosure(
			IContainer container) {
		if (container == null) {
			return Collections.emptySet();
		}

		Set<IContainer> closure = getAliasTransitiveClosure(container);
		closure.add(container); // add self to set ==> reflexive
		return closure;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tum.in.i22.uc.pip.core.ifm.IBasicInformationFlowModel#removeAllAliasesFrom
	 * (de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer)
	 */
	@Override
	public void removeAllAliasesFrom(IContainer fromContainer) {
		_aliasesMap.remove(fromContainer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tum.in.i22.uc.pip.core.ifm.IBasicInformationFlowModel#removeAllAliasesTo
	 * (de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer)
	 */
	@Override
	public void removeAllAliasesTo(IContainer toContainer) {
		if (toContainer == null) {
			return;
		}

		List<IContainer> toRemove = new LinkedList<IContainer>();

		for (IContainer from : _aliasesMap.keySet()) {
			Set<IContainer> toSet = _aliasesMap.get(from);
			if (toSet != null && toSet.remove(toContainer)) {
				if (toSet.size() == 0) {
					toRemove.add(from);
				}
			}
		}

		for (IContainer rem : toRemove) {
			_aliasesMap.remove(rem);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tum.in.i22.uc.pip.core.ifm.IBasicInformationFlowModel#getAliasesTo
	 * (de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer)
	 */
	@Override
	public Set<IContainer> getAliasesTo(IContainer container) {
		Set<IContainer> result = new HashSet<>();

		if (container != null) {
			for (Entry<IContainer, Set<IContainer>> aliasEntry : _aliasesMap
					.entrySet()) {
				if (aliasEntry.getValue().contains(container)) {
					result.add(aliasEntry.getKey());
				}
			}
		}

		return Collections.unmodifiableSet(result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.tum.in.i22.uc.pip.core.ifm.IBasicInformationFlowModel#
	 * getAliasTransitiveClosure
	 * (de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer)
	 */
	@Override
	public Set<IContainer> getAliasTransitiveClosure(IContainer container) {
		Set<IContainer> result = new HashSet<>();
		getAliasTransitiveClosure(container, result);
		result.remove(container);
		return result;
	}

	private void getAliasTransitiveClosure(IContainer container,
			Set<IContainer> visitedContainers) {
		for (IContainer id : getAliasesFrom(container)) {
			if (visitedContainers.add(id)) {
				getAliasTransitiveClosure(id, visitedContainers);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tum.in.i22.uc.pip.core.ifm.IBasicInformationFlowModel#addData(de.tum
	 * .in.i22.uc.cm.datatypes.interfaces.IData,
	 * de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer)
	 */
	@Override
	public void addData(IData data, IContainer container) {
		if (data == null || container == null) {
			return;
		}

		addData(Collections.singleton(data), container);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tum.in.i22.uc.pip.core.ifm.IBasicInformationFlowModel#removeData(de
	 * .tum.in.i22.uc.cm.datatypes.interfaces.IData,
	 * de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer)
	 */
	@Override
	public void removeData(IData data, IContainer container) {
		Set<IData> dataSet;

		if (container == null || data == null
				|| (dataSet = _containerToDataMap.get(container)) == null) {
			return;
		}

		dataSet.remove(data);
		if (dataSet.size() == 0) {
			_containerToDataMap.remove(container);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tum.in.i22.uc.pip.core.ifm.IBasicInformationFlowModel#getData(de.tum
	 * .in.i22.uc.cm.datatypes.interfaces.IContainer)
	 */
	@Override
	public Set<IData> getData(IContainer container) {
		Set<IData> result;
		if (container == null
				|| (result = _containerToDataMap.get(container)) == null) {
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tum.in.i22.uc.pip.core.ifm.IBasicInformationFlowModel#getData(de.tum
	 * .in.i22.uc.cm.datatypes.interfaces.IName)
	 */
	@Override
	public Set<IData> getData(IName containerName) {
		IContainer cont;

		if (containerName == null
				|| (cont = _namingMap.get(containerName)) == null) {
			return Collections.emptySet();
		}

		return getData(cont);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tum.in.i22.uc.pip.core.ifm.IBasicInformationFlowModel#copyData(de.
	 * tum.in.i22.uc.cm.datatypes.interfaces.IName,
	 * de.tum.in.i22.uc.cm.datatypes.interfaces.IName)
	 */
	@Override
	public boolean copyData(IName srcContainerName, IName dstContainerName) {
		if (srcContainerName == null || dstContainerName == null) {
			return false;
		}

		return copyData(_namingMap.get(srcContainerName),
				_namingMap.get(dstContainerName));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tum.in.i22.uc.pip.core.ifm.IBasicInformationFlowModel#copyData(de.
	 * tum.in.i22.uc.cm.datatypes.interfaces.IContainer,
	 * de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer)
	 */
	@Override
	public boolean copyData(IContainer srcContainer, IContainer dstContainer) {
		if (srcContainer == null || dstContainer == null) {
			return false;
		}

		_logger.info("copyData() from " + srcContainer + " to " + dstContainer);

		Set<IData> srcData = _containerToDataMap.get(srcContainer);
		if (srcData != null) {
			Set<IData> dstData = _containerToDataMap.get(dstContainer);
			if (dstData == null) {
				dstData = new HashSet<IData>();
				_containerToDataMap.put(dstContainer, dstData);
			}
			dstData.addAll(srcData);
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tum.in.i22.uc.pip.core.ifm.IBasicInformationFlowModel#addDataTransitively
	 * (java.util.Collection, de.tum.in.i22.uc.cm.datatypes.interfaces.IName)
	 */
	@Override
	public void addDataTransitively(Collection<IData> data,
			IName dstContainerName) {
		if (dstContainerName == null) {
			return;
		}

		addDataTransitively(data, getContainer(dstContainerName));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tum.in.i22.uc.pip.core.ifm.IBasicInformationFlowModel#addDataTransitively
	 * (java.util.Collection,
	 * de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer)
	 */
	@Override
	public void addDataTransitively(Collection<IData> data,
			IContainer dstContainer) {
		if (data == null || data.size() == 0 || dstContainer == null) {
			return;
		}

		addData(data, dstContainer);
		for (IContainer c : getAliasesFrom(dstContainer)) {
			addData(data, c);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tum.in.i22.uc.pip.core.ifm.IBasicInformationFlowModel#getContainers
	 * (de.tum.in.i22.uc.cm.datatypes.interfaces.IData)
	 */
	@Override
	public Set<IContainer> getContainers(IData data) {
		if (data == null) {
			return Collections.emptySet();
		}

		Set<IContainer> result = new HashSet<>();

		for (Entry<IContainer, Set<IData>> entry : _containerToDataMap
				.entrySet()) {
			if (entry.getValue().contains(data)) {
				result.add(entry.getKey());
			}
		}
		return result;
	}

	/**
	 * Returns all containers of the specified type in which the specified data
	 * is in.
	 * 
	 * ~ Double checked, 2014/04/11. FK.
	 * 
	 * @param data
	 *            the data whose containers are returned.
	 * @return The set of containers containing the specified data.
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tum.in.i22.uc.pip.core.ifm.IBasicInformationFlowModel#getContainers
	 * (de.tum.in.i22.uc.cm.datatypes.interfaces.IData, java.lang.Class)
	 */
	@Override
	public <T extends IContainer> Set<T> getContainers(IData data, Class<T> type) {
		if (data == null) {
			return Collections.emptySet();
		}

		Set<T> result = new HashSet<>();

		for (Entry<IContainer, Set<IData>> entry : _containerToDataMap
				.entrySet()) {
			if (entry.getValue().contains(data)) {

				IContainer container = entry.getKey();
				if (type.isInstance(container)) {
					result.add(type.cast(container));
				}
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tum.in.i22.uc.pip.core.ifm.IBasicInformationFlowModel#addData(java
	 * .util.Collection, de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer)
	 */
	@Override
	public void addData(Collection<IData> data, IContainer container) {
		if (data == null || container == null) {
			return;
		}

		Set<IData> dstData = _containerToDataMap.get(container);
		if (dstData == null) {
			dstData = new HashSet<IData>();
			_containerToDataMap.put(container, dstData);
		}

		_logger.info("Adding data " + data + " to container " + container);
		;
		dstData.addAll(data);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tum.in.i22.uc.pip.core.ifm.IBasicInformationFlowModel#addName(de.tum
	 * .in.i22.uc.cm.datatypes.interfaces.IName,
	 * de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer, boolean)
	 */
	@Override
	public void addName(IName name, IContainer container,
			boolean deleteUnreferencedContainer) {
		if (name == null || container == null) {
			return;
		}

		_logger.info("addName: " + name + " -> " + container);

		IContainer oldAssigned;
		if ((oldAssigned = _namingMap.put(name, container)) != null) {
			_logger.info("A container (" + oldAssigned
					+ ") was already assigned to name " + name + ". "
					+ "This mapping has been removed.");

			if (getAllNames(oldAssigned).size() == 0
					&& deleteUnreferencedContainer) {
				remove(oldAssigned);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tum.in.i22.uc.pip.core.ifm.IBasicInformationFlowModel#addName(de.tum
	 * .in.i22.uc.cm.datatypes.interfaces.IName,
	 * de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer)
	 */
	@Override
	public void addName(IName name, IContainer container) {
		addName(name, container, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tum.in.i22.uc.pip.core.ifm.IBasicInformationFlowModel#addName(de.tum
	 * .in.i22.uc.cm.datatypes.interfaces.IName,
	 * de.tum.in.i22.uc.cm.datatypes.interfaces.IName)
	 */
	@Override
	public void addName(IName oldName, IName newName) {
		if (oldName == null || newName == null) {
			return;
		}

		IContainer cont;
		if ((cont = getContainer(oldName)) != null) {
			addName(newName, cont);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tum.in.i22.uc.pip.core.ifm.IBasicInformationFlowModel#removeName(de
	 * .tum.in.i22.uc.cm.datatypes.interfaces.IName, boolean)
	 */
	@Override
	public void removeName(IName name, boolean deleteUnreferencedContainer) {
		if (name != null) {
			_logger.info("removeName() " + name);
			IContainer cont = _namingMap.remove(name);

			// if this was the last name, we can remove the container
			Collection<IName> remainingNames = getAllNames(cont);
			if ((remainingNames == null || remainingNames.size() == 0)
					&& deleteUnreferencedContainer) {
				remove(cont);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tum.in.i22.uc.pip.core.ifm.IBasicInformationFlowModel#removeName(de
	 * .tum.in.i22.uc.cm.datatypes.interfaces.IName)
	 */
	@Override
	@Deprecated
	public void removeName(IName name) {
		removeName(name, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tum.in.i22.uc.pip.core.ifm.IBasicInformationFlowModel#getContainer
	 * (de.tum.in.i22.uc.cm.datatypes.interfaces.IName)
	 */
	@Override
	public IContainer getContainer(IName name) {
		if (name != null) {
			return _namingMap.get(name);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tum.in.i22.uc.pip.core.ifm.IBasicInformationFlowModel#getAllContainers
	 * ()
	 */
	@Override
	public Set<IContainer> getAllContainers() {
		return Collections
				.unmodifiableSet(Sets.newHashSet(_namingMap.values()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tum.in.i22.uc.pip.core.ifm.IBasicInformationFlowModel#getAllNames()
	 */
	@Override
	public Collection<IName> getAllNames() {
		return Collections.unmodifiableCollection(_namingMap.keySet());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tum.in.i22.uc.pip.core.ifm.IBasicInformationFlowModel#getAllNames(
	 * java.lang.Class)
	 */
	@Override
	public <T extends IName> Collection<T> getAllNames(Class<T> type) {
		Collection<T> result = new LinkedList<>();

		for (IName name : _namingMap.keySet()) {
			if (type.isInstance(name)) {
				result.add(type.cast(name));
			}
		}

		return Collections.unmodifiableCollection(result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tum.in.i22.uc.pip.core.ifm.IBasicInformationFlowModel#getAllNames(
	 * de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer)
	 */
	@Override
	public Collection<IName> getAllNames(IContainer container) {
		Collection<IName> result = new LinkedList<>();

		for (Entry<IName, IContainer> nameEntry : _namingMap.entrySet()) {
			if (nameEntry.getValue().equals(container)) {
				result.add(nameEntry.getKey());
			}
		}

		return Collections.unmodifiableCollection(result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tum.in.i22.uc.pip.core.ifm.IBasicInformationFlowModel#getAllNames(
	 * de.tum.in.i22.uc.cm.datatypes.interfaces.IName, java.lang.Class)
	 */
	@Override
	public <T extends IName> List<T> getAllNames(IName containerName,
			Class<T> type) {
		IContainer cont;

		if (containerName == null
				|| (cont = _namingMap.get(containerName)) == null) {
			return Collections.emptyList();
		}

		return getAllNames(cont, type);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tum.in.i22.uc.pip.core.ifm.IBasicInformationFlowModel#getAllNames(
	 * de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer, java.lang.Class)
	 */
	@Override
	public <T extends IName> List<T> getAllNames(IContainer cont, Class<T> type) {
		List<T> result = new LinkedList<>();

		for (IName name : _namingMap.keySet()) {
			if (_namingMap.get(name).equals(cont) && type.isInstance(name)) {
				result.add(type.cast(name));
			}
		}

		return Collections.unmodifiableList(result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tum.in.i22.uc.pip.core.ifm.IBasicInformationFlowModel#getAllNamingsFrom
	 * (de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer)
	 */
	@Override
	/*
	 * TODO This method seems odd. Moreover, it is layer-specific and should
	 * thus not go into this class. Fix, delete, move, ... -FK-
	 */
	@Deprecated
	public List<IName> getAllNamingsFrom(IContainer pid) {
		List<IName> result = new LinkedList<>();

		for (Entry<IName, IContainer> entry : _namingMap.entrySet()) {
			if (entry.getKey().getName().equals(pid))
				result.add(entry.getKey());
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.tum.in.i22.uc.pip.core.ifm.IBasicInformationFlowModel#niceString()
	 */
	@Override
	public String niceString() {
		boolean showFullIFM = Settings.getInstance().getShowFullIFModel();

		StringBuilder sb = new StringBuilder();

		String nl = System.getProperty("line.separator");
		String arrow = " ---> ";
		String arrowL = " <--- ";

		boolean showNamesInsteadOfContainers = Settings.getInstance()
				.getShowIFNamesInsteadOfContainer();

		boolean sort = Settings.getInstance().getSortStorageNames();

		sb.append("  Storage:" + nl);
		if (showNamesInsteadOfContainers) {
			int nameLength = 0;
			for (Entry<IName, IContainer> entry : _namingMap.entrySet()) {
				int currLength = entry.getKey().getName().toString().length();
				if (currLength > nameLength)
					nameLength = currLength;
			}
			Set<Entry<IName, IContainer>> entryset;
			if (sort) {
				entryset = new TreeSet<Entry<IName, IContainer>>(
						new SortByNames());
				entryset.addAll(_namingMap.entrySet());
			} else {
				entryset = _namingMap.entrySet();
			}

			for (Entry<IName, IContainer> entry : entryset) {
				Set<IData> ds = _containerToDataMap.get(entry.getValue());
				if (ds != null && ds.size() != 0 || showFullIFM) {
					sb.append("    "
							+ String.format("%1$" + nameLength + "s", entry
									.getKey().getName()) + arrow);
					boolean first = true;
					if (ds != null) {
						if (ds.size() == 0) {
							sb.append(nl);
						} else {
							for (IData d : ds) {
								if (first) {
									first = false;
								} else {
									sb.append("    ");
									for (int i = 0; i < nameLength
											+ arrow.length(); i++) {
										sb.append(" ");
									}
								}
								sb.append(d.getId() + nl);
							}
						}
					} else {
						sb.append(nl);
					}
				}
			}
		} else {
			for (Entry<IContainer, Set<IData>> entry : _containerToDataMap
					.entrySet()) {
				if (entry.getValue() != null && entry.getValue().size() != 0
						|| showFullIFM) {
					sb.append("    " + entry.getKey().getId() + arrow);
					boolean first = true;
					if (entry.getValue() != null) {
						if (entry.getValue().size() == 0) {
							sb.append(nl);
						} else {
							for (IData d : entry.getValue()) {
								if (first) {
									first = false;
								} else {
									sb.append("    ");
									for (int i = 0; i < entry.getKey().getId()
											.length()
											+ arrow.length(); i++) {
										sb.append(" ");
									}
								}
								sb.append(d.getId() + nl);
							}
						}
					} else {
						sb.append(nl);
					}
				}
			}
		}
		sb.append(nl);

		sb.append("  Aliases:" + nl);
		if (_aliasesMap.size()==0) sb.append("  Empty" + nl);
		for (Entry<IContainer, Set<IContainer>> entry : _aliasesMap.entrySet()) {
			if (entry.getValue() != null && entry.getValue().size() != 0
					|| showFullIFM) {
				sb.append("    " + entry.getKey().getId() + arrow);
				boolean first = true;
				if (entry.getValue() != null) {
					for (IContainer c : entry.getValue()) {
						if (first) {
							first = false;
						} else {
							sb.append("    ");
							for (int i = 0; i < entry.getKey().getId().length()
									+ arrow.length(); i++) {
								sb.append(" ");
							}
						}
						sb.append(c.getId() + nl);
					}
				} else {
					sb.append(nl);
				}
			}
		}
		sb.append(nl);

		sb.append("  Naming:" + nl);
		if (_namingMap.size()==0) sb.append("  Empty" + nl);
		Set<IContainer> wasPrinted = new HashSet<IContainer>();
		for (IContainer cont : _namingMap.values()) {
			Set<IData> isItAContainerWorthPrinting = _containerToDataMap
					.get(cont);
			if (isItAContainerWorthPrinting != null && isItAContainerWorthPrinting
					.size() != 0 || showFullIFM) {
				if (wasPrinted.contains(cont)) {
					continue;
				}
				wasPrinted.add(cont);
				sb.append("    " + cont.getId() + arrowL);
				boolean first = true;
				for (IName name : _namingMap.keySet()) {
					if (_namingMap.get(name).equals(cont)) {
						if (first) {
							first = false;
						} else {
							sb.append("    ");
							for (int i = 0; i < cont.getId().length()
									+ arrowL.length(); i++) {
								sb.append(" ");
							}
						}
						sb.append(name.getName() + nl);
					}
				}
			}
		}

		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.tum.in.i22.uc.pip.core.ifm.IBasicInformationFlowModel#toString()
	 */
	@Override
	public String toString() {
		return com.google.common.base.Objects.toStringHelper(this)
				.add("_containerToDataMap", _containerToDataMap)
				.add("_aliasesMap", _aliasesMap).add("_namingMap", _namingMap)
				.toString();
	}

	public class SortByNames implements Comparator<Entry<IName, IContainer>> {
		@Override
		public int compare(Entry<IName, IContainer> e1,
				Entry<IName, IContainer> e2) {
			return e1.getKey().getName().compareTo(e2.getKey().getName());
		}
	}

}
