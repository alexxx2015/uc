package de.tum.in.i22.pip.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import de.tum.in.i22.uc.cm.basic.ContainerName;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IIdentifiable;

/**
 * Information flow model Singleton.
 */
public class InformationFlowModel {
	private static final Logger _logger = Logger
			.getLogger(InformationFlowModel.class);

	@Override
	public String toString() {
		return "InformationFlowModel [_containerSet=" + _containerSet
				+ ", _dataSet=" + _dataSet + ", _dataToContainerMap="
				+ _dataToContainerMap + ", _containerAliasesMap="
				+ _containerAliasesMap + ", _namingSet=" + _namingSet
				+ ", _scopeSet=" + _scopeSet + " IS_SIMULATING="+isSimulating()+"]";
	}

	private static InformationFlowModel _instance = new InformationFlowModel();

	// list of containers
	private Set<IContainer> _containerSet = null;
	// list of data
	private Set<IData> _dataSet = null;

	// [Container.identifier -> List[Data.identifier]]
	private Map<String, Set<String>> _dataToContainerMap = null;

	// [Container.identifier -> List[Container.identifier]]
	private Map<String, Set<String>> _containerAliasesMap = null;

	// the naming set [name -> Container.identifier]
	private Map<ContainerName, String> _namingSet = null;

	// list of currently opened scopes
	private Set<Scope> _scopeSet = null;

	// BACKUP TABLES FOR SIMULATION
	private Set<IContainer> _containerSetBackup;
	private Set<IData> _dataSetBackup;
	private Map<String, Set<String>> _dataToContainerMapBackup;
	private Map<String, Set<String>> _containerAliasesMapBackup;
	private Map<ContainerName, String> _namingSetBackup;
	private Set<Scope> _scopeSetBackup;

	public InformationFlowModel() {
		_containerSet = new HashSet<>();
		_dataSet = new HashSet<>();
		_dataToContainerMap = new HashMap<>();
		_containerAliasesMap = new HashMap<>();
		_namingSet = new HashMap<>();
		_scopeSet = new HashSet<>();

		_containerSetBackup = null;
		_dataSetBackup = null;
		_dataToContainerMapBackup = null;
		_containerAliasesMapBackup = null;
		_namingSetBackup = null;
		_scopeSetBackup = null;

	}

	public static InformationFlowModel getInstance() {
		if (_instance==null) _instance = new InformationFlowModel();
		return _instance;
	}


