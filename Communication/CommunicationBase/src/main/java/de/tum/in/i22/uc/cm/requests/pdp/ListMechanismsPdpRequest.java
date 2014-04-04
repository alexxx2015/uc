package de.tum.in.i22.uc.cm.requests.pdp;

import java.util.List;
import java.util.Map;

import de.tum.in.i22.uc.cm.server.PdpProcessor;

public class ListMechanismsPdpRequest extends PdpRequest<Map<String,List<String>>> {

	@Override
	public Map<String,List<String>> process(PdpProcessor processor) {
		return processor.listMechanisms();
	}

}
