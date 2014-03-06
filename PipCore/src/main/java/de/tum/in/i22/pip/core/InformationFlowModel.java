package de.tum.in.i22.pip.core;

import java.util.ArrayList;
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
	private static final Logger _logger = Logger
			.getLogger(InformationFlowModel.class);

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


	public void remove(IContainer cont) {
		if (cont != null) {
			removeAllAliasesTo(cont);
			removeAllAliasesFrom(cont);
			removeAllNames(cont);
			emptyContainer(cont);
		}
	}

	public void removeAllNames(IContainer cont) {
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
			_containerToDataMap.remove(cont);
		}
	}

	/**
	 * Adds an alias relation from one container to another.
	 *
	 * @return
	 */
	public void addAlias(IContainer fromContainer, IContainer toContainer) {
		Set<IContainer> aliases = _aliasesMap.get(fromContainer);
		if (aliases == null) {
			aliases = new HashSet<IContainer>();
			_aliasesMap.put(fromContainer, aliases);
		}
		aliases.add(toContainer);
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
		Set<IContainer> result = _aliasesMap.get(cont);

		if (result == null) {
			result = new HashSet<>();
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
		Set<IContainer> closureSet = getAliasClosureExcludeStartId(container);
		// add self to set ==> reflexive
		closureSet.add(container);

		return closureSet;
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
		if (toContainer != null) {
			for (IContainer from : _aliasesMap.keySet()) {
				_aliasesMap.get(from).remove(toContainer);
			}
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

	private Set<IContainer> getAliasClosureExcludeStartId(IContainer container) {
		Set<IContainer> result = new HashSet<>();
		getAliasClosureExcludeStartId(container, result);
		return result;
	}

	private void getAliasClosureExcludeStartId(IContainer container,
			Set<IContainer> visitedContainers) {
		Set<IContainer> containerAliasesSet = getAliasesFromContainer(container);
		for (IContainer id : containerAliasesSet) {
			if (visitedContainers.add(id)) {
				getAliasClosureExcludeStartId(id, visitedContainers);
			}
		}
	}


	public void addDataToContainerMapping(IData data, IContainer container) {
		Set<IData> s = new HashSet<IData>();
		addDataToContainerMappings(s, container);
	}

	public boolean removeContainerToDataMapping(IContainer container,
			IData data) {
		boolean res = false;
		if (_containerToDataMap.containsKey(container)) {
			Set<IData> dataSet = _containerToDataMap.get(container);
			dataSet.remove(data);
			res = true;
		}
		return res;
	}

	/**
	 * @param containerId
	 * @return All data items that are stored in the given data container. If
	 *         there are no data for the given data container, returns empty
	 *         set.
	 */
	public Set<IData> getDataInContainer(IContainer container) {
		Set<IData> result = _containerToDataMap.get(container);
		if (result == null) {
			result = new HashSet<>();
		}
		return result;
	}


	/**
	 * Returns the data contained in the container identified by the given name.
	 * @param containerName
	 * @return
	 */
	public Set<IData> getDataInContainer(IName containerName) {
		IContainer cont;
		if (containerName != null) {
			cont = _namingMap.get(containerName);

			if (cont != null) {
				return _containerToDataMap.get(cont);
			}
		}
		return null;
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

		IContainer srcContainer = _namingMap.get(srcContainerName);
		IContainer dstContainer = _namingMap.get(dstContainerName);

		if (srcContainer == null || dstContainer == null) {
			return false;
		}

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
	 * Adds an entry to the naming mapping for container contID, with the
	 * naming/representation name.
	 *
	 * @param name
	 * @param containerId
	 * @return
	 */
	public boolean addName(IName name, IContainer container) {
		_logger.info("addName: " + name + " -> " + container);
		boolean res = false;
		IContainer oldAssigned;
		if (name != null && !name.getName().isEmpty()) {
			if ((oldAssigned = _namingMap.put(name, container)) != null) {
				_logger.info("A container (" + oldAssigned + ") was already assigned to name " + name + ". This mapping has been removed.");
			}
			res = true;
		}
		return res;
	}

	/**
	 * Removes the naming/representation name from the naming set.
	 *
	 * @param name
	 * @return
	 */
	public boolean removeName(IName name) {
		return _namingMap.remove(name) != null;
	}

	/**
	 * Returns the container that is referenced by the naming name.
	 *
	 * @param name
	 * @return
	 */
	public IContainer getContainer(IName name) {
		if (name != null && name.getName() != null) {
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

		return result;
	}

	/**
	 * Returns all representations that correspond to the process with pid.
	 *
	 */
	public List<IName> getAllNamingsFrom(IContainer pid) {
		List<IName> result = new ArrayList<IName>();

		for (Entry<IName, IContainer> entry : _namingMap.entrySet()) {
			if (entry.getKey().getName().equals(pid))
				result.add(entry.getKey());
		}

		return result;
	}

}
