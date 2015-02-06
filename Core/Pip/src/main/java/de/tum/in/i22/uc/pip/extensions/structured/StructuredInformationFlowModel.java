package de.tum.in.i22.uc.pip.extensions.structured;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Sets;

import de.tum.in.i22.uc.cm.datatypes.basic.DataBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IChecksum;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.pip.ifm.IStructuredInformationFlowModel;
import de.tum.in.i22.uc.generic.observable.NotifyingMap;
import de.tum.in.i22.uc.generic.observable.NotifyingSet;
import de.tum.in.i22.uc.pip.core.ifm.InformationFlowModelExtension;
import de.tum.in.i22.uc.pip.core.ifm.InformationFlowModelManager;

/**
 * Visibility of this class and its methods has been developed carefully. Access
 * via {@link InformationFlowModelManager}.
 *
 * @author Enrico Lovat
 *
 */
public final class StructuredInformationFlowModel extends InformationFlowModelExtension implements
		IStructuredInformationFlowModel {
	private static final Logger _logger = LoggerFactory.getLogger(StructuredInformationFlowModel.class);

	// Structured data map
	private Map<IData, Map<String, Set<IData>>> _structureMap;
	// Checksum map
	private Map<IData, IChecksum> _structureChecksumMap;

	// BACKUP TABLES FOR SIMULATION
	private Map<IData, Map<String, Set<IData>>> _structureBackup;
	private Map<IData, IChecksum> _structureChecksumBackup;

	/**
	 * Basic constructor. Here we initialize the reference to the basic
	 * information flow model and the tables to store the structured data
	 * information.
	 * 
	 * @param informationFlowModelManager
	 */
	public StructuredInformationFlowModel(InformationFlowModelManager informationFlowModelManager) {
		super(informationFlowModelManager);
		this.reset();
	}

	@Override
	public void reset() {
		super.reset();
		_structureMap = new NotifyingMap<>(new HashMap<IData, Map<String, Set<IData>>>(), _observer);
		_structureChecksumMap = new NotifyingMap<>(new HashMap<IData, IChecksum>(), _observer);
		_structureBackup = null;
		_structureChecksumBackup = null;
	}

	/**
	 * Simulation step: push. Stores the current IF state, if not already stored
	 *
	 * @return true if the state has been successfully pushed, false otherwise
	 */
	@Override
	public IStatus startSimulation() {
		super.startSimulation();
		_logger.info("Pushing current PIP state...");
		if (_structureMap != null) {
			_structureBackup = new NotifyingMap<>(new HashMap<IData, Map<String, Set<IData>>>(), _observer);
			for (IData d : _structureMap.keySet()) {
				Map<String, Set<IData>> m = _structureMap.get(d);
				Map<String, Set<IData>> mbackup = new NotifyingMap<>(new HashMap<String, Set<IData>>(), _observer);
				if (m != null) {
					for (String s : m.keySet()) {
						Set<IData> set = m.get(s);
						if (set == null)
							set = new NotifyingSet<>(new HashSet<IData>(), _observer);
						mbackup.put(s, set);
					}
				}
				_structureBackup.put(d, mbackup);
			}
		}
		if (_structureChecksumMap != null) {
			_structureChecksumBackup = new NotifyingMap<>(new HashMap<IData, IChecksum>(), _observer);
			for (IData d : _structureChecksumMap.keySet()) {
				IChecksum c = _structureChecksumMap.get(d);
				_structureChecksumBackup.put(d, c);
			}
		}

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

		_logger.info("Popping current PIP state...");
		if (_structureBackup != null) {
			_structureMap = _structureBackup;
			_structureChecksumMap = _structureChecksumBackup;
			_structureBackup = null;
			_structureChecksumBackup = null;
		}

		return new StatusBasic(EStatus.OKAY);
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

		_logger.debug("Current size of structured data map : " + _structureMap.size());
		if (structure != null) {
			_logger.debug("Adding structure for " + d + " to the map [ " + structure + "]");
			_structureMap.put(d, structure);
			_logger.debug("Current size of structured data map : " + _structureMap.size());
			return d;
		} else {
			_logger.debug("structure is null. nothing to do here. returning null");
			return null;
		}
	}

	/**
	 * This method takes as parameter a structured data, a valid checksum for it
	 * and a flag that states whether the new checksum should overwrite an
	 * existing value, if present. If data is <code>null</code>, not structured,
	 * if new checksum is <code>null</code>, or if an existing checksum already
	 * exists and the boolean flag is false, the method returns false. Otherwise
	 * it returns true.
	 *
	 */
	@Override
	public boolean newChecksum(IData data, IChecksum checksum, boolean overwrite) {
		if ((data == null) || (checksum == null)) {
			_logger.debug("impossible to store checksum for null values. ( Data=" + data + ", checksum=" + checksum
					+ ")");
			return false;
		}

		if (getStructureOf(data) == null) {
			_logger.debug("impossible to store checksum for unstructred data " + data + ".");
			return false;
		}

		IChecksum cOld = _structureChecksumMap.get(data);
		if (cOld != null) {
			if (cOld.equals(checksum)) {
				_logger.debug("Checksum [" + checksum + "] for data [" + data + "] was already stored in the mapping.");
				return false;
			} else {
				_logger.debug("data [" + data + "] already associated to checksum [" + cOld + "] in the mapping.");
				if (!overwrite) {
					_logger.debug("Overwrite flag == false --> Old checksum preserved. Returning false.");
					return false;
				}
			}
		}

		_logger.debug("Storing new checksum [" + checksum + "] for data [" + data + "]");
		_structureChecksumMap.put(data, checksum);
		return true;
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
	 * This method takes as parameter a data item and returns the checksum
	 * associated to it. If no checksum for it exists, then the
	 * <code>null</code> value is returned.
	 */
	@Override
	public IChecksum getChecksumOf(IData data) {
		if (data == null) {
			_logger.error("no checksum for NULL. returning empty object");
			return null;
		}
		IChecksum c = _structureChecksumMap.get(data);
		if (c == null) {
			_logger.debug("No checksum associated to data " + data);
			_logger.debug("returning null.");
			return null;
		}
		_logger.debug("returning checksum for data " + data + " = [ " + c + "]");
		return c;
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
			_logger.debug("loopAgain=" + loopAgain + ". resultSet(" + res.size() + ")=[" + res + "]");
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
		if ((_structureMap == null) || (_structureMap.size() == 0)) {
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

	/**
	 * This method delete the checksum for a given data. If data is
	 * <code>null</code> or has no checksum associated to it, the method returns
	 * false. Otherwise it returns true.
	 *
	 */
	@Override
	public boolean deleteChecksum(IData d) {
		if (d == null) {
			_logger.debug("Impossible to delete checksum for null data.");
			return false;
		}

		IChecksum c = getChecksumOf(d);
		if (c == null) {
			_logger.debug("No checksum currently associated to data " + d + ".");
			return false;
		}

		_logger.debug("Deleting checksum [" + c + "] for data [" + d + "]...");
		_structureChecksumMap.remove(d);
		return true;
	}

	/**
	 * This method delete the structure for a given data. If data is
	 * <code>null</code> or has no structure associated to it, the method
	 * returns false. Otherwise it returns true. Note that deleting the
	 * structure also delete the respective checksum.
	 *
	 */
	@Override
	public boolean deleteStructure(IData d) {
		if (d == null) {
			_logger.debug("Impossible to delete structure for null data.");
			return false;
		}

		Map<String, Set<IData>> map = _structureMap.get(d);
		if (map == null) {
			_logger.debug("No structure currently associated to data " + d + ".");
			return false;
		}

		_logger.debug("Deleting checksum for data [" + d + "]...");
		deleteChecksum(d);
		_logger.debug("Deleting structure for data [" + d + "]...");
		_structureMap.remove(d);
		return true;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("_structure", _structureMap).toString();
	}

	@Override
	public boolean isSimulating() {
		return _structureBackup != null;
	}

}
