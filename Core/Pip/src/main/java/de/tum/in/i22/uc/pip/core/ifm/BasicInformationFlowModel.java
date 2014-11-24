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

import com.google.common.base.MoreObjects;
import com.google.common.collect.Sets;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.pip.ifm.IBasicInformationFlowModel;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.generic.observable.NotifyingMap;
import de.tum.in.i22.uc.generic.observable.NotifyingSet;

/**
 * Information flow model Singleton.
 */
public final class BasicInformationFlowModel extends InformationFlowModel implements IBasicInformationFlowModel {
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
	@Override
	public void reset() {
		super.reset();
		_containerToDataMap = new NotifyingMap<>(new HashMap<IContainer, Set<IData>>(), _observer);
		_aliasesMap = new NotifyingMap<>(new HashMap<IContainer, Set<IContainer>>(), _observer);
		_namingMap = new NotifyingMap<>(new HashMap<IName, IContainer>(), _observer);

		_containerToDataMapBackup = null;
		_aliasesMapBackup = null;
		_namingSetBackup = null;
	}

	/**
	 * Simulation step: push. Stores the current IF state, if not already stored
	 *
	 * @return true if the state has been successfully pushed, false otherwise
	 */
	@Override
	public
	IStatus startSimulation() {
		super.startSimulation();
		_logger.info("Pushing current PIP state.");

		_containerToDataMapBackup = new NotifyingMap<>(new HashMap<IContainer, Set<IData>>(), _observer);
		for (Entry<IContainer, Set<IData>> e : _containerToDataMap.entrySet()) {
			Set<IData> s = new NotifyingSet<>(new HashSet<>(e.getValue()), _observer);
			_containerToDataMapBackup.put(e.getKey(), s);
		}

		_aliasesMapBackup = new NotifyingMap<>(new HashMap<IContainer, Set<IContainer>>(), _observer);
		for (Entry<IContainer, Set<IContainer>> e : _aliasesMap.entrySet()) {
			Set<IContainer> s = new NotifyingSet<>(new HashSet<>(e.getValue()), _observer);
			_aliasesMapBackup.put(e.getKey(), s);
		}

		_namingSetBackup = new NotifyingMap<>(new HashMap<IName, IContainer>(_namingMap), _observer);

		return new StatusBasic(EStatus.OKAY);
	}