	public boolean isSimulating(){
		return !((_containerSetBackup == null) && (_dataSetBackup == null)
				&& (_dataToContainerMapBackup == null)
				&& (_containerAliasesMapBackup == null)
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
			_containerSetBackup = _containerSet;
			_dataSetBackup = new HashSet<>(_dataSet);

			_dataToContainerMapBackup = new HashMap<String, Set<String>>();
			for (Entry<String, Set<String>> e : _dataToContainerMap.entrySet()) {
				Set<String> s = new HashSet<String>(e.getValue());
				_dataToContainerMapBackup.put(e.getKey(), s);
			}

			_containerAliasesMapBackup = new HashMap<>();
			for (Entry<String, Set<String>> e : _containerAliasesMap.entrySet()) {
				Set<String> s = new HashSet<String>(e.getValue());
				_containerAliasesMapBackup.put(e.getKey(), s);
			}

			_namingSetBackup = new HashMap<ContainerName, String>(_namingSet);
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
			_containerSet = _containerSetBackup;
			_dataSet = _dataSetBackup;
			_dataToContainerMap = _dataToContainerMapBackup;
			_containerAliasesMap = _containerAliasesMapBackup;
			_namingSet = _namingSetBackup;
			_scopeSet = _scopeSetBackup;

			_containerSetBackup = null;
			_dataSetBackup = null;
			_dataToContainerMapBackup = null;
			_containerAliasesMapBackup = null;
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
	 * Adds data object to a set. If the set already contains the data object,
	 * the method will return null. Otherwise, it will return the id of the data
	 * object.
	 *
	 * @param data
	 * @return Id of the data object if set does not contain the data object,
	 *         otherwise null.
	 */
	public String addData(IData data) {
		assert (data != null);

		if (_dataSet.contains(data)) {
			return null;
		}

		_dataSet.add(data);

		return data.getId();
	}

	/**
	 * Removes data object form an internal set.
	 *
	 * @param data
	 * @return true if the data object is successfully removed.
	 */
	public boolean removeData(IData data) {
		assert (data != null);
		return _dataSet.remove(data);
	}


	/**
	 * Removes data object form an internal set.
	 *
	 * @param dataId
	 * @return true if the data object is successfully removed.
	 */
	public boolean removeData(String dataId) {
		assert (dataId != null);

		return _dataSet.remove(dataId);
	}



	/**
	 * Searches for data object by id.
	 *
	 * @param id
	 * @return Data object if it is present in the set, otherwise null.
	 */
	public IData getDataById(String id) {
		return (IData) getElementFromSet(id, _dataSet);
	}

	/**
	 * Checks if the model contains a data object with given id.
	 *
	 * @param id
	 * @return
	 */
	public boolean hasData(String id) {
		IData data = getDataById(id);
		return data != null ? true : false;
	}

	/**
	 * Inserts container into the model.
	 *
	 * @param container
	 * @return Id of the container.
	 */
	public String addContainer(IContainer container) {
		if (_containerSet.contains(container)) {
			return null;
		}

		_containerSet.add(container);
		return container.getId();
	}

	public boolean removeContainer(String id) {
		IContainer container = (IContainer) getElementFromSet(id, _containerSet);
		boolean res = false;
		if (container == null) {
			res = false;
		} else {
			res = _containerSet.remove(container);
		}
		return res;
	}

	public boolean hasContainerWithId(String id) {
		IContainer container = getContainerById(id);
		return container != null ? true : false;
	}

	public IContainer getContainerById(String id) {
		return (IContainer) getElementFromSet(id, _containerSet);
	}

	public boolean emptyContainer(String id) {
//		boolean res = false;
//
//		// TODO, FK: deleted this code. Can be done in one line (below).
//		// If you agree, delete this comment.
//		if (hasContainerWithId(id)) {
//			Set<String> set = _dataToContainerMap.get(id);
//			if (set != null) {
//
//				//WELL DONE TO WHOMEVER WROTE SET.clear(); INSTEAD Of THE FOLLOWING LINE
//				//JUST WASTED HOURS FINDING THE BUG
//				_dataToContainerMap.remove(id);
//
//
//				res = true;
//			}
//		}
//
//		return res;

		return _dataToContainerMap.remove(id) != null;
	}

	/**
	 * Adds an alias relation from one container to another.
	 *
	 * @return
	 */
	public boolean addAlias(String fromContainerId, String toContainerId) {
		boolean res = false;
		if (_containerAliasesMap.containsKey(fromContainerId)) {
			Set<String> set = _containerAliasesMap.get(fromContainerId);
			res = set.add(toContainerId);
		} else {
			// Create new set and add it to the map
			Set<String> newSet = new HashSet<>();
			newSet.add(toContainerId);
			_containerAliasesMap.put(fromContainerId, newSet);
			res = true;
		}
		return res;
	}

	/**
	 * Removes the alias relation identified by the tuple (fromContainerId,
	 * toContainerId)
	 *
	 * @param fromContainerId
	 * @param toContainerId
	 * @return
	 */
	public boolean removeAlias(String fromContainerId, String toContainerId) {
		boolean res = false;
		if (_containerAliasesMap.containsKey(fromContainerId)) {
			Set<String> set = _containerAliasesMap.get(fromContainerId);
			res = set.remove(fromContainerId);
		}
		return res;
	}

	/**
	 * Fins all aliases of a container given by its id.
	 *
	 * @param id
	 * @return All aliases of a container given by its id, empty set if the
	 *         container has no aliases.
	 */
	public Set<String> getAliasesForContainer(String id) {
		Set<String> result = _containerAliasesMap.get(id);
		// if no such element is present, return an empty set
		if (result == null)
			result = new HashSet<>();

		return result;
	}

	/**
	 * Returns the reflexive, transitive closure of the alias function for
	 * container with id containerId.
	 *
	 * @param containerId
	 * @return
	 */
	public Set<String> getAliasTransitiveReflexiveClosure(String containerId) {
		Set<String> closureSet = getAliasClosureExcludeStartId(containerId);
		// add self to set ==> reflexive
		closureSet.add(containerId);

		return closureSet;
	}

	/**
	 * Removes all aliases that start from the container with given id.
	 *
	 * @param fromContainerId
	 * @return
	 */
	public boolean removeAllAliasesFrom(String fromContainerId) {
		boolean res = false;
		if (_containerAliasesMap.containsKey(fromContainerId)) {
			Set<String> set = _containerAliasesMap.get(fromContainerId);
			set.clear();
			res = true;
		}
		return res;
	}

	/**
	 * Removes all aliases that end in the container with the given id.
	 *
	 * @param toContainerId
	 * @return
	 */
	public boolean removeAllAliasesTo(String toContainerId) {
		// TODO This method seems to have a bug.
		boolean res = false;
		Set<String> aliasesToContainer = getAliasesTo(toContainerId);
		if (aliasesToContainer.size() > 0) {
			Collection<Set<String>> aliasesCollection = _containerAliasesMap
					.values();
			for (Set<String> aliases : aliasesCollection) {
				if (aliases.contains(toContainerId)) {
					_containerAliasesMap.remove(toContainerId);
				}
			}
			res = true;
		}
		return res;
	}

	/**
	 *
	 * @param toContainerId
	 * @return All aliases that go to the container with the given id.
	 */
	public Set<String> getAliasesTo(String toContainerId) {
		Set<String> result = new HashSet<String>();
		Set<Entry<String, Set<String>>> entrySet = _containerAliasesMap
				.entrySet();
		for (Entry<String, Set<String>> entry : entrySet) {
			Set<String> aliasesSet = entry.getValue();
			if (aliasesSet.contains(toContainerId)) {
				result.add(entry.getKey());
			}
		}
		return result;
	}

	private Set<String> getAliasClosureExcludeStartId(String containerId) {
		Set<String> result = new HashSet<>();
		getAliasClosureExcludeStartId(containerId, result);
		return result;
	}

	private void getAliasClosureExcludeStartId(String containerId,
			Set<String> visitedContainers) {
		Set<String> containerAliasesSet = getAliasesForContainer(containerId);
		for (String id : containerAliasesSet) {
			if (visitedContainers.add(id)) {
				getAliasClosureExcludeStartId(id, visitedContainers);
			}
		}
	}

	private IIdentifiable getElementFromSet(String id,
			Set<? extends IIdentifiable> set) {
		if (set==null) return null;
		Iterator<? extends IIdentifiable> it = set.iterator();
		IIdentifiable element = null;
		boolean found = false;
		while (!found && it.hasNext()) {
			element = it.next();
			if (element.getId().equals(id)) {
				found = true;
			}
		}
		return element;
	}

	public boolean addDataToContainerMapping(String dataId, String containerId) {
		boolean res = false;
		if (_dataToContainerMap.containsKey(containerId)) {
			Set<String> dataSet = _dataToContainerMap.get(containerId);
			res = dataSet.add(dataId);
		} else {
			Set<String> newDataSet = new HashSet<>();

			//TODO: check if dataId corresponds to a valid data element
			newDataSet.add(dataId);

			_dataToContainerMap.put(containerId, newDataSet);
			res = true;
		}
		return res;
	}

	public boolean removeContainerToDataMapping(String containerId,
			String dataId) {
		boolean res = false;
		if (_dataToContainerMap.containsKey(containerId)) {
			Set<String> dataSet = _dataToContainerMap.get(containerId);
			dataSet.remove(dataId);
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
	public Set<String> getDataInContainer(String containerId) {
		Set<String> result = _dataToContainerMap.get(containerId);
		if (result == null) {
			result = new HashSet<>();
		}
		return result;
	}

	/**
	 *
	 * @param dataId
	 * @return All container ids as a set for the data with the given id.
	 */
	public Set<String> getContainersForData(String dataId) {
		Set<String> result = new HashSet<>();
		Set<Entry<String, Set<String>>> entrySet = _dataToContainerMap
				.entrySet();
		for (Entry<String, Set<String>> entry : entrySet) {
			if (entry.getValue().contains(dataId)) {
				result.add(entry.getKey());
			}
		}
		return result;
	}

	public boolean addDataToContainerMappings(Set<String> dataSet,
			String containerId) {
		if (dataSet == null) {
			return true;
		}

		boolean res = false;

		Set<String> existingDataSet = _dataToContainerMap.get(containerId);
		if (existingDataSet == null) {
			Set<String> newDataSet = new HashSet<>(dataSet);
			_dataToContainerMap.put(containerId, newDataSet);
			res = true;
		} else {
			res = existingDataSet.addAll(dataSet);
		}
		return res;
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
	public boolean addName(ContainerName name, String containerId) {
		boolean res = false;
		if (name != null && !name.getName().isEmpty()) {
			_namingSet.put(name, containerId);
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
	public boolean removeName(ContainerName name) {
		boolean res = false;
		if (name != null) {
			String removedEntry = _namingSet.remove(name);
			res = removedEntry != null ? true : false;
		}
		return res;
	}

	/**
	 * Returns the container that is referenced by the naming name.
	 *
	 * @param name
	 * @return
	 */
	public String getContainerIdByName(ContainerName name) {
		String containerId = null;
		if (_namingSet != null && name != null && name.getName() != null) {
//			// FK: Malte's old code. The single line below should do the job as well
//			// If you (anyone) agree, please delete
//			Set<Name> pipNameSet = _namingSet.keySet();
//			for (Name nm : pipNameSet) {
//				String representationName = nm.getName();
//				if (name.getName().equals(representationName)) {
//					containerId = _namingSet.get(nm);
//					break;
//				}
//			}

			return _namingSet.get(name);
		}
		return containerId;
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
	public String getContainerIdByNameRelaxed(ContainerName name) {
		String containerId = null;
		if (name != null && name.getName() != null) {
			String representationName = name.getName();
			for (ContainerName nm : _namingSet.keySet()) {
				if (nm.getName() != null
						&& nm.getName().contains(representationName)) {

					containerId = _namingSet.get(nm);
					break;
				}
			}
		}
		return containerId;
	}

	/**
	 * Return all names that refer to the container with containerId.
	 *
	 * @param containerId
	 * @return
	 */
	public List<ContainerName> getAllNames(String containerId) {
		List<ContainerName> result = new ArrayList<ContainerName>();

//		// FK: Malte's old code. The (more efficient) code below should do the job as well
//		// If you (anyone) agree, please delete
//		if (_namingSet.containsValue(containerId)) {
//			for (Entry<Name, String> entry : _namingSet.entrySet()) {
//				if (entry.getValue() == containerId) {
//					result.add(entry.getKey());
//				}
//			}
//		}

		if (_namingSet != null) {
			for (ContainerName name : _namingSet.keySet()) {
				if (_namingSet.get(name).equals(containerId)) {
					result.add(name);
				}
			}
		}

		return result;
	}

	/**
	 * Returns all representations that correspond to the process with pid.
	 *
	 */
	public List<ContainerName> getAllNamingsFrom(String pid) {
		List<ContainerName> result = new ArrayList<ContainerName>();

		for (Entry<ContainerName, String> entry : _namingSet.entrySet()) {
			if (entry.getKey().getName().equals(pid))
				result.add(entry.getKey());
		}

		return result;
	}

}
