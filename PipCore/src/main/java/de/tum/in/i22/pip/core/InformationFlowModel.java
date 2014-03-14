package de.tum.in.i22.pip.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IName;

/**
 * Information flow model Singleton.
 */
public class InformationFlowModel {
	private static final Logger _logger = Logger.getLogger(InformationFlowModel.class);

	@Override
	public String toString() {
		return com.google.common.base.Objects.toStringHelper(this)
				.add("_containerToDataMap", _containerToDataMap)
				.add("_aliasesMap", _aliasesMap)
				.add("_namingMap", _namingMap)
				.add("_scopeSet", _scopeSet)
				.add("_isSimulating", isSimulating())
				.toString();
	}

	private final static InformationFlowModel _instance = new InformationFlowModel();

	// [Container.identifier -> List[Data.identifier]]
	private Map<IContainer, Set<IData>> _containerToDataMap = null;

	// [Container.identifier -> List[Container.identifier]]
	private Map<IContainer, Set<IContainer>> _aliasesMap = null;

	// the naming set [name -> Container.identifier]
	private Map<IName, IContainer> _namingMap = null;

	// list of currently opened scopes
	private Set<Scope> _scopeSet = null;

	// BACKUP TABLES FOR SIMULATION
	private Set<IContainer> _containerSetBackup;
	private Set<IData> _dataSetBackup;
	private Map<IContainer, Set<IData>> _containerToDataMapBackup;
	private Map<IContainer, Set<IContainer>> _aliasesMapBackup;
	private Map<IName, IContainer> _namingSetBackup;
	private Set<Scope> _scopeSetBackup;

	public InformationFlowModel() {
		_containerToDataMap = new HashMap<>();
		_aliasesMap = new HashMap<>();
		_namingMap = new HashMap<>();
		_scopeSet = new HashSet<>();

		_containerSetBackup = null;
		_dataSetBackup = null;
		_containerToDataMapBackup = null;
		_aliasesMapBackup = null;
		_namingSetBackup = null;
		_scopeSetBackup = null;

	}

	public static InformationFlowModel getInstance() {
		return _instance;
	}


	public boolean isSimulating(){
		return !((_containerSetBackup == null) && (_dataSetBackup == null)
				&& (_containerToDataMapBackup == null)
				&& (_aliasesMapBackup == null)
				&& (_namingSetBackup == null) && (_scopeSetBackup == null));
	}


	/**
	 * Simulation step: push. Stores the current IF state, if not already stored
	 * @return true if the state has been successfully pushed, false otherwise
	 */
	public boolean push() {
		_logger.info("Pushing current PIP state...");
		if (!isSimulating()) {
			_logger.info("..done!");

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
			_scopeSetBackup = new HashSet<Scope>(_scopeSet);
			return true;
		}
		_logger.error("Current stack not empty!");
		return false;
	}

	/**
	 * Simulation step: pop. Restore a previously pushed IF state, if any.
	 * @return true if the state has been successfully restored, false otherwise
	 */
	public boolean pop() {
		_logger.info("Popping current PIP state...");
		if (isSimulating()) {
			_logger.info("..done!");
			_containerToDataMap = _containerToDataMapBackup;
			_aliasesMap = _aliasesMapBackup;
			_namingMap = _namingSetBackup;
			_scopeSet = _scopeSetBackup;

			_containerSetBackup = null;
			_dataSetBackup = null;
			_containerToDataMapBackup = null;
			_aliasesMapBackup = null;
			_namingSetBackup = null;
			_scopeSetBackup = null;

			return true;
		}
		_logger.error("Current stack empty!");
		return false;
	}

	/**
	 * Adds a new scope to the set.
	 *
	 * @param scope
	 * @return true if the scope is not already present in the set. false
	 *         otherwise.
	 *
	 */
	public boolean addScope(Scope scope) {
		assert (scope != null);
		return _scopeSet.add(scope);
	}

	/**
	 * opens a new scope.
	 *
	 * @param the
	 *            new scope to open
	 * @return true if the scope is not already opened. false otherwise.
	 *
	 */
	public boolean openScope(Scope scope) {
		return addScope(scope);
	}

	/**
	 * Removes a scope from the set.
	 *
	 * @param scope
	 * @return true if the scope is successfully removed. false otherwise.
	 *
	 */
	public boolean removeScope(Scope scope) {
		assert (scope != null);
		return _scopeSet.remove(scope);
	}

