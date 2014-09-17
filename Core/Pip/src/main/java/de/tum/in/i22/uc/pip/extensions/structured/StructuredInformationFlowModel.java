package de.tum.in.i22.uc.pip.extensions.structured;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import de.tum.in.i22.uc.cm.datatypes.basic.DataBasic;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.interfaces.informationFlowModel.IStructuredInformationFlowModel;
import de.tum.in.i22.uc.pip.core.ifm.InformationFlowModelExtension;
import de.tum.in.i22.uc.pip.core.ifm.InformationFlowModelManager;

/**
 * Visibility of this class and its methods has been developed carefully. Access
 * via {@link InformationFlowModelManager}.
 *
 * @author Enrico Lovat
 *
 */
public final class StructuredInformationFlowModel extends
		InformationFlowModelExtension implements
		IStructuredInformationFlowModel {
	private static final Logger _logger = LoggerFactory
			.getLogger(StructuredInformationFlowModel.class);

	// Structured data map
	private Map<IData, Map<String, Set<IData>>> _structureMap;

	// BACKUP TABLES FOR SIMULATION
	private Map<IData, Map<String, Set<IData>>> _structureBackup;

	/**
	 * Basic constructor. Here we initialize the reference to the basic
	 * information flow model and the tables to store the structured data
	 * information.
	 * @param informationFlowModelManager
	 */
	public StructuredInformationFlowModel(InformationFlowModelManager informationFlowModelManager) {
		super (informationFlowModelManager);
		this.reset();
	}

	@Override
	public void reset() {
		_structureMap = new HashMap<IData, Map<String, Set<IData>>>();
		_structureBackup = null;
	}

	/**
	 * Simulation step: push. Stores the current IF state, if not already stored
	 *
	 * @return true if the state has been successfully pushed, false otherwise
	 */
	@Override
	public void push() {
		_logger.info("Pushing current PIP state...");
		if (_structureMap != null) {
			_structureBackup = new HashMap<IData, Map<String, Set<IData>>>();
			for (IData d : _structureMap.keySet()) {
				Map<String, Set<IData>> m = _structureMap.get(d);
				Map<String, Set<IData>> mbackup = new HashMap<String, Set<IData>>();
				if (m != null) {
					for (String s : m.keySet()) {
						Set<IData> set= mbackup.get(d);
						if (set==null) set = new HashSet<IData>();
						mbackup.put(s, new HashSet<IData>(set));
					}
				}
				_structureBackup.put(d, mbackup);
			}
		}
	}

	/**
	 * Simulation step: pop. Restore a previously pushed IF state, if any.
	 *
	 * @return true if the state has been successfully restored, false otherwise
	 */
	@Override
	public void pop() {
		_logger.info("Popping current PIP state...");
		if (_structureBackup != null) {
			_structureMap = _structureBackup;
			_structureBackup = null;
		}
	}

	/**
	 * This method takes as parameter a list of pairs (label - set of data) that
	 * represents the structure to be associated to a new structured data item,
	 * which should be returned. The behavior is to add another entry in our
	 * _structureMap table where a new IData is associated to the structure
	 * given as parameter.
	 *
	 * The new data item associated to the structured is returned.
	 *
	 */
	@Override
	public IData newStructuredData(Map<String, Set<IData>> structure) {
		IData d = new DataBasic();
		_logger.debug("new data [ " + d + " ] for structure created.");

		_logger.debug("Current size of structured data map : "
				+ _structureMap.size());
		if (structure != null) {
			_logger.debug("Adding structure for " + d + " to the map [ "
					+ structure + "]");
			_structureMap.put(d, structure);
			_logger.debug("Current size of structured data map : "
					+ _structureMap.size());
			return d;
		} else {
			_logger.debug("structure is null. nothing to do here. returning null");
			return null;
		}
	}

	/**
	 * This method takes as parameter a data item and returns the structure
	 * associated to it. If no structure for it exists, then the
	 * <code>null</code> value is returned.
	 */
	@Override
	public Map<String, Set<IData>> getStructureOf(IData data) {
		if (data == null) {
			_logger.error("no structure for NULL. returning empty map");
			return Collections.emptyMap();
		}
		Map<String, Set<IData>> map = _structureMap.get(data);
		if (map == null) {
			_logger.debug("No structure associated to data " + data);
			_logger.debug("returning empty map.");
			return Collections.emptyMap();
		}
		_logger.debug("returning structure for data " + data);
		_logger.debug("[ " + map + "]");
		return map;

	}

	/**
	 * This method receives a (structured) data item in input and returns the
	 * list of all the structured and non-structured data-items it corresponds
	 * to. If the initial item is not structured, this method returns only it.
	 *
	 * Because every structured data-item is freshly created, it is not possible
	 * to have circular dependency that would lead to a loop.
	 *
	 */
	@Override
	public Set<IData> flattenStructure(IData data) {
		if (data == null) {
			_logger.debug("flattening null data is pointless. returning null");
			return null;
		}
		HashSet<IData> res = new HashSet<IData>();
		// starting element
		res.add(data);

		boolean loopAgain = false;
		do {
			loopAgain = false;
			HashSet<IData> tmp = new HashSet<IData>();
			for (IData d : res) {
				Map<String, Set<IData>> table = _structureMap.get(d);

				// if data is structured (table!=null) we add all the data in
				// the table to the result set.
				// res.addAll returns true if the operation changed the previous
				// value of res. if it returns false, it means all the elements
				// were already present in the set.
				// If at least one element is added, we re-iterate again over
				// the whole set. Otherwise, we are done.
				// The size of res keeps growing (monotonically) and is bounded
				// by the maximum number of data elements in the system.Thus it
				// converges and there cannot be any infinite loop.
				if (table != null) {
					tmp.addAll(getDataInStructure(table));
				}
			}
			loopAgain = res.addAll(tmp);
			_logger.debug("loopAgain=" + loopAgain + ". resultSet("
					+ res.size() + ")=[" + res + "]");
		} while (loopAgain);
		return res;
	}

	private Set<IData> getDataInStructure(Map<String, Set<IData>> structure) {
		if (structure == null) {
			_logger.error("no data in null structure. returning null");
			return null;
		}
		Set<IData> res = new HashSet<IData>();
		for (String s : structure.keySet()) {
			res = Sets.union(res, structure.get(s));
		}
		return res;
	}

	@Override
	public String niceString() {
		StringBuilder sb = new StringBuilder();

		String nl = System.getProperty("line.separator");
		String arrow = " ---> ";

		sb.append("  Structures:" + nl);
		if ((_structureMap==null)||(_structureMap.size()==0)){
			sb.append("  Empty" + nl);
			return sb.toString();
		}
		for (IData d : _structureMap.keySet()) {
			sb.append("    " + d.getId() + arrow);
			boolean first = true;
			Map<String, Set<IData>> set = _structureMap.get(d);
			for (String s : set.keySet()) {
				if (first) {
					first = false;
				} else {
					sb.append("    ");
					for (int i = 0; i < d.getId().length() + arrow.length(); i++) {
						sb.append(" ");
					}
				}
				sb.append(String.format("%-10s [%s]", s, set.get(s)));
				sb.append(nl);
			}
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		return com.google.common.base.MoreObjects.toStringHelper(this)
				.add("_structure", _structureMap).toString();
	}

}
