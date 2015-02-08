package de.tum.in.i22.uc.pip.extensions.structured;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.pip.ifm.IAnyInformationFlowModel;

public class Merger extends StructuredEvent {
	private Map<String, IName> _src;
	private IName _dst;
	private IData _structuredData;

	public Merger(String name, Map<String, IName> labelContMap, IName dst, IAnyInformationFlowModel informationFlowModel) {
		_name = name;
		_src = labelContMap;
		_dst = dst;
		_informationFlowModel = informationFlowModel;
		_structuredData = null;
	}

	/***
	 *
	 * Build structured data
	 *
	 ****/

	public IData buildStructure() {
		if (_structuredData == null) {

			Map<String, Set<IData>> _structure = new HashMap<String, Set<IData>>();
			if (_src != null) {
				for (String lbl : _src.keySet()) {
					IName srcName = _src.get(lbl);
					_logger.debug("Adding pair " + lbl + "-" + srcName + " to the structure");
					Set<IData> sd = _informationFlowModel.getData(srcName);
					_structure.put(lbl, sd);
				}
				_logger.debug("Creating new structured data");
				IData newD = _informationFlowModel.newStructuredData(_structure);
				_structuredData = newD;
				return newD;
			}
			_logger.debug("Merger with empty list of sources, nothing to do here");
			return null;
		} else {
			_logger.debug("_structured data already built for this event");
			return _structuredData;
		}
	}

	public Map<String, IName> getSources() {
		return _src;
	}

	public IName getIntermediateContainerName() {
		return _dst;
	}

	public IData getStructuredData() {
		return _structuredData;
	}

}