	/**
	 * Close a specific scope.
	 *
	 * @param the
	 *            scope to be closed
	 * @return true if the scope is successfully closed. false otherwise.
	 *
	 */
	public boolean closeScope(Scope scope) {
		return removeScope(scope);
	}

	/**
	 * Checks whether a specific scope has been opened. Note that the scope can
	 * be under-specified with respect to the matching element in the set.
	 *
	 * @param the
	 *            (possibly under-specified) scope to be found
	 * @return true if the scope is in the set. false otherwise.
	 *
	 */
	public boolean isScopeOpened(Scope scope) {
		return _scopeSet.contains(scope);
	}

	/**
	 * Returns the only element that should match the (possibly under-specified)
	 * scope in the set of currently opened scopes. Note that if more than one
	 * active (i.e. opened but not closed) scope matches the parameter, the
	 * method returns null. Similarly, if no scope is found, the method returns
	 * null.
	 *
	 * There must exists only one matching otherwise the information about the
	 * scope are not enough to identify to which scope a certain event belongs
	 *
	 * @param the
	 *            (possibly under-specified) scope to be found
	 * @return the opened scope, if found. null if more than one match or no
	 *         match is found.
	 *
	 */
	public Scope getOpenedScope(Scope scope) {
		// if at least one matching exists...
		if (isScopeOpened(scope)) {

			// ...then clone the set...
			Set<Scope> tmpSet = new HashSet<>();
			tmpSet.addAll(_scopeSet);

			// ...remove it...
			tmpSet.remove(scope);

			// ..and check whether another one exists. If that is the case
			// return null...
			if (tmpSet.contains(scope))
				return null;

			// ..otherwise return the only matching from the original set.
			for (Scope s : _scopeSet)
				if (scope.equals(s))
					return s;
			// Note that we have to iterate until the matching is found again
			// and return that element.
			// This is due to our definition of equality.

			// This line should never be reached. It is added so Eclipse does
			// not complain about the lack of a return value
			assert (false);
			return null;

		} else
			return null;
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

	public void emptyContainer(IContainer cont) {
		if (cont != null) {
			_logger.info("Emptying container " + cont);
			_containerToDataMap.remove(cont);
		}
	}

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
	 * Removes the alias relation identified
	 *
	 * @param fromContainer
	 * @param toContainer
	 */
	public void removeAlias(IContainer fromContainer, IContainer toContainer) {
		Set<IContainer> aliases = _aliasesMap.get(fromContainer);
		if (aliases != null) {
			aliases.remove(toContainer);
		}
	}

	/**
	 * Finds all aliases from the specified container.
	 *
	 * @param cont
	 * @return All aliases from the container or an empty set.
	 */
	public Set<IContainer> getAliasesFromContainer(IContainer cont) {
		Set<IContainer> result;

		if (cont == null || (result = _aliasesMap.get(cont)) == null) {
			result = Collections.emptySet();
		}

		return result;
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

		List<IContainer> toRemove = new ArrayList<IContainer>();

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
	 *
	 * @param toContainerId
	 * @return All aliases that go to the container with the given id.
	 */
	public Set<IContainer> getAliasesTo(IContainer toContainer) {
		Set<IContainer> result = new HashSet<IContainer>();
		if (toContainer != null) {
			for (IContainer from : _aliasesMap.keySet()) {
				if (_aliasesMap.get(from).contains(toContainer)) {
					result.add(from);
				}
			}
		}
		return result;
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
		for (IContainer id : getAliasesFromContainer(container)) {
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
	public void addDataToContainer(IData data, IContainer container) {
		if (data == null || container == null) {
			return;
		}

		addDataToContainerMappings(Collections.singleton(data), container);
	}

	/**
	 * Removes the given data from the given container.
	 *
	 * ~ Double checked, 2014/03/14. FK.
	 *
	 * @param container the container from which the data will be removed
	 * @param data the data to remove
	 * @return true, if the data has been removed
	 */
	public boolean removeDataFromContainer(IContainer container, IData data) {
		Set<IData> dataSet;

		if (container == null || data == null || (dataSet = _containerToDataMap.get(container)) == null) {
			return false;
		}

		boolean res = dataSet.remove(data);

		if (dataSet.size() == 0) {
			_containerToDataMap.remove(container);
		}

		return res;
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
	public Set<IData> getDataInContainer(IContainer container) {
		Set<IData> result;
		if (container == null ||  (result = _containerToDataMap.get(container)) == null) {
			result = Collections.emptySet();
		}
		return Collections.unmodifiableSet(result);
	}


	/**
	 * Returns the data contained in the container identified by the given name,
	 * cf. {@link #getDataInContainer(IContainer)}.
	 *
	 * ~ Double checked, 2014/03/14. FK.
	 *
	 * @param containerName a name of the container of which the containing data will be returned.
	 * @return an immutable view onto the set of data within the container
	 */
	public Set<IData> getDataInContainer(IName containerName) {
		IContainer cont;

		if (containerName == null || (cont = _namingMap.get(containerName)) == null) {
			return Collections.emptySet();
		}

		return getDataInContainer(cont);
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

	public void addDataToContainerAndAliases(Set<IData> data, IName dstContainerName) {
		if (dstContainerName == null) {
			return;
		}

		addDataToContainerAndAliases(data, getContainer(dstContainerName));
	}

	public void addDataToContainerAndAliases(Set<IData> data, IContainer dstContainer) {
		if (data == null || data.size() == 0 || dstContainer == null) {
			return;
		}

		addDataToContainerMappings(data, dstContainer);
		for (IContainer c : getAliasesFromContainer(dstContainer)) {
			addDataToContainerMappings(data, c);
		}
	}


	/**
	 *
	 * @param dataId
	 * @return All container ids as a set for the data with the given id.
	 */
	public Set<IContainer> getContainersForData(IData data) {
		Set<IContainer> result = new HashSet<>();
		for (Entry<IContainer, Set<IData>> entry : _containerToDataMap.entrySet()) {
			if (entry.getValue().contains(data)) {
				result.add(entry.getKey());
			}
		}
		return result;
	}

	public void addDataToContainerMappings(Set<IData> data, IContainer container) {
		if (data == null || container == null) {
			return;
		}

		Set<IData> dstData = _containerToDataMap.get(container);
		if (dstData == null) {
			dstData = new HashSet<IData>();
			_containerToDataMap.put(container, dstData);
		}
		dstData.addAll(data);
	}

	// Naming set manipulation functions:
	/**
	 * Adds an entry to the naming mapping for container, with the
	 * naming/representation name.
	 *
	 * @param name
	 * @param container
	 * @return
	 */
	public boolean addName(IName name, IContainer container) {
		if (name == null || container == null) {
			return false;
		}

		_logger.info("addName: " + name + " -> " + container);

		IContainer oldAssigned;
		if ((oldAssigned = _namingMap.put(name, container)) != null) {
			_logger.info("A container (" + oldAssigned + ") was already assigned to name " + name + ". This mapping has been removed.");
		}
		return true;
	}

	/**
	 * Adds an additional name, newName, for to the container that is already identified by another name, oldName.
	 * @param oldName
	 * @param newName
	 * @return
	 */
	public boolean addName(IName oldName, IName newName) {
		if (oldName == null || newName == null) {
			return false;
		}

		IContainer cont = getContainer(oldName);

		return (cont != null) ? addName(newName, cont) : false;
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
			List<IName> remainingNames = getAllNames(cont);
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

// 	FK:  Why would it be useful???
//		If there is someone who agrees: Please delete this code.
	/**
	 * Returns the container that is referenced by the naming name. The search
	 * is done in a less strict way; it is enough that the name only partially
	 * fits an entry in the naming mapping.
	 *
	 * @param name
	 * @return
	 */
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
	 * Returns the set of all names.
	 * The returned set is unmodifiable as described in
	 * Collections.unmodifiableSet()
	 * @return
	 */
	public Set<IName> getAllNames() {
		return Collections.unmodifiableSet(_namingMap.keySet());
	}


	/**
	 * Return all names that refer to the container with containerId.
	 *
	 * @param containerId
	 * @return
	 */
	public List<IName> getAllNames(IContainer container) {
		List<IName> result = new ArrayList<IName>();

		for (IName name : _namingMap.keySet()) {
			if (_namingMap.get(name).equals(container)) {
				result.add(name);
			}
		}

		return Collections.unmodifiableList(result);
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
		List<T> result = new ArrayList<>();

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
	// TODO, FK: This method seems odd.
	public List<IName> getAllNamingsFrom(IContainer pid) {
		List<IName> result = new ArrayList<IName>();

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

		sb.append("-----------------------------------------------");

		return sb.toString();
	}
}
