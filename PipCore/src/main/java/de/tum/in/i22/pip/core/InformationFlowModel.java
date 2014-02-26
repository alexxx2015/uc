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

import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IIdentifiable;
import de.tum.in.i22.uc.cm.datatypes.IName;

/**
 * Information flow model Singleton.
 */
public class InformationFlowModel {
	private static final Logger _logger = Logger
			.getLogger(InformationFlowModel.class);

	@Override
	public String toString() {
		return "InformationFlowModel ["+System.getProperty("line.separator")+"_containerSet=" + _containerSet
				+ System.getProperty("line.separator")+", _dataSet=" + _dataSet + System.getProperty("line.separator")+", _dataToContainerMap="
				+ containerToDataMap + System.getProperty("line.separator")+", _containerAliasesMap="
				+ _aliasesMap + System.getProperty("line.separator")+", _namingSet=" + _namingMap+System.getProperty("line.separator")
				+ ", _scopeSet=" + _scopeSet + System.getProperty("line.separator")+" IS_SIMULATING="+isSimulating()+"]";
	}

	private final static InformationFlowModel _instance = new InformationFlowModel();

	// list of containers
	private Set<IContainer> _containerSet;
	// list of data
	private Set<IData> _dataSet;

	// [Container.identifier -> List[Data.identifier]]
	private Map<IContainer, Set<IData>> containerToDataMap = null;

	// [Container.identifier -> List[Container.identifier]]
	private Map<IContainer, Set<IContainer>> _aliasesMap = null;

	// the naming set [name -> Container.identifier]
	private Map<IName, IContainer> _namingMap = null;

	// list of currently opened scopes
	private Set<Scope> _scopeSet = null;

	// BACKUP TABLES FOR SIMULATION
	private Set<IContainer> _containerSetBackup;
	private Set<IData> _dataSetBackup;
	private Map<IContainer, Set<IData>> containerToDataMapBackup;
	private Map<IContainer, Set<IContainer>> _aliasesMapBackup;
	private Map<IName, IContainer> _namingSetBackup;
	private Set<Scope> _scopeSetBackup;

	public InformationFlowModel() {
		_containerSet = new HashSet<>();
		_dataSet = new HashSet<>();
		containerToDataMap = new HashMap<>();
		_aliasesMap = new HashMap<>();
		_namingMap = new HashMap<>();
		_scopeSet = new HashSet<>();

		_containerSetBackup = null;
		_dataSetBackup = null;
		containerToDataMapBackup = null;
		_aliasesMapBackup = null;
		_namingSetBackup = null;
		_scopeSetBackup = null;

	}

	public static InformationFlowModel getInstance() {
		return _instance;
	}


