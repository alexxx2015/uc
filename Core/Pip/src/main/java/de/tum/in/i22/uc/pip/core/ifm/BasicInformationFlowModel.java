package de.tum.in.i22.uc.pip.core.ifm;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;

/**
 * Information flow model Singleton.
 */
public final class BasicInformationFlowModel {
	private static final Logger _logger = LoggerFactory.getLogger(BasicInformationFlowModel.class);

	private static BasicInformationFlowModel _instance;

	// [Container.identifier -> List[Data.identifier]]
	private Map<IContainer, Set<IData>> _containerToDataMap;

	// [Container.identifier -> List[Container.identifier]]
	private Map<IContainer, Set<IContainer>> _aliasesMap;

	// the naming set [name -> Container.identifier]
	private Map<IName, IContainer> _namingMap;

	// BACKUP TABLES FOR SIMULATION
	private Map<IContainer, Set<IData>> _containerToDataMapBackup;
	private Map<IContainer, Set<IContainer>> _aliasesMapBackup;
	private Map<IName, IContainer> _namingSetBackup;

	BasicInformationFlowModel() {
		reset();
	}

	/**
	 * Visibility set to 'default' (i.e. package) on purpose.
	 * Use {@link InformationFlowModelManager} to get an instance.
	 *
	 * @return
	 */
	static BasicInformationFlowModel getInstance() {
		/*
		 * This implementation may seem odd, overengineered, redundant, or all of it.
		 * Yet, it is the best way to implement a thread-safe singleton, cf.
		 * http://www.journaldev.com/171/thread-safety-in-java-singleton-classes-with-example-code
		 * -FK-
		 */
		if (_instance == null) {
			synchronized (BasicInformationFlowModel.class) {
				if (_instance == null) _instance = new BasicInformationFlowModel();
			}
		}
		return _instance;
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

	/**
	 * Removes data object.
	 *
	 * @param data
	 */
	public void remove(IData data) {
		if (data != null) {
			for (IContainer cont : _containerToDataMap.keySet()) {
				_containerToDataMap.get(cont).remove(data);
			}
		}
	}


	/**
	 * Removes the given container completely by deleting
	 * associated names, aliases, and data.
	 *
	 * ~ Double checked, 2014/03/14. FK.
	 *
	 * @param cont the container to be removed.
	 */
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

	/**
	 * Removes all data from the specified container
	 *
	 * ~ Double checked, 2014/03/14. FK.
	 *
	 * @param container the container of which the data is to be removed.
	 */
	public void emptyContainer(IContainer container) {
		if (container != null) {
			_logger.info("Emptying container " + container);
			_containerToDataMap.remove(container);
		}
	}

	/**
	 * Removes all data from the container identified by the given container name.
	 *
	 * ~ Double checked, 2014/03/14. FK.
	 *
	 * @param containerName a name of the container that is to be emptied.
	 */
	public void emptyContainer(IName containerName) {
		if (containerName != null) {
			emptyContainer(_namingMap.get(containerName));
		}
	}

	/**
	 * Adds an alias relation from one container to another.
	 *
	 * @return
	 */
	public void addAlias(IContainer fromContainer, IContainer toContainer) {
		if (fromContainer == null || toContainer == null || fromContainer.equals(toContainer)) {
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


	public void addAlias(IName fromContainerName, IName toContainerName) {
		if (fromContainerName == null || toContainerName == null) {
			return;
		}

		addAlias(_namingMap.get(fromContainerName), _namingMap.get(toContainerName));
	}


	/**
	 * Removes the alias from fromContainer to toContainer.
	 *
	 * ~ Double checked, 2014/03/14. FK.
	 *
	 * @param fromContainer the container of which the alias is outgoing
	 * @param toContainer the container of which the alias is incoming
	 */
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

	/**
	 * Returns an immutable view onto the set of all aliases *from* the specified container.
	 *
	 * ~ Double checked, 2014/03/14. FK.
	 *
	 * @param container the container whose outgoing aliases will be returned.
	 * @return An immutable view onto the set of all aliases *from* the specified container.
	 */
	public Collection<IContainer> getAliasesFrom(IContainer container) {
		Set<IContainer> result;

		if (container == null || (result = _aliasesMap.get(container)) == null) {
			result = Collections.emptySet();
		}

		return Collections.unmodifiableSet(result);
	}

	/**
	 * Returns the reflexive, transitive closure of the alias function for
	 * container with id containerId.
	 *
	 * @param containerId
	 * @return
	 */
	public Set<IContainer> getAliasTransitiveReflexiveClosure(IContainer container) {
		if (container == null) {
			return Collections.emptySet();
		}

		Set<IContainer> closure = getAliasTransitiveClosure(container);
		closure.add(container);	// add self to set ==> reflexive
		return closure;
	}

	/**
	 * Removes all aliases that start from the container with given id.
	 *
	 * @param fromContainerId
	 * @return
	 */
	public void removeAllAliasesFrom(IContainer fromContainer) {
		_aliasesMap.remove(fromContainer);
	}

	/**
	 * Removes all aliases that end in the container with the given id.
	 *
	 * @param toContainerId
	 * @return
	 */
	public void removeAllAliasesTo(IContainer toContainer) {
		if (toContainer == null) {
			return;
		}

		List<IContainer> toRemove = new LinkedList<IContainer>();

		for (IContainer from : _aliasesMap.keySet()) {
			Set<IContainer> toSet = _aliasesMap.get(from) ;
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

	/**
	 * Returns an immutable view onto the set of all aliases *to* the specified container.
	 *
	 * ~ Double checked, 2014/03/14. FK.
	 *
	 * @param container the container whose incoming aliases will be returned.
	 * @return An immutable view onto the set of all aliases *to* the specified container.
	 */
	public Set<IContainer> getAliasesTo(IContainer container) {
		Set<IContainer> result = new HashSet<>();

		if (container != null) {
			for (Entry<IContainer,Set<IContainer>> aliasEntry : _aliasesMap.entrySet()) {
				if (aliasEntry.getValue().contains(container)) {
					result.add(aliasEntry.getKey());
				}
			}
		}

		return Collections.unmodifiableSet(result);
	}

	/**
	 * Returns the non-reflexive transitive alias closure of the specified container.
	 * The resulting set will NOT contain the specified container.
	 * @param container
	 * @return
	 */
	public Set<IContainer> getAliasTransitiveClosure(IContainer container) {
		Set<IContainer> result = new HashSet<>();
		getAliasTransitiveClosure(container, result);
		result.remove(container);
		return result;
	}

	private void getAliasTransitiveClosure(IContainer container, Set<IContainer> visitedContainers) {
		for (IContainer id : getAliasesFrom(container)) {
			if (visitedContainers.add(id)) {
				getAliasTransitiveClosure(id, visitedContainers);
			}
		}
	}


	/**
	 * Adds the given data to the given container. If data
	 * or container is null, nothing will happen.
	 *
	 * ~ Double checked, 2014/03/14. FK.
	 *
	 * @param data the data to add
	 * @param container to which container the data is added.
	 */
	public void addData(IData data, IContainer container) {
		if (data == null || container == null) {
			return;
		}

		addData(Collections.singleton(data), container);
	}

	/**
	 * Removes the given data from the given container.
	 *
	 * ~ Double checked, 2014/03/14. FK.
	 *
	 * @param data the data to remove
	 * @param container the container from which the data will be removed
	 * @return true, if the data has been removed
	 */
	public void removeData(IData data, IContainer container) {
		Set<IData> dataSet;

		if (container == null || data == null || (dataSet = _containerToDataMap.get(container)) == null) {
			return;
		}

		dataSet.remove(data);
		if (dataSet.size() == 0) {
			_containerToDataMap.remove(container);
		}
	}

	/**
	 * Returns an immutable view onto the set of data within the given container.
	 * In doubt, returns an empty set; never null.
	 *
	 * ~ Double checked, 2014/03/14. FK.
	 *
	 * @param container the container of which we want to get the data
	 * @return an immutable view onto the set of data items stored in the given container
	 */
	public Set<IData> getData(IContainer container) {
		Set<IData> result;
		if (container == null ||  (result = _containerToDataMap.get(container)) == null) {
			result = Collections.emptySet();
		}
		return Collections.unmodifiableSet(result);
	}


	/**
	 * Returns the data contained in the container identified by the given name,
	 * cf. {@link #getData(IContainer)}.
	 *
	 * ~ Double checked, 2014/03/14. FK.
	 *
	 * @param containerName a name of the container of which the containing data will be returned.
	 * @return an immutable view onto the set of data within the container
	 */
	public Set<IData> getData(IName containerName) {
		IContainer cont;

		if (containerName == null || (cont = _namingMap.get(containerName)) == null) {
			return Collections.emptySet();
		}

		return getData(cont);
	}


	/**
	 * Copies all data contained in the container identified by srcContainerName to
	 * the container identified by dstContainerName.
	 *
	 * @param srcContainerName
	 * @param dstContainerName
	 * @return true if both containers existed and data (possibly none, if fromContainer was empty) was copied.
	 */
	public boolean copyData(IName srcContainerName, IName dstContainerName) {
		if (srcContainerName == null || dstContainerName == null) {
			return false;
		}

		return copyData(_namingMap.get(srcContainerName), _namingMap.get(dstContainerName));
	}


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

	public void addDataTransitively(Collection<IData> data, IName dstContainerName) {
		if (dstContainerName == null) {
			return;
		}

		addDataTransitively(data, getContainer(dstContainerName));
	}

	public void addDataTransitively(Collection<IData> data, IContainer dstContainer) {
		if (data == null || data.size() == 0 || dstContainer == null) {
			return;
		}

		addData(data, dstContainer);
		for (IContainer c : getAliasesFrom(dstContainer)) {
			addData(data, c);
		}
	}


	/**
	 * Returns all containers in which the specified data is in
	 *
	 * ~ Double checked, 2014/04/10. FK.
	 *
	 * @param data the data whose containers are returned.
	 * @return The set of containers containing the specified data.
	 */
	public Set<IContainer> getContainers(IData data) {
		if (data == null) {
			return Collections.emptySet();
		}

		Set<IContainer> result = new HashSet<>();

		for (Entry<IContainer, Set<IData>> entry : _containerToDataMap.entrySet()) {
			if (entry.getValue().contains(data)) {
				result.add(entry.getKey());
			}
		}
		return result;
	}

	public void addData(Collection<IData> data, IContainer container) {
		if (data == null || container == null) {
			return;
		}

		Set<IData> dstData = _containerToDataMap.get(container);
		if (dstData == null) {
			dstData = new HashSet<IData>();
			_containerToDataMap.put(container, dstData);
		}

		_logger.info("Adding data " + data + " to container " + container);;
		dstData.addAll(data);
	}


	/**
	 * Makes the given name point to the given container.
	 *
	 * If the given name was already assigned to another container,
	 * this old name/container mapping is overwritten. If this was the
	 * last name for that container, the corresponding container is deleted.
	 *
	 * ~ Double checked, 2014/03/14. FK.
	 *
	 * @param name the new name for the given container.
	 * @param container the container for which the new name applies.
	 */
	public void addName(IName name, IContainer container) {
		if (name == null || container == null) {
			return;
		}

		_logger.info("addName: " + name + " -> " + container);

		IContainer oldAssigned;
		if ((oldAssigned = _namingMap.put(name, container)) != null) {
			_logger.info("A container (" + oldAssigned + ") was already assigned to name " + name + ". "
					+ "This mapping has been removed.");

			if (getAllNames(oldAssigned).size() == 0) {
				remove(oldAssigned);
			}
		}
	}

	/**
	 * Adds an additional name, newName, for the container that is
	 * already identified by another name, oldName.
	 *
	 * ~ Double checked, 2014/03/14. FK.
	 *
	 * @param oldName a name identifying an already existing container
	 * @param newName the additional new name for the container identified by oldName.
	 */
	public void addName(IName oldName, IName newName) {
		if (oldName == null || newName == null) {
			return;
		}

		IContainer cont;
		if ((cont = getContainer(oldName)) != null) {
			addName(newName, cont);
		}
	}

	/**
	 * Removes the name.
	 *
	 * @param name
	 * @return
	 */
	public void removeName(IName name) {
		if (name != null) {
			_logger.info("removeName() " + name);
			IContainer cont = _namingMap.remove(name);

			// if this was the last name, we can remove the container
			Collection<IName> remainingNames = getAllNames(cont);
			if (remainingNames == null || remainingNames.size() == 0) {
				remove(cont);
			}
		}
	}

	/**
	 * Returns the container that is referenced by the naming name.
	 *
	 * @param name
	 * @return
	 */
	public IContainer getContainer(IName name) {
		if (name != null) {
			return _namingMap.get(name);
		}
		return null;
	}


	/**
	 * Returns the container that is referenced by the naming name. The search
	 * is done in a less strict way; it is enough that the name only partially
	 * fits an entry in the naming mapping.
	 * @deprecated Why would it be useful?. Should imo be deleted. -FK-
	 * @param name
	 * @return
	 */
// 	FK:  Why would it be useful???
//		If there is someone who agrees: Please delete this code.
	@Deprecated
	public IContainer getContainerRelaxed(IName name) {
		IContainer container = null;
		if (name != null && name.getName() != null) {
			String representationName = name.getName();
			for (IName nm : _namingMap.keySet()) {
				if (nm.getName() != null
						&& nm.getName().contains(representationName)) {

					container = _namingMap.get(nm);
					break;
				}
			}
		}
		return container;
	}

	/**
	 * Returns an unmodifiable view onto all containers.
	 *
	 * ~ Double checked, 2014/03/30. FK.
	 *
	 * @return an unmodifiable view onto all containers.
	 */
	public Set<IContainer> getAllContainers() {
		return Collections.unmodifiableSet(Sets.newHashSet(_namingMap.values()));
	}

	/**
	 * Returns an unmodifiable view onto all names.
	 *
	 * ~ Double checked, 2014/03/14. FK.
	 *
	 * @return an unmodifiable view onto all names.
	 */
	public Collection<IName> getAllNames() {
		return Collections.unmodifiableCollection(_namingMap.keySet());
	}


	/**
	 * Returns an unmodifiable view onto all names
	 * for the given container.
	 *
	 * ~ Double checked, 2014/03/14. FK.
	 *
	 * @param container the container whose names are returned.
	 * @return an unmodifiable view onto all names for the given container
	 */
	public Collection<IName> getAllNames(IContainer container) {
		Collection<IName> result = new LinkedList<>();

		for (Entry<IName, IContainer> nameEntry : _namingMap.entrySet()) {
			if (nameEntry.getValue().equals(container)) {
				result.add(nameEntry.getKey());
			}
		}

		return Collections.unmodifiableCollection(result);
	}


	/**
	 * Get all names of the container identified by the given containerName.
	 * It is ensured that all names within the result are of the specified type.
	 * @param containerName
	 * @param type
	 * @return
	 */
	public <T extends IName> List<T> getAllNames(IName containerName, Class<T> type) {
		IContainer cont;

		if (containerName == null || (cont = _namingMap.get(containerName)) == null) {
			return Collections.emptyList();
		}

		return getAllNames(cont, type);
	}

	/**
	 * Get all names of the specified container
	 * It is ensured that all names within the result are of the specified type.
	 * @param containerName
	 * @param type
	 * @return
	 */
	public <T extends IName> List<T> getAllNames(IContainer cont, Class<T> type) {
		List<T> result = new LinkedList<>();

		for (IName name : _namingMap.keySet()) {
			if (type.isInstance(name)) {
				if (_namingMap.get(name).equals(cont)) {
					result.add(type.cast(name));
				}
			}
		}

		return Collections.unmodifiableList(result);
	}

	/**
	 * Returns all representations that correspond to the process with pid.
	 *
	 */
	/*
	 *  TODO
	 *  This method seems odd.
	 *  Moreover, it is layer-specific and should thus not go
	 *  into this class.
	 *  Fix, delete, move, ...
	 *  -FK-
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

	public String niceString() {
		StringBuilder sb = new StringBuilder();

		String nl = System.getProperty("line.separator");
		String arrow = " ---> ";
		String arrowL = " <--- ";

		sb.append("  Storage:" + nl);
		for (Entry<IContainer,Set<IData>> entry :_containerToDataMap.entrySet()) {
			sb.append("    " + entry.getKey().getId() + arrow);
			boolean first = true;
			for (IData d : entry.getValue()) {
				if (first) {
					first = false;
				}
				else {
					sb.append("    ");
					for (int i = 0; i < entry.getKey().getId().length() + arrow.length(); i++) {
						sb.append(" ");
					}
				}
				sb.append(d.getId() + nl);
			}
		}
		sb.append(nl);

		sb.append("  Aliases:" + nl);
		for (Entry<IContainer,Set<IContainer>> entry :_aliasesMap.entrySet()) {
			sb.append("    " + entry.getKey().getId() + arrow);
			boolean first = true;
			for (IContainer c : entry.getValue()) {
				if (first) {
					first = false;
				}
				else {
					sb.append("    ");
					for (int i = 0; i < entry.getKey().getId().length() + arrow.length(); i++) {
						sb.append(" ");
					}
				}
				sb.append(c.getId() + nl);
			}
		}
		sb.append(nl);

		sb.append("  Naming:" + nl);
		Set<IContainer> wasPrinted = new HashSet<IContainer>();
		for (IContainer cont : _namingMap.values()) {
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
					}
					else {
						sb.append("    ");
						for (int i = 0; i < cont.getId().length() + arrowL.length(); i++) {
							sb.append(" ");
						}
					}
					sb.append(name.getName() + nl);
				}
			}
		}
		sb.append(nl);

		return sb.toString();
	}

	@Override
	public String toString() {
		return com.google.common.base.Objects.toStringHelper(this)
				.add("_containerToDataMap", _containerToDataMap)
				.add("_aliasesMap", _aliasesMap)
				.add("_namingMap", _namingMap)
				.toString();
	}
}
