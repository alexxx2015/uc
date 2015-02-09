package de.tum.in.i22.uc.pip.extensions.structured;

import java.util.Map;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.pip.ifm.IAnyInformationFlowModel;

public class Splitter extends StructuredEvent {
	private IName _src;
	private Map<String, IName> _dst;

	public Splitter(String name, IName src, Map<String, IName> labelContMap, IAnyInformationFlowModel informationFlowModel) {
		_name = name;
		_src = src;
		_dst = labelContMap;
		_informationFlowModel = informationFlowModel;
	}

	public Map<String, IName> getDestinations() {
		return _dst;
	}

	public IName getIntermediateContainer() {
		return _src;
	}

	/**
	 * return the name of the destination container associated to a specific label
	 * @param label
	 * @return
	 */
	public IName getDestination(String label) {
		return _dst.get(label);
	}

}
