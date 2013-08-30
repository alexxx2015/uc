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

import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IIdentifiable;

/**
 * Information flow model
 * Singleton.
 */
public class InformationFlowModel {
	private static final InformationFlowModel _instance = new InformationFlowModel();
	
	
	// list of containers
	private Set<IContainer> _containerSet = null;
	// list of models
	private Set<IData> _dataSet = null;
	
	// [Container.identifier -> List[Data.identifier]]
	private Map<String, Set<String>> _dataToContainerMap = null;
	
	// [Container.identifier -> List[Container.identifier]]
	private Map<String, Set<String>> _containerAliasesMap = null;
	
	// the naming set [name -> List[Container.identifier]]
	private Map<Name, String> _namingSet = null;
	
	public InformationFlowModel() {
		_containerSet = new HashSet<>();
		_dataSet = new HashSet<>();
		_dataToContainerMap = new HashMap<>();
		_containerAliasesMap = new HashMap<>();
		_namingSet = new HashMap<>();
	}
	
	public static InformationFlowModel getInstance() {
		return _instance;
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
	 * @return Id )of the container.
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
	
	public boolean hasContainerWithId(String id) {
		IContainer container = getContainerById(id);
		return container != null ? true : false;
	}
	
	public IContainer getContainerById(String id) {
		return (IContainer)getElementFromSet(id, _containerSet);
	}
	
	public boolean emptyContainer(String id) {
		boolean res = false;
		if (hasContainerWithId(id)) {
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
		if (result == null) result = new HashSet<>();
		
		return result;
	}
	
	
	/**
	 *  Returns the reflexive, transitive closure of the alias function for container with 
	 *  id containerId.
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
	 * @param toContainerId
	 * @return
	 */
	public boolean removeAllAliasesTo(String toContainerId) {
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
	public Set<String> getAliasesTo(String toContainerId) {
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
	
	public boolean addDataToContainerMapping(String dataId, String containerId) {
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
	
	public boolean addDataToContainerMappings(Set<String> dataSet, String containerId) {
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
	 * Adds an entry to the naming mapping for container contID, with the naming/representation name.
	 * @param name
	 * @param containerId
	 * @return
	 */
	public boolean addName(Name name, String containerId) {
		boolean res = false;
		if (name != null && !name.getName().isEmpty()) {
			_namingSet.put(name, containerId);
			res = true;
		}
		return res;
	}
	
	/**
	 * Removes the naming/representation name from the naming set.
	 * @param name
	 * @return
	 */
	public boolean removeName(Name name) {
		boolean res = false;
		if (name != null) {
			String removedEntry = _namingSet.remove(name);
			res = removedEntry != null ? true : false;
		}
		return res;
	}
	
	/**
	 * Returns the container that is referenced by the naming name.
	 * @param name
	 * @return
	 */
	public String getContainerIdByName(Name name) {
		String containerId = null;
		if (name != null && name.getName() != null) {
			Set<Name> pipNameSet = _namingSet.keySet();
			for (Name nm: pipNameSet) {
				String representationName = nm.getName();
				if (name.getName().equals(representationName)) {
					containerId = _namingSet.get(nm);
					break;
				}
			}
		}
		return containerId;
	}
	
	/**
	 * Returns the container that is referenced by the naming name.
     * The search is done in a less strict way; it is enough that the name only partially
     * fits an entry in the naming mapping.
	 * @param name
	 * @return
	 */
	public String getContainerIdByNameRelaxed(Name name) {
		String containerId = null;
		if (name != null && name.getName() != null) {
			String representationName = name.getName();
			for (Name nm : _namingSet.keySet()) {
				if (nm.getName() != null && 
						nm.getName().contains(representationName)) {
					
					containerId = _namingSet.get(nm);
					break;
				}
			}
		}
		return containerId;
	}
	
	/**
	 * Return all re that refer to the container with containerId.
	 * @param containerId
	 * @return
	 */
	public List<Name> getAllNames(String containerId) {
		List<Name> result = new ArrayList<Name>();
		
		if (_namingSet.containsValue(containerId)) {
			for (Entry<Name, String> entry : _namingSet.entrySet()) {
				if (entry.getValue() == containerId) {
					result.add(entry.getKey());
				}
			}
		}
		return result;
	}
	
    /**
     *  Returns all representations that correspond to the process with pid.
     *   
    */
    public List<Name> getAllNamingsFrom(String pid)
    {
        List<Name> result = new ArrayList<Name>();

        for (Entry<Name, String> entry : _namingSet.entrySet())
        {
            if (entry.getKey().getName().equals(pid))
            	result.add(entry.getKey());
        }

        return result;
    }
	
}