	/**
	 * Simulation step: pop. Restore a previously pushed IF state, if any.
	 *
	 * @return true if the state has been successfully restored, false otherwise
	 */
	@Override
	public IStatus stopSimulation() {
		super.stopSimulation();
		_logger.info("Popping current PIP state.");

		_containerToDataMap = _containerToDataMapBackup;
		_aliasesMap = _aliasesMapBackup;
		_namingMap = _namingSetBackup;

		_containerToDataMapBackup = null;
		_aliasesMapBackup = null;
		_namingSetBackup = null;

		return new StatusBasic(EStatus.OKAY);
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
			_containerToDataMap.values().forEach(c -> c.remove(data));

//			for (IContainer cont : _containerToDataMap.keySet()) {
//				_containerToDataMap.get(cont).remove(data);
//			}
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
			Set<IName> toRemove = new HashSet<>();
			for (Entry<IName, IContainer> entry : _namingMap.entrySet()) {
				if (cont.equals(entry.getValue())) {
					toRemove.add(entry.getKey());
				}
			}

			toRemove.forEach(n -> _namingMap.remove(n));
//			for (IName key : toRemove) {
//				_namingMap.remove(key);
//			}
		}
	}


	@Override
	public void emptyContainer(IContainer container) {
		if (container != null) {
			_logger.info("Emptying container " + container);
			_containerToDataMap.remove(container);
		}
	}


	@Override
	public void emptyContainer(IName containerName) {
		if (containerName != null) {
			emptyContainer(_namingMap.get(containerName));
		}
	}


	@Override
	public void addAlias(IContainer fromContainer, IContainer toContainer) {
		if (fromContainer == null || toContainer == null
				|| fromContainer.equals(toContainer)) {
			return;
		}

		_logger.info("addAlias from " + fromContainer + " to " + toContainer);

		Set<IContainer> aliases = _aliasesMap.get(fromContainer);
		if (aliases == null) {
			aliases = new NotifyingSet<>(new HashSet<IContainer>(), _observer);
			_aliasesMap.put(fromContainer, aliases);
		}
		aliases.add(toContainer);
	}


	@Override
	public void addAlias(IName fromContainerName, IName toContainerName) {
		if (fromContainerName == null || toContainerName == null) {
			return;
		}

		addAlias(_namingMap.get(fromContainerName),
				_namingMap.get(toContainerName));
	}


	@Override
	public void removeAlias(IContainer fromContainer, IContainer toContainer) {
		if (fromContainer == null || toContainer == null) {
			return;
		}

		Set<IContainer> aliases;

		if ((aliases = _aliasesMap.get(fromContainer)) != null) {
			aliases.remove(toContainer);

			if (aliases.isEmpty()) {
				_aliasesMap.remove(fromContainer);
			}
		}
	}


	@Override
	public Collection<IContainer> getAliasesFrom(IContainer container) {
		Set<IContainer> result;

		if (container == null || (result = _aliasesMap.get(container)) == null) {
			return Collections.emptySet();
		}

		return Collections.unmodifiableSet(result);
	}


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


	@Override
	public void removeAllAliasesFrom(IContainer fromContainer) {
		_aliasesMap.remove(fromContainer);
	}


	@Override
	public void removeAllAliasesTo(IContainer toContainer) {
		if (toContainer == null) {
			return;
		}

		List<IContainer> toRemove = new LinkedList<IContainer>();

		_aliasesMap.keySet().forEach(k -> {
			Set<IContainer> toSet = _aliasesMap.get(k);
			if (toSet != null && toSet.remove(toContainer) && toSet.isEmpty()) {
				toRemove.add(k);
			}
		});

		toRemove.forEach(c -> _aliasesMap.remove(c));
	}


	@Override
	public Set<IContainer> getAliasesTo(IContainer container) {
		Set<IContainer> result = new HashSet<>();

		if (container != null) {

			_aliasesMap.entrySet().forEach(e -> {
				if (e.getValue().contains(container))
					result.add(e.getKey());
			});

//			for (Entry<IContainer, Set<IContainer>> aliasEntry : _aliasesMap
//					.entrySet()) {
//				if (aliasEntry.getValue().contains(container)) {
//					result.add(aliasEntry.getKey());
//				}
//			}
		}

		return Collections.unmodifiableSet(result);
	}


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
				dstData = new NotifyingSet<>(new HashSet<IData>(), _observer);
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


	@Override
	public void addDataTransitively(Collection<IData> data, IContainer dstContainer) {
		if (data == null || data.size() == 0 || dstContainer == null) {
			return;
		}

		addData(data, dstContainer);
		getAliasTransitiveClosure(dstContainer).forEach(c -> addData(data, c));
	}


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
		if (data == null || container == null || data.size() == 0) {
			return;
		}

		Set<IData> dstData = _containerToDataMap.get(container);
		if (dstData == null) {
			dstData = new NotifyingSet<>(new HashSet<IData>(), _observer);
			_containerToDataMap.put(container, dstData);
		}

		_logger.info("Adding data " + data + " to container " + container);
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

		IContainer cont = getContainer(oldName);
		if (cont != null) {
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
		return name != null ? _namingMap.get(name) : null;
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
		List<T> result = new LinkedList<>();

		_namingMap.keySet().forEach(n -> {
			if (type.isInstance(n))
				result.add(type.cast(n));
		});

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

		_namingMap.entrySet().forEach(e -> {
			if (e.getValue().equals(container))
				result.add(e.getKey());
		});

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


	@Override
	public <T extends IName> List<T> getAllNames(IContainer cont, Class<T> type) {
		List<T> result = new LinkedList<>();

		_namingMap.entrySet().forEach(e -> {
			if (e.getValue().equals(cont) && type.isInstance(e.getKey()))
				result.add(type.cast(e.getKey()));
		});

//		for (IName name : _namingMap.keySet()) {
//			if (_namingMap.get(name).equals(cont) && type.isInstance(name)) {
//				result.add(type.cast(name));
//			}
//		}

		return Collections.unmodifiableList(result);
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
				Set<IData> ds = _containerToDataMap.get(entry.getValue());
				if (((ds != null) && (ds.size() > 0)) || showFullIFM) {
					int currLength = entry.getKey().getName().toString()
							.length();
					if (currLength > nameLength)
						nameLength = currLength;
				}
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
								sb.append(d.getId() + "  " + nl);
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
		if (_aliasesMap.size() == 0)
			sb.append("  Empty" + nl);
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
		if (_namingMap.size() == 0)
			sb.append("  Empty" + nl);
		Set<IContainer> wasPrinted = new HashSet<IContainer>();
		for (IContainer cont : _namingMap.values()) {
			Set<IData> isItAContainerWorthPrinting = _containerToDataMap
					.get(cont);
			if (isItAContainerWorthPrinting != null
					&& isItAContainerWorthPrinting.size() != 0 || showFullIFM) {
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
		return MoreObjects.toStringHelper(this)
				.add("_containerToDataMap", _containerToDataMap)
				.add("_aliasesMap", _aliasesMap)
				.add("_namingMap", _namingMap)
				.toString();
	}

	private class SortByNames implements Comparator<Entry<IName, IContainer>> {
		@Override
		public int compare(Entry<IName, IContainer> e1,
				Entry<IName, IContainer> e2) {
			return e1.getKey().getName().compareTo(e2.getKey().getName());
		}
	}

	@Override
	public IData getDataFromId(String id) {
		if (id == null || id.isEmpty()) {
			return null;
		}

		for (Set<IData> dataSet : _containerToDataMap.values()) {
			for (IData d : dataSet) {
				if (d.getId().equals(id)) return d;
			}
		}

		return null;
	}

	@Override
	public boolean isSimulating() {
		return _containerToDataMapBackup != null;
	}
}