	public boolean isSimulating(){
		return !((_containerSetBackup == null) && (_dataSetBackup == null)
				&& (containerToDataMapBackup == null)
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
			_containerSetBackup = _containerSet;
			_dataSetBackup = new HashSet<>(_dataSet);

			containerToDataMapBackup = new HashMap<IContainer, Set<IData>>();
			for (Entry<IContainer, Set<IData>> e : containerToDataMap.entrySet()) {
				Set<IData> s = new HashSet<IData>(e.getValue());
				containerToDataMapBackup.put(e.getKey(), s);
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
			_containerSet = _containerSetBackup;
			_dataSet = _dataSetBackup;
			containerToDataMap = containerToDataMapBackup;
			_aliasesMap = _aliasesMapBackup;
			_namingMap = _namingSetBackup;
			_scopeSet = _scopeSetBackup;

			_containerSetBackup = null;
			_dataSetBackup = null;
			containerToDataMapBackup = null;
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
	 * Adds data object.
	 *
	 * @param data
	 */
	public void add(IData data) {
		if (data != null) {
			_dataSet.add(data);
		}
	}

	/**
	 * Removes data object.
	 *
	 * @param data
	 */
	public void remove(IData data) {
		if (data != null) {
			_dataSet.remove(data);
		}
	}


//	/**
//	 * Removes data object form an internal set.
//	 *
//	 * @param dataId
//	 * @return true if the data object is successfully removed.
//	 */
//	public boolean removeData(String dataId) {
//		assert (dataId != null);
//
//		return _dataSet.remove(dataId);
//	}




	public IData getDataById(IData id) {
		return (IData) getElementFromSet(id, _dataSet);
	}

	/**
	 * Checks if the model contains a data object with given id.
	 *
	 * @param id
	 * @return
	 */
	public boolean hasData(IData id) {
		IData data = getDataById(id);
		return data != null ? true : false;
	}

	/**
	 * Inserts container into the model.
	 *
	 * @param container
	 * @return Id of the container.
	 */
	public void addContainer(IContainer container) {
		if (container != null) {
			_containerSet.add(container);
		}
	}

	public boolean removeContainer(IContainer id) {
		IContainer container = (IContainer) getElementFromSet(id, _containerSet);
		boolean res = false;
		if (container == null) {
			res = false;
		} else {
			res = _containerSet.remove(container);
		}
		return res;
	}

	public boolean hasContainerWithId(IContainer id) {
		IContainer container = getContainerById(id);
		return container != null ? true : false;
	}

	public IContainer getContainerById(IContainer id) {
		return (IContainer) getElementFromSet(id, _containerSet);
	}

	public boolean emptyContainer(IContainer id) {
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

		return containerToDataMap.remove(id) != null;
	}

	/**
	 * Adds an alias relation from one container to another.
	 *
	 * @return
	 */
	public boolean addAlias(IContainer fromContainer, IContainer toContainer) {
		boolean res = false;
		if (_aliasesMap.containsKey(fromContainer)) {
			Set<IContainer> set = _aliasesMap.get(fromContainer);
			res = set.add(toContainer);
		} else {
			// Create new set and add it to the map
			Set<IContainer> newSet = new HashSet<>();
			newSet.add(toContainer);
			_aliasesMap.put(fromContainer, newSet);
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
	public boolean removeAlias(IContainer fromContainer, IContainer toContainer) {
		boolean res = false;
		if (_aliasesMap.containsKey(fromContainer)) {
			Set<IContainer> set = _aliasesMap.get(fromContainer);
			res = set.remove(fromContainer);
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
	public Set<IContainer> getAliasesForContainer(IContainer id) {
		Set<IContainer> result = _aliasesMap.get(id);
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
	public boolean removeAllAliasesFrom(IContainer fromContainer) {
		boolean res = false;
		if (_aliasesMap.containsKey(fromContainer)) {
			Set<IContainer> set = _aliasesMap.get(fromContainer);
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
	public boolean removeAllAliasesTo(IContainer toContainer) {
		// TODO This method seems to have a bug.
		boolean res = false;
		Set<IContainer> aliasesToContainer = getAliasesTo(toContainer);
		if (aliasesToContainer.size() > 0) {
			Collection<Set<IContainer>> aliasesCollection = _aliasesMap
					.values();
			for (Set<IContainer> aliases : aliasesCollection) {
				if (aliases.contains(toContainer)) {
					_aliasesMap.remove(toContainer);
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
	public Set<IContainer> getAliasesTo(IContainer toContainer) {
		Set<IContainer> result = new HashSet<IContainer>();
		Set<Entry<IContainer, Set<IContainer>>> entrySet = _aliasesMap
				.entrySet();
		for (Entry<IContainer, Set<IContainer>> entry : entrySet) {
			Set<IContainer> aliasesSet = entry.getValue();
			if (aliasesSet.contains(toContainer)) {
				result.add(entry.getKey());
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
		Set<IContainer> containerAliasesSet = getAliasesForContainer(container);
		for (IContainer id : containerAliasesSet) {
			if (visitedContainers.add(id)) {
				getAliasClosureExcludeStartId(id, visitedContainers);
			}
		}
	}

	private IIdentifiable getElementFromSet(IIdentifiable id,
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

	public boolean addDataToContainerMapping(IData data, IContainer container) {
		boolean res = false;
		if (containerToDataMap.containsKey(container)) {
			Set<IData> dataSet = containerToDataMap.get(container);
			res = dataSet.add(data);
		} else {
			Set<IData> newDataSet = new HashSet<>();

			//TODO: check if dataId corresponds to a valid data element
			newDataSet.add(data);

			containerToDataMap.put(container, newDataSet);
			res = true;
		}
		return res;
	}

	public boolean removeContainerToDataMapping(IContainer container,
			IData data) {
		boolean res = false;
		if (containerToDataMap.containsKey(container)) {
			Set<IData> dataSet = containerToDataMap.get(container);
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
		Set<IData> result = containerToDataMap.get(container);
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
	public Set<IContainer> getContainersForData(IData data) {
		Set<IContainer> result = new HashSet<>();
		Set<Entry<IContainer, Set<IData>>> entrySet = containerToDataMap
				.entrySet();
		for (Entry<IContainer, Set<IData>> entry : entrySet) {
			if (entry.getValue().contains(data)) {
				result.add(entry.getKey());
			}
		}
		return result;
	}

	public boolean addDataToContainerMappings(Set<IData> dataSet,
			IContainer container) {
		if (dataSet == null) {
			return true;
		}

		boolean res = false;

		Set<IData> existingDataSet = containerToDataMap.get(container);
		if (existingDataSet == null) {
			Set<IData> newDataSet = new HashSet<>(dataSet);
			containerToDataMap.put(container, newDataSet);
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
	public boolean addName(IName name, IContainer container) {
		boolean res = false;
		if (name != null && !name.getName().isEmpty()) {
			_namingMap.put(name, container);
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

//		// FK: Malte's old code. The (more efficient) code below should do the job as well
//		// If you (anyone) agree, please delete
//		if (_namingSet.containsValue(containerId)) {
//			for (Entry<Name, String> entry : _namingSet.entrySet()) {
//				if (entry.getValue() == containerId) {
//					result.add(entry.getKey());
//				}
//			}
//		}

		if (_namingMap != null) {
			for (IName name : _namingMap.keySet()) {
				if (_namingMap.get(name).equals(container)) {
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
	public List<IName> getAllNamingsFrom(IContainer pid) {
		List<IName> result = new ArrayList<IName>();

		for (Entry<IName, IContainer> entry : _namingMap.entrySet()) {
			if (entry.getKey().getName().equals(pid))
				result.add(entry.getKey());
		}

		return result;
	}

}
