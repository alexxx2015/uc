package de.tum.in.i22.uc.pmp.requests;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.XmlPolicy;
import de.tum.in.i22.uc.cm.processing.PmpProcessor;

public class ListPoliciesPmpRequest extends PmpRequest<Set<XmlPolicy>> {

	public ListPoliciesPmpRequest() {
	}
	
	@Override
	public Set<XmlPolicy> process(PmpProcessor processor) {
		return processor.listPoliciesPmp();
	}
}
