package de.tum.in.i22.uc.pmp.requests;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.processing.PmpProcessor;

public class GetPoliciesPmpRequest extends PmpRequest<Set<XmlPolicy>> {
	private final IData _data;

	public GetPoliciesPmpRequest(IData data) {
		_data = data;
	}
	
	@Override
	public Set<XmlPolicy> process(PmpProcessor processor) {
		return processor.getPolicies(_data);
	}
}
