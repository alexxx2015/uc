package de.tum.in.i22.uc.pdp.requests;

import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.cm.processing.PdpProcessor;

public class ListMechanismsPdpRequest extends PdpRequest<Map<String,Set<String>>> {

	@Override
	public Map<String,Set<String>> process(PdpProcessor processor) {
		return processor.listMechanisms();
	}

}
