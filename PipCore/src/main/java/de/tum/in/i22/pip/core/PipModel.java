package de.tum.in.i22.pip.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IIdentifiable;

public class PipModel {
	// counter of the unique data and counter ids
	private int _dataIdCounter = 0;
	private int _containerIdCounter = 0;
	
	// list of containers
	private Set<IContainer> _containerSet = null;
	// list of models
	private Set<IData> _dataSet = null;
	
	// [Container.identifier -> List[Data.identifier]]
	private Map<String, Set<String>> _dataToContainerMap = null;
	// [Container.identifier -> List[Container.identifier]]
	private Map<String, Set<String>> _containerAliasesMap = null;
	
	public PipModel() {
		_containerSet = new HashSet<>();
		_dataSet = new HashSet<>();
		_dataToContainerMap = new HashMap<>();
		_containerAliasesMap = new HashMap<>();
	}
	
	/**
	 * Adds data object to a set. If the set already 
	 * contains the data object, the method will return null.
	 * Otherwise, it will return the id of the data object.
	 * @param data
	 * @return Id of the data object if set does not contain the data
	 * object, otherwise null.
	 */
	public String addData(IData data) {
		assert(data != null);
		
		if (_dataSet.contains(data)) {
			return null;
		}
		
		_dataSet.add(data);
		
		return data.getId();
	}
	
	/**
	 * Removes data object form an internal set.
	 * @param data
	 * @return true if the data object is successfully removed.
	 */
	public boolean removeData(IData data) {
		assert(data != null);
		return _dataSet.remove(data);
	}
	
	/**
	 * Searches for data object by id.
	 * @param id
	 * @return Data object if it is present in the set, otherwise null.
	 */
	public IData getDataById(String id) {
		return (IData)getElementFromSet(id, _dataSet);
	}
	
	/**
	 * Checks if the model contains a data object with given id.
	 * @param id
	 * @return
	 */
	public boolean hasData(String id) {
		IData data = getDataById(id);
		return data != null ? true : false;
	}

	/**
	 * Inserts container into the model.
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
		IContainer container = (IContainer)getElementFromSet(id, _containerSet);
		boolean res = false;
		if (container == null) {
			res = false;
		} else {
			res = _containerSet.remove(container);
		}
		return res;
	}
	
	public boolean hasContainer(String id) {
		IContainer container = getContainer(id);
		return container != null ? true : false;
	}
	
	public IContainer getContainer(String id) {
		return (IContainer)getElementFromSet(id, _containerSet);
	}
	
	public boolean emptyContainer(String id) {
		boolean res = false;
		if (hasContainer(id)) {
			Set<String> set = _dataToContainerMap.get(id);
			if (set != null) {
				set.clear();
				res = true;
			}
		}
		
		return res;
	}
	
	/**
	 * Adds an alias relation from one container to another.
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
	 * Removes the alias relation identified by the tuple (fromContainerId, toContainerId)
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
	 * @param id
	 * @return All aliases of a container given by its id, empty set if 
	 * the container has no aliases.
	 */
	public Set<String> getAliasesForContainer(String id) {
		Set<String> result = _containerAliasesMap.get(id);
		// if no such element is present, return an empty set
		if (result != null) result = new HashSet<>();
		
		return result;
	}
	
	
	/**
	 *  Returns the reflexive, transitive closure of the alias function for container with 
	 *  id containerId.
	 * @param containerId
	 * @return
	 */
	public Set<String> getAliasClosure(String containerId) {
		Set<String> closureSet = getAliasClosureExcludeStartId(containerId);
		// add self to set ==> reflexive
		closureSet.add(containerId);
		
		return closureSet;
	}
	
	/**
	 * Removes all aliases that start from the container with given id.
	 * @param fromContainerId
	 * @return
	 */
	public boolean removeAllAliasesFrom(int fromContainerId) {
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
	 * @param toContainerId
	 * @return
	 */
	public boolean removeAllAliasesTo(int toContainerId) {
		//TODO This method seems to have a bug.
		boolean res = false;
		Set<String> aliasesToContainer = getAliasesTo(toContainerId);
		if (aliasesToContainer.size() > 0) {
			Collection<Set<String>> aliasesCollection = _containerAliasesMap.values();
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
	public Set<String> getAliasesTo(int toContainerId) {
		Set<String> result = new HashSet<String>();
		Set<Entry<String, Set<String>>> entrySet = _containerAliasesMap.entrySet();
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
	
	private void getAliasClosureExcludeStartId(String containerId, Set<String> visitedContainers) {
		Set<String> containerAliasesSet = getAliasesForContainer(containerId);
		for (String id: containerAliasesSet) {
			if (visitedContainers.add(id)) {
				getAliasClosureExcludeStartId(id, visitedContainers);
			}
		}
	}
	
	private IIdentifiable getElementFromSet(String id, Set<? extends IIdentifiable> set) {
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
	
	public boolean addContainerToDataMapping(String containerId, String dataId) {
		boolean res = false;
		if (_dataToContainerMap.containsKey(containerId)) {
			Set<String> dataSet = _dataToContainerMap.get(containerId);
			res = dataSet.add(dataId);
		} else {
			Set<String> newDataSet = new HashSet<>();
			newDataSet.add(dataId);
			
			_dataToContainerMap.put(containerId, newDataSet);
			res = true;
		}
		return res;
	}
	
	public boolean removeContainerToDataMapping(String containerId, String dataId) {
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
	 * @return All data items that are stored in the given data container.
	 * If there are no data for the given data container, returns empty set.
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
		Set<Entry<String, Set<String>>> entrySet = _dataToContainerMap.entrySet();
		for (Entry<String, Set<String>> entry : entrySet) {
			if (entry.getValue().contains(dataId)) {
				result.add(entry.getKey());
			}
		}
		return result;
	}
	
	public boolean addDataToContainerMappings(String containerId, Set<String> dataSet) {
		assert(dataSet != null);
		
		boolean res = false;
		
		Set<String> existingDataSet = _dataToContainerMap.get(containerId);
		if (dataSet == null) {
			existingDataSet = new HashSet<String>();
		}
	
		res = existingDataSet.addAll(dataSet);
		return res;
	}
	
}
